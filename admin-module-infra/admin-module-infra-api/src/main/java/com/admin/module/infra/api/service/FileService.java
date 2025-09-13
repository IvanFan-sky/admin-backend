package com.admin.module.infra.api.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileService {

    /**
     * 文件上传
     * 
     * @param uploadDTO 上传参数
     * @return 上传结果
     */
    FileUploadVO uploadFile(FileUploadDTO uploadDTO);

    /**
     * 文件下载
     * 
     * @param fileId 文件ID
     * @param request HTTP请求
     * @param response HTTP响应
     * @param inline 是否内联显示
     * @param downloadName 下载文件名
     */
    void downloadFile(Long fileId, HttpServletRequest request, HttpServletResponse response,
                     Boolean inline, String downloadName);

    /**
     * 获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfoVO getFileInfo(Long fileId);

    /**
     * 根据哈希值查找文件
     * 
     * @param fileHash 文件哈希值
     * @return 文件信息
     */
    FileInfoVO findFileByHash(String fileHash);

    /**
     * 分页查询文件列表
     * 
     * @param pageDTO 分页查询参数
     * @return 文件列表
     */
    PageResult<FileInfoVO> getFileList(FilePageDTO pageDTO);

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    Boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     * 
     * @param fileIds 文件ID列表
     * @return 删除成功数量
     */
    Integer batchDeleteFiles(java.util.List<Long> fileIds);

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @param expireSeconds 过期时间（秒）
     * @return 访问URL
     */
    String getFileAccessUrl(Long fileId, Integer expireSeconds);
}
