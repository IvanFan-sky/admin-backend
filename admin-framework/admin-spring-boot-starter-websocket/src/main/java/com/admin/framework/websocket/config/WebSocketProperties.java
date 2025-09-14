package com.admin.framework.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * WebSocket 配置属性
 *
 * @author admin
 */
@Data
@ConfigurationProperties(prefix = "admin.websocket")
public class WebSocketProperties {

    /**
     * 是否启用 WebSocket
     */
    private Boolean enabled = true;

    /**
     * WebSocket 端点路径
     */
    private String endpoint = "/websocket";

    /**
     * 允许的跨域来源
     */
    private String[] allowedOrigins = {"*"};

    /**
     * 是否启用 SockJS 支持
     */
    private Boolean sockjsEnabled = true;

    /**
     * 心跳间隔（毫秒）
     */
    private Long heartbeatInterval = 25000L;

    /**
     * 消息缓冲区大小
     */
    private Integer messageBufferSize = 8192;

    /**
     * 最大会话空闲时间（毫秒）
     */
    private Long maxSessionIdleTimeout = 60000L;

    /**
     * 是否启用用户认证
     */
    private Boolean authEnabled = true;

    /**
     * Redis 配置
     */
    private Redis redis = new Redis();

    @Data
    public static class Redis {
        /**
         * 是否启用 Redis 广播
         */
        private Boolean enabled = false;

        /**
         * Redis 频道前缀
         */
        private String channelPrefix = "websocket:";

        /**
         * 消息序列化方式
         */
        private String serializer = "json";
    }
}