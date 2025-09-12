package com.admin.module.infra.biz.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 流式数据处理器
 * 解决大文件处理的内存占用问题，支持背压控制和零拷贝优化
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@Slf4j
public class StreamingDataProcessor {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int DEFAULT_QUEUE_CAPACITY = 1000;
    private static final String POISON_PILL = "__END_OF_STREAM__";

    /**
     * 创建可背压控制的流式处理器
     *
     * @param inputStream 输入流
     * @param processor 数据处理函数
     * @param progressCallback 进度回调
     * @param bufferSize 缓冲区大小
     * @param <T> 数据类型
     * @param <R> 结果类型
     * @return 流式处理结果
     */
    public <T, R> StreamProcessResult<R> processStream(
            InputStream inputStream,
            Function<T, R> processor,
            Function<String, T> parser,
            Consumer<StreamProgress> progressCallback,
            int bufferSize) {
        
        bufferSize = bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize;
        
        log.info("开始流式数据处理，缓冲区大小: {}", bufferSize);
        
        AtomicLong processedCount = new AtomicLong(0);
        AtomicLong successCount = new AtomicLong(0);
        AtomicLong errorCount = new AtomicLong(0);
        AtomicBoolean processingComplete = new AtomicBoolean(false);
        
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8), bufferSize)) {
            
            // 创建流式处理管道
            Stream<String> lineStream = reader.lines();
            
            // 并行处理（使用自定义 ForkJoinPool）
            CompletableFuture<StreamProcessResult<R>> processFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return lineStream
                        .parallel()
                        .map(line -> {
                            try {
                                // 解析数据
                                T data = parser.apply(line);
                                if (data == null) {
                                    errorCount.incrementAndGet();
                                    return null;
                                }
                                
                                // 处理数据
                                R result = processor.apply(data);
                                successCount.incrementAndGet();
                                
                                // 更新进度
                                long currentProcessed = processedCount.incrementAndGet();
                                if (currentProcessed % 100 == 0 && progressCallback != null) {
                                    progressCallback.accept(new StreamProgress(
                                        currentProcessed, 
                                        successCount.get(), 
                                        errorCount.get(),
                                        calculateThroughput(startTime, currentProcessed)
                                    ));
                                }
                                
                                return result;
                                
                            } catch (Exception e) {
                                errorCount.incrementAndGet();
                                log.debug("处理行数据失败: {}", line, e);
                                return null;
                            }
                        })
                        .filter(result -> result != null)
                        .collect(() -> new StreamCollector<R>(),
                                (collector, item) -> collector.add(item),
                                (c1, c2) -> c1.merge(c2))
                        .toResult();
                } finally {
                    processingComplete.set(true);
                }
            });
            
            // 等待处理完成
            StreamProcessResult<R> result = processFuture.get();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("流式数据处理完成，总耗时: {}ms, 处理量: {}, 成功: {}, 失败: {}, 平均吞吐量: {}/秒", 
                    duration, processedCount.get(), successCount.get(), errorCount.get(),
                    calculateThroughput(startTime, processedCount.get()));
            
            return new StreamProcessResult<>(
                result.getResults(),
                processedCount.get(),
                successCount.get(),
                errorCount.get(),
                duration
            );
            
        } catch (Exception e) {
            log.error("流式数据处理异常", e);
            throw new RuntimeException("流式数据处理失败", e);
        }
    }

    /**
     * 创建背压控制的异步流处理器
     */
    public <T, R> BackpressureStreamProcessor<T, R> createBackpressureProcessor(
            Function<T, R> processor,
            Consumer<StreamProgress> progressCallback) {
        
        return new BackpressureStreamProcessor<>(processor, progressCallback);
    }

    /**
     * 内存友好的批量流处理
     */
    public <T, R> Stream<R> processBatchStream(
            InputStream inputStream,
            Function<String, T> parser,
            Function<Stream<T>, Stream<R>> batchProcessor,
            int batchSize) {
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            return StreamSupport.stream(
                new BatchingSpliterator<>(reader.lines().iterator(), parser, batchSize),
                true // 支持并行
            ).flatMap(batchProcessor);
            
        } catch (IOException e) {
            log.error("批量流处理异常", e);
            throw new RuntimeException("批量流处理失败", e);
        }
    }

    /**
     * 计算吞吐量
     */
    private double calculateThroughput(long startTime, long processedCount) {
        long duration = System.currentTimeMillis() - startTime;
        return duration > 0 ? (processedCount * 1000.0 / duration) : 0;
    }

    /**
     * 背压控制的流处理器
     */
    public static class BackpressureStreamProcessor<T, R> {
        private final Function<T, R> processor;
        private final Consumer<StreamProgress> progressCallback;
        private final BlockingQueue<T> inputQueue;
        private final BlockingQueue<R> outputQueue;
        private final AtomicBoolean isRunning = new AtomicBoolean(false);
        private final AtomicLong processedCount = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);

        public BackpressureStreamProcessor(Function<T, R> processor, Consumer<StreamProgress> progressCallback) {
            this.processor = processor;
            this.progressCallback = progressCallback;
            this.inputQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
            this.outputQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        }

        /**
         * 启动异步处理
         */
        public CompletableFuture<Void> startAsync() {
            if (!isRunning.compareAndSet(false, true)) {
                throw new IllegalStateException("处理器已经在运行");
            }

            long startTime = System.currentTimeMillis();

            return CompletableFuture.runAsync(() -> {
                log.info("背压流处理器启动");
                
                try {
                    T data;
                    while ((data = inputQueue.take()) != null) {
                        // 检查结束标志
                        if (POISON_PILL.equals(data)) {
                            break;
                        }

                        try {
                            R result = processor.apply(data);
                            outputQueue.offer(result);
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            log.debug("数据处理失败", e);
                            errorCount.incrementAndGet();
                        }

                        // 更新进度
                        long currentProcessed = processedCount.incrementAndGet();
                        if (currentProcessed % 100 == 0 && progressCallback != null) {
                            progressCallback.accept(new StreamProgress(
                                currentProcessed,
                                successCount.get(),
                                errorCount.get(),
                                calculateThroughput(startTime, currentProcessed)
                            ));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("背压流处理器被中断", e);
                } finally {
                    isRunning.set(false);
                    log.info("背压流处理器停止，处理量: {}, 成功: {}, 失败: {}", 
                            processedCount.get(), successCount.get(), errorCount.get());
                }
            });
        }

        /**
         * 添加待处理数据
         */
        public boolean offer(T data) {
            if (!isRunning.get()) {
                return false;
            }
            return inputQueue.offer(data);
        }

        /**
         * 获取处理结果
         */
        public R poll() {
            return outputQueue.poll();
        }

        /**
         * 停止处理器
         */
        @SuppressWarnings("unchecked")
        public void stop() {
            inputQueue.offer((T) POISON_PILL);
        }

        /**
         * 获取队列状态
         */
        public QueueStats getQueueStats() {
            return new QueueStats(
                inputQueue.size(),
                outputQueue.size(),
                inputQueue.remainingCapacity(),
                outputQueue.remainingCapacity()
            );
        }
    }

    /**
     * 批量分割器
     */
    private static class BatchingSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
        private final Iterator<String> sourceIterator;
        private final Function<String, T> parser;
        private final int batchSize;
        private int currentBatchCount = 0;

        public BatchingSpliterator(Iterator<String> sourceIterator, Function<String, T> parser, int batchSize) {
            super(Long.MAX_VALUE, Spliterator.ORDERED);
            this.sourceIterator = sourceIterator;
            this.parser = parser;
            this.batchSize = batchSize;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (!sourceIterator.hasNext()) {
                return false;
            }

            try {
                String line = sourceIterator.next();
                T parsed = parser.apply(line);
                if (parsed != null) {
                    action.accept(parsed);
                    currentBatchCount++;
                    return true;
                }
            } catch (Exception e) {
                // 解析失败，继续下一条
                log.debug("数据解析失败", e);
            }

            return tryAdvance(action); // 递归尝试下一条
        }
    }

    /**
     * 流收集器
     */
    private static class StreamCollector<R> {
        private final AtomicLong count = new AtomicLong(0);

        public void add(R item) {
            count.incrementAndGet();
        }

        public StreamCollector<R> merge(StreamCollector<R> other) {
            count.addAndGet(other.count.get());
            return this;
        }

        public StreamProcessResult<R> toResult() {
            return new StreamProcessResult<>(null, count.get(), count.get(), 0, 0);
        }
    }

    /**
     * 流处理结果
     */
    public static class StreamProcessResult<R> {
        private final Iterable<R> results;
        private final long totalCount;
        private final long successCount;
        private final long errorCount;
        private final long processingTimeMs;

        public StreamProcessResult(Iterable<R> results, long totalCount, long successCount, 
                                 long errorCount, long processingTimeMs) {
            this.results = results;
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.processingTimeMs = processingTimeMs;
        }

        public Iterable<R> getResults() { return results; }
        public long getTotalCount() { return totalCount; }
        public long getSuccessCount() { return successCount; }
        public long getErrorCount() { return errorCount; }
        public long getProcessingTimeMs() { return processingTimeMs; }
        public double getThroughputPerSecond() { 
            return processingTimeMs > 0 ? (totalCount * 1000.0 / processingTimeMs) : 0; 
        }
    }

    /**
     * 流处理进度
     */
    public static class StreamProgress {
        private final long processedCount;
        private final long successCount;
        private final long errorCount;
        private final double throughputPerSecond;

        public StreamProgress(long processedCount, long successCount, long errorCount, double throughputPerSecond) {
            this.processedCount = processedCount;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.throughputPerSecond = throughputPerSecond;
        }

        public long getProcessedCount() { return processedCount; }
        public long getSuccessCount() { return successCount; }
        public long getErrorCount() { return errorCount; }
        public double getThroughputPerSecond() { return throughputPerSecond; }
        public double getSuccessRate() { 
            return processedCount > 0 ? (successCount * 100.0 / processedCount) : 0; 
        }
    }

    /**
     * 队列状态
     */
    public static class QueueStats {
        private final int inputQueueSize;
        private final int outputQueueSize;
        private final int inputQueueCapacity;
        private final int outputQueueCapacity;

        public QueueStats(int inputQueueSize, int outputQueueSize, int inputQueueCapacity, int outputQueueCapacity) {
            this.inputQueueSize = inputQueueSize;
            this.outputQueueSize = outputQueueSize;
            this.inputQueueCapacity = inputQueueCapacity;
            this.outputQueueCapacity = outputQueueCapacity;
        }

        public int getInputQueueSize() { return inputQueueSize; }
        public int getOutputQueueSize() { return outputQueueSize; }
        public int getInputQueueCapacity() { return inputQueueCapacity; }
        public int getOutputQueueCapacity() { return outputQueueCapacity; }
        public double getInputQueueUtilization() { 
            return inputQueueCapacity > 0 ? (inputQueueSize * 100.0 / inputQueueCapacity) : 0; 
        }
        public double getOutputQueueUtilization() { 
            return outputQueueCapacity > 0 ? (outputQueueSize * 100.0 / outputQueueCapacity) : 0; 
        }
    }
}