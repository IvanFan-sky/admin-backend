package com.admin.module.infra.api.service;

import com.admin.module.infra.api.dto.ChunkUploadDTO;
import com.admin.module.infra.api.dto.ChunkUploadInitDTO;
import com.admin.module.infra.api.vo.ChunkUploadInitVO;
import com.admin.module.infra.api.vo.ChunkUploadVO;

/**
 * 分片上传服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ChunkUploadService {

    /**
     * 初始化分片上传
     * <p>
     * 1. 检查文件是否已存在（秒传）
     * 2. 创建上传会话
     * 3. 返回上传参数和已上传分片信息
     * 
     * @param initDTO 初始化请求参数
     * @return 初始化响应
     */
    ChunkUploadInitVO initChunkUpload(ChunkUploadInitDTO initDTO);

    /**
     * 上传文件分片
     * <p>
     * 1. 验证分片参数
     * 2. 上传分片到MinIO
     * 3. 记录分片信息
     * 4. 检查是否所有分片都已上传
     * 5. 如果完成则合并文件
     * 
     * @param chunkDTO 分片上传请求参数
     * @return 分片上传响应
     */
    ChunkUploadVO uploadChunk(ChunkUploadDTO chunkDTO);

    /**
     * 完成分片上传（手动触发合并）
     * <p>
     * 1. 验证所有分片都已上传
     * 2. 合并分片为完整文件
     * 3. 清理临时分片
     * 4. 更新文件状态
     * 
     * @param uploadId 上传会话ID
     * @return 完成响应
     */
    ChunkUploadVO completeChunkUpload(String uploadId);

    /**
     * 取消分片上传
     * <p>
     * 1. 删除已上传的分片
     * 2. 清理上传会话信息
     * 3. 更新文件状态为取消
     * 
     * @param uploadId 上传会话ID
     */
    void cancelChunkUpload(String uploadId);

    /**
     * 获取分片上传进度
     * <p>
     * 查询指定上传会话的进度信息
     * 
     * @param uploadId 上传会话ID
     * @return 上传进度信息
     */
    ChunkUploadVO getUploadProgress(String uploadId);

    /**
     * 检查分片是否已存在
     * <p>
     * 用于断点续传，检查指定分片是否已经上传
     * 
     * @param uploadId 上传会话ID
     * @param chunkNumber 分片序号
     * @return 是否已存在
     */
    boolean isChunkExists(String uploadId, Integer chunkNumber);
}
