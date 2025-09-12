package com.admin.framework.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO配置属性
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.minio")
public class AdminMinioProperties {

    /**
     * 是否启用MinIO功能
     */
    private Boolean enabled = true;

    /**
     * MinIO服务端点
     */
    private String endpoint = "http://localhost:9000";

    /**
     * 访问密钥
     */
    private String accessKey = "minioadmin";

    /**
     * 密钥
     */
    private String secretKey = "minioadmin";

    /**
     * 默认存储桶名称
     */
    private String defaultBucket = "admin";

    /**
     * 连接超时时间（毫秒）
     */
    private Long connectTimeout = 10000L;

    /**
     * 写超时时间（毫秒）
     */
    private Long writeTimeout = 60000L;

    /**
     * 读超时时间（毫秒）
     */
    private Long readTimeout = 10000L;

    /**
     * 上传配置
     */
    private Upload upload = new Upload();

    /**
     * 下载配置
     */
    private Download download = new Download();

    /**
     * 上传配置
     */
    @Data
    public static class Upload {
        /**
         * 最大文件大小（MB）
         */
        private Long maxFileSize = 100L;

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", // 图片
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", // 文档
            "mp4", "avi", "mkv", "mov", "wmv", // 视频
            "mp3", "wav", "flac", "aac", // 音频
            "zip", "rar", "7z", "tar", "gz" // 压缩包
        };

        /**
         * 文件名生成策略：UUID、DATE、ORIGINAL
         */
        private String fileNameStrategy = "UUID";

        /**
         * 是否保留原始文件名
         */
        private Boolean keepOriginalName = false;

        /**
         * 文件路径前缀
         */
        private String pathPrefix = "";
    }

    /**
     * 下载配置
     */
    @Data
    public static class Download {
        /**
         * 预签名URL过期时间（秒）
         */
        private Integer presignedUrlExpiry = 3600;

        /**
         * 是否启用断点续传
         */
        private Boolean enableRangeDownload = true;

        /**
         * 缓存控制头
         */
        private String cacheControl = "max-age=3600";
    }
}
