package com.admin.framework.minio.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MinIO工具类
 * 
 * 提供MinIO操作的通用工具方法
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class MinioUtils {

    /**
     * 文件名生成策略枚举
     */
    public enum FileNameStrategy {
        /**
         * 使用UUID作为文件名
         */
        UUID,
        /**
         * 使用日期时间作为前缀
         */
        DATE,
        /**
         * 保留原始文件名
         */
        ORIGINAL
    }

    /**
     * 根据策略生成文件名
     * 
     * @param originalFileName 原始文件名
     * @param strategy 生成策略
     * @return 生成的文件名
     */
    public static String generateFileName(String originalFileName, FileNameStrategy strategy) {
        String extension = getFileExtension(originalFileName);
        
        switch (strategy) {
            case UUID:
                return IdUtil.simpleUUID() + (StrUtil.isNotBlank(extension) ? "." + extension : "");
            case DATE:
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss"));
                return timestamp + "_" + originalFileName;
            case ORIGINAL:
            default:
                return originalFileName;
        }
    }

    /**
     * 根据策略生成文件名（字符串策略）
     * 
     * @param originalFileName 原始文件名
     * @param strategyName 策略名称
     * @return 生成的文件名
     */
    public static String generateFileName(String originalFileName, String strategyName) {
        try {
            FileNameStrategy strategy = FileNameStrategy.valueOf(strategyName.toUpperCase());
            return generateFileName(originalFileName, strategy);
        } catch (IllegalArgumentException e) {
            return generateFileName(originalFileName, FileNameStrategy.ORIGINAL);
        }
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 文件扩展名（不包含点号）
     */
    public static String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取MIME类型
     * 
     * @param fileName 文件名
     * @return MIME类型
     */
    public static String getContentType(String fileName) {
        String extension = getFileExtension(fileName);
        switch (extension) {
            // 图片类型
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            
            // 文档类型
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            
            // 视频类型
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mkv":
                return "video/x-matroska";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            
            // 音频类型
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "flac":
                return "audio/flac";
            case "aac":
                return "audio/aac";
            
            // 压缩包类型
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            case "tar":
                return "application/x-tar";
            case "gz":
                return "application/gzip";
            
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 验证文件类型是否允许
     * 
     * @param fileName 文件名
     * @param allowedTypes 允许的文件类型数组
     * @return 是否允许
     */
    public static boolean isAllowedFileType(String fileName, String[] allowedTypes) {
        if (StrUtil.isBlank(fileName) || allowedTypes == null || allowedTypes.length == 0) {
            return false;
        }
        
        String extension = getFileExtension(fileName);
        if (StrUtil.isBlank(extension)) {
            return false;
        }
        
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建对象路径
     * 
     * @param pathPrefix 路径前缀
     * @param fileName 文件名
     * @return 完整的对象路径
     */
    public static String buildObjectPath(String pathPrefix, String fileName) {
        if (StrUtil.isBlank(pathPrefix)) {
            return fileName;
        }
        
        // 确保路径前缀以斜杠结尾
        if (!pathPrefix.endsWith("/")) {
            pathPrefix += "/";
        }
        
        // 确保路径前缀不以斜杠开头（MinIO不推荐）
        if (pathPrefix.startsWith("/")) {
            pathPrefix = pathPrefix.substring(1);
        }
        
        return pathPrefix + fileName;
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 验证存储桶名称是否合法
     * 
     * @param bucketName 存储桶名称
     * @return 是否合法
     */
    public static boolean isValidBucketName(String bucketName) {
        if (StrUtil.isBlank(bucketName)) {
            return false;
        }
        
        // 存储桶名称规则：
        // 1. 长度在3-63个字符之间
        // 2. 只能包含小写字母、数字和连字符
        // 3. 不能以连字符开头或结尾
        // 4. 不能包含连续的连字符
        
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            return false;
        }
        
        if (bucketName.startsWith("-") || bucketName.endsWith("-")) {
            return false;
        }
        
        if (bucketName.contains("--")) {
            return false;
        }
        
        return bucketName.matches("^[a-z0-9-]+$");
    }
}
