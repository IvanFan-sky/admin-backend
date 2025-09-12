package com.admin.module.infra.biz.service.impl;

import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.biz.service.FileCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件缓存服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileCacheServiceImpl implements FileCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FILE_INFO_PREFIX = "file:info:";
    private static final String FILE_EXISTS_PREFIX = "file:exists:";
    private static final String STORAGE_STATS_KEY = "file:stats:storage";
    private static final String BUSINESS_STATS_KEY = "file:stats:business";
    private static final String UPLOAD_TREND_KEY = "file:stats:trend";

    @Override
    @CachePut(value = "fileInfo", key = "#fileId", cacheManager = "caffeineCacheManager")
    public void cacheFileInfo(Long fileId, FileInfoVO fileInfo) {
        // Redis缓存
        redisTemplate.opsForValue().set(FILE_INFO_PREFIX + fileId, fileInfo, Duration.ofMinutes(30));
        log.debug("缓存文件信息，文件ID: {}", fileId);
    }

    @Override
    @Cacheable(value = "fileInfo", key = "#fileId", cacheManager = "caffeineCacheManager")
    public FileInfoVO getCachedFileInfo(Long fileId) {
        // 先尝试从本地缓存获取，如果没有则从Redis获取
        Object cached = redisTemplate.opsForValue().get(FILE_INFO_PREFIX + fileId);
        return cached instanceof FileInfoVO ? (FileInfoVO) cached : null;
    }

    @Override
    @CacheEvict(value = "fileInfo", key = "#fileId", cacheManager = "caffeineCacheManager")
    public void evictFileInfo(Long fileId) {
        redisTemplate.delete(FILE_INFO_PREFIX + fileId);
        log.debug("清除文件信息缓存，文件ID: {}", fileId);
    }

    @Override
    public void cacheFileExists(String storageKey, Boolean exists) {
        String key = FILE_EXISTS_PREFIX + storageKey.hashCode();
        redisTemplate.opsForValue().set(key, exists, Duration.ofMinutes(5)); // 存在性检查缓存较短时间
        log.debug("缓存文件存在性，存储键: {}, 存在: {}", storageKey, exists);
    }

    @Override
    public Boolean getCachedFileExists(String storageKey) {
        String key = FILE_EXISTS_PREFIX + storageKey.hashCode();
        Object cached = redisTemplate.opsForValue().get(key);
        return cached instanceof Boolean ? (Boolean) cached : null;
    }

    @Override
    public void cacheStorageStatistics(Map<String, Object> statistics) {
        redisTemplate.opsForValue().set(STORAGE_STATS_KEY, statistics, Duration.ofHours(1));
        log.debug("缓存存储统计信息");
    }

    @Override
    @Cacheable(value = "storageStats", key = "'storage'", cacheManager = "redisCacheManager")
    public Map<String, Object> getCachedStorageStatistics() {
        Object cached = redisTemplate.opsForValue().get(STORAGE_STATS_KEY);
        return cached instanceof Map ? (Map<String, Object>) cached : null;
    }

    @Override
    public void cacheBusinessStatistics(Map<String, Object> statistics) {
        redisTemplate.opsForValue().set(BUSINESS_STATS_KEY, statistics, Duration.ofHours(1));
        log.debug("缓存业务统计信息");
    }

    @Override
    @Cacheable(value = "businessStats", key = "'business'", cacheManager = "redisCacheManager")
    public Map<String, Object> getCachedBusinessStatistics() {
        Object cached = redisTemplate.opsForValue().get(BUSINESS_STATS_KEY);
        return cached instanceof Map ? (Map<String, Object>) cached : null;
    }

    @Override
    public void cacheUploadTrend(Map<String, Object> trend) {
        redisTemplate.opsForValue().set(UPLOAD_TREND_KEY, trend, Duration.ofHours(2));
        log.debug("缓存上传趋势数据");
    }

    @Override
    @Cacheable(value = "uploadTrend", key = "'trend'", cacheManager = "redisCacheManager")
    public Map<String, Object> getCachedUploadTrend() {
        Object cached = redisTemplate.opsForValue().get(UPLOAD_TREND_KEY);
        return cached instanceof Map ? (Map<String, Object>) cached : null;
    }

    @Override
    public void evictFileInfoBatch(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        
        // 批量删除Redis缓存
        List<String> keys = fileIds.stream()
                .map(id -> FILE_INFO_PREFIX + id)
                .toList();
        redisTemplate.delete(keys);
        
        log.debug("批量清除文件信息缓存，数量: {}", fileIds.size());
    }

    @Override
    @CacheEvict(value = {"storageStats", "businessStats", "uploadTrend"}, allEntries = true, 
                cacheManager = "redisCacheManager")
    public void evictAllStatisticsCache() {
        redisTemplate.delete(STORAGE_STATS_KEY);
        redisTemplate.delete(BUSINESS_STATS_KEY);
        redisTemplate.delete(UPLOAD_TREND_KEY);
        log.debug("清除所有统计相关缓存");
    }

    @Override
    public void warmupCache() {
        log.info("开始缓存预热");
        
        try {
            // 这里可以预加载一些热点数据
            // 例如：最近访问的文件信息、常用统计数据等
            
            // 预热存储统计
            // storageStatistics = fileManagementService.getStorageStatistics();
            // cacheStorageStatistics(storageStatistics);
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }
}