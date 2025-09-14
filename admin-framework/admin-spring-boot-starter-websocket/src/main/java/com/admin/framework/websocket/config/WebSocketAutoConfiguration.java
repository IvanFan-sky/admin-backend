package com.admin.framework.websocket.config;

import com.admin.framework.websocket.core.handler.WebSocketHandler;
import com.admin.framework.websocket.core.interceptor.WebSocketInterceptor;
import com.admin.framework.websocket.core.sender.WebSocketMessageSender;
import com.admin.framework.websocket.core.session.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WebSocket 自动配置类
 *
 * @author admin
 */
@Slf4j
@AutoConfiguration
@EnableScheduling
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(prefix = "admin.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({WebSocketConfiguration.class})
public class WebSocketAutoConfiguration {

    /**
     * WebSocket 会话管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketSessionManager webSocketSessionManager() {
        log.info("初始化 WebSocket 会话管理器");
        return new WebSocketSessionManager();
    }

    /**
     * WebSocket 消息发送器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager sessionManager,
                                                          ObjectMapper objectMapper) {
        log.info("初始化 WebSocket 消息发送器");
        return new WebSocketMessageSender(sessionManager, objectMapper);
    }

    /**
     * WebSocket 处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager,
                                             WebSocketMessageSender messageSender,
                                             ObjectMapper objectMapper) {
        log.info("初始化 WebSocket 处理器");
        return new WebSocketHandler(sessionManager, messageSender, objectMapper);
    }

    /**
     * WebSocket 拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketInterceptor webSocketInterceptor() {
        log.info("初始化 WebSocket 拦截器");
        return new WebSocketInterceptor();
    }

    /**
     * ObjectMapper（如果不存在）
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        log.info("初始化 ObjectMapper");
        return new ObjectMapper();
    }
}