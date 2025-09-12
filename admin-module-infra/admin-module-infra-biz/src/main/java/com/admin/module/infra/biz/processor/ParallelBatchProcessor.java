package com.admin.module.infra.biz.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 并行批处理引擎
 * 解决单线程处理瓶颈，支持动态批量大小调整和工作窃取
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@Slf4j
public class ParallelBatchProcessor {

    private static final int DEFAULT_BATCH_SIZE = 500;
    private static final int MIN_BATCH_SIZE = 100;
    private static final int MAX_BATCH_SIZE = 2000;
    
    private final ForkJoinPool processingPool;
    private final AtomicLong totalProcessedCount = new AtomicLong(0);
    private final AtomicInteger activeTasks = new AtomicInteger(0);

    public ParallelBatchProcessor() {
        // 创建工作窃取式线程池，线程数为CPU核心数的2倍
        int parallelism = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
        this.processingPool = new ForkJoinPool(
            parallelism,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (thread, exception) -> {
                log.error("并行处理线程异常", exception);
            },
            true // 启用异步模式
        );
        
        log.info("并行批处理引擎初始化完成，并行度: {}", parallelism);
    }

    /**
     * 并行批处理数据
     *
     * @param dataList 待处理数据列表
     * @param batchProcessor 批处理函数
     * @param progressCallback 进度回调
     * @param <T> 数据类型
     * @param <R> 结果类型
     * @return 批处理结果
     */
    public <T, R> BatchProcessResult<R> processBatches(
            List<T> dataList,
            Function<List<T>, List<R>> batchProcessor,
            ProgressCallback progressCallback) {
        
        if (dataList == null || dataList.isEmpty()) {
            return new BatchProcessResult<>(new ArrayList<>(), 0, 0);
        }

        long startTime = System.currentTimeMillis();
        int totalSize = dataList.size();
        
        // 动态计算批量大小
        int batchSize = calculateOptimalBatchSize(totalSize, getCurrentSystemLoad());
        
        log.info("开始并行批处理，总数据量: {}, 批量大小: {}, 预计批次: {}", 
                totalSize, batchSize, (totalSize + batchSize - 1) / batchSize);

        List<List<T>> batches = createBatches(dataList, batchSize);
        List<CompletableFuture<BatchResult<R>>> futures = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<R> allResults = new CopyOnWriteArrayList<>();

        // 提交并行任务
        for (int i = 0; i < batches.size(); i++) {
            final int batchIndex = i;
            final List<T> batch = batches.get(i);
            
            CompletableFuture<BatchResult<R>> future = CompletableFuture
                .supplyAsync(() -> {
                    activeTasks.incrementAndGet();
                    try {
                        return processSingleBatch(batch, batchIndex, batchProcessor, progressCallback, 
                                                processedCount, totalSize);
                    } finally {
                        activeTasks.decrementAndGet();
                    }
                }, processingPool)
                .exceptionally(throwable -> {
                    log.error("批处理异常，批次索引: {}", batchIndex, throwable);
                    errorCount.incrementAndGet();
                    return new BatchResult<>(new ArrayList<>(), batch.size(), 0, 1);
                });
            
            futures.add(future);
        }

        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        try {
            // 设置超时时间为30分钟
            allFutures.get(30, TimeUnit.MINUTES);
            
            // 收集结果
            int successCount = 0;
            int failureCount = 0;
            
            for (CompletableFuture<BatchResult<R>> future : futures) {
                BatchResult<R> batchResult = future.get();
                allResults.addAll(batchResult.getResults());
                successCount += batchResult.getSuccessCount();
                failureCount += batchResult.getFailureCount();
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 更新全局统计
            totalProcessedCount.addAndGet(successCount);
            
            log.info("并行批处理完成，总耗时: {}ms, 成功: {}, 失败: {}, 平均处理速度: {}/秒", 
                    duration, successCount, failureCount, 
                    duration > 0 ? (successCount * 1000 / duration) : 0);
            
            return new BatchProcessResult<>(allResults, successCount, failureCount);
            
        } catch (TimeoutException e) {
            log.error("并行批处理超时");
            // 取消未完成的任务
            futures.forEach(future -> future.cancel(true));
            throw new RuntimeException("批处理超时", e);
        } catch (InterruptedException | ExecutionException e) {
            log.error("并行批处理异常", e);
            throw new RuntimeException("批处理异常", e);
        }
    }

    /**
     * 处理单个批次
     */
    private <T, R> BatchResult<R> processSingleBatch(
            List<T> batch,
            int batchIndex,
            Function<List<T>, List<R>> batchProcessor,
            ProgressCallback progressCallback,
            AtomicInteger processedCount,
            int totalSize) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行批处理
            List<R> results = batchProcessor.apply(batch);
            
            // 更新进度
            int currentProcessed = processedCount.addAndGet(batch.size());
            if (progressCallback != null) {
                int progressPercent = totalSize > 0 ? (currentProcessed * 100 / totalSize) : 0;
                progressCallback.onProgress(currentProcessed, totalSize, 
                    String.format("批次 %d 处理完成", batchIndex + 1));
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("批次 {} 处理完成，数据量: {}, 耗时: {}ms, 速度: {}/秒", 
                    batchIndex, batch.size(), duration,
                    duration > 0 ? (batch.size() * 1000 / duration) : 0);
            
            return new BatchResult<>(results != null ? results : new ArrayList<>(), 
                                   batch.size(), results != null ? results.size() : 0, 0);
                                   
        } catch (Exception e) {
            log.error("批次 {} 处理失败，数据量: {}", batchIndex, batch.size(), e);
            return new BatchResult<>(new ArrayList<>(), batch.size(), 0, batch.size());
        }
    }

    /**
     * 创建批次
     */
    private <T> List<List<T>> createBatches(List<T> dataList, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        
        for (int i = 0; i < dataList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, dataList.size());
            batches.add(dataList.subList(i, endIndex));
        }
        
        return batches;
    }

    /**
     * 动态计算最优批量大小
     */
    private int calculateOptimalBatchSize(int totalSize, double systemLoad) {
        int baseBatchSize = DEFAULT_BATCH_SIZE;
        
        // 根据数据量调整
        if (totalSize < 1000) {
            baseBatchSize = Math.max(MIN_BATCH_SIZE, totalSize / 4);
        } else if (totalSize > 10000) {
            baseBatchSize = Math.min(MAX_BATCH_SIZE, totalSize / 20);
        }
        
        // 根据系统负载调整
        if (systemLoad > 0.8) {
            baseBatchSize = (int) (baseBatchSize * 0.7); // 高负载时减小批量
        } else if (systemLoad < 0.3) {
            baseBatchSize = (int) (baseBatchSize * 1.3); // 低负载时增大批量
        }
        
        return Math.max(MIN_BATCH_SIZE, Math.min(MAX_BATCH_SIZE, baseBatchSize));
    }

    /**
     * 获取当前系统负载
     */
    private double getCurrentSystemLoad() {
        try {
            java.lang.management.OperatingSystemMXBean osBean = 
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            
            double cpuLoad = osBean.getProcessCpuLoad();
            if (cpuLoad < 0) {
                cpuLoad = 0.5; // 默认中等负载
            }
            
            // 考虑活跃任务数
            int activeTaskCount = activeTasks.get();
            double taskLoad = Math.min(1.0, activeTaskCount / (double) processingPool.getParallelism());
            
            return Math.max(cpuLoad, taskLoad);
        } catch (Exception e) {
            log.debug("获取系统负载失败，使用默认值", e);
            return 0.5;
        }
    }

    /**
     * 获取处理统计信息
     */
    public ProcessorStats getStats() {
        return new ProcessorStats(
            totalProcessedCount.get(),
            activeTasks.get(),
            processingPool.getPoolSize(),
            processingPool.getActiveThreadCount(),
            processingPool.getRunningThreadCount(),
            processingPool.getQueuedTaskCount()
        );
    }

    /**
     * 关闭处理器
     */
    public void shutdown() {
        processingPool.shutdown();
        try {
            if (!processingPool.awaitTermination(30, TimeUnit.SECONDS)) {
                processingPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            processingPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 进度回调接口
     */
    @FunctionalInterface
    public interface ProgressCallback {
        void onProgress(int processedCount, int totalCount, String currentOperation);
    }

    /**
     * 批处理结果
     */
    public static class BatchProcessResult<R> {
        private final List<R> results;
        private final int successCount;
        private final int failureCount;

        public BatchProcessResult(List<R> results, int successCount, int failureCount) {
            this.results = results;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public List<R> getResults() { return results; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getTotalCount() { return successCount + failureCount; }
    }

    /**
     * 单批次结果
     */
    private static class BatchResult<R> {
        private final List<R> results;
        private final int totalCount;
        private final int successCount;
        private final int failureCount;

        public BatchResult(List<R> results, int totalCount, int successCount, int failureCount) {
            this.results = results;
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public List<R> getResults() { return results; }
        public int getTotalCount() { return totalCount; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
    }

    /**
     * 处理器统计信息
     */
    public static class ProcessorStats {
        private final long totalProcessedCount;
        private final int activeTasks;
        private final int poolSize;
        private final int activeThreadCount;
        private final int runningThreadCount;
        private final long queuedTaskCount;

        public ProcessorStats(long totalProcessedCount, int activeTasks, int poolSize, 
                            int activeThreadCount, int runningThreadCount, long queuedTaskCount) {
            this.totalProcessedCount = totalProcessedCount;
            this.activeTasks = activeTasks;
            this.poolSize = poolSize;
            this.activeThreadCount = activeThreadCount;
            this.runningThreadCount = runningThreadCount;
            this.queuedTaskCount = queuedTaskCount;
        }

        // Getters
        public long getTotalProcessedCount() { return totalProcessedCount; }
        public int getActiveTasks() { return activeTasks; }
        public int getPoolSize() { return poolSize; }
        public int getActiveThreadCount() { return activeThreadCount; }
        public int getRunningThreadCount() { return runningThreadCount; }
        public long getQueuedTaskCount() { return queuedTaskCount; }
    }
}