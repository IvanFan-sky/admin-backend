package com.admin.framework.minio.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * MinIO自动配置类
 * 
 * 只提供MinIO客户端和配置属性的自动装配，具体的服务实现由业务模块提供
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AdminMinioProperties.class)
@ConditionalOnProperty(prefix = "admin.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AdminMinioAutoConfiguration {

    private final AdminMinioProperties properties;

    public AdminMinioAutoConfiguration(AdminMinioProperties properties) {
        this.properties = properties;
        log.info("Admin MinIO组件已启用，配置: endpoint={}, defaultBucket={}", 
                properties.getEndpoint(), properties.getDefaultBucket());
    }

    /**
     * MinIO客户端
     * 提供基础的MinIO客户端配置，业务模块可以直接使用
     */
    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() {
        // 自定义OkHttpClient以设置超时
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();

        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .httpClient(httpClient)
                .build();
    }
}
