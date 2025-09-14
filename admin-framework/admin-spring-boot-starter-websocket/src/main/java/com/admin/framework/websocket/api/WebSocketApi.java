package com.admin.framework.websocket.api;

import com.admin.framework.websocket.core.message.WebSocketMessage;
import com.admin.framework.websocket.core.sender.WebSocketMessageSender;
import com.admin.framework.websocket.core.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * WebSocket API 接口
 * 提供给外部模块使用的WebSocket功能接口
 *
 * @author admin
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "admin.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketApi {

    private final WebSocketMessageSender messageSender;
    private final WebSocketSessionManager sessionManager;

    // ==================== 消息发送相关 ====================

    /**
     * 发送消息给指定用户
     *
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendToUser(Long userId, WebSocketMessage message) {
        messageSender.sendToUser(userId, message);
    }

    /**
     * 发送消息给多个用户
     *
     * @param userIds 用户ID集合
     * @param message 消息内容
     */
    public void sendToUsers(Collection<Long> userIds, WebSocketMessage message) {
        messageSender.sendToUsers(userIds, message);
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public void broadcast(WebSocketMessage message) {
        messageSender.broadcast(message);
    }

    /**
     * 发送系统通知
     *
     * @param userId 用户ID（为空则广播）
     * @param title 通知标题
     * @param content 通知内容
     */
    public void sendSystemNotification(Long userId, String title, Object content) {
        messageSender.sendSystemNotification(userId, title, content);
    }

    /**
     * 发送系统公告
     *
     * @param title 公告标题
     * @param content 公告内容
     */
    public void sendSystemAnnouncement(String title, Object content) {
        messageSender.sendSystemAnnouncement(title, content);
    }

    /**
     * 发送站内信通知
     *
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     */
    public void sendInternalMessage(Long userId, String title, Object content) {
        messageSender.sendInternalMessage(userId, title, content);
    }

    /**
     * 发送在线用户数更新
     */
    public void sendOnlineUserCountUpdate() {
        messageSender.sendOnlineUserCountUpdate();
    }

    /**
     * 发送用户上线通知
     *
     * @param userId 用户ID
     * @param userName 用户名称
     */
    public void sendUserOnlineNotification(Long userId, String userName) {
        messageSender.sendUserOnlineNotification(userId, userName);
    }

    /**
     * 发送用户下线通知
     *
     * @param userId 用户ID
     * @param userName 用户名称
     */
    public void sendUserOfflineNotification(Long userId, String userName) {
        messageSender.sendUserOfflineNotification(userId, userName);
    }

    // ==================== 会话管理相关 ====================

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        return sessionManager.isUserOnline(userId);
    }

    /**
     * 获取在线用户ID列表
     *
     * @return 用户ID集合
     */
    public Set<Long> getOnlineUserIds() {
        return sessionManager.getOnlineUserIds();
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return sessionManager.getOnlineUserCount();
    }

    /**
     * 获取活跃会话数量
     *
     * @return 活跃会话数量
     */
    public int getActiveSessionCount() {
        return sessionManager.getActiveSessionCount();
    }

    /**
     * 获取会话统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getSessionStats() {
        return sessionManager.getSessionStats();
    }

    /**
     * 清理无效会话
     */
    public void cleanInvalidSessions() {
        sessionManager.cleanInvalidSessions();
    }

    // ==================== 便捷方法 ====================

    /**
     * 发送简单文本消息给用户
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param title 消息标题
     * @param content 消息内容
     */
    public void sendSimpleMessage(Long userId, String type, String title, String content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.MEDIUM)
                .build();
        sendToUser(userId, message);
    }

    /**
     * 广播简单文本消息
     *
     * @param type 消息类型
     * @param title 消息标题
     * @param content 消息内容
     */
    public void broadcastSimpleMessage(String type, String title, String content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.MEDIUM)
                .build();
        broadcast(message);
    }

    /**
     * 发送高优先级消息给用户
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param title 消息标题
     * @param content 消息内容
     */
    public void sendUrgentMessage(Long userId, String type, String title, Object content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.URGENT)
                .needAck(true)
                .build();
        sendToUser(userId, message);
    }

    /**
     * 广播高优先级消息
     *
     * @param type 消息类型
     * @param title 消息标题
     * @param content 消息内容
     */
    public void broadcastUrgentMessage(String type, String title, Object content) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .title(title)
                .content(content)
                .priority(WebSocketMessage.Priority.URGENT)
                .needAck(true)
                .build();
        broadcast(message);
    }
}