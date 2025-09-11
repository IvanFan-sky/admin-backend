package com.admin.framework.web.config;

import com.admin.common.log.StructuredLogger;
import com.admin.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 日志增强配置类
 * 
 * 提供应用启动、关闭等生命周期事件的日志记录
 * 以及其他日志增强功能的配置
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Configuration
public class LoggingEnhanceConfig {

    /**
     * 应用启动完成事件监听器
     */
    @Component
    @Slf4j
    public static class ApplicationLifecycleLogger {

        /**
         * 应用启动完成后记录日志
         */
        @EventListener(ApplicationReadyEvent.class)
        @Async("asyncLogExecutor")
        public void onApplicationReady(ApplicationReadyEvent event) {
            try {
                // 初始化链路追踪
                String traceId = TraceContext.initTrace();
                
                String appName = event.getApplicationContext().getId();
                String[] activeProfiles = event.getApplicationContext().getEnvironment().getActiveProfiles();
                
                // 记录结构化日志
                StructuredLogger.logBusiness(
                    "应用启动", 
                    "系统管理", 
                    "成功",
                    String.format("应用: %s, 环境: %s", appName, String.join(",", activeProfiles))
                );
                
                log.info("=== 应用启动完成 ===");
                log.info("应用名称: {}", appName);
                log.info("激活环境: {}", String.join(",", activeProfiles));
                log.info("链路追踪ID: {}", traceId);
                log.info("启动时间: {}", java.time.LocalDateTime.now());
                log.info("===================");
                
            } catch (Exception e) {
                log.error("记录应用启动日志失败", e);
            } finally {
                TraceContext.clear();
            }
        }
    }

    /**
     * JVM关闭钩子 - 记录应用关闭日志
     */
    @Component
    public static class ShutdownHookLogger {
        
        public ShutdownHookLogger() {
            // 注册JVM关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(this::logApplicationShutdown));
        }
        
        private void logApplicationShutdown() {
            try {
                // 初始化链路追踪
                String traceId = TraceContext.initTrace();
                
                // 记录结构化日志
                StructuredLogger.logBusiness(
                    "应用关闭", 
                    "系统管理", 
                    "成功",
                    "应用正常关闭"
                );
                
                log.info("=== 应用正在关闭 ===");
                log.info("关闭时间: {}", java.time.LocalDateTime.now());
                log.info("链路追踪ID: {}", traceId);
                log.info("===================");
                
            } catch (Exception e) {
                log.error("记录应用关闭日志失败", e);
            } finally {
                TraceContext.clear();
            }
        }
    }
}
