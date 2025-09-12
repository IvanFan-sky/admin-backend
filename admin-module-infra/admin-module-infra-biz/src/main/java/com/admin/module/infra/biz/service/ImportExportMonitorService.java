package com.admin.module.infra.biz.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导入导出监控服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportMonitorService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // 内存中的性能指标缓存
    private final Map<String, AtomicLong> performanceMetrics = new ConcurrentHashMap<>();
    private final Map<String, Long> taskExecutionTimes = new ConcurrentHashMap<>();

    /**
     * 记录任务开始
     *
     * @param taskId 任务ID
     * @param taskType 任务类型
     */
    public void recordTaskStart(Long taskId, String taskType) {
        String key = "task_start_time:" + taskId;
        long startTime = System.currentTimeMillis();
        
        redisTemplate.opsForValue().set(key, startTime, Duration.ofHours(2));
        taskExecutionTimes.put(taskId.toString(), startTime);
        
        // 增加任务启动计数
        incrementMetric("task_started_" + taskType);
        
        log.info("记录任务开始，任务ID: {}, 任务类型: {}, 开始时间: {}", 
                taskId, taskType, System.currentTimeMillis());
    }

    /**
     * 记录任务完成
     *
     * @param taskId 任务ID
     * @param taskType 任务类型
     * @param success 是否成功
     * @param processedCount 处理数量
     */
    public void recordTaskComplete(Long taskId, String taskType, boolean success, int processedCount) {
        String key = "task_start_time:" + taskId;
        Object startTimeObj = redisTemplate.opsForValue().get(key);
        
        long startTime = startTimeObj instanceof Long ? (Long) startTimeObj : 
                        taskExecutionTimes.getOrDefault(taskId.toString(), System.currentTimeMillis());
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // 记录执行时间
        recordExecutionTime(taskType, executionTime);
        
        // 记录处理数量
        recordProcessedCount(taskType, processedCount);
        
        // 增加完成/失败计数
        if (success) {
            incrementMetric("task_completed_" + taskType);
        } else {
            incrementMetric("task_failed_" + taskType);
        }
        
        // 清理缓存
        redisTemplate.delete(key);
        taskExecutionTimes.remove(taskId.toString());
        
        log.info("记录任务完成，任务ID: {}, 任务类型: {}, 成功: {}, 执行时间: {}ms, 处理数量: {}", 
                taskId, taskType, success, executionTime, processedCount);
    }

    /**
     * 记录错误信息
     *
     * @param taskId 任务ID
     * @param taskType 任务类型
     * @param errorType 错误类型
     * @param errorMessage 错误信息
     */
    public void recordError(Long taskId, String taskType, String errorType, String errorMessage) {
        String key = "task_error:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("taskId", taskId);
        errorInfo.put("taskType", taskType);
        errorInfo.put("errorType", errorType);
        errorInfo.put("errorMessage", errorMessage);
        errorInfo.put("timestamp", System.currentTimeMillis());
        
        redisTemplate.opsForList().leftPush(key, errorInfo);
        redisTemplate.expire(key, Duration.ofDays(7)); // 错误日志保存7天
        
        // 增加错误计数
        incrementMetric("error_" + taskType + "_" + errorType);
        
        log.warn("记录任务错误，任务ID: {}, 任务类型: {}, 错误类型: {}, 错误信息: {}", 
                taskId, taskType, errorType, errorMessage);
    }

    /**
     * 获取性能统计信息
     *
     * @param taskType 任务类型
     * @return 性能统计
     */
    public PerformanceStats getPerformanceStats(String taskType) {
        PerformanceStats stats = new PerformanceStats();
        stats.setTaskType(taskType);
        
        // 获取任务计数
        stats.setTaskStartedCount(getMetric("task_started_" + taskType));
        stats.setTaskCompletedCount(getMetric("task_completed_" + taskType));
        stats.setTaskFailedCount(getMetric("task_failed_" + taskType));
        
        // 计算成功率
        long totalTasks = stats.getTaskCompletedCount() + stats.getTaskFailedCount();
        if (totalTasks > 0) {
            stats.setSuccessRate((double) stats.getTaskCompletedCount() / totalTasks * 100);
        }
        
        // 获取平均执行时间
        stats.setAverageExecutionTime(getAverageExecutionTime(taskType));
        
        // 获取平均处理数量
        stats.setAverageProcessedCount(getAverageProcessedCount(taskType));
        
        return stats;
    }

    /**
     * 获取系统负载信息
     *
     * @return 系统负载信息
     */
    public SystemLoadInfo getSystemLoadInfo() {
        SystemLoadInfo loadInfo = new SystemLoadInfo();
        
        // 获取当前运行任务数
        loadInfo.setCurrentImportTasks(getCurrentTaskCount("import"));
        loadInfo.setCurrentExportTasks(getCurrentTaskCount("export"));
        
        // 获取内存使用情况
        Runtime runtime = Runtime.getRuntime();
        loadInfo.setTotalMemory(runtime.totalMemory());
        loadInfo.setFreeMemory(runtime.freeMemory());
        loadInfo.setUsedMemory(runtime.totalMemory() - runtime.freeMemory());
        loadInfo.setMemoryUsagePercent((double) loadInfo.getUsedMemory() / loadInfo.getTotalMemory() * 100);
        
        // 获取线程池状态（需要注入线程池执行器来获取实际状态）
        loadInfo.setActiveThreads(Thread.activeCount());
        
        return loadInfo;
    }

    /**
     * 清理过期的性能数据
     */
    public void cleanupExpiredData() {
        // 清理过期的错误日志
        String pattern = "task_error:*";
        var keys = redisTemplate.keys(pattern);
        
        if (keys != null) {
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            String expiredDateKey = "task_error:" + sevenDaysAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            for (String key : keys) {
                if (key.compareTo(expiredDateKey) < 0) {
                    redisTemplate.delete(key);
                    log.info("清理过期错误日志: {}", key);
                }
            }
        }
        
        // 重置每日性能指标（如果需要）
        resetDailyMetrics();
        
        log.info("性能数据清理完成");
    }

    private void incrementMetric(String metricName) {
        performanceMetrics.computeIfAbsent(metricName, k -> new AtomicLong(0)).incrementAndGet();
        
        // 同时保存到Redis（用于分布式环境）
        String key = "metric:" + metricName;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    private long getMetric(String metricName) {
        // 优先从内存获取
        AtomicLong memoryValue = performanceMetrics.get(metricName);
        if (memoryValue != null) {
            return memoryValue.get();
        }
        
        // 从Redis获取
        String key = "metric:" + metricName;
        Object value = redisTemplate.opsForValue().get(key);
        return value instanceof Long ? (Long) value : 0L;
    }

    private void recordExecutionTime(String taskType, long executionTime) {
        String key = "execution_time:" + taskType;
        redisTemplate.opsForList().leftPush(key, executionTime);
        
        // 只保留最近100条记录
        redisTemplate.opsForList().trim(key, 0, 99);
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    private void recordProcessedCount(String taskType, int processedCount) {
        String key = "processed_count:" + taskType;
        redisTemplate.opsForList().leftPush(key, processedCount);
        
        // 只保留最近100条记录
        redisTemplate.opsForList().trim(key, 0, 99);
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    private double getAverageExecutionTime(String taskType) {
        String key = "execution_time:" + taskType;
        var times = redisTemplate.opsForList().range(key, 0, -1);
        
        if (times == null || times.isEmpty()) {
            return 0.0;
        }
        
        return times.stream()
                .mapToLong(obj -> obj instanceof Long ? (Long) obj : 0L)
                .average()
                .orElse(0.0);
    }

    private double getAverageProcessedCount(String taskType) {
        String key = "processed_count:" + taskType;
        var counts = redisTemplate.opsForList().range(key, 0, -1);
        
        if (counts == null || counts.isEmpty()) {
            return 0.0;
        }
        
        return counts.stream()
                .mapToInt(obj -> obj instanceof Integer ? (Integer) obj : 0)
                .average()
                .orElse(0.0);
    }

    private long getCurrentTaskCount(String taskType) {
        String key = "system_load:" + taskType;
        Object value = redisTemplate.opsForValue().get(key);
        return value instanceof Long ? (Long) value : 0L;
    }

    private void resetDailyMetrics() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String resetKey = "daily_reset:" + today;
        
        if (redisTemplate.opsForValue().setIfAbsent(resetKey, "1", Duration.ofDays(1))) {
            // 重置每日指标
            performanceMetrics.clear();
            log.info("重置每日性能指标");
        }
    }

    @Data
    public static class PerformanceStats {
        private String taskType;
        private long taskStartedCount;
        private long taskCompletedCount;
        private long taskFailedCount;
        private double successRate;
        private double averageExecutionTime;
        private double averageProcessedCount;
    }

    @Data
    public static class SystemLoadInfo {
        private long currentImportTasks;
        private long currentExportTasks;
        private long totalMemory;
        private long freeMemory;
        private long usedMemory;
        private double memoryUsagePercent;
        private int activeThreads;
    }
}