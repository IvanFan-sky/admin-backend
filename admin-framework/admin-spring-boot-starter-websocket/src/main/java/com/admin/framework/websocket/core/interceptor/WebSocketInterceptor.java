package com.admin.framework.websocket.core.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * @author admin
 */
@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        log.debug("WebSocket握手前处理，URI: {}", request.getURI());
        
        try {
            // 获取用户ID
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                log.warn("WebSocket握手失败：缺少用户ID参数");
                return false;
            }
            
            // 将用户ID存储到会话属性中
            attributes.put("userId", userId);
            
            // 获取用户token（可选）
            String token = getTokenFromRequest(request);
            if (token != null) {
                attributes.put("token", token);
            }
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            attributes.put("clientIp", clientIp);
            
            // 获取User-Agent
            String userAgent = getUserAgent(request);
            attributes.put("userAgent", userAgent);
            
            log.info("WebSocket握手成功，用户ID: {}，客户端IP: {}", userId, clientIp);
            return true;
            
        } catch (Exception e) {
            log.error("WebSocket握手处理异常", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        
        if (exception != null) {
            log.error("WebSocket握手后处理异常，URI: {}", request.getURI(), exception);
        } else {
            log.debug("WebSocket握手后处理完成，URI: {}", request.getURI());
        }
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserIdFromRequest(ServerHttpRequest request) {
        try {
            // 1. 从URL参数中获取
            URI uri = request.getURI();
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                        return Long.parseLong(keyValue[1]);
                    }
                }
            }
            
            // 2. 从请求头中获取
            String userIdHeader = request.getHeaders().getFirst("X-User-Id");
            if (userIdHeader != null) {
                return Long.parseLong(userIdHeader);
            }
            
        } catch (Exception e) {
            log.error("解析用户ID失败", e);
        }
        
        return null;
    }

    /**
     * 从请求中获取token
     *
     * @param request HTTP请求
     * @return token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        try {
            // 1. 从URL参数中获取
            URI uri = request.getURI();
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                        return keyValue[1];
                    }
                }
            }
            
            // 2. 从请求头中获取
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            
            // 3. 从X-Token请求头中获取
            String tokenHeader = request.getHeaders().getFirst("X-Token");
            if (tokenHeader != null) {
                return tokenHeader;
            }
            
        } catch (Exception e) {
            log.error("解析token失败", e);
        }
        
        return null;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        try {
            // 1. X-Forwarded-For
            String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            // 2. X-Real-IP
            String xRealIp = request.getHeaders().getFirst("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                return xRealIp;
            }
            
            // 3. Proxy-Client-IP
            String proxyClientIp = request.getHeaders().getFirst("Proxy-Client-IP");
            if (proxyClientIp != null && !proxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(proxyClientIp)) {
                return proxyClientIp;
            }
            
            // 4. WL-Proxy-Client-IP
            String wlProxyClientIp = request.getHeaders().getFirst("WL-Proxy-Client-IP");
            if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
                return wlProxyClientIp;
            }
            
            // 5. 从RemoteAddress获取
            if (request.getRemoteAddress() != null) {
                return request.getRemoteAddress().getAddress().getHostAddress();
            }
            
        } catch (Exception e) {
            log.error("获取客户端IP失败", e);
        }
        
        return "unknown";
    }

    /**
     * 获取User-Agent
     *
     * @param request HTTP请求
     * @return User-Agent
     */
    private String getUserAgent(ServerHttpRequest request) {
        try {
            return request.getHeaders().getFirst("User-Agent");
        } catch (Exception e) {
            log.error("获取User-Agent失败", e);
        }
        
        return "unknown";
    }
}