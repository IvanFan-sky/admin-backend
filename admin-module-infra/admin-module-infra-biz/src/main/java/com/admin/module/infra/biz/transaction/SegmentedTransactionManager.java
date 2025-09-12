package com.admin.module.infra.biz.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 分段事务管理器
 * 解决长事务问题，支持分段提交和异步补偿机制
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SegmentedTransactionManager {

    private final PlatformTransactionManager transactionManager;
    
    private static final int DEFAULT_SEGMENT_SIZE = 100;
    private static final int MAX_RETRY_TIMES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    /**
     * 分段事务处理
     *
     * @param dataList 待处理数据
     * @param processor 处理函数
     * @param compensator 补偿函数（可选）
     * @param segmentSize 分段大小
     * @param <T> 数据类型
     * @param <R> 结果类型
     * @return 分段事务结果
     */
    public <T, R> SegmentedTransactionResult<R> executeSegmentedTransaction(
            List<T> dataList,
            Function<List<T>, List<R>> processor,
            Consumer<List<T>> compensator,
            int segmentSize) {
        
        if (dataList == null || dataList.isEmpty()) {
            return new SegmentedTransactionResult<>(new ArrayList<>(), 0, 0, new ArrayList<>());
        }

        segmentSize = segmentSize <= 0 ? DEFAULT_SEGMENT_SIZE : segmentSize;
        
        log.info("开始分段事务处理，总数据量: {}, 分段大小: {}, 预计段数: {}", 
                dataList.size(), segmentSize, (dataList.size() + segmentSize - 1) / segmentSize);

        List<List<T>> segments = createSegments(dataList, segmentSize);
        List<R> allResults = new CopyOnWriteArrayList<>();
        List<TransactionFailure<T>> failures = new CopyOnWriteArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 串行处理每个分段（避免分布式事务复杂性）
        for (int i = 0; i < segments.size(); i++) {
            List<T> segment = segments.get(i);
            
            try {
                SegmentTransactionResult<R> segmentResult = executeSegmentTransaction(
                    segment, processor, i + 1, segments.size()
                );
                
                allResults.addAll(segmentResult.getResults());
                successCount.addAndGet(segmentResult.getSuccessCount());
                
                log.debug("分段 {}/{} 事务提交成功，处理数量: {}", 
                         i + 1, segments.size(), segmentResult.getSuccessCount());
                         
            } catch (Exception e) {
                log.error("分段 {}/{} 事务失败，数据量: {}", i + 1, segments.size(), segment.size(), e);
                
                // 记录失败信息
                failures.add(new TransactionFailure<>(segment, i + 1, e.getMessage()));
                failureCount.addAndGet(segment.size());
                
                // 执行补偿逻辑
                if (compensator != null) {
                    executeCompensation(segment, compensator, i + 1);
                }
            }
        }

        // 异步执行失败重试（可选）
        if (!failures.isEmpty()) {
            executeAsyncRetry(failures, processor, compensator, allResults, successCount, failureCount);
        }

        log.info("分段事务处理完成，成功: {}, 失败: {}, 失败段数: {}", 
                successCount.get(), failureCount.get(), failures.size());

        return new SegmentedTransactionResult<>(allResults, successCount.get(), failureCount.get(), failures);
    }

    /**
     * 执行单个分段事务
     */
    private <T, R> SegmentTransactionResult<R> executeSegmentTransaction(
            List<T> segment,
            Function<List<T>, List<R>> processor,
            int segmentIndex,
            int totalSegments) {
        
        // 创建新事务定义
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionDefinition.setTimeout(30); // 30秒超时
        
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 执行业务处理
            List<R> results = processor.apply(segment);
            
            // 提交事务
            transactionManager.commit(transactionStatus);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("分段事务 {}/{} 执行成功，耗时: {}ms, 处理数量: {}", 
                     segmentIndex, totalSegments, duration, segment.size());
            
            return new SegmentTransactionResult<>(
                results != null ? results : new ArrayList<>(),
                segment.size(),
                0
            );
            
        } catch (Exception e) {
            // 回滚事务
            transactionManager.rollback(transactionStatus);
            
            log.error("分段事务 {}/{} 执行失败，回滚成功", segmentIndex, totalSegments, e);
            
            throw new RuntimeException(String.format("分段事务 %d 执行失败", segmentIndex), e);
        }
    }

    /**
     * 执行补偿逻辑
     */
    private <T> void executeCompensation(List<T> segment, Consumer<T> compensator, int segmentIndex) {
        try {
            log.info("开始执行分段 {} 的补偿逻辑，数据量: {}", segmentIndex, segment.size());
            
            DefaultTransactionDefinition compensationTransactionDef = new DefaultTransactionDefinition();
            compensationTransactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            
            TransactionStatus compensationStatus = transactionManager.getTransaction(compensationTransactionDef);
            
            try {
                compensator.accept(segment);
                transactionManager.commit(compensationStatus);
                
                log.info("分段 {} 补偿逻辑执行成功", segmentIndex);
                
            } catch (Exception e) {
                transactionManager.rollback(compensationStatus);
                log.error("分段 {} 补偿逻辑执行失败", segmentIndex, e);
            }
            
        } catch (Exception e) {
            log.error("分段 {} 补偿逻辑异常", segmentIndex, e);
        }
    }

    /**
     * 异步执行失败重试
     */
    private <T, R> void executeAsyncRetry(
            List<TransactionFailure<T>> failures,
            Function<List<T>, List<R>> processor,
            Consumer<List<T>> compensator,
            List<R> allResults,
            AtomicInteger successCount,
            AtomicInteger failureCount) {
        
        CompletableFuture.runAsync(() -> {
            log.info("开始异步重试失败的分段，失败数量: {}", failures.size());
            
            for (TransactionFailure<T> failure : failures) {
                for (int retry = 1; retry <= MAX_RETRY_TIMES; retry++) {
                    try {
                        // 延迟重试
                        Thread.sleep(RETRY_DELAY_MS * retry);
                        
                        SegmentTransactionResult<R> retryResult = executeSegmentTransaction(
                            failure.getSegmentData(), processor, failure.getSegmentIndex(), failures.size()
                        );
                        
                        allResults.addAll(retryResult.getResults());
                        successCount.addAndGet(retryResult.getSuccessCount());
                        failureCount.addAndGet(-failure.getSegmentData().size()); // 减去之前的失败计数
                        
                        log.info("分段 {} 重试第 {} 次成功", failure.getSegmentIndex(), retry);
                        break;
                        
                    } catch (Exception e) {
                        log.warn("分段 {} 重试第 {} 次失败", failure.getSegmentIndex(), retry, e);
                        
                        if (retry == MAX_RETRY_TIMES) {
                            log.error("分段 {} 重试 {} 次后最终失败", failure.getSegmentIndex(), MAX_RETRY_TIMES);
                            
                            // 最终失败后执行补偿
                            if (compensator != null) {
                                executeCompensation(failure.getSegmentData(), compensator, failure.getSegmentIndex());
                            }
                        }
                    }
                }
            }
            
            log.info("异步重试完成");
        });
    }

    /**
     * 乐观锁事务处理
     */
    public <T, R> R executeWithOptimisticLock(
            T data,
            Function<T, R> processor,
            int maxRetries) {
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
            
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            
            try {
                R result = processor.apply(data);
                transactionManager.commit(transactionStatus);
                return result;
                
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                transactionManager.rollback(transactionStatus);
                
                if (attempt < maxRetries) {
                    log.warn("乐观锁冲突，正在重试 ({}/{})", attempt, maxRetries);
                    
                    try {
                        // 指数退避
                        Thread.sleep((long) Math.pow(2, attempt) * 100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("乐观锁重试被中断", ie);
                    }
                } else {
                    throw new RuntimeException("乐观锁重试次数已达上限", e);
                }
                
            } catch (Exception e) {
                transactionManager.rollback(transactionStatus);
                throw new RuntimeException("事务执行失败", e);
            }
        }
        
        throw new RuntimeException("乐观锁处理异常");
    }

    /**
     * 创建数据分段
     */
    private <T> List<List<T>> createSegments(List<T> dataList, int segmentSize) {
        List<List<T>> segments = new ArrayList<>();
        
        for (int i = 0; i < dataList.size(); i += segmentSize) {
            int endIndex = Math.min(i + segmentSize, dataList.size());
            segments.add(new ArrayList<>(dataList.subList(i, endIndex)));
        }
        
        return segments;
    }

    /**
     * 分段事务结果
     */
    public static class SegmentedTransactionResult<R> {
        private final List<R> results;
        private final int successCount;
        private final int failureCount;
        private final List<TransactionFailure<?>> failures;

        public SegmentedTransactionResult(List<R> results, int successCount, int failureCount, 
                                        List<TransactionFailure<?>> failures) {
            this.results = results;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.failures = failures;
        }

        public List<R> getResults() { return results; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getTotalCount() { return successCount + failureCount; }
        public List<TransactionFailure<?>> getFailures() { return failures; }
        public boolean hasFailures() { return !failures.isEmpty(); }
    }

    /**
     * 单分段事务结果
     */
    private static class SegmentTransactionResult<R> {
        private final List<R> results;
        private final int successCount;
        private final int failureCount;

        public SegmentTransactionResult(List<R> results, int successCount, int failureCount) {
            this.results = results;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public List<R> getResults() { return results; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
    }

    /**
     * 事务失败信息
     */
    public static class TransactionFailure<T> {
        private final List<T> segmentData;
        private final int segmentIndex;
        private final String errorMessage;
        private final long timestamp;

        public TransactionFailure(List<T> segmentData, int segmentIndex, String errorMessage) {
            this.segmentData = segmentData;
            this.segmentIndex = segmentIndex;
            this.errorMessage = errorMessage;
            this.timestamp = System.currentTimeMillis();
        }

        public List<T> getSegmentData() { return segmentData; }
        public int getSegmentIndex() { return segmentIndex; }
        public String getErrorMessage() { return errorMessage; }
        public long getTimestamp() { return timestamp; }
    }
}