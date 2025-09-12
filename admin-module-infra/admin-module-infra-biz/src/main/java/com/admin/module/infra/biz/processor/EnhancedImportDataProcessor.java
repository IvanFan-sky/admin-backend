package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.biz.stream.StreamingDataProcessor;
import com.admin.module.infra.biz.transaction.SegmentedTransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 增强的导入数据处理器
 * 集成并行处理、分段事务和流式处理能力
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public abstract class EnhancedImportDataProcessor implements ImportDataProcessor {

    private final ParallelBatchProcessor parallelBatchProcessor;
    private final SegmentedTransactionManager segmentedTransactionManager;
    private final StreamingDataProcessor streamingDataProcessor;

    /**
     * 增强的导入处理实现
     */
    @Override
    public ImportProcessResult processImport(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("开始增强导入处理，任务ID: {}, 数据类型: {}", taskId, getSupportedDataType());

        try {
            // 选择处理策略
            ImportStrategy strategy = selectImportStrategy(inputStream);
            
            switch (strategy) {
                case PARALLEL_BATCH:
                    return processWithParallelBatch(taskId, inputStream, progressCallback);
                case STREAMING:
                    return processWithStreaming(taskId, inputStream, progressCallback);
                case HYBRID:
                    return processWithHybrid(taskId, inputStream, progressCallback);
                default:
                    return processWithDefault(taskId, inputStream, progressCallback);
            }
            
        } catch (Exception e) {
            log.error("增强导入处理失败，任务ID: {}", taskId, e);
            throw new RuntimeException("增强导入处理失败", e);
        }
    }

    /**
     * 并行批处理策略
     */
    private ImportProcessResult processWithParallelBatch(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("使用并行批处理策略，任务ID: {}", taskId);

        // 1. 解析数据
        List<ParsedData> dataList = parseInputData(inputStream);
        
        if (dataList.isEmpty()) {
            return new ImportProcessResult(0, 0, 0, new ArrayList<>());
        }

        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<ImportErrorDetailVO> allErrors = new CopyOnWriteArrayList<>();

        // 2. 并行批处理
        ParallelBatchProcessor.BatchProcessResult<ProcessedData> batchResult = parallelBatchProcessor.processBatches(
            dataList,
            batch -> processBatchWithTransaction(taskId, batch, allErrors),
            (processed, total, operation) -> {
                processedCount.set(processed);
                if (progressCallback != null) {
                    progressCallback.onProgress(processed, total, operation);
                }
            }
        );

        return new ImportProcessResult(
            dataList.size(),
            batchResult.getSuccessCount(),
            batchResult.getFailureCount(),
            allErrors
        );
    }

    /**
     * 流式处理策略
     */
    private ImportProcessResult processWithStreaming(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("使用流式处理策略，任务ID: {}", taskId);

        List<ImportErrorDetailVO> allErrors = new CopyOnWriteArrayList<>();
        AtomicInteger errorCount = new AtomicInteger(0);

        // 流式处理
        StreamingDataProcessor.StreamProcessResult<ProcessedData> streamResult = streamingDataProcessor.processStream(
            inputStream,
            data -> processDataWithErrorHandling(taskId, data, allErrors, errorCount),
            this::parseLineToData,
            progress -> {
                if (progressCallback != null) {
                    progressCallback.onProgress((int) progress.getProcessedCount(), -1, 
                        String.format("流式处理，吞吐量: %.2f/秒", progress.getThroughputPerSecond()));
                }
            },
            8192
        );

        return new ImportProcessResult(
            (int) streamResult.getTotalCount(),
            (int) streamResult.getSuccessCount(),
            (int) streamResult.getErrorCount(),
            allErrors
        );
    }

    /**
     * 混合处理策略（大文件用流式，小文件用并行批）
     */
    private ImportProcessResult processWithHybrid(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("使用混合处理策略，任务ID: {}", taskId);

        // 先进行流式预处理，分批收集
        List<ParsedData> collectedBatches = new ArrayList<>();
        List<ImportErrorDetailVO> allErrors = new CopyOnWriteArrayList<>();
        
        // 创建背压控制的处理器
        StreamingDataProcessor.BackpressureStreamProcessor<ParsedData, ProcessedData> backpressureProcessor = 
            streamingDataProcessor.createBackpressureProcessor(
                data -> processDataWithErrorHandling(taskId, data, allErrors, new AtomicInteger()),
                progress -> {
                    if (progressCallback != null) {
                        progressCallback.onProgress((int) progress.getProcessedCount(), -1, 
                            "混合处理 - 背压控制");
                    }
                }
            );

        // 启动异步处理
        CompletableFuture<Void> processFuture = backpressureProcessor.startAsync();

        try {
            // 流式读取并批量提交
            streamingDataProcessor.processBatchStream(
                inputStream,
                this::parseLineToData,
                batch -> {
                    batch.forEach(backpressureProcessor::offer);
                    return batch.map(data -> new ProcessedData()); // 占位符
                },
                500 // 批量大小
            ).forEach(result -> {
                // 处理结果收集
            });

            // 停止处理器
            backpressureProcessor.stop();
            processFuture.get();

        } catch (Exception e) {
            log.error("混合处理策略执行失败", e);
            throw new RuntimeException("混合处理失败", e);
        }

        // 返回结果（这里需要根据实际业务逻辑调整）
        return new ImportProcessResult(0, 0, 0, allErrors);
    }

    /**
     * 默认处理策略
     */
    private ImportProcessResult processWithDefault(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("使用默认处理策略，任务ID: {}", taskId);
        
        // 这里可以调用原有的处理逻辑
        return processOriginalImport(taskId, inputStream, progressCallback);
    }

    /**
     * 带事务的批处理
     */
    private List<ProcessedData> processBatchWithTransaction(Long taskId, List<ParsedData> batch, List<ImportErrorDetailVO> allErrors) {
        List<ProcessedData> results = new ArrayList<>();

        try {
            // 使用分段事务处理
            SegmentedTransactionManager.SegmentedTransactionResult<ProcessedData> txResult = 
                segmentedTransactionManager.executeSegmentedTransaction(
                    batch,
                    this::processBatchData,
                    this::compensateBatchData,
                    50 // 分段大小
                );

            results.addAll(txResult.getResults());

            // 处理失败记录
            for (SegmentedTransactionManager.TransactionFailure<?> failure : txResult.getFailures()) {
                ImportErrorDetailVO error = new ImportErrorDetailVO();
                error.setRowIndex(failure.getSegmentIndex());
                error.setErrorMessage(failure.getErrorMessage());
                error.setCreateTime(java.time.LocalDateTime.now());
                allErrors.add(error);
            }

        } catch (Exception e) {
            log.error("批事务处理失败", e);
            
            // 记录整批失败
            ImportErrorDetailVO error = new ImportErrorDetailVO();
            error.setErrorMessage("批事务处理失败: " + e.getMessage());
            error.setCreateTime(java.time.LocalDateTime.now());
            allErrors.add(error);
        }

        return results;
    }

    /**
     * 带错误处理的数据处理
     */
    private ProcessedData processDataWithErrorHandling(Long taskId, ParsedData data, 
                                                      List<ImportErrorDetailVO> allErrors, 
                                                      AtomicInteger errorCount) {
        try {
            return processSingleData(data);
        } catch (Exception e) {
            errorCount.incrementAndGet();
            
            ImportErrorDetailVO error = new ImportErrorDetailVO();
            error.setErrorMessage(e.getMessage());
            error.setCreateTime(java.time.LocalDateTime.now());
            allErrors.add(error);
            
            return null;
        }
    }

    /**
     * 选择导入策略
     */
    private ImportStrategy selectImportStrategy(InputStream inputStream) {
        try {
            // 标记输入流以便重置
            inputStream.mark(1024 * 1024); // 1MB 标记缓冲区
            
            // 估算数据大小和复杂度
            int sampleSize = 0;
            int lineCount = 0;
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1 && sampleSize < 1024 * 1024) {
                sampleSize += bytesRead;
                for (byte b : buffer) {
                    if (b == '\n') {
                        lineCount++;
                    }
                }
            }
            
            // 重置流
            inputStream.reset();
            
            // 根据估算选择策略
            if (sampleSize > 10 * 1024 * 1024) { // 大于10MB
                return ImportStrategy.STREAMING;
            } else if (lineCount > 10000) {
                return ImportStrategy.PARALLEL_BATCH;
            } else if (sampleSize > 5 * 1024 * 1024) {
                return ImportStrategy.HYBRID;
            } else {
                return ImportStrategy.DEFAULT;
            }
            
        } catch (Exception e) {
            log.debug("策略选择异常，使用默认策略", e);
            return ImportStrategy.DEFAULT;
        }
    }

    // 抽象方法，由具体实现类提供
    protected abstract List<ParsedData> parseInputData(InputStream inputStream);
    protected abstract ParsedData parseLineToData(String line);
    protected abstract List<ProcessedData> processBatchData(List<ParsedData> batch);
    protected abstract void compensateBatchData(List<ParsedData> batch);
    protected abstract ProcessedData processSingleData(ParsedData data);
    protected abstract ImportProcessResult processOriginalImport(Long taskId, InputStream inputStream, ProgressCallback progressCallback);

    /**
     * 导入策略枚举
     */
    private enum ImportStrategy {
        PARALLEL_BATCH,    // 并行批处理
        STREAMING,         // 流式处理
        HYBRID,           // 混合处理
        DEFAULT           // 默认处理
    }

    /**
     * 解析后的数据
     */
    protected static class ParsedData {
        private Object data;
        private int rowIndex;
        
        // Getters and Setters
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public int getRowIndex() { return rowIndex; }
        public void setRowIndex(int rowIndex) { this.rowIndex = rowIndex; }
    }

    /**
     * 处理后的数据
     */
    protected static class ProcessedData {
        private Object result;
        private boolean success;
        
        // Getters and Setters
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }
}