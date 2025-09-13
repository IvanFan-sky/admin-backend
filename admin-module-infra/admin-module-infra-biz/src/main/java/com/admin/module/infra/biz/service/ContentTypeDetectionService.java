package com.admin.module.infra.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * 内容类型检测服务
 * 
 * 使用Apache Tika进行文件类型检测和安全验证
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@Slf4j
public class ContentTypeDetectionService {

    private final Tika tika = new Tika();
    
    /**
     * 允许的文件类型白名单
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        // 图片类型
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp",
        "image/svg+xml",
        
        // 文档类型
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        
        // Excel类型
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        
        // PowerPoint类型
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        
        // 文本类型
        "text/plain",
        "text/csv",
        "application/json",
        "application/xml",
        "text/xml",
        
        // 压缩文件
        "application/zip",
        "application/x-rar-compressed",
        "application/x-7z-compressed",
        
        // 其他常用类型
        "application/octet-stream"
    );
    
    /**
     * 检测文件的真实内容类型
     * 
     * @param file 上传文件
     * @return 检测到的内容类型
     */
    public String detectContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            String detectedType = tika.detect(inputStream, file.getOriginalFilename());
            log.debug("文件 {} 检测到的内容类型: {}, 声明类型: {}", 
                    file.getOriginalFilename(), detectedType, file.getContentType());
            return detectedType;
        } catch (IOException e) {
            log.error("检测文件内容类型失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件内容类型检测失败", e);
        }
    }
    
    /**
     * 验证文件类型是否在白名单中
     * 
     * @param file 上传文件
     * @return 是否允许上传
     */
    public boolean isAllowedFileType(MultipartFile file) {
        String detectedType = detectContentType(file);
        boolean allowed = ALLOWED_CONTENT_TYPES.contains(detectedType);
        
        if (!allowed) {
            log.warn("文件类型不被允许: 文件={}, 检测类型={}, 声明类型={}", 
                    file.getOriginalFilename(), detectedType, file.getContentType());
        }
        
        return allowed;
    }
    
    /**
     * 验证文件安全性（检查是否为声明的类型）
     * 
     * @param file 上传文件
     * @return 验证结果
     */
    public FileTypeValidationResult validateFileType(MultipartFile file) {
        String detectedType = detectContentType(file);
        
        // 检查是否在白名单中
        if (!ALLOWED_CONTENT_TYPES.contains(detectedType)) {
            return FileTypeValidationResult.rejected("文件类型不被允许: " + detectedType);
        }
        
        return FileTypeValidationResult.accepted(detectedType);
    }
    
    /**
     * 文件类型验证结果
     */
    public static class FileTypeValidationResult {
        private final boolean allowed;
        private final String contentType;
        private final String message;
        
        private FileTypeValidationResult(boolean allowed, String contentType, String message) {
            this.allowed = allowed;
            this.contentType = contentType;
            this.message = message;
        }
        
        public static FileTypeValidationResult accepted(String contentType) {
            return new FileTypeValidationResult(true, contentType, "文件类型验证通过");
        }
        
        public static FileTypeValidationResult rejected(String message) {
            return new FileTypeValidationResult(false, null, message);
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
