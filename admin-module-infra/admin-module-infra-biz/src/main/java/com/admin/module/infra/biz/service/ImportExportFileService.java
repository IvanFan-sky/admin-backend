package com.admin.module.infra.biz.service;

import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 导入导出文件服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportExportFileService {

    /**
     * 上传导入文件
     *
     * @param file 上传的文件
     * @param dataType 数据类型
     * @return 文件上传结果
     */
    FileUploadVO uploadImportFile(MultipartFile file, String dataType);

    /**
     * 生成并上传导出文件
     *
     * @param taskId 任务ID
     * @param fileName 文件名
     * @param fileData 文件数据流
     * @param contentType 文件类型
     * @return 文件上传结果
     */
    FileUploadVO uploadExportFile(Long taskId, String fileName, InputStream fileData, String contentType);

    /**
     * 下载导入模板
     *
     * @param dataType 数据类型
     * @param fileFormat 文件格式
     * @param response HTTP响应
     */
    void downloadImportTemplate(String dataType, String fileFormat, HttpServletResponse response);

    /**
     * 根据文件ID获取文件输入流
     *
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream getFileInputStream(Long fileId);

    /**
     * 根据任务ID获取导入文件流
     *
     * @param taskId 任务ID
     * @return 文件输入流
     */
    InputStream getImportFileInputStream(Long taskId);

    /**
     * 根据任务ID下载结果文件
     *
     * @param taskId 任务ID
     * @param response HTTP响应
     */
    void downloadResultFile(Long taskId, HttpServletResponse response);

    /**
     * 清理过期的导入导出文件
     *
     * @return 清理的文件数量
     */
    int cleanupExpiredImportExportFiles();

    /**
     * 获取文件信息（通过任务）
     *
     * @param taskId 任务ID
     * @return 文件信息
     */
    FileInfoVO getFileInfoByTask(Long taskId);
}