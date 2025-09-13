package com.admin.module.infra.biz.service;

import cn.hutool.core.util.StrUtil;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.biz.convert.FileConvert;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件缓存服务
 * 
 * 提供文件信息的Redis缓存功能，提升查询性能
 * 支持单个文件和批量文件的缓存操作
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FileInfoMapper fileInfoMapper;
    private final ObjectMapper objectMapper;

    /**
     * 文件信息缓存Key前缀
     */
    private static final String FILE_CACHE_PREFIX = "file:info:";
    
    /**
     * 文件哈希缓存Key前缀
     */
    private static final String FILE_HASH_CACHE_PREFIX = "file:hash:";
    
    /**
     * 用户文件列表缓存Key前缀
     */
    private static final String USER_FILES_CACHE_PREFIX = "file:user:";
    
    /**
     * 业务文件列表缓存Key前缀
     */
    private static final String BUSINESS_FILES_CACHE_PREFIX = "file:business:";

    /**
     * 默认缓存过期时间（小时）
     */
    @Value("${admin.cache.file.ttl-hours:2}")
    private Integer defaultCacheTtlHours;

    /**
     * 热点文件缓存过期时间（小时）
     */
    @Value("${admin.cache.file.hot-ttl-hours:6}")
    private Integer hotFileCacheTtlHours;

    /**
     * 缓存空值的过期时间（分钟）
     */
    @Value("${admin.cache.file.null-value-ttl-minutes:5}")
    private Integer nullValueTtlMinutes;

    /**
     * 获取文件信息（带缓存）
     * 
     * @param fileId 文件ID
     * @return 文件信息，不存在返回null
     */
    public FileInfoVO getFileInfo(Long fileId) {
        if (fileId == null) {
            return null;
        }

        String cacheKey = FILE_CACHE_PREFIX + fileId;
        
        try {
            // 1. 先从缓存获取
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if ("NULL".equals(cached)) {
                    // 空值缓存，直接返回null
                    return null;
                }
                
                // 反序列化为FileInfoVO对象
                if (cached instanceof LinkedHashMap) {
                    return objectMapper.convertValue(cached, FileInfoVO.class);
                } else if (cached instanceof FileInfoVO) {
                    return (FileInfoVO) cached;
                }
            }

            // 2. 从数据库查询
            FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
            if (fileInfo == null || fileInfo.getDeleted() == 1) {
                // 缓存空值，防止缓存穿透
                cacheNullValue(cacheKey);
                return null;
            }

            // 3. 转换为VO并写入缓存
            FileInfoVO fileInfoVO = FileConvert.INSTANCE.convert(fileInfo);
            cacheFileInfo(fileId, fileInfoVO);
            
            return fileInfoVO;

        } catch (Exception e) {
            log.error("获取文件缓存失败: fileId={}", fileId, e);
            // 缓存异常时，直接查数据库
            FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
            return fileInfo != null ? FileConvert.INSTANCE.convert(fileInfo) : null;
        }
    }

    /**
     * 批量获取文件信息（带缓存）
     * 
     * @param fileIds 文件ID列表
     * @return 文件信息映射，Key为文件ID，Value为文件信息
     */
    public Map<Long, FileInfoVO> batchGetFileInfo(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, FileInfoVO> result = new HashMap<>();
        List<Long> missedIds = new ArrayList<>();

        try {
            // 1. 批量从缓存获取
            List<String> cacheKeys = fileIds.stream()
                    .map(id -> FILE_CACHE_PREFIX + id)
                    .toList();

            List<Object> cachedValues = redisTemplate.opsForValue().multiGet(cacheKeys);
            
            for (int i = 0; i < fileIds.size(); i++) {
                Long fileId = fileIds.get(i);
                Object cached = cachedValues.get(i);
                
                if (cached == null) {
                    // 缓存未命中
                    missedIds.add(fileId);
                } else if ("NULL".equals(cached)) {
                    // 空值缓存，不加入结果
                    continue;
                } else {
                    // 缓存命中，反序列化
                    try {
                        FileInfoVO fileInfo;
                        if (cached instanceof LinkedHashMap) {
                            fileInfo = objectMapper.convertValue(cached, FileInfoVO.class);
                        } else {
                            fileInfo = (FileInfoVO) cached;
                        }
                        result.put(fileId, fileInfo);
                    } catch (Exception e) {
                        log.warn("反序列化缓存文件信息失败: fileId={}", fileId, e);
                        missedIds.add(fileId);
                    }
                }
            }

            // 2. 查询缓存未命中的文件
            if (!missedIds.isEmpty()) {
                List<FileInfoDO> dbResults = fileInfoMapper.selectValidFilesByIds(missedIds);
                
                for (FileInfoDO fileInfo : dbResults) {
                    FileInfoVO fileInfoVO = FileConvert.INSTANCE.convert(fileInfo);
                    result.put(fileInfo.getId(), fileInfoVO);
                    
                    // 异步写入缓存
                    cacheFileInfoAsync(fileInfo.getId(), fileInfoVO);
                }

                // 3. 对于数据库中也不存在的文件ID，缓存空值
                Set<Long> foundIds = dbResults.stream()
                        .map(FileInfoDO::getId)
                        .collect(java.util.stream.Collectors.toSet());
                
                for (Long missedId : missedIds) {
                    if (!foundIds.contains(missedId)) {
                        cacheNullValueAsync(FILE_CACHE_PREFIX + missedId);
                    }
                }
            }

        } catch (Exception e) {
            log.error("批量获取文件缓存失败: fileIds={}", fileIds, e);
            // 异常时直接查数据库
            List<FileInfoDO> dbResults = fileInfoMapper.selectValidFilesByIds(fileIds);
            for (FileInfoDO fileInfo : dbResults) {
                result.put(fileInfo.getId(), FileConvert.INSTANCE.convert(fileInfo));
            }
        }

        return result;
    }

    /**
     * 根据文件哈希值获取文件信息（带缓存）
     * 
     * @param fileHash 文件哈希值
     * @param uploadStatus 上传状态
     * @return 文件信息，不存在返回null
     */
    public FileInfoVO getFileByHash(String fileHash, Integer uploadStatus) {
        if (StrUtil.isBlank(fileHash)) {
            return null;
        }

        String cacheKey = FILE_HASH_CACHE_PREFIX + fileHash + ":" + uploadStatus;

        try {
            // 1. 先从缓存获取
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if ("NULL".equals(cached)) {
                    return null;
                }
                
                if (cached instanceof LinkedHashMap) {
                    return objectMapper.convertValue(cached, FileInfoVO.class);
                } else if (cached instanceof FileInfoVO) {
                    return (FileInfoVO) cached;
                }
            }

            // 2. 从数据库查询
            FileInfoDO fileInfo = fileInfoMapper.selectByFileHash(fileHash, uploadStatus);
            if (fileInfo == null) {
                cacheNullValue(cacheKey);
                return null;
            }

            // 3. 转换并缓存
            FileInfoVO fileInfoVO = FileConvert.INSTANCE.convert(fileInfo);
            cacheValue(cacheKey, fileInfoVO, Duration.ofHours(defaultCacheTtlHours));
            
            return fileInfoVO;

        } catch (Exception e) {
            log.error("根据哈希值获取文件缓存失败: fileHash={}", fileHash, e);
            FileInfoDO fileInfo = fileInfoMapper.selectByFileHash(fileHash, uploadStatus);
            return fileInfo != null ? FileConvert.INSTANCE.convert(fileInfo) : null;
        }
    }

    /**
     * 缓存文件信息
     * 
     * @param fileId 文件ID
     * @param fileInfo 文件信息
     */
    public void cacheFileInfo(Long fileId, FileInfoVO fileInfo) {
        if (fileId == null || fileInfo == null) {
            return;
        }

        try {
            String cacheKey = FILE_CACHE_PREFIX + fileId;
            Duration ttl = isHotFile(fileInfo) ? 
                    Duration.ofHours(hotFileCacheTtlHours) : 
                    Duration.ofHours(defaultCacheTtlHours);
            
            cacheValue(cacheKey, fileInfo, ttl);
            
            // 如果有哈希值，也缓存哈希映射
            if (StrUtil.isNotBlank(fileInfo.getFileHash())) {
                String hashKey = FILE_HASH_CACHE_PREFIX + fileInfo.getFileHash() + ":" + fileInfo.getUploadStatus();
                cacheValue(hashKey, fileInfo, ttl);
            }
            
        } catch (Exception e) {
            log.error("缓存文件信息失败: fileId={}", fileId, e);
        }
    }

    /**
     * 异步缓存文件信息
     * 
     * @param fileId 文件ID
     * @param fileInfo 文件信息
     */
    public void cacheFileInfoAsync(Long fileId, FileInfoVO fileInfo) {
        // 这里可以使用@Async异步执行，简化实现直接调用
        cacheFileInfo(fileId, fileInfo);
    }

    /**
     * 删除文件缓存
     * 
     * @param fileId 文件ID
     */
    public void evictFileCache(Long fileId) {
        if (fileId == null) {
            return;
        }

        try {
            // 1. 删除文件信息缓存
            String cacheKey = FILE_CACHE_PREFIX + fileId;
            redisTemplate.delete(cacheKey);

            // 2. 如果能获取到文件信息，同时删除哈希缓存
            FileInfoVO fileInfo = getFileInfoFromDb(fileId);
            if (fileInfo != null && StrUtil.isNotBlank(fileInfo.getFileHash())) {
                String hashKey = FILE_HASH_CACHE_PREFIX + fileInfo.getFileHash() + ":" + fileInfo.getUploadStatus();
                redisTemplate.delete(hashKey);
            }

        } catch (Exception e) {
            log.error("删除文件缓存失败: fileId={}", fileId, e);
        }
    }

    /**
     * 批量删除文件缓存
     * 
     * @param fileIds 文件ID列表
     */
    public void batchEvictFileCache(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        try {
            // 构建缓存Key列表
            List<String> cacheKeys = fileIds.stream()
                    .map(id -> FILE_CACHE_PREFIX + id)
                    .toList();

            // 批量删除
            redisTemplate.delete(cacheKeys);
            
            log.info("批量删除文件缓存成功: 数量={}", fileIds.size());

        } catch (Exception e) {
            log.error("批量删除文件缓存失败: fileIds={}", fileIds, e);
        }
    }

    /**
     * 预热热点文件缓存
     * 
     * @param fileIds 文件ID列表
     */
    public void warmUpCache(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        try {
            List<FileInfoDO> fileInfos = fileInfoMapper.selectValidFilesByIds(fileIds);
            
            for (FileInfoDO fileInfo : fileInfos) {
                FileInfoVO fileInfoVO = FileConvert.INSTANCE.convert(fileInfo);
                cacheFileInfoAsync(fileInfo.getId(), fileInfoVO);
            }
            
            log.info("预热文件缓存完成: 数量={}", fileInfos.size());

        } catch (Exception e) {
            log.error("预热文件缓存失败: fileIds={}", fileIds, e);
        }
    }

    /**
     * 清理过期缓存
     */
    public void cleanupExpiredCache() {
        try {
            Set<String> keys = redisTemplate.keys(FILE_CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                // Redis的过期策略会自动清理，这里不需要主动删除
                log.info("发现文件缓存Key数量: {}", keys.size());
            }
        } catch (Exception e) {
            log.error("清理过期文件缓存失败", e);
        }
    }

    // =============== 私有辅助方法 ===============

    /**
     * 缓存空值，防止缓存穿透
     */
    private void cacheNullValue(String cacheKey) {
        try {
            redisTemplate.opsForValue().set(cacheKey, "NULL", 
                    Duration.ofMinutes(nullValueTtlMinutes));
        } catch (Exception e) {
            log.warn("缓存空值失败: key={}", cacheKey, e);
        }
    }

    /**
     * 异步缓存空值
     */
    private void cacheNullValueAsync(String cacheKey) {
        cacheNullValue(cacheKey);
    }

    /**
     * 缓存值
     */
    private void cacheValue(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 从数据库获取文件信息（不走缓存）
     */
    private FileInfoVO getFileInfoFromDb(Long fileId) {
        try {
            FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
            return fileInfo != null ? FileConvert.INSTANCE.convert(fileInfo) : null;
        } catch (Exception e) {
            log.warn("从数据库获取文件信息失败: fileId={}", fileId, e);
            return null;
        }
    }

    /**
     * 判断是否为热点文件
     * 热点文件的标准：下载次数 > 10 或 文件大小 < 1MB
     */
    private boolean isHotFile(FileInfoVO fileInfo) {
        if (fileInfo == null) {
            return false;
        }
        
        return (fileInfo.getDownloadCount() != null && fileInfo.getDownloadCount() > 10) ||
               (fileInfo.getFileSize() != null && fileInfo.getFileSize() < 1024 * 1024); // 1MB
    }
}