package com.admin.module.infra.biz.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.vo.FileInfoVO;

import java.util.Map;

/**
 * 文件管理服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileManagementService {

    /**
     * 获取文件存储统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getStorageStatistics();

    /**
     * 获取业务文件统计
     *
     * @return 业务统计
     */
    Map<String, Object> getBusinessStatistics();

    /**
     * 获取存储类型分布
     *
     * @return 存储类型分布
     */
    Map<String, Object> getStorageTypeDistribution();

    /**
     * 获取文件上传趋势（最近30天）
     *
     * @return 上传趋势数据
     */
    Map<String, Object> getUploadTrend();

    /**
     * 获取大文件列表（超过指定大小的文件）
     *
     * @param minSize 最小文件大小（字节）
     * @param pageDTO 分页参数
     * @return 大文件列表
     */
    PageResult<FileInfoVO> getLargeFiles(Long minSize, FilePageDTO pageDTO);

    /**
     * 获取重复文件列表
     *
     * @param pageDTO 分页参数
     * @return 重复文件列表
     */
    PageResult<FileInfoVO> getDuplicateFiles(FilePageDTO pageDTO);

    /**
     * 获取孤儿文件列表（数据库中存在但存储中不存在的文件）
     *
     * @param pageDTO 分页参数
     * @return 孤儿文件列表
     */
    PageResult<FileInfoVO> getOrphanFiles(FilePageDTO pageDTO);

    /**
     * 存储空间清理
     *
     * @return 清理结果
     */
    Map<String, Object> performStorageCleanup();

    /**
     * 修复孤儿文件
     *
     * @return 修复结果
     */
    Map<String, Object> repairOrphanFiles();
}