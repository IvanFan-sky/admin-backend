package com.admin.module.infra.biz.service;

import cn.hutool.core.util.StrUtil;
import com.admin.framework.redis.core.RedisCache;
import com.admin.module.infra.api.vo.ChunkInfoVO;
import com.admin.module.infra.api.vo.ChunkUploadSessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * 分片上传缓存服务
 * <p>
 * 使用Redis缓存分片上传的临时信息，提高性能并支持断点续传
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkUploadCacheService {

    private final RedisCache redisCache;

    /**
     * 缓存键前缀
     */
    private static final String CACHE_KEY_PREFIX = "chunk_upload:";
    
    /**
     * 分片信息缓存键前缀
     */
    private static final String CHUNK_INFO_PREFIX = "chunk_info:";
    
    /**
     * 上传会话缓存键前缀
     */
    private static final String UPLOAD_SESSION_PREFIX = "upload_session:";
    
    /**
     * 默认缓存过期时间（24小时）
     */
    private static final Duration DEFAULT_EXPIRE_TIME = Duration.ofHours(24);

    /**
     * 缓存上传会话信息
     * 
     * @param uploadId 上传会话ID
     * @param sessionInfo 会话信息
     */
    public void cacheUploadSession(String uploadId, ChunkUploadSessionVO sessionInfo) {
        String key = CACHE_KEY_PREFIX + UPLOAD_SESSION_PREFIX + uploadId;
        redisCache.set(key, sessionInfo, DEFAULT_EXPIRE_TIME);
        log.debug("缓存上传会话信息: uploadId={}", uploadId);
    }

    /**
     * 获取上传会话信息
     * 
     * @param uploadId 上传会话ID
     * @return 会话信息
     */
    public ChunkUploadSessionVO getUploadSession(String uploadId) {
        String key = CACHE_KEY_PREFIX + UPLOAD_SESSION_PREFIX + uploadId;
        ChunkUploadSessionVO session = redisCache.get(key);
        log.debug("获取上传会话信息: uploadId={}, exists={}", uploadId, session != null);
        return session;
    }

    /**
     * 删除上传会话信息
     * 
     * @param uploadId 上传会话ID
     */
    public void removeUploadSession(String uploadId) {
        String key = CACHE_KEY_PREFIX + UPLOAD_SESSION_PREFIX + uploadId;
        redisCache.delete(key);
        log.debug("删除上传会话信息: uploadId={}", uploadId);
    }

    /**
     * 标记分片已上传
     * 
     * @param uploadId 上传会话ID
     * @param chunkNumber 分片序号
     * @param etag 分片ETag
     */
    public void markChunkUploaded(String uploadId, Integer chunkNumber, String etag) {
        String key = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        String field = String.valueOf(chunkNumber);
        
        ChunkInfoVO chunkInfoVO = new ChunkInfoVO();
        chunkInfoVO.setChunkNumber(chunkNumber);
        chunkInfoVO.setEtag(etag);
        chunkInfoVO.setUploadTime(System.currentTimeMillis());
        
        redisCache.hSet(key, field, chunkInfoVO);
        redisCache.expire(key, DEFAULT_EXPIRE_TIME);
        
        log.debug("标记分片已上传: uploadId={}, chunkNumber={}, etag={}", uploadId, chunkNumber, etag);
    }

    /**
     * 检查分片是否已上传
     * 
     * @param uploadId 上传会话ID
     * @param chunkNumber 分片序号
     * @return 是否已上传
     */
    public boolean isChunkUploaded(String uploadId, Integer chunkNumber) {
        String key = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        String field = String.valueOf(chunkNumber);
        
        ChunkInfoVO chunkInfoVO = redisCache.hGet(key, field);
        boolean uploaded = chunkInfoVO != null && StrUtil.isNotBlank(chunkInfoVO.getEtag());
        
        log.debug("检查分片上传状态: uploadId={}, chunkNumber={}, uploaded={}", uploadId, chunkNumber, uploaded);
        return uploaded;
    }

    /**
     * 获取已上传的分片信息
     * 
     * @param uploadId 上传会话ID
     * @return 已上传的分片信息Map，key为分片序号，value为分片信息
     */
    public Map<Integer, ChunkInfoVO> getUploadedChunks(String uploadId) {
        String key = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        Map<Object, Object> cacheMap = redisCache.hGetAll(key);
        
        Map<String, ChunkInfoVO> result = new HashMap<>();
        if (cacheMap != null) {
            for (Map.Entry<Object, Object> entry : cacheMap.entrySet()) {
                try {
                    String chunkKey = String.valueOf(entry.getKey());
                    ChunkInfoVO chunkInfo = (ChunkInfoVO) entry.getValue();
                    result.put(chunkKey, chunkInfo);
                } catch (Exception e) {
                    log.warn("转换分片信息失败: {}", entry.getKey());
                }
            }
        }
        
        Map<Integer, ChunkInfoVO> finalResult = new HashMap<>();
        for (Map.Entry<String, ChunkInfoVO> entry : result.entrySet()) {
            try {
                Integer chunkNumber = Integer.valueOf(entry.getKey());
                finalResult.put(chunkNumber, entry.getValue());
            } catch (NumberFormatException e) {
                log.warn("无效的分片序号: {}", entry.getKey());
            }
        }
        
        log.debug("获取已上传分片信息: uploadId={}, count={}", uploadId, finalResult.size());
        return finalResult;
    }

    /**
     * 获取已上传的分片序号列表
     * 
     * @param uploadId 上传会话ID
     * @return 已上传的分片序号列表
     */
    public List<Integer> getUploadedChunkNumbers(String uploadId) {
        String key = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        Map<Object, Object> cacheMap = redisCache.hGetAll(key);
        
        List<Integer> result = new ArrayList<>();
        if (cacheMap != null) {
            for (Object chunkKey : cacheMap.keySet()) {
                try {
                    Integer chunkNumber = Integer.valueOf(String.valueOf(chunkKey));
                    result.add(chunkNumber);
                } catch (NumberFormatException e) {
                    log.warn("无效的分片序号: {}", chunkKey);
                }
            }
        }
        
        // 排序
        result.sort(Integer::compareTo);
        
        log.debug("获取已上传分片序号: uploadId={}, chunks={}", uploadId, result);
        return result;
    }

    /**
     * 清理上传缓存
     * 
     * @param uploadId 上传会话ID
     */
    public void clearUploadCache(String uploadId) {
        // 删除会话信息
        String sessionKey = CACHE_KEY_PREFIX + UPLOAD_SESSION_PREFIX + uploadId;
        redisCache.delete(sessionKey);
        
        // 删除分片信息
        String chunkKey = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        redisCache.delete(chunkKey);
        
        log.debug("清理上传缓存: uploadId={}", uploadId);
    }

    /**
     * 延长缓存过期时间
     * 
     * @param uploadId 上传会话ID
     */
    public void extendCacheExpire(String uploadId) {
        String sessionKey = CACHE_KEY_PREFIX + UPLOAD_SESSION_PREFIX + uploadId;
        String chunkKey = CACHE_KEY_PREFIX + CHUNK_INFO_PREFIX + uploadId;
        
        redisCache.expire(sessionKey, DEFAULT_EXPIRE_TIME);
        redisCache.expire(chunkKey, DEFAULT_EXPIRE_TIME);
        
        log.debug("延长缓存过期时间: uploadId={}", uploadId);
    }
}
