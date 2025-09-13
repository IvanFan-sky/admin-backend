package com.admin.framework.minio.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * MinIO自动配置类
 * <p>
 * 只提供MinIO客户端和配置属性的自动装配，具体的服务实现由业务模块提供
 * 所有配置参数从配置文件中动态加载，启动时进行严格验证
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AdminMinioProperties.class)
@ConditionalOnProperty(prefix = "admin.minio", name = "enabled", havingValue = "true")
public class AdminMinioAutoConfiguration {

    private final AdminMinioProperties properties;

    public AdminMinioAutoConfiguration(AdminMinioProperties properties) {
        this.properties = properties;
        validateProperties();
        logConfiguration();
    }

    /**
     * 验证配置属性
     */
    private void validateProperties() {
        Assert.notNull(properties.getEnabled(), "MinIO启用状态不能为空");
        Assert.hasText(properties.getEndpoint(), "MinIO服务端点不能为空");
        Assert.hasText(properties.getAccessKey(), "MinIO访问密钥不能为空");
        Assert.hasText(properties.getSecretKey(), "MinIO密钥不能为空");
        Assert.hasText(properties.getDefaultBucket(), "默认存储桶名称不能为空");
        
        Assert.notNull(properties.getConnectTimeout(), "连接超时时间不能为空");
        Assert.isTrue(properties.getConnectTimeout() > 0, "连接超时时间必须大于0");
        
        Assert.notNull(properties.getWriteTimeout(), "写超时时间不能为空");
        Assert.isTrue(properties.getWriteTimeout() > 0, "写超时时间必须大于0");
        
        Assert.notNull(properties.getReadTimeout(), "读超时时间不能为空");
        Assert.isTrue(properties.getReadTimeout() > 0, "读超时时间必须大于0");

        // 验证上传配置
        AdminMinioProperties.Upload upload = properties.getUpload();
        if (upload != null) {
            Assert.notNull(upload.getMaxFileSize(), "最大文件大小不能为空");
            Assert.isTrue(upload.getMaxFileSize() > 0, "最大文件大小必须大于0");
            Assert.notNull(upload.getAllowedTypes(), "允许的文件类型不能为空");
            Assert.isTrue(upload.getAllowedTypes().length > 0, "允许的文件类型不能为空数组");
            Assert.hasText(upload.getFileNameStrategy(), "文件名生成策略不能为空");
            Assert.notNull(upload.getKeepOriginalName(), "是否保留原始文件名不能为空");
        }

        // 验证下载配置
        AdminMinioProperties.Download download = properties.getDownload();
        if (download != null) {
            Assert.notNull(download.getPresignedUrlExpiry(), "预签名URL过期时间不能为空");
            Assert.isTrue(download.getPresignedUrlExpiry() > 0, "预签名URL过期时间必须大于0");
            Assert.notNull(download.getEnableRangeDownload(), "是否启用断点续传不能为空");
            Assert.hasText(download.getCacheControl(), "缓存控制头不能为空");
        }
    }

    /**
     * 记录配置信息
     */
    private void logConfiguration() {
        log.info("Admin MinIO组件已启用");
        log.info("MinIO配置信息:");
        log.info("  - 服务端点: {}", properties.getEndpoint());
        log.info("  - 访问密钥: {}", maskSensitiveInfo(properties.getAccessKey()));
        log.info("  - 默认存储桶: {}", properties.getDefaultBucket());
        log.info("  - 连接超时: {}ms", properties.getConnectTimeout());
        log.info("  - 写超时: {}ms", properties.getWriteTimeout());
        log.info("  - 读超时: {}ms", properties.getReadTimeout());
        
        if (properties.getUpload() != null) {
            AdminMinioProperties.Upload upload = properties.getUpload();
            log.info("  - 最大文件大小: {}MB", upload.getMaxFileSize());
            log.info("  - 文件名策略: {}", upload.getFileNameStrategy());
            log.info("  - 保留原始文件名: {}", upload.getKeepOriginalName());
            log.info("  - 路径前缀: {}", StringUtils.hasText(upload.getPathPrefix()) ? upload.getPathPrefix() : "无");
        }
        
        if (properties.getDownload() != null) {
            AdminMinioProperties.Download download = properties.getDownload();
            log.info("  - 预签名URL过期时间: {}秒", download.getPresignedUrlExpiry());
            log.info("  - 启用断点续传: {}", download.getEnableRangeDownload());
        }
    }

    /**
     * 脱敏敏感信息
     */
    private String maskSensitiveInfo(String info) {
        if (!StringUtils.hasText(info) || info.length() <= 4) {
            return "****";
        }
        return info.substring(0, 2) + "****" + info.substring(info.length() - 2);
    }

    /**
     * MinIO客户端
     * <p>
     * 提供基础的MinIO客户端配置，业务模块可以直接使用
     * 客户端配置完全基于配置文件中的参数
     */
    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() {
        try {
            // 自定义OkHttpClient以设置超时
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(properties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();

            MinioClient client = MinioClient.builder()
                    .endpoint(properties.getEndpoint())
                    .credentials(properties.getAccessKey(), properties.getSecretKey())
                    .httpClient(httpClient)
                    .build();
            
            log.info("MinIO客户端创建成功，端点: {}", properties.getEndpoint());
            return client;
        } catch (Exception e) {
            log.error("MinIO客户端创建失败: {}", e.getMessage(), e);
            throw new IllegalStateException("无法创建MinIO客户端，请检查配置参数", e);
        }
    }
}
