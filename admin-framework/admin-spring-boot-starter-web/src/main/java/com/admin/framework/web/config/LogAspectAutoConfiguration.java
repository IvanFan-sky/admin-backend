package com.admin.framework.web.config;

import com.admin.framework.web.aspect.LogAspect;
import com.admin.framework.web.service.AsyncLogProcessor;
import com.admin.module.log.api.service.LogRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 日志切面自动配置类
 * 
 * 自动装配日志切面相关的Bean，支持条件化配置
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
@EnableConfigurationProperties(LogAspectProperties.class)
@ConditionalOnProperty(prefix = "admin.log.aspect", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
@Slf4j
public class LogAspectAutoConfiguration {

    /**
     * 配置日志切面
     */
    @Bean
    @ConditionalOnProperty(prefix = "admin.log.aspect", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LogAspect logAspect(AsyncLogProcessor asyncLogProcessor, LogAspectProperties properties) {
        log.info("初始化日志切面，配置: {}", properties);
        return new LogAspect(asyncLogProcessor, properties);
    }

    /**
     * 配置异步日志处理器
     */
    @Bean
    @ConditionalOnBean(LogRecordService.class)
    @ConditionalOnProperty(prefix = "admin.log.aspect", name = "async-enabled", havingValue = "true", matchIfMissing = true)
    public AsyncLogProcessor asyncLogProcessor(LogRecordService logRecordService) {
        log.info("初始化异步日志处理器");
        return new AsyncLogProcessor(logRecordService);
    }
}