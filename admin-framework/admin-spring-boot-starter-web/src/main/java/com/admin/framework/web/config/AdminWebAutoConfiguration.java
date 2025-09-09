package com.admin.framework.web.config;

import com.admin.framework.web.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Web层自动配置类
 * 
 * 提供Web相关的基础功能配置
 * 包括全局异常处理器等核心组件
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@AutoConfiguration
public class AdminWebAutoConfiguration {

    /**
     * 配置全局异常处理器
     * 
     * 统一处理系统中的各类异常
     * 提供友好的错误响应格式
     *
     * @return 全局异常处理器实例
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}