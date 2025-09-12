package com.admin.module.infra.biz.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 性能监控器
 * 监控并行处理、事务和流式处理的性能指标
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@Slf4j
public class PerformanceMonitor {

    private final ScheduledExecutorService monitorScheduler = Executors.newScheduledThreadPool(2);
    private final ConcurrentHashMap<String, PerformanceMetrics> metricsMap = new ConcurrentHashMap<>();
    
    // 全局统计指标
    private final LongAdder totalProcessedRecords = new LongAdder();
    private final LongAdder totalProcessingTime = new LongAdder();
    private final LongAdder totalParallelBatches = new LongAdder();
    private final LongAdder totalTransactionSegments = new LongAdder();
    private final LongAdder totalStreamingOperations = new LongAdder();
    
    // 性能阈值配置
    private static final double CPU_THRESHOLD = 0.8;
    private static final long MEMORY_THRESHOLD = 1024 * 1024 * 1024; // 1GB
    private static final double THROUGHPUT_THRESHOLD = 1000.0; // 1000条/秒
    
    public PerformanceMonitor() {
        startPerformanceMonitoring();
    }

    /**
     * 开始性能监控
     */
    private void startPerformanceMonitoring() {
        // 每30秒收集系统性能指标
        monitorScheduler.scheduleAtFixedRate(this::collectSystemMetrics, 0, 30, TimeUnit.SECONDS);
        
        // 每5分钟生成性能报告
        monitorScheduler.scheduleAtFixedRate(this::generatePerformanceReport, 0, 5, TimeUnit.MINUTES);
    }

    /**
     * 记录处理开始
     */
    public ProcessingContext startProcessing(String operationType, String taskId) {
        String metricKey = operationType + ":" + taskId;
        PerformanceMetrics metrics = new PerformanceMetrics(operationType, taskId);
        metricsMap.put(metricKey, metrics);
        
        return new ProcessingContext(metricKey, System.currentTimeMillis());
    }

    /**
     * 记录处理完成
     */
    public void endProcessing(ProcessingContext context, int recordCount, boolean success) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - context.getStartTime();
        
        PerformanceMetrics metrics = metricsMap.get(context.getMetricKey());
        if (metrics != null) {
            metrics.recordProcessing(duration, recordCount, success);
            
            // 更新全局统计
            totalProcessedRecords.add(recordCount);
            totalProcessingTime.add(duration);
            
            // 根据操作类型更新相应计数器
            if (context.getMetricKey().startsWith("PARALLEL_BATCH")) {
                totalParallelBatches.increment();
            } else if (context.getMetricKey().startsWith("TRANSACTION")) {
                totalTransactionSegments.increment();
            } else if (context.getMetricKey().startsWith("STREAMING")) {
                totalStreamingOperations.increment();
            }
            
            log.debug("处理完成 - 操作: {}, 耗时: {}ms, 记录数: {}, 成功: {}", 
                     context.getMetricKey(), duration, recordCount, success);
        }
    }

    /**
     * 记录错误
     */
    public void recordError(String operationType, String taskId, String errorMessage) {
        String metricKey = operationType + ":" + taskId;
        PerformanceMetrics metrics = metricsMap.get(metricKey);
        if (metrics != null) {
            metrics.recordError(errorMessage);
        }
        
        log.warn("处理错误 - 操作: {}, 任务: {}, 错误: {}", operationType, taskId, errorMessage);
    }

    /**
     * 收集系统性能指标
     */
    private void collectSystemMetrics() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            double cpuUsage = osBean.getProcessCpuLoad();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            
            SystemMetrics systemMetrics = new SystemMetrics(
                cpuUsage, usedMemory, maxMemory, 
                Runtime.getRuntime().availableProcessors(),
                Thread.activeCount()
            );
            
            // 检查性能阈值
            checkPerformanceThresholds(systemMetrics);
            
            log.debug("系统指标 - CPU: {:.2f}%, 内存使用: {}MB/{}MB, 活跃线程: {}", 
                     cpuUsage * 100, usedMemory / (1024 * 1024), maxMemory / (1024 * 1024),
                     Thread.activeCount());
                     
        } catch (Exception e) {
            log.error("收集系统性能指标失败", e);
        }
    }

    /**
     * 检查性能阈值
     */
    private void checkPerformanceThresholds(SystemMetrics systemMetrics) {
        if (systemMetrics.getCpuUsage() > CPU_THRESHOLD) {
            log.warn("CPU使用率过高: {:.2f}%", systemMetrics.getCpuUsage() * 100);
        }
        
        if (systemMetrics.getUsedMemory() > MEMORY_THRESHOLD) {
            log.warn("内存使用量过高: {}MB", systemMetrics.getUsedMemory() / (1024 * 1024));
        }
        
        // 检查平均吞吐量
        long totalTime = totalProcessingTime.sum();
        long totalRecords = totalProcessedRecords.sum();
        
        if (totalTime > 0) {
            double avgThroughput = totalRecords * 1000.0 / totalTime;
            if (avgThroughput < THROUGHPUT_THRESHOLD) {
                log.warn("平均吞吐量过低: {:.2f} 条/秒", avgThroughput);
            }
        }
    }

    /**
     * 生成性能报告
     */
    private void generatePerformanceReport() {
        try {
            long totalRecords = totalProcessedRecords.sum();
            long totalTime = totalProcessingTime.sum();
            long parallelBatches = totalParallelBatches.sum();
            long transactionSegments = totalTransactionSegments.sum();
            long streamingOps = totalStreamingOperations.sum();
            
            double avgThroughput = totalTime > 0 ? (totalRecords * 1000.0 / totalTime) : 0;
            
            PerformanceReport report = new PerformanceReport(
                totalRecords, totalTime, avgThroughput,
                parallelBatches, transactionSegments, streamingOps,
                metricsMap.size(), LocalDateTime.now()
            );
            
            log.info("性能报告 - 总处理记录: {}, 总耗时: {}ms, 平均吞吐量: {:.2f}/秒, " +
                    "并行批次: {}, 事务段: {}, 流操作: {}, 活跃任务: {}", 
                    totalRecords, totalTime, avgThroughput,
                    parallelBatches, transactionSegments, streamingOps, metricsMap.size());
            
            // 清理过期的指标数据
            cleanupExpiredMetrics();
            
        } catch (Exception e) {
            log.error("生成性能报告失败", e);
        }
    }

    /**
     * 清理过期的指标数据
     */
    private void cleanupExpiredMetrics() {
        long cutoffTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1); // 1小时前
        
        metricsMap.entrySet().removeIf(entry -> {
            PerformanceMetrics metrics = entry.getValue();
            return metrics.getLastUpdateTime() < cutoffTime;
        });
    }

    /**
     * 获取性能统计信息
     */
    public PerformanceStats getPerformanceStats() {
        long totalRecords = totalProcessedRecords.sum();
        long totalTime = totalProcessingTime.sum();
        double avgThroughput = totalTime > 0 ? (totalRecords * 1000.0 / totalTime) : 0;
        
        return new PerformanceStats(
            totalRecords, totalTime, avgThroughput,
            totalParallelBatches.sum(), totalTransactionSegments.sum(), totalStreamingOperations.sum(),
            metricsMap.size()
        );
    }

    /**
     * 关闭监控器
     */
    public void shutdown() {
        monitorScheduler.shutdown();
        try {
            if (!monitorScheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                monitorScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            monitorScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理上下文
     */
    public static class ProcessingContext {
        private final String metricKey;
        private final long startTime;

        public ProcessingContext(String metricKey, long startTime) {
            this.metricKey = metricKey;
            this.startTime = startTime;
        }

        public String getMetricKey() { return metricKey; }
        public long getStartTime() { return startTime; }
    }

    /**
     * 性能指标
     */
    private static class PerformanceMetrics {
        private final String operationType;
        private final String taskId;
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong totalRecords = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);
        private volatile long lastUpdateTime = System.currentTimeMillis();

        public PerformanceMetrics(String operationType, String taskId) {
            this.operationType = operationType;
            this.taskId = taskId;
        }

        public void recordProcessing(long duration, int recordCount, boolean success) {
            totalDuration.addAndGet(duration);
            totalRecords.addAndGet(recordCount);
            if (success) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
            }
            lastUpdateTime = System.currentTimeMillis();
        }

        public void recordError(String errorMessage) {
            errorCount.incrementAndGet();
            lastUpdateTime = System.currentTimeMillis();
        }

        public long getLastUpdateTime() { return lastUpdateTime; }
        public String getOperationType() { return operationType; }
        public String getTaskId() { return taskId; }
        public long getTotalDuration() { return totalDuration.get(); }
        public long getTotalRecords() { return totalRecords.get(); }
        public long getSuccessCount() { return successCount.get(); }
        public long getErrorCount() { return errorCount.get(); }
    }

    /**
     * 系统指标
     */
    private static class SystemMetrics {
        private final double cpuUsage;
        private final long usedMemory;
        private final long maxMemory;
        private final int availableProcessors;
        private final int activeThreads;

        public SystemMetrics(double cpuUsage, long usedMemory, long maxMemory, 
                           int availableProcessors, int activeThreads) {
            this.cpuUsage = cpuUsage;
            this.usedMemory = usedMemory;
            this.maxMemory = maxMemory;
            this.availableProcessors = availableProcessors;
            this.activeThreads = activeThreads;
        }

        public double getCpuUsage() { return cpuUsage; }
        public long getUsedMemory() { return usedMemory; }
        public long getMaxMemory() { return maxMemory; }
        public int getAvailableProcessors() { return availableProcessors; }
        public int getActiveThreads() { return activeThreads; }
    }

    /**
     * 性能报告
     */
    public static class PerformanceReport {
        private final long totalRecords;
        private final long totalTime;
        private final double avgThroughput;
        private final long parallelBatches;
        private final long transactionSegments;
        private final long streamingOperations;
        private final int activeTasks;
        private final LocalDateTime reportTime;

        public PerformanceReport(long totalRecords, long totalTime, double avgThroughput,
                               long parallelBatches, long transactionSegments, long streamingOperations,
                               int activeTasks, LocalDateTime reportTime) {
            this.totalRecords = totalRecords;
            this.totalTime = totalTime;
            this.avgThroughput = avgThroughput;
            this.parallelBatches = parallelBatches;
            this.transactionSegments = transactionSegments;
            this.streamingOperations = streamingOperations;
            this.activeTasks = activeTasks;
            this.reportTime = reportTime;
        }

        // Getters
        public long getTotalRecords() { return totalRecords; }
        public long getTotalTime() { return totalTime; }
        public double getAvgThroughput() { return avgThroughput; }
        public long getParallelBatches() { return parallelBatches; }
        public long getTransactionSegments() { return transactionSegments; }
        public long getStreamingOperations() { return streamingOperations; }
        public int getActiveTasks() { return activeTasks; }
        public LocalDateTime getReportTime() { return reportTime; }
    }

    /**
     * 性能统计信息
     */
    public static class PerformanceStats {
        private final long totalProcessedRecords;
        private final long totalProcessingTime;
        private final double averageThroughput;
        private final long totalParallelBatches;
        private final long totalTransactionSegments;
        private final long totalStreamingOperations;
        private final int activeTaskCount;

        public PerformanceStats(long totalProcessedRecords, long totalProcessingTime, 
                              double averageThroughput, long totalParallelBatches,
                              long totalTransactionSegments, long totalStreamingOperations,
                              int activeTaskCount) {
            this.totalProcessedRecords = totalProcessedRecords;
            this.totalProcessingTime = totalProcessingTime;
            this.averageThroughput = averageThroughput;
            this.totalParallelBatches = totalParallelBatches;
            this.totalTransactionSegments = totalTransactionSegments;
            this.totalStreamingOperations = totalStreamingOperations;
            this.activeTaskCount = activeTaskCount;
        }

        // Getters
        public long getTotalProcessedRecords() { return totalProcessedRecords; }
        public long getTotalProcessingTime() { return totalProcessingTime; }
        public double getAverageThroughput() { return averageThroughput; }
        public long getTotalParallelBatches() { return totalParallelBatches; }
        public long getTotalTransactionSegments() { return totalTransactionSegments; }
        public long getTotalStreamingOperations() { return totalStreamingOperations; }
        public int getActiveTaskCount() { return activeTaskCount; }
    }
}