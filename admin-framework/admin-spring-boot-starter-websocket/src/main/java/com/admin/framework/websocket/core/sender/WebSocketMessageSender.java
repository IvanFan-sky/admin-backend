package com.admin.framework.websocket.core.sender;

import com.admin.framework.websocket.core.message.WebSocketMessage;
import com.admin.framework.websocket.core.session.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * WebSocket 消息发送服务
 *
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageSender {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    /**
     * 发送消息给指定用户
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendToUser(Long userId, WebSocketMessage message) {
        if (userId == null || message == null) {
            return;
        }

        // 设置接收者信息
        message.setReceiverId(userId);
        message.setReceiverType(WebSocketMessage.ReceiverType.USER);
        
        // 补充消息信息
        fillMessageInfo(message);

        Set<WebSocketSession> sessions = sessionManager.getUserSessions(userId);
        if (sessions.isEmpty()) {
            log.debug("用户 {} 不在线，消息发送失败", userId);
            return;
        }

        sendToSessions(sessions, message);
    }

    /**
     * 发送消息给多个用户
     *
     * @param userIds 用户ID集合
     * @param message 消息内容
     */
    public void sendToUsers(Collection<Long> userIds, WebSocketMessage message) {
        if (userIds == null || userIds.isEmpty() || message == null) {
            return;
        }

        // 设置接收者信息
        message.setReceiverType(WebSocketMessage.ReceiverType.USER);
        
        // 补充消息信息
        fillMessageInfo(message);

        for (Long userId : userIds) {
            Set<WebSocketSession> sessions = sessionManager.getUserSessions(userId);
            if (!sessions.isEmpty()) {
                // 为每个用户创建独立的消息副本
                WebSocketMessage userMessage = cloneMessage(message);
                userMessage.setReceiverId(userId);
                sendToSessions(sessions, userMessage);
            }
        }
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public void broadcast(WebSocketMessage message) {
        if (message == null) {
            return;
        }

        // 设置接收者信息
        message.setReceiverType(WebSocketMessage.ReceiverType.ALL);
        
        // 补充消息信息
        fillMessageInfo(message);

        Set<WebSocketSession> allSessions = sessionManager.getAllSessions();
        if (allSessions.isEmpty()) {
            log.debug("没有在线用户，广播消息取消");
            return;
        }

        sendToSessions(allSessions, message);
    }

    /**
     * 发送系统通知
     *
     * @param userId 用户ID（为空则广播）
     * @param title 通知标题
     * @param content 通知内容
     */
    public void sendSystemNotification(Long userId, String title, Object content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.SYSTEM_NOTIFICATION)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.MEDIUM)
                .build();

        if (userId != null) {
            sendToUser(userId, message);
        } else {
            broadcast(message);
        }
    }

    /**
     * 发送系统公告
     *
     * @param title 公告标题
     * @param content 公告内容
     */
    public void sendSystemAnnouncement(String title, Object content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.SYSTEM_ANNOUNCEMENT)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.HIGH)
                .build();

        broadcast(message);
    }

    /**
     * 发送站内信通知
     *
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     */
    public void sendInternalMessage(Long userId, String title, Object content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.INTERNAL_MESSAGE)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.MEDIUM)
                .needAck(true)
                .build();

        sendToUser(userId, message);
    }

    /**
     * 发送在线用户数更新
     */
    public void sendOnlineUserCountUpdate() {
        int onlineCount = sessionManager.getOnlineUserCount();
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.ONLINE_USER_COUNT)
                .content(onlineCount)
                .priority(WebSocketMessage.Priority.LOW)
                .build();

        broadcast(message);
    }

    /**
     * 发送用户上线通知
     *
     * @param userId 用户ID
     * @param userName 用户名称
     */
    public void sendUserOnlineNotification(Long userId, String userName) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.USER_ONLINE)
                .content(Map.of("userId", userId, "userName", userName))
                .priority(WebSocketMessage.Priority.LOW)
                .build();

        broadcast(message);
    }

    /**
     * 发送用户下线通知
     *
     * @param userId 用户ID
     * @param userName 用户名称
     */
    public void sendUserOfflineNotification(Long userId, String userName) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.USER_OFFLINE)
                .content(Map.of("userId", userId, "userName", userName))
                .priority(WebSocketMessage.Priority.LOW)
                .build();

        broadcast(message);
    }

    /**
     * 发送心跳消息
     *
     * @param session WebSocket会话
     */
    public void sendHeartbeat(WebSocketSession session) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.Type.HEARTBEAT)
                .content("ping")
                .priority(WebSocketMessage.Priority.LOW)
                .build();

        fillMessageInfo(message);
        sendToSession(session, message);
    }

    /**
     * 发送消息到指定会话集合
     *
     * @param sessions 会话集合
     * @param message 消息内容
     */
    private void sendToSessions(Collection<WebSocketSession> sessions, WebSocketMessage message) {
        String messageText = convertToJson(message);
        if (messageText == null) {
            return;
        }

        TextMessage textMessage = new TextMessage(messageText);
        for (WebSocketSession session : sessions) {
            sendToSession(session, textMessage);
        }
    }

    /**
     * 发送消息到指定会话
     *
     * @param session 会话
     * @param message 消息内容
     */
    private void sendToSession(WebSocketSession session, WebSocketMessage message) {
        String messageText = convertToJson(message);
        if (messageText != null) {
            sendToSession(session, new TextMessage(messageText));
        }
    }

    /**
     * 发送消息到指定会话
     *
     * @param session 会话
     * @param message 文本消息
     */
    private void sendToSession(WebSocketSession session, TextMessage message) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            synchronized (session) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            log.error("发送WebSocket消息失败，会话ID: {}", session.getId(), e);
            // 移除无效会话
            sessionManager.removeSession(session);
        }
    }

    /**
     * 补充消息信息
     *
     * @param message 消息对象
     */
    private void fillMessageInfo(WebSocketMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        if (message.getCreateTime() == null) {
            message.setCreateTime(LocalDateTime.now());
        }
        if (message.getPriority() == null) {
            message.setPriority(WebSocketMessage.Priority.MEDIUM);
        }
    }

    /**
     * 克隆消息对象
     *
     * @param message 原消息
     * @return 克隆的消息
     */
    private WebSocketMessage cloneMessage(WebSocketMessage message) {
        return WebSocketMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type(message.getType())
                .title(message.getTitle())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .receiverType(message.getReceiverType())
                .priority(message.getPriority())
                .needAck(message.getNeedAck())
                .extra(message.getExtra())
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 将消息对象转换为JSON字符串
     *
     * @param message 消息对象
     * @return JSON字符串
     */
    private String convertToJson(WebSocketMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("消息序列化失败", e);
            return null;
        }
    }
}