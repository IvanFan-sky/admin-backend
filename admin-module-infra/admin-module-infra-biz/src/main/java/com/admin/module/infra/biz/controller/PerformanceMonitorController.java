package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.module.infra.biz.monitor.PerformanceMonitor;
import com.admin.module.infra.biz.processor.ParallelBatchProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 性能监控控制器
 * 提供系统性能监控和统计数据接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "性能监控管理")
@RestController
@RequestMapping("/admin-api/infra/performance")
@RequiredArgsConstructor
public class PerformanceMonitorController {

    private final PerformanceMonitor performanceMonitor;
    private final ParallelBatchProcessor parallelBatchProcessor;

    @GetMapping("/stats")
    @Operation(summary = "获取性能统计信息")
    @PreAuthorize("@ss.hasPermission('infra:performance:query')")
    public R<PerformanceStatsVO> getPerformanceStats() {
        PerformanceMonitor.PerformanceStats stats = performanceMonitor.getPerformanceStats();
        
        PerformanceStatsVO statsVO = new PerformanceStatsVO();
        statsVO.setTotalProcessedRecords(stats.getTotalProcessedRecords());
        statsVO.setTotalProcessingTime(stats.getTotalProcessingTime());
        statsVO.setAverageThroughput(stats.getAverageThroughput());
        statsVO.setTotalParallelBatches(stats.getTotalParallelBatches());
        statsVO.setTotalTransactionSegments(stats.getTotalTransactionSegments());
        statsVO.setTotalStreamingOperations(stats.getTotalStreamingOperations());
        statsVO.setActiveTaskCount(stats.getActiveTaskCount());
        
        return R.ok(statsVO);
    }

    @GetMapping("/processor-stats")
    @Operation(summary = "获取并行处理器统计信息")
    @PreAuthorize("@ss.hasPermission('infra:performance:query')")
    public R<ProcessorStatsVO> getProcessorStats() {
        ParallelBatchProcessor.ProcessorStats stats = parallelBatchProcessor.getStats();
        
        ProcessorStatsVO statsVO = new ProcessorStatsVO();
        statsVO.setTotalProcessedCount(stats.getTotalProcessedCount());
        statsVO.setActiveTasks(stats.getActiveTasks());
        statsVO.setPoolSize(stats.getPoolSize());
        statsVO.setActiveThreadCount(stats.getActiveThreadCount());
        statsVO.setRunningThreadCount(stats.getRunningThreadCount());
        statsVO.setQueuedTaskCount(stats.getQueuedTaskCount());
        
        return R.ok(statsVO);
    }

    @GetMapping("/system-info")
    @Operation(summary = "获取系统信息")
    @PreAuthorize("@ss.hasPermission('infra:performance:query')")
    public R<SystemInfoVO> getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        java.lang.management.OperatingSystemMXBean osBean = 
            java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        java.lang.management.MemoryMXBean memoryBean = 
            java.lang.management.ManagementFactory.getMemoryMXBean();
        
        SystemInfoVO systemInfo = new SystemInfoVO();
        
        // CPU信息
        systemInfo.setAvailableProcessors(osBean.getAvailableProcessors());
        systemInfo.setCpuLoad(osBean.getProcessCpuLoad());
        
        // 内存信息
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        systemInfo.setMaxMemory(maxMemory);
        systemInfo.setTotalMemory(totalMemory);
        systemInfo.setUsedMemory(usedMemory);
        systemInfo.setFreeMemory(freeMemory);
        systemInfo.setMemoryUsagePercent(maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0);
        
        // 堆内存信息
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        systemInfo.setHeapMemoryUsed(heapUsed);
        systemInfo.setHeapMemoryMax(heapMax);
        systemInfo.setHeapUsagePercent(heapMax > 0 ? (double) heapUsed / heapMax * 100 : 0);
        
        // 线程信息
        systemInfo.setActiveThreadCount(Thread.activeCount());
        systemInfo.setDaemonThreadCount(Thread.getAllStackTraces().size());
        
        return R.ok(systemInfo);
    }

    /**
     * 性能统计VO
     */
    public static class PerformanceStatsVO {
        private long totalProcessedRecords;
        private long totalProcessingTime;
        private double averageThroughput;
        private long totalParallelBatches;
        private long totalTransactionSegments;
        private long totalStreamingOperations;
        private int activeTaskCount;

        // Getters and Setters
        public long getTotalProcessedRecords() { return totalProcessedRecords; }
        public void setTotalProcessedRecords(long totalProcessedRecords) { this.totalProcessedRecords = totalProcessedRecords; }
        
        public long getTotalProcessingTime() { return totalProcessingTime; }
        public void setTotalProcessingTime(long totalProcessingTime) { this.totalProcessingTime = totalProcessingTime; }
        
        public double getAverageThroughput() { return averageThroughput; }
        public void setAverageThroughput(double averageThroughput) { this.averageThroughput = averageThroughput; }
        
        public long getTotalParallelBatches() { return totalParallelBatches; }
        public void setTotalParallelBatches(long totalParallelBatches) { this.totalParallelBatches = totalParallelBatches; }
        
        public long getTotalTransactionSegments() { return totalTransactionSegments; }
        public void setTotalTransactionSegments(long totalTransactionSegments) { this.totalTransactionSegments = totalTransactionSegments; }
        
        public long getTotalStreamingOperations() { return totalStreamingOperations; }
        public void setTotalStreamingOperations(long totalStreamingOperations) { this.totalStreamingOperations = totalStreamingOperations; }
        
        public int getActiveTaskCount() { return activeTaskCount; }
        public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }
    }

    /**
     * 处理器统计VO
     */
    public static class ProcessorStatsVO {
        private long totalProcessedCount;
        private int activeTasks;
        private int poolSize;
        private int activeThreadCount;
        private int runningThreadCount;
        private long queuedTaskCount;

        // Getters and Setters
        public long getTotalProcessedCount() { return totalProcessedCount; }
        public void setTotalProcessedCount(long totalProcessedCount) { this.totalProcessedCount = totalProcessedCount; }
        
        public int getActiveTasks() { return activeTasks; }
        public void setActiveTasks(int activeTasks) { this.activeTasks = activeTasks; }
        
        public int getPoolSize() { return poolSize; }
        public void setPoolSize(int poolSize) { this.poolSize = poolSize; }
        
        public int getActiveThreadCount() { return activeThreadCount; }
        public void setActiveThreadCount(int activeThreadCount) { this.activeThreadCount = activeThreadCount; }
        
        public int getRunningThreadCount() { return runningThreadCount; }
        public void setRunningThreadCount(int runningThreadCount) { this.runningThreadCount = runningThreadCount; }
        
        public long getQueuedTaskCount() { return queuedTaskCount; }
        public void setQueuedTaskCount(long queuedTaskCount) { this.queuedTaskCount = queuedTaskCount; }
    }

    /**
     * 系统信息VO
     */
    public static class SystemInfoVO {
        private int availableProcessors;
        private double cpuLoad;
        private long maxMemory;
        private long totalMemory;
        private long usedMemory;
        private long freeMemory;
        private double memoryUsagePercent;
        private long heapMemoryUsed;
        private long heapMemoryMax;
        private double heapUsagePercent;
        private int activeThreadCount;
        private int daemonThreadCount;

        // Getters and Setters
        public int getAvailableProcessors() { return availableProcessors; }
        public void setAvailableProcessors(int availableProcessors) { this.availableProcessors = availableProcessors; }
        
        public double getCpuLoad() { return cpuLoad; }
        public void setCpuLoad(double cpuLoad) { this.cpuLoad = cpuLoad; }
        
        public long getMaxMemory() { return maxMemory; }
        public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }
        
        public long getTotalMemory() { return totalMemory; }
        public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
        
        public long getUsedMemory() { return usedMemory; }
        public void setUsedMemory(long usedMemory) { this.usedMemory = usedMemory; }
        
        public long getFreeMemory() { return freeMemory; }
        public void setFreeMemory(long freeMemory) { this.freeMemory = freeMemory; }
        
        public double getMemoryUsagePercent() { return memoryUsagePercent; }
        public void setMemoryUsagePercent(double memoryUsagePercent) { this.memoryUsagePercent = memoryUsagePercent; }
        
        public long getHeapMemoryUsed() { return heapMemoryUsed; }
        public void setHeapMemoryUsed(long heapMemoryUsed) { this.heapMemoryUsed = heapMemoryUsed; }
        
        public long getHeapMemoryMax() { return heapMemoryMax; }
        public void setHeapMemoryMax(long heapMemoryMax) { this.heapMemoryMax = heapMemoryMax; }
        
        public double getHeapUsagePercent() { return heapUsagePercent; }
        public void setHeapUsagePercent(double heapUsagePercent) { this.heapUsagePercent = heapUsagePercent; }
        
        public int getActiveThreadCount() { return activeThreadCount; }
        public void setActiveThreadCount(int activeThreadCount) { this.activeThreadCount = activeThreadCount; }
        
        public int getDaemonThreadCount() { return daemonThreadCount; }
        public void setDaemonThreadCount(int daemonThreadCount) { this.daemonThreadCount = daemonThreadCount; }
    }
}