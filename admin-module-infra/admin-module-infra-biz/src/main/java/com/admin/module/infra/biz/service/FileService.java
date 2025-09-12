package com.admin.module.infra.biz.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FileChunkUploadDTO;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.vo.FileChunkUploadVO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 文件服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param uploadDTO 上传请求
     * @return 上传结果
     */
    FileUploadVO uploadFile(FileUploadDTO uploadDTO);

    /**
     * 初始化分片上传
     *
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param contentType 文件类型
     * @param businessType 业务类型
     * @param businessId 业务关联ID
     * @return 上传会话ID
     */
    String initiateChunkUpload(String fileName, Long fileSize, String contentType, 
                              String businessType, String businessId);

    /**
     * 分片上传
     *
     * @param chunkUploadDTO 分片上传请求
     * @return 分片上传结果
     */
    FileChunkUploadVO uploadChunk(FileChunkUploadDTO chunkUploadDTO);

    /**
     * 完成分片上传
     *
     * @param uploadId 上传会话ID
     * @return 文件信息
     */
    FileInfoVO completeChunkUpload(String uploadId);

    /**
     * 取消分片上传
     *
     * @param uploadId 上传会话ID
     */
    void abortChunkUpload(String uploadId);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @param inline 是否内联显示
     * @param downloadName 下载文件名
     * @param response HTTP响应
     */
    void downloadFile(Long fileId, Boolean inline, String downloadName, HttpServletResponse response);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 删除成功的数量
     */
    int deleteFiles(List<Long> fileIds);

    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfoVO getFileInfo(Long fileId);

    /**
     * 根据业务获取文件列表
     *
     * @param businessType 业务类型
     * @param businessId 业务关联ID
     * @return 文件列表
     */
    List<FileInfoVO> getFilesByBusiness(String businessType, String businessId);

    /**
     * 分页查询文件
     *
     * @param pageDTO 查询条件
     * @return 分页结果
     */
    PageResult<FileInfoVO> getFilePage(FilePageDTO pageDTO);

    /**
     * 生成预签名上传URL
     *
     * @param fileName 文件名
     * @param contentType 文件类型
     * @param expirationSeconds 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUploadUrl(String fileName, String contentType, int expirationSeconds);

    /**
     * 生成预签名下载URL
     *
     * @param fileId 文件ID
     * @param expirationSeconds 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedDownloadUrl(Long fileId, int expirationSeconds);

    /**
     * 清理过期文件
     *
     * @return 清理的文件数量
     */
    int cleanupExpiredFiles();

    /**
     * 清理临时文件和分片
     *
     * @return 清理的文件数量
     */
    int cleanupTempFiles();

    /**
     * 根据哈希值查找文件
     *
     * @param fileHash 文件哈希值
     * @return 文件信息
     */
    FileInfoDO findFileByHash(String fileHash);
}