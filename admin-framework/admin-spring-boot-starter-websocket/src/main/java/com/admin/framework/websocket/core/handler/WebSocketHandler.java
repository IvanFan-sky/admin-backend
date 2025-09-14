package com.admin.framework.websocket.core.handler;

import com.admin.framework.websocket.core.message.WebSocketMessage;
import com.admin.framework.websocket.core.sender.WebSocketMessageSender;
import com.admin.framework.websocket.core.session.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 处理器
 *
 * @author admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final WebSocketMessageSender messageSender;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接建立，会话ID: {}", session.getId());
        
        // 从URL参数中获取用户ID
        Long userId = getUserIdFromSession(session);
        if (userId == null) {
            log.warn("无法获取用户ID，关闭连接，会话ID: {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("Missing user ID"));
            return;
        }

        // 添加会话到管理器
        sessionManager.addSession(userId, session);
        
        // 发送连接成功消息
        WebSocketMessage welcomeMessage = WebSocketMessage.builder()
                .type("connection_established")
                .content("WebSocket连接成功")
                .build();
        messageSender.sendToUser(userId, welcomeMessage);
        
        // 发送在线用户数更新
        messageSender.sendOnlineUserCountUpdate();
        
        log.info("用户 {} 连接成功，当前在线用户数: {}", userId, sessionManager.getOnlineUserCount());
    }

    @Override
    public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);
        } else if (message instanceof BinaryMessage) {
            handleBinaryMessage(session, (BinaryMessage) message);
        } else if (message instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) message);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误，会话ID: {}", session.getId(), exception);
        
        // 移除会话
        sessionManager.removeSession(session);
        
        // 关闭会话
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接关闭，会话ID: {}，关闭状态: {}", session.getId(), closeStatus);
        
        Long userId = sessionManager.getSessionUserId(session);
        
        // 移除会话
        sessionManager.removeSession(session);
        
        // 发送在线用户数更新
        messageSender.sendOnlineUserCountUpdate();
        
        if (userId != null) {
            log.info("用户 {} 断开连接，当前在线用户数: {}", userId, sessionManager.getOnlineUserCount());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 处理文本消息
     *
     * @param session WebSocket会话
     * @param message 文本消息
     */
    private void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            log.debug("收到文本消息，会话ID: {}，内容: {}", session.getId(), payload);
            
            // 解析消息
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            // 处理不同类型的消息
            handleWebSocketMessage(session, wsMessage);
            
        } catch (Exception e) {
            log.error("处理文本消息失败，会话ID: {}", session.getId(), e);
            sendErrorMessage(session, "消息格式错误");
        }
    }

    /**
     * 处理二进制消息
     *
     * @param session WebSocket会话
     * @param message 二进制消息
     */
    private void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.debug("收到二进制消息，会话ID: {}，大小: {} bytes", session.getId(), message.getPayloadLength());
        // 暂不处理二进制消息
    }

    /**
     * 处理Pong消息
     *
     * @param session WebSocket会话
     * @param message Pong消息
     */
    private void handlePongMessage(WebSocketSession session, PongMessage message) {
        log.debug("收到Pong消息，会话ID: {}", session.getId());
        // 心跳响应，更新会话活跃时间
    }

    /**
     * 处理WebSocket消息
     *
     * @param session WebSocket会话
     * @param message WebSocket消息
     */
    private void handleWebSocketMessage(WebSocketSession session, WebSocketMessage message) {
        String type = message.getType();
        Long userId = sessionManager.getSessionUserId(session);
        
        switch (type) {
            case WebSocketMessage.Type.HEARTBEAT:
                handleHeartbeat(session, message);
                break;
            case WebSocketMessage.Type.ACK:
                handleAck(session, message);
                break;
            default:
                log.debug("收到未知类型消息: {}，用户: {}", type, userId);
                break;
        }
    }

    /**
     * 处理心跳消息
     *
     * @param session WebSocket会话
     * @param message 心跳消息
     */
    private void handleHeartbeat(WebSocketSession session, WebSocketMessage message) {
        log.debug("收到心跳消息，会话ID: {}", session.getId());
        
        // 回复心跳
        WebSocketMessage pongMessage = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.HEARTBEAT)
                .content("pong")
                .build();
        
        Long userId = sessionManager.getSessionUserId(session);
        if (userId != null) {
            messageSender.sendToUser(userId, pongMessage);
        }
    }

    /**
     * 处理确认消息
     *
     * @param session WebSocket会话
     * @param message 确认消息
     */
    private void handleAck(WebSocketSession session, WebSocketMessage message) {
        log.debug("收到确认消息，会话ID: {}，消息ID: {}", session.getId(), message.getMessageId());
        // 可以在这里处理消息确认逻辑，比如更新消息状态
    }

    /**
     * 发送错误消息
     *
     * @param session WebSocket会话
     * @param error 错误信息
     */
    private void sendErrorMessage(WebSocketSession session, String error) {
        try {
            WebSocketMessage errorMessage = WebSocketMessage.builder()
                    .type("error")
                    .content(error)
                    .build();
            
            String messageText = objectMapper.writeValueAsString(errorMessage);
            session.sendMessage(new TextMessage(messageText));
        } catch (Exception e) {
            log.error("发送错误消息失败，会话ID: {}", session.getId(), e);
        }
    }

    /**
     * 从会话中获取用户ID
     *
     * @param session WebSocket会话
     * @return 用户ID
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri != null) {
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
            }
            
            // 尝试从会话属性中获取
            Object userIdAttr = session.getAttributes().get("userId");
            if (userIdAttr != null) {
                return Long.parseLong(userIdAttr.toString());
            }
            
        } catch (Exception e) {
            log.error("解析用户ID失败，会话ID: {}", session.getId(), e);
        }
        
        return null;
    }
}