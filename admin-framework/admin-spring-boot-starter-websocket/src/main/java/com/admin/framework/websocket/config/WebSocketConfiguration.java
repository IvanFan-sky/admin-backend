package com.admin.framework.websocket.config;

import com.admin.framework.websocket.core.handler.WebSocketHandler;
import com.admin.framework.websocket.core.interceptor.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 *
 * @author admin
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(prefix = "admin.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketProperties properties;
    private final WebSocketHandler webSocketHandler;
    private final WebSocketInterceptor webSocketInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器
        registry.addHandler(webSocketHandler, properties.getEndpoint())
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins(properties.getAllowedOrigins());
        
        // 如果启用SockJS，则添加SockJS支持
        if (properties.getSockjsEnabled()) {
            registry.addHandler(webSocketHandler, properties.getEndpoint() + "/sockjs")
                    .addInterceptors(webSocketInterceptor)
                    .setAllowedOrigins(properties.getAllowedOrigins())
                    .withSockJS()
                    .setHeartbeatTime(properties.getHeartbeatInterval())
                    .setDisconnectDelay(5000L)
                    .setSessionCookieNeeded(false)
                    .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js");
        }
    }
}