package com.admin.framework.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 日志切面配置属性
 * 
 * 支持外化配置，可在不同环境中灵活调整日志记录行为
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.log.aspect")
@Validated
public class LogAspectProperties {

    /**
     * 是否启用日志切面
     */
    @NotNull
    private Boolean enabled = true;

    /**
     * 是否启用异步处理
     */
    @NotNull
    private Boolean asyncEnabled = true;

    /**
     * 最大参数长度
     */
    @Min(100)
    @Max(10000)
    private Integer maxParamLength = 2000;

    /**
     * 最大返回结果长度
     */
    @Min(100)
    @Max(10000)
    private Integer maxResultLength = 4000;

    /**
     * 最大错误信息长度
     */
    @Min(100)
    @Max(2000)
    private Integer maxErrorLength = 1000;

    /**
     * 排除路径模式
     */
    private List<String> excludePatterns = List.of(
        "*/health/**",
        "*/metrics/**",
        "*/actuator/**"
    );

    /**
     * 异步配置
     */
    private AsyncConfig async = new AsyncConfig();

    /**
     * 重试配置
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * 告警配置
     */
    private AlertConfig alert = new AlertConfig();

    /**
     * 异步配置
     */
    @Data
    public static class AsyncConfig {
        /**
         * 核心线程池大小
         */
        @Min(1)
        @Max(20)
        private Integer corePoolSize = 2;

        /**
         * 最大线程池大小
         */
        @Min(1)
        @Max(50)
        private Integer maxPoolSize = 8;

        /**
         * 队列容量
         */
        @Min(10)
        @Max(10000)
        private Integer queueCapacity = 500;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "LogAspect-";

        /**
         * 线程存活时间(秒)
         */
        @Min(30)
        @Max(300)
        private Integer keepAliveSeconds = 60;
    }

    /**
     * 重试配置
     */
    @Data
    public static class RetryConfig {
        /**
         * 最大重试次数
         */
        @Min(1)
        @Max(10)
        private Integer maxAttempts = 3;

        /**
         * 重试延迟(毫秒)
         */
        @Min(100)
        @Max(10000)
        private Long backoffDelay = 1000L;

        /**
         * 重试延迟倍数
         */
        @Min(1)
        @Max(5)
        private Double multiplier = 2.0;
    }

    /**
     * 告警配置
     */
    @Data
    public static class AlertConfig {
        /**
         * 是否启用告警
         */
        private Boolean enabled = false;

        /**
         * 失败阈值
         */
        @Min(1)
        @Max(100)
        private Integer failureThreshold = 10;

        /**
         * 时间窗口(秒)
         */
        @Min(60)
        @Max(3600)
        private Integer timeWindow = 300;

        /**
         * 告警接收人
         */
        private List<String> recipients;
    }
}