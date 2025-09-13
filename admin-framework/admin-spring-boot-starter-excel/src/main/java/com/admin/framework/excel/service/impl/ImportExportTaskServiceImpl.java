package com.admin.framework.excel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.framework.excel.dal.mapper.ImportExportTaskMapper;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.framework.excel.domain.ImportExportTask.TaskStatus;
import com.admin.framework.excel.domain.ImportExportTask.TaskType;
import com.admin.framework.excel.service.ImportExportTaskService;
import com.admin.framework.security.utils.SecurityContextHolder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 导入导出任务服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class
ImportExportTaskServiceImpl implements ImportExportTaskService {

    private final ImportExportTaskMapper taskMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor taskExecutor;
    
    // 并发控制：每个用户最多同时执行的任务数
    private static final int MAX_CONCURRENT_TASKS_PER_USER = 2;
    // 系统总体并发限制
    private static final Semaphore SYSTEM_TASK_SEMAPHORE = new Semaphore(10);
    
    // Redis Key前缀
    private static final String TASK_LOCK_PREFIX = "import_export:task_lock:";
    private static final String USER_TASK_COUNT_PREFIX = "import_export:user_count:";

    @Override
    @Transactional
    public Long createTask(String taskName, TaskType taskType, String businessType, String fileName) {
        String currentUser = SecurityContextHolder.getCurrentUsernameOrDefault("system");
        
        ImportExportTask task = new ImportExportTask();
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setBusinessType(businessType);
        task.setFileName(fileName);
        task.setStatus(TaskStatus.PENDING);
        task.setCreateBy(currentUser);
        
        taskMapper.insert(task);
        
        log.info("创建{}任务成功，任务ID: {}, 任务名称: {}", taskType.getDescription(), task.getId(), taskName);
        return task.getId();
    }

    @Override
    public ImportExportTask getTask(Long taskId) {
        ImportExportTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        return task;
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus status) {
        ImportExportTask task = new ImportExportTask();
        task.setId(taskId);
        task.setStatus(status);
        
        if (status == TaskStatus.PROCESSING) {
            task.setStartTime(LocalDateTime.now());
        } else if (status == TaskStatus.SUCCESS || status == TaskStatus.FAILED) {
            task.setEndTime(LocalDateTime.now());
        }
        
        taskMapper.updateById(task);
        
        // 更新Redis缓存中的任务状态
        String cacheKey = "task:status:" + taskId;
        redisTemplate.opsForValue().set(cacheKey, status.name(), 30, TimeUnit.MINUTES);
        
        log.debug("更新任务{}状态为: {}", taskId, status);
    }

    @Override
    @Transactional
    public void updateTaskProgress(Long taskId, int processed, int total) {
        int progress = total > 0 ? (int) ((processed * 100.0) / total) : 0;
        
        ImportExportTask task = new ImportExportTask();
        task.setId(taskId);
        task.setProgress(progress);
        
        taskMapper.updateById(task);
        
        // 更新Redis缓存中的进度
        String progressKey = "task:progress:" + taskId;
        redisTemplate.opsForHash().putAll(progressKey, 
            java.util.Map.of("processed", processed, "total", total, "progress", progress));
        redisTemplate.expire(progressKey, 30, TimeUnit.MINUTES);
        
        log.debug("更新任务{}进度: {}/{} ({}%)", taskId, processed, total, progress);
    }

    @Override
    @Transactional
    public void updateTaskStatistics(Long taskId, int totalCount, int successCount, int failCount) {
        ImportExportTask task = new ImportExportTask();
        task.setId(taskId);
        task.setTotalCount(totalCount);
        task.setSuccessCount(successCount);
        task.setFailCount(failCount);
        
        taskMapper.updateById(task);
        
        log.debug("更新任务{}统计信息: 总数{}, 成功{}, 失败{}", taskId, totalCount, successCount, failCount);
    }

    @Override
    @Transactional
    public void completeTask(Long taskId, boolean success, String errorMessage) {
        ImportExportTask task = new ImportExportTask();
        task.setId(taskId);
        task.setStatus(success ? TaskStatus.SUCCESS : TaskStatus.FAILED);
        task.setEndTime(LocalDateTime.now());
        task.setProgress(success ? 100 : task.getProgress());
        
        if (!success && StrUtil.isNotBlank(errorMessage)) {
            task.setErrorMessage(errorMessage);
        }
        
        taskMapper.updateById(task);
        
        // 清理Redis缓存
        String progressKey = "task:progress:" + taskId;
        String statusKey = "task:status:" + taskId;
        redisTemplate.delete(progressKey);
        redisTemplate.delete(statusKey);
        
        // 释放用户任务计数
        String userTaskCountKey = USER_TASK_COUNT_PREFIX + task.getCreateBy() + ":" + task.getTaskType();
        redisTemplate.opsForValue().decrement(userTaskCountKey);
        
        log.info("任务{}完成，状态: {}, 错误信息: {}", taskId, task.getStatus(), errorMessage);
    }

    @Override
    @Transactional
    public void setTaskFilePath(Long taskId, String filePath) {
        ImportExportTask task = new ImportExportTask();
        task.setId(taskId);
        task.setFilePath(filePath);
        
        taskMapper.updateById(task);
        
        log.debug("设置任务{}文件路径: {}", taskId, filePath);
    }

    @Override
    public PageResult<ImportExportTask> getTaskPage(int pageNum, int pageSize, TaskType taskType, 
                                                  String businessType, TaskStatus status) {
        Long currentUserId = SecurityContextHolder.getCurrentUserId();
        if (currentUserId == null) {
            return PageResult.empty();
        }
        
        Page<ImportExportTask> page = new Page<>(pageNum, pageSize);
        List<ImportExportTask> records = taskMapper.selectTasksByUser(currentUserId, taskType, businessType, status);
        
        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, records.size());
        List<ImportExportTask> pageRecords = records.subList(start, end);
        
        return new PageResult<>(pageRecords, (long) records.size());
    }

    @Override
    public List<ImportExportTask> getUserProcessingTasks(Long userId, TaskType taskType) {
        return taskMapper.selectProcessingTasksByUser(userId, taskType);
    }

    @Override
    public boolean canCreateTask(Long userId, TaskType taskType) {
        if (userId == null) {
            return false;
        }
        
        // 检查用户并发限制
        String userTaskCountKey = USER_TASK_COUNT_PREFIX + userId + ":" + taskType;
        Long currentCount = (Long) redisTemplate.opsForValue().get(userTaskCountKey);
        if (currentCount != null && currentCount >= MAX_CONCURRENT_TASKS_PER_USER) {
            log.warn("用户{}的{}任务已达到并发限制: {}", userId, taskType, currentCount);
            return false;
        }
        
        // 检查系统总体并发限制
        if (!SYSTEM_TASK_SEMAPHORE.tryAcquire()) {
            log.warn("系统任务并发已达到限制");
            return false;
        }
        
        // 预占用户任务计数
        redisTemplate.opsForValue().increment(userTaskCountKey, 1);
        redisTemplate.expire(userTaskCountKey, 24, TimeUnit.HOURS);
        
        return true;
    }

    @Override
    @Transactional
    public int cleanExpiredTasks(int days) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
        int deletedCount = taskMapper.deleteExpiredTasks(beforeDate);
        
        log.info("清理{}天前的过期任务，共清理{}个任务", days, deletedCount);
        return deletedCount;
    }

    @Override
    @Async
    public CompletableFuture<Void> executeImportTaskAsync(Long taskId, ImportTaskProcessor processor) {
        return CompletableFuture.runAsync(() -> {
            String taskLockKey = TASK_LOCK_PREFIX + taskId;
            
            try {
                // 获取分布式锁
                Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(taskLockKey, "locked", 1, TimeUnit.HOURS);
                if (!Boolean.TRUE.equals(lockAcquired)) {
                    log.warn("任务{}正在执行中，跳过", taskId);
                    return;
                }
                
                // 执行任务处理器
                processor.process(taskId);
                
            } catch (Exception e) {
                log.error("执行导入任务{}失败", taskId, e);
                completeTask(taskId, false, "任务执行异常: " + e.getMessage());
            } finally {
                // 释放分布式锁
                redisTemplate.delete(taskLockKey);
                // 释放系统并发限制
                SYSTEM_TASK_SEMAPHORE.release();
            }
        }, taskExecutor);
    }

    @Override
    @Async
    public CompletableFuture<Void> executeExportTaskAsync(Long taskId, ExportTaskProcessor processor) {
        return CompletableFuture.runAsync(() -> {
            String taskLockKey = TASK_LOCK_PREFIX + taskId;
            
            try {
                // 获取分布式锁
                Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(taskLockKey, "locked", 1, TimeUnit.HOURS);
                if (!Boolean.TRUE.equals(lockAcquired)) {
                    log.warn("任务{}正在执行中，跳过", taskId);
                    return;
                }
                
                // 执行任务处理器
                processor.process(taskId);
                
            } catch (Exception e) {
                log.error("执行导出任务{}失败", taskId, e);
                completeTask(taskId, false, "任务执行异常: " + e.getMessage());
            } finally {
                // 释放分布式锁
                redisTemplate.delete(taskLockKey);
                // 释放系统并发限制
                SYSTEM_TASK_SEMAPHORE.release();
            }
        }, taskExecutor);
    }

    /**
     * 获取任务实时状态（从Redis缓存）
     */
    public TaskStatus getTaskStatusFromCache(Long taskId) {
        String cacheKey = "task:status:" + taskId;
        String status = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(status)) {
            return TaskStatus.valueOf(status);
        }
        
        // 缓存未命中，从数据库查询
        ImportExportTask task = getTask(taskId);
        return task.getStatus();
    }

    /**
     * 获取任务实时进度（从Redis缓存）
     */
    public TaskProgress getTaskProgressFromCache(Long taskId) {
        String progressKey = "task:progress:" + taskId;
        java.util.Map<Object, Object> progressData = redisTemplate.opsForHash().entries(progressKey);
        
        if (!progressData.isEmpty()) {
            TaskProgress progress = new TaskProgress();
            progress.setTaskId(taskId);
            progress.setProcessed((Integer) progressData.get("processed"));
            progress.setTotal((Integer) progressData.get("total"));
            progress.setProgress((Integer) progressData.get("progress"));
            return progress;
        }
        
        // 缓存未命中，从数据库查询
        ImportExportTask task = getTask(taskId);
        TaskProgress progress = new TaskProgress();
        progress.setTaskId(taskId);
        progress.setTotal(task.getTotalCount());
        progress.setProcessed(task.getSuccessCount() + task.getFailCount());
        progress.setProgress(task.getProgress());
        return progress;
    }

    /**
     * 任务进度信息
     */
    public static class TaskProgress {
        private Long taskId;
        private Integer processed;
        private Integer total;
        private Integer progress;

        // Getters and Setters
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        
        public Integer getProcessed() { return processed; }
        public void setProcessed(Integer processed) { this.processed = processed; }
        
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
    }
}