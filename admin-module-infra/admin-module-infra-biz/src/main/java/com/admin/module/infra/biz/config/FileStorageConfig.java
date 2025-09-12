package com.admin.module.infra.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储配置
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
@ConfigurationProperties(prefix = "admin.file.storage")
@Data
public class FileStorageConfig {

    /**
     * 默认存储类型
     */
    private String defaultType = "minio";

    /**
     * 文件上传配置
     */
    private Upload upload = new Upload();

    /**
     * 文件下载配置
     */
    private Download download = new Download();

    /**
     * MinIO 存储配置
     */
    private MinioConfig minio = new MinioConfig();

    /**
     * 阿里云 OSS 存储配置
     */
    private OssConfig oss = new OssConfig();

    @Data
    public static class Upload {
        /**
         * 允许的文件类型（MIME类型）
         */
        private String[] allowedTypes = {
                "image/jpeg", "image/png", "image/gif", "image/webp",
                "application/pdf", "text/plain",
                "application/vnd.ms-excel", 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/csv",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };

        /**
         * 单文件最大大小（字节）
         */
        private long maxFileSize = 100 * 1024 * 1024L; // 100MB

        /**
         * 分片上传阈值（字节）
         */
        private long chunkThreshold = 10 * 1024 * 1024L; // 10MB

        /**
         * 分片大小（字节）
         */
        private long chunkSize = 5 * 1024 * 1024L; // 5MB

        /**
         * 允许的文件扩展名
         */
        private String[] allowedExtensions = {
                "jpg", "jpeg", "png", "gif", "webp",
                "pdf", "txt", "xlsx", "xls", "csv",
                "doc", "docx", "zip", "rar"
        };

        /**
         * 是否启用文件去重
         */
        private boolean enableDeduplication = true;

        /**
         * 临时文件保存时间（小时）
         */
        private int tempFileRetentionHours = 24;
    }

    @Data
    public static class Download {
        /**
         * 下载链接有效期（秒）
         */
        private long urlExpirationSeconds = 3600; // 1小时

        /**
         * 是否启用下载统计
         */
        private boolean enableDownloadStats = true;

        /**
         * 断点续传支持
         */
        private boolean enableRangeDownload = true;

        /**
         * 下载速度限制（字节/秒）
         */
        private long speedLimitBytesPerSecond = 0; // 0表示不限速
    }

    @Data
    public static class MinioConfig {
        /**
         * MinIO 服务端点
         */
        private String endpoint = "http://localhost:9000";

        /**
         * 访问密钥
         */
        private String accessKey = "minioadmin";

        /**
         * 秘密密钥
         */
        private String secretKey = "minioadmin";

        /**
         * 默认存储桶
         */
        private String defaultBucket = "admin-files";

        /**
         * 区域
         */
        private String region = "us-east-1";

        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 10000;

        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 30000;

        /**
         * 写入超时时间（毫秒）
         */
        private int writeTimeout = 30000;

        /**
         * 是否启用HTTPS
         */
        private boolean secure = false;

        /**
         * 路径前缀
         */
        private String pathPrefix = "files";

        /**
         * 是否自动创建存储桶
         */
        private boolean autoCreateBucket = true;
    }

    @Data
    public static class OssConfig {
        /**
         * OSS 端点
         */
        private String endpoint;

        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥密码
         */
        private String accessKeySecret;

        /**
         * 默认存储桶
         */
        private String defaultBucket;

        /**
         * 区域
         */
        private String region;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeout = 50000;

        /**
         * Socket 超时时间（毫秒）
         */
        private int socketTimeout = 50000;

        /**
         * 路径前缀
         */
        private String pathPrefix = "files";

        /**
         * 是否启用HTTPS
         */
        private boolean secure = true;
    }
}