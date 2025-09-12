package com.admin.module.infra.biz.service;

import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.vo.ImportExportTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 导入导出缓存服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存任务信息
     *
     * @param taskVO 任务VO
     */
    public void cacheTask(ImportExportTaskVO taskVO) {
        String key = ImportExportConstants.CacheKey.IMPORT_TASK_PREFIX + taskVO.getId();
        redisTemplate.opsForValue().set(key, taskVO, Duration.ofHours(2));
        log.debug("缓存任务信息，任务ID: {}", taskVO.getId());
    }

    /**
     * 获取缓存的任务信息
     *
     * @param taskId 任务ID
     * @return 任务VO
     */
    public ImportExportTaskVO getCachedTask(Long taskId) {
        String key = ImportExportConstants.CacheKey.IMPORT_TASK_PREFIX + taskId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof ImportExportTaskVO) {
            log.debug("从缓存获取任务信息，任务ID: {}", taskId);
            return (ImportExportTaskVO) cached;
        }
        return null;
    }

    /**
     * 删除任务缓存
     *
     * @param taskId 任务ID
     */
    public void evictTask(Long taskId) {
        String key = ImportExportConstants.CacheKey.IMPORT_TASK_PREFIX + taskId;
        redisTemplate.delete(key);
        log.debug("删除任务缓存，任务ID: {}", taskId);
    }

    /**
     * 缓存用户任务数量限制
     *
     * @param username 用户名
     * @param count 当前任务数
     */
    public void cacheUserTaskLimit(String username, int count) {
        String key = ImportExportConstants.CacheKey.USER_TASK_LIMIT_PREFIX + username;
        redisTemplate.opsForValue().set(key, count, Duration.ofMinutes(5));
        log.debug("缓存用户任务限制，用户: {}, 任务数: {}", username, count);
    }

    /**
     * 获取用户任务数量限制
     *
     * @param username 用户名
     * @return 任务数量，如果未缓存返回-1
     */
    public int getUserTaskLimit(String username) {
        String key = ImportExportConstants.CacheKey.USER_TASK_LIMIT_PREFIX + username;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Integer) {
            return (Integer) cached;
        }
        return -1;
    }

    /**
     * 增加用户任务计数
     *
     * @param username 用户名
     * @return 当前任务数
     */
    public Long incrementUserTaskCount(String username) {
        String key = ImportExportConstants.CacheKey.USER_TASK_LIMIT_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(5));
        log.debug("增加用户任务计数，用户: {}, 当前任务数: {}", username, count);
        return count;
    }

    /**
     * 减少用户任务计数
     *
     * @param username 用户名
     * @return 当前任务数
     */
    public Long decrementUserTaskCount(String username) {
        String key = ImportExportConstants.CacheKey.USER_TASK_LIMIT_PREFIX + username;
        Long count = redisTemplate.opsForValue().decrement(key);
        if (count != null && count <= 0) {
            redisTemplate.delete(key);
        }
        log.debug("减少用户任务计数，用户: {}, 当前任务数: {}", username, count);
        return count != null ? count : 0L;
    }

    /**
     * 缓存模板信息
     *
     * @param dataType 数据类型
     * @param fileFormat 文件格式
     * @param templateData 模板数据
     */
    public void cacheTemplate(String dataType, String fileFormat, Object templateData) {
        String key = ImportExportConstants.CacheKey.TEMPLATE_PREFIX + dataType + ":" + fileFormat;
        redisTemplate.opsForValue().set(key, templateData, Duration.ofHours(24));
        log.debug("缓存模板信息，数据类型: {}, 文件格式: {}", dataType, fileFormat);
    }

    /**
     * 获取缓存的模板信息
     *
     * @param dataType 数据类型
     * @param fileFormat 文件格式
     * @return 模板数据
     */
    public Object getCachedTemplate(String dataType, String fileFormat) {
        String key = ImportExportConstants.CacheKey.TEMPLATE_PREFIX + dataType + ":" + fileFormat;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("从缓存获取模板信息，数据类型: {}, 文件格式: {}", dataType, fileFormat);
        }
        return cached;
    }

    /**
     * 清除所有模板缓存
     */
    public void evictAllTemplates() {
        String pattern = ImportExportConstants.CacheKey.TEMPLATE_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除所有模板缓存，数量: {}", keys.size());
        }
    }

    /**
     * 缓存任务进度
     *
     * @param taskId 任务ID
     * @param progress 进度信息
     */
    public void cacheTaskProgress(Long taskId, TaskProgress progress) {
        String key = "task_progress:" + taskId;
        redisTemplate.opsForValue().set(key, progress, Duration.ofMinutes(30));
        log.debug("缓存任务进度，任务ID: {}, 进度: {}%", taskId, progress.getProgressPercent());
    }

    /**
     * 获取任务进度
     *
     * @param taskId 任务ID
     * @return 进度信息
     */
    public TaskProgress getTaskProgress(Long taskId) {
        String key = "task_progress:" + taskId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof TaskProgress) {
            return (TaskProgress) cached;
        }
        return null;
    }

    /**
     * 删除任务进度缓存
     *
     * @param taskId 任务ID
     */
    public void evictTaskProgress(Long taskId) {
        String key = "task_progress:" + taskId;
        redisTemplate.delete(key);
        log.debug("删除任务进度缓存，任务ID: {}", taskId);
    }

    /**
     * 批量删除用户相关缓存
     *
     * @param username 用户名
     */
    public void evictUserCache(String username) {
        String pattern = "*:" + username + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除用户相关缓存，用户: {}, 数量: {}", username, keys.size());
        }
    }

    /**
     * 检查系统负载限制
     *
     * @param taskType 任务类型
     * @return 是否超过限制
     */
    public boolean isSystemLoadExceeded(String taskType) {
        String key = "system_load:" + taskType;
        String countStr = (String) redisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
        
        int maxLimit = "import".equals(taskType) ? 
            ImportExportConstants.DefaultConfig.MAX_CONCURRENT_TASKS : 
            ImportExportConstants.DefaultConfig.MAX_CONCURRENT_TASKS;
            
        return currentCount >= maxLimit;
    }

    /**
     * 增加系统负载计数
     *
     * @param taskType 任务类型
     * @return 当前负载数
     */
    public Long incrementSystemLoad(String taskType) {
        String key = "system_load:" + taskType;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return count;
    }

    /**
     * 减少系统负载计数
     *
     * @param taskType 任务类型
     * @return 当前负载数
     */
    public Long decrementSystemLoad(String taskType) {
        String key = "system_load:" + taskType;
        Long count = redisTemplate.opsForValue().decrement(key);
        if (count != null && count <= 0) {
            redisTemplate.delete(key);
        }
        return count != null ? count : 0L;
    }

    /**
     * 任务进度信息
     */
    public static class TaskProgress {
        private int progressPercent;
        private String currentOperation;
        private long updateTime;

        public TaskProgress() {}

        public TaskProgress(int progressPercent, String currentOperation) {
            this.progressPercent = progressPercent;
            this.currentOperation = currentOperation;
            this.updateTime = System.currentTimeMillis();
        }

        // getters and setters
        public int getProgressPercent() {
            return progressPercent;
        }

        public void setProgressPercent(int progressPercent) {
            this.progressPercent = progressPercent;
        }

        public String getCurrentOperation() {
            return currentOperation;
        }

        public void setCurrentOperation(String currentOperation) {
            this.currentOperation = currentOperation;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }
}