package com.admin.module.infra.biz.service;

import com.admin.module.infra.api.vo.FileInfoVO;

import java.util.List;
import java.util.Map;

/**
 * 文件缓存服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileCacheService {

    /**
     * 缓存文件信息
     *
     * @param fileId 文件ID
     * @param fileInfo 文件信息
     */
    void cacheFileInfo(Long fileId, FileInfoVO fileInfo);

    /**
     * 获取缓存的文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfoVO getCachedFileInfo(Long fileId);

    /**
     * 删除文件信息缓存
     *
     * @param fileId 文件ID
     */
    void evictFileInfo(Long fileId);

    /**
     * 缓存文件存在性检查结果
     *
     * @param storageKey 存储键
     * @param exists 是否存在
     */
    void cacheFileExists(String storageKey, Boolean exists);

    /**
     * 获取缓存的文件存在性
     *
     * @param storageKey 存储键
     * @return 是否存在
     */
    Boolean getCachedFileExists(String storageKey);

    /**
     * 缓存存储统计信息
     *
     * @param statistics 统计信息
     */
    void cacheStorageStatistics(Map<String, Object> statistics);

    /**
     * 获取缓存的存储统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getCachedStorageStatistics();

    /**
     * 缓存业务统计信息
     *
     * @param statistics 业务统计
     */
    void cacheBusinessStatistics(Map<String, Object> statistics);

    /**
     * 获取缓存的业务统计信息
     *
     * @return 业务统计
     */
    Map<String, Object> getCachedBusinessStatistics();

    /**
     * 缓存上传趋势数据
     *
     * @param trend 趋势数据
     */
    void cacheUploadTrend(Map<String, Object> trend);

    /**
     * 获取缓存的上传趋势数据
     *
     * @return 趋势数据
     */
    Map<String, Object> getCachedUploadTrend();

    /**
     * 批量删除文件相关缓存
     *
     * @param fileIds 文件ID列表
     */
    void evictFileInfoBatch(List<Long> fileIds);

    /**
     * 清理所有统计相关缓存
     */
    void evictAllStatisticsCache();

    /**
     * 预热缓存（加载热点数据）
     */
    void warmupCache();
}