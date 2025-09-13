package com.admin.framework.excel.service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 导入导出文件存储服务接口
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
     * @param taskId 任务ID
     * @return 文件存储路径
     */
    String uploadImportFile(MultipartFile file, Long taskId);

    /**
     * 保存导出文件
     * 
     * @param data 文件数据
     * @param fileName 文件名
     * @param taskId 任务ID
     * @return 文件存储路径
     */
    String saveExportFile(byte[] data, String fileName, Long taskId);

    /**
     * 保存错误报告文件
     * 
     * @param errorContent 错误内容
     * @param taskId 任务ID
     * @return 文件存储路径
     */
    String saveErrorReportFile(String errorContent, Long taskId);

    /**
     * 获取导入文件输入流
     * 
     * @param filePath 文件路径
     * @return 文件输入流
     */
    InputStream getImportFileStream(String filePath);

    /**
     * 下载导出文件
     * 
     * @param filePath 文件路径
     * @param response HTTP响应
     */
    void downloadExportFile(String filePath, HttpServletResponse response);

    /**
     * 下载错误报告文件
     * 
     * @param filePath 文件路径
     * @param response HTTP响应
     */
    void downloadErrorReportFile(String filePath, HttpServletResponse response);

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);

    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean fileExists(String filePath);

    /**
     * 获取文件大小
     * 
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    long getFileSize(String filePath);

    /**
     * 生成文件访问URL
     * 
     * @param filePath 文件路径
     * @param expireMinutes 过期时间（分钟）
     * @return 访问URL
     */
    String generateFileUrl(String filePath, int expireMinutes);

    /**
     * 清理过期文件
     * 
     * @param days 保留天数
     * @return 清理文件数量
     */
    int cleanExpiredFiles(int days);
}