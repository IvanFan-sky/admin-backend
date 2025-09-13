package com.admin.framework.excel.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.framework.excel.service.ImportExportFileService;
import com.admin.framework.minio.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 导入导出文件存储服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportFileServiceImpl implements ImportExportFileService {

    private final MinioService minioService;

    @Value("${admin.file.default-bucket:default}")
    private String defaultBucket;

    // 文件路径前缀
    private static final String IMPORT_PATH_PREFIX = "import-export/import/";
    private static final String EXPORT_PATH_PREFIX = "import-export/export/";
    private static final String ERROR_REPORT_PREFIX = "import-export/error-report/";

    @Override
    public String uploadImportFile(MultipartFile file, Long taskId) {
        try {
            // 生成文件路径
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = String.format("task_%d_%s%s", 
                taskId, 
                DateUtil.format(LocalDateTime.now(), "yyyyMMdd_HHmmss"),
                extension);
            String filePath = IMPORT_PATH_PREFIX + generateDatePath() + "/" + fileName;

            // 上传到MinIO
            minioService.uploadFile(defaultBucket, filePath, file.getInputStream(), file.getSize(), file.getContentType());
            
            log.info("导入文件上传成功，任务ID: {}, 文件路径: {}", taskId, filePath);
            return filePath;
            
        } catch (Exception e) {
            log.error("导入文件上传失败，任务ID: {}", taskId, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String saveExportFile(byte[] data, String fileName, Long taskId) {
        try {
            // 生成文件路径
            String extension = getFileExtension(fileName);
            String newFileName = String.format("export_task_%d_%s%s", 
                taskId, 
                DateUtil.format(LocalDateTime.now(), "yyyyMMdd_HHmmss"),
                extension);
            String filePath = EXPORT_PATH_PREFIX + generateDatePath() + "/" + newFileName;

            // 上传到MinIO
            try (InputStream inputStream = new ByteArrayInputStream(data)) {
                String contentType = getContentType(extension);
                minioService.uploadFile(defaultBucket, filePath, inputStream, data.length, contentType);
            }
            
            log.info("导出文件保存成功，任务ID: {}, 文件路径: {}", taskId, filePath);
            return filePath;
            
        } catch (Exception e) {
            log.error("导出文件保存失败，任务ID: {}", taskId, e);
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }
    }

    @Override
    public String saveErrorReportFile(String errorContent, Long taskId) {
        try {
            // 生成错误报告文件名
            String fileName = String.format("error_report_task_%d_%s.txt", 
                taskId, 
                DateUtil.format(LocalDateTime.now(), "yyyyMMdd_HHmmss"));
            String filePath = ERROR_REPORT_PREFIX + generateDatePath() + "/" + fileName;

            // 转换为字节数组并上传
            byte[] data = errorContent.getBytes(StandardCharsets.UTF_8);
            try (InputStream inputStream = new ByteArrayInputStream(data)) {
                minioService.uploadFile(defaultBucket, filePath, inputStream, data.length, "text/plain; charset=utf-8");
            }
            
            log.info("错误报告文件保存成功，任务ID: {}, 文件路径: {}", taskId, filePath);
            return filePath;
            
        } catch (Exception e) {
            log.error("错误报告文件保存失败，任务ID: {}", taskId, e);
            throw new RuntimeException("错误报告保存失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream getImportFileStream(String filePath) {
        try {
            return minioService.downloadFile(defaultBucket, filePath);
        } catch (Exception e) {
            log.error("获取导入文件流失败，文件路径: {}", filePath, e);
            throw new RuntimeException("获取文件失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadExportFile(String filePath, HttpServletResponse response) {
        try {
            // 从文件路径中提取文件名
            String fileName = extractFileName(filePath);
            if (StrUtil.isBlank(fileName)) {
                fileName = "export_file.xlsx";
            }

            // 设置响应头
            setupDownloadResponse(response, fileName);

            // 下载文件
            try (InputStream inputStream = minioService.downloadFile(defaultBucket, filePath)) {
                IoUtil.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
            }
            
            log.info("导出文件下载成功，文件路径: {}", filePath);
            
        } catch (Exception e) {
            log.error("导出文件下载失败，文件路径: {}", filePath, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadErrorReportFile(String filePath, HttpServletResponse response) {
        try {
            // 从文件路径中提取文件名
            String fileName = extractFileName(filePath);
            if (StrUtil.isBlank(fileName)) {
                fileName = "error_report.txt";
            }

            // 设置响应头
            response.setContentType("text/plain; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

            // 下载文件
            try (InputStream inputStream = minioService.downloadFile(defaultBucket, filePath)) {
                IoUtil.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
            }
            
            log.info("错误报告文件下载成功，文件路径: {}", filePath);
            
        } catch (Exception e) {
            log.error("错误报告文件下载失败，文件路径: {}", filePath, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            minioService.deleteFile(defaultBucket, filePath);
            log.info("文件删除成功，文件路径: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败，文件路径: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            return minioService.getObjectInfo(defaultBucket, filePath) != null;
        } catch (Exception e) {
            log.error("检查文件存在性失败，文件路径: {}", filePath, e);
            return false;
        }
    }

    @Override
    public long getFileSize(String filePath) {
        try {
            var objectInfo = minioService.getObjectInfo(defaultBucket, filePath);
            return objectInfo != null ? objectInfo.getSize() : 0;
        } catch (Exception e) {
            log.error("获取文件大小失败，文件路径: {}", filePath, e);
            return 0;
        }
    }

    @Override
    public String generateFileUrl(String filePath, int expireMinutes) {
        try {
            return minioService.getPresignedDownloadUrl(defaultBucket, filePath, expireMinutes * 60);
        } catch (Exception e) {
            log.error("生成文件访问URL失败，文件路径: {}", filePath, e);
            return null;
        }
    }

    @Override
    public int cleanExpiredFiles(int days) {
        try {
            LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
            String beforeDatePath = beforeDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            
            int deletedCount = 0;
            
            // 清理导入文件
            deletedCount += cleanFilesByPrefix(IMPORT_PATH_PREFIX, beforeDatePath);
            
            // 清理导出文件
            deletedCount += cleanFilesByPrefix(EXPORT_PATH_PREFIX, beforeDatePath);
            
            // 清理错误报告文件
            deletedCount += cleanFilesByPrefix(ERROR_REPORT_PREFIX, beforeDatePath);
            
            log.info("清理{}天前的过期文件完成，共清理{}个文件", days, deletedCount);
            return deletedCount;
            
        } catch (Exception e) {
            log.error("清理过期文件失败", e);
            return 0;
        }
    }

    /**
     * 根据前缀清理文件
     */
    private int cleanFilesByPrefix(String prefix, String beforeDatePath) {
        try {
            List<com.admin.common.result.minio.ObjectInfo> objectInfos = minioService.listObjects(defaultBucket, prefix, 1000);
            List<String> files = objectInfos.stream().map(com.admin.common.result.minio.ObjectInfo::getObjectName).collect(java.util.stream.Collectors.toList());
            int deletedCount = 0;
            
            for (String filePath : files) {
                // 检查文件路径中的日期是否在指定日期之前
                if (isFileBeforeDate(filePath, beforeDatePath)) {
                    if (deleteFile(filePath)) {
                        deletedCount++;
                    }
                }
            }
            
            return deletedCount;
        } catch (Exception e) {
            log.error("清理前缀为{}的文件失败", prefix, e);
            return 0;
        }
    }

    /**
     * 检查文件是否在指定日期之前
     */
    private boolean isFileBeforeDate(String filePath, String beforeDatePath) {
        try {
            // 从文件路径中提取日期部分 (例如: import-export/import/2024/01/15/file.xlsx)
            String[] pathParts = filePath.split("/");
            if (pathParts.length >= 5) {
                String year = pathParts[2];
                String month = pathParts[3];
                String day = pathParts[4];
                String fileDatePath = year + "/" + month + "/" + day;
                return fileDatePath.compareTo(beforeDatePath) < 0;
            }
            return false;
        } catch (Exception e) {
            log.warn("解析文件日期失败，文件路径: {}", filePath);
            return false;
        }
    }

    /**
     * 生成日期路径 (yyyy/MM/dd)
     */
    private String generateDatePath() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return ".xlsx";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex != -1 ? fileName.substring(lastDotIndex) : ".xlsx";
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".csv":
                return "text/csv";
            case ".txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 从文件路径中提取文件名
     */
    private String extractFileName(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return null;
        }
        int lastSlashIndex = filePath.lastIndexOf("/");
        return lastSlashIndex != -1 ? filePath.substring(lastSlashIndex + 1) : filePath;
    }

    /**
     * 设置文件下载响应头
     */
    private void setupDownloadResponse(HttpServletResponse response, String fileName) throws IOException {
        String extension = getFileExtension(fileName);
        String contentType = getContentType(extension);
        
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);
        
        // 禁用缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}