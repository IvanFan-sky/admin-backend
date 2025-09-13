package com.admin.framework.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * MinIO配置属性
 * <p>
 * 所有属性值从配置文件中动态加载，不设置硬编码默认值
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Validated
@ConfigurationProperties(prefix = "admin.minio")
public class AdminMinioProperties {

    /**
     * 是否启用MinIO功能
     */
    @NotNull(message = "MinIO启用状态不能为空")
    private Boolean enabled;

    /**
     * MinIO服务端点
     */
    @NotBlank(message = "MinIO服务端点不能为空")
    private String endpoint;

    /**
     * 访问密钥
     */
    @NotBlank(message = "MinIO访问密钥不能为空")
    private String accessKey;

    /**
     * 密钥
     */
    @NotBlank(message = "MinIO密钥不能为空")
    private String secretKey;

    /**
     * 默认存储桶名称
     */
    @NotBlank(message = "默认存储桶名称不能为空")
    private String defaultBucket;

    /**
     * 连接超时时间（毫秒）
     */
    @NotNull(message = "连接超时时间不能为空")
    @Positive(message = "连接超时时间必须大于0")
    private Long connectTimeout;

    /**
     * 写超时时间（毫秒）
     */
    @NotNull(message = "写超时时间不能为空")
    @Positive(message = "写超时时间必须大于0")
    private Long writeTimeout;

    /**
     * 读超时时间（毫秒）
     */
    @NotNull(message = "读超时时间不能为空")
    @Positive(message = "读超时时间必须大于0")
    private Long readTimeout;

    /**
     * 上传配置
     */
    @NotNull(message = "上传配置不能为空")
    private Upload upload;

    /**
     * 下载配置
     */
    @NotNull(message = "下载配置不能为空")
    private Download download;

    /**
     * 上传配置
     */
    @Data
    @Validated
    public static class Upload {
        /**
         * 最大文件大小（MB）
         */
        @NotNull(message = "最大文件大小不能为空")
        @Positive(message = "最大文件大小必须大于0")
        private Long maxFileSize;

        /**
         * 允许的文件类型
         */
        @NotNull(message = "允许的文件类型不能为空")
        private String[] allowedTypes;

        /**
         * 文件名生成策略：UUID、DATE、ORIGINAL
         */
        @NotBlank(message = "文件名生成策略不能为空")
        private String fileNameStrategy;

        /**
         * 是否保留原始文件名
         */
        @NotNull(message = "是否保留原始文件名不能为空")
        private Boolean keepOriginalName;

        /**
         * 文件路径前缀
         */
        private String pathPrefix;
    }

    /**
     * 下载配置
     */
    @Data
    @Validated
    public static class Download {
        /**
         * 预签名URL过期时间（秒）
         */
        @NotNull(message = "预签名URL过期时间不能为空")
        @Positive(message = "预签名URL过期时间必须大于0")
        private Integer presignedUrlExpiry;

        /**
         * 是否启用断点续传
         */
        @NotNull(message = "是否启用断点续传不能为空")
        private Boolean enableRangeDownload;

        /**
         * 缓存控制头
         */
        @NotBlank(message = "缓存控制头不能为空")
        private String cacheControl;
    }
}
