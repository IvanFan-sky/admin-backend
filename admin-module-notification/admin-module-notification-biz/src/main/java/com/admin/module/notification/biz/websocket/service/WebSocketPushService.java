package com.admin.module.notification.biz.websocket.service;

import com.admin.framework.websocket.api.WebSocketApi;
import com.admin.framework.websocket.core.message.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebSocket推送服务
 * 
 * 提供统一的WebSocket消息推送接口
 * 支持单播、多播、广播等多种推送方式
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPushService {

    private final WebSocketApi webSocketApi;

    /**
     * 推送系统通知给指定用户
     *
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param data 扩展数据
     * @return 是否推送成功
     */
    public boolean pushSystemNotification(Long userId, String title, String content, Map<String, Object> data) {
        try {
            Object finalContent = (data == null || data.isEmpty())
                    ? content
                    : Map.of("text", content, "extra", data);
            boolean success = (userId == null) ? webSocketApi.getOnlineUserCount() > 0 : webSocketApi.isUserOnline(userId);
            webSocketApi.sendSystemNotification(userId, title, finalContent);
            log.debug("推送系统通知：userId={}, title={}, success={}", userId, title, success);
            return success;
        } catch (Exception e) {
            log.error("推送系统通知失败：userId={}, title={}", userId, title, e);
            return false;
        }
    }

    /**
     * 推送用户通知给指定用户
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param title 通知标题
     * @param content 通知内容
     * @param data 扩展数据
     * @return 是否推送成功
     */
    public boolean pushUserNotification(Long senderId, Long receiverId, String title, String content, Map<String, Object> data) {
        try {
            boolean success = webSocketApi.isUserOnline(receiverId);
            Map<String, Object> body = (data == null || data.isEmpty())
                    ? Map.of("text", content, "senderId", senderId)
                    : new java.util.HashMap<>(data);
            if (!(data == null || data.isEmpty())) {
                body.put("senderId", senderId);
                body.putIfAbsent("text", content);
            }
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("user_notification")
                    .title(title)
                    .content(body)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .build();
            webSocketApi.sendToUser(receiverId, message);
            log.debug("推送用户通知：senderId={}, receiverId={}, title={}, success={}", senderId, receiverId, title, success);
            return success;
        } catch (Exception e) {
            log.error("推送用户通知失败：senderId={}, receiverId={}, title={}", senderId, receiverId, title, e);
            return false;
        }
    }

    /**
     * 推送站内信给指定用户
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param title 消息标题
     * @param content 消息内容
     * @param data 扩展数据
     * @return 是否推送成功
     */
    public boolean pushPrivateMessage(Long senderId, Long receiverId, String title, String content, Map<String, Object> data) {
        try {
            boolean success = webSocketApi.isUserOnline(receiverId);
            Object finalContent = (data == null || data.isEmpty())
                    ? Map.of("text", content, "senderId", senderId)
                    : new java.util.HashMap<>(data);
            if (finalContent instanceof Map) {
                ((Map<String, Object>) finalContent).putIfAbsent("text", content);
                ((Map<String, Object>) finalContent).put("senderId", senderId);
            }
            webSocketApi.sendInternalMessage(receiverId, title, finalContent);
            log.debug("推送站内信：senderId={}, receiverId={}, title={}, success={}", senderId, receiverId, title, success);
            return success;
        } catch (Exception e) {
            log.error("推送站内信失败：senderId={}, receiverId={}, title={}", senderId, receiverId, title, e);
            return false;
        }
    }

    /**
     * 广播系统公告给所有在线用户
     *
     * @param title 公告标题
     * @param content 公告内容
     * @param data 扩展数据
     * @return 推送成功的用户数量（以广播时在线用户数估算）
     */
    public int broadcastSystemAnnouncement(String title, String content, Map<String, Object> data) {
        try {
            int onlineCount = webSocketApi.getOnlineUserCount();
            Object finalContent = (data == null || data.isEmpty())
                    ? content
                    : Map.of("text", content, "extra", data);
            webSocketApi.sendSystemAnnouncement(title, finalContent);
            log.info("广播系统公告：title={}, estimatedSuccessCount={}, totalOnlineUsers={}", title, onlineCount, webSocketApi.getOnlineUserCount());
            return onlineCount;
        } catch (Exception e) {
            log.error("广播系统公告失败：title={}", title, e);
            return 0;
        }
    }

    /**
     * 推送消息给多个用户
     *
     * @param userIds 用户ID列表
     * @param title 消息标题
     * @param content 消息内容
     * @param data 扩展数据
     * @param messageType 消息类型
     * @return 推送成功的用户数量（以在线用户数估算）
     */
    public int pushToMultipleUsers(Set<Long> userIds, String title, String content, Map<String, Object> data, String messageType) {
        try {
            int onlineCount = (userIds == null) ? 0 : (int) userIds.stream().filter(webSocketApi::isUserOnline).count();
            Object body = (data == null || data.isEmpty())
                    ? Map.of("text", content)
                    : Map.of("text", content, "extra", data);
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(messageType)
                    .title(title)
                    .content(body)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .build();
            webSocketApi.sendToUsers(userIds, message);
            log.debug("推送消息给多个用户：userCount={}, estimatedSuccessCount={}, messageType={}", (userIds == null ? 0 : userIds.size()), onlineCount, messageType);
            return onlineCount;
        } catch (Exception e) {
            log.error("推送消息给多个用户失败：userCount={}, messageType={}", (userIds == null ? 0 : userIds.size()), messageType, e);
            return 0;
        }
    }

    /**
     * 推送未读数量更新消息
     *
     * @param userId 用户ID
     * @param unreadCount 未读数量
     * @return 是否推送成功
     */
    public boolean pushUnreadCountUpdate(Long userId, Long unreadCount) {
        try {
            boolean success = webSocketApi.isUserOnline(userId);
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("unreadCount", unreadCount);
            body.put("text", "您有 " + unreadCount + " 条未读消息");
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("unread_count_update")
                    .title("未读消息更新")
                    .content(body)
                    .priority(WebSocketMessage.Priority.LOW)
                    .build();
            webSocketApi.sendToUser(userId, message);
            log.debug("推送未读数量更新：userId={}, unreadCount={}, success={}", userId, unreadCount, success);
            return success;
        } catch (Exception e) {
            log.error("推送未读数量更新失败：userId={}, unreadCount={}", userId, unreadCount, e);
            return false;
        }
    }

    /**
     * 推送自定义消息
     *
     * @param userId 用户ID
     * @param message 自定义消息
     * @return 是否推送成功
     */
    public boolean pushCustomMessage(Long userId, WebSocketMessage message) {
        try {
            boolean success = webSocketApi.isUserOnline(userId);
            webSocketApi.sendToUser(userId, message);
            log.debug("推送自定义消息：userId={}, messageType={}, success={}", userId, message.getType(), success);
            return success;
        } catch (Exception e) {
            log.error("推送自定义消息失败：userId={}, messageType={}", userId, message.getType(), e);
            return false;
        }
    }

    /**
     * 广播自定义消息给所有在线用户
     *
     * @param message 自定义消息
     * @return 推送成功的用户数量（以广播时在线用户数估算）
     */
    public int broadcastCustomMessage(WebSocketMessage message) {
        try {
            int onlineCount = webSocketApi.getOnlineUserCount();
            webSocketApi.broadcast(message);
            log.debug("广播自定义消息：messageType={}, estimatedSuccessCount={}, totalOnlineUsers={}", message.getType(), onlineCount, webSocketApi.getOnlineUserCount());
            return onlineCount;
        } catch (Exception e) {
            log.error("广播自定义消息失败：messageType={}", message.getType(), e);
            return 0;
        }
    }

    // 新增：广播自定义消息（便捷重载）
    public int broadcastCustomMessage(String title, String content, Map<String, Object> data) {
        try {
            int onlineCount = webSocketApi.getOnlineUserCount();
            Object body = (data == null || data.isEmpty())
                    ? Map.of("text", content)
                    : Map.of("text", content, "extra", data);
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("custom")
                    .title(title)
                    .content(body)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .build();
            webSocketApi.broadcast(message);
            log.debug("广播自定义消息(重载)：title={}, estimatedSuccessCount={}", title, onlineCount);
            return onlineCount;
        } catch (Exception e) {
            log.error("广播自定义消息(重载)失败：title={}", title, e);
            return 0;
        }
    }

    // 新增：广播站内信
    public int broadcastInternalMessage(String title, String content) {
        try {
            int onlineCount = webSocketApi.getOnlineUserCount();
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.Type.INTERNAL_MESSAGE)
                    .title(title)
                    .content(content)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .needAck(true)
                    .build();
            webSocketApi.broadcast(message);
            log.debug("广播站内信：title={}, estimatedSuccessCount={}", title, onlineCount);
            return onlineCount;
        } catch (Exception e) {
            log.error("广播站内信失败：title={}", title, e);
            return 0;
        }
    }

    // 新增：向多个用户推送站内信
    public int pushInternalMessageToUsers(List<Long> userIds, String title, String content) {
        try {
            int onlineCount = (userIds == null) ? 0 : (int) userIds.stream().filter(webSocketApi::isUserOnline).count();
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.Type.INTERNAL_MESSAGE)
                    .title(title)
                    .content(content)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .needAck(true)
                    .build();
            webSocketApi.sendToUsers(userIds, message);
            log.debug("推送站内信给多个用户：count={}, estimatedSuccessCount={}", (userIds == null ? 0 : userIds.size()), onlineCount);
            return onlineCount;
        } catch (Exception e) {
            log.error("推送站内信给多个用户失败", e);
            return 0;
        }
    }

    // 新增：推送站内信撤回
    public void pushInternalMessageWithdraw(List<Long> userIds, Long messageId) {
        try {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("messageId", messageId);
            body.put("action", "withdraw");
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("internal_message_withdraw")
                    .title("站内信撤回")
                    .content(body)
                    .priority(WebSocketMessage.Priority.MEDIUM)
                    .build();
            webSocketApi.sendToUsers(userIds, message);
            log.debug("推送站内信撤回：messageId={}, userCount={}", messageId, (userIds == null ? 0 : userIds.size()));
        } catch (Exception e) {
            log.error("推送站内信撤回失败：messageId={}", messageId, e);
        }
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        return webSocketApi.isUserOnline(userId);
    }

    /**
     * 获取用户的在线会话数量
     *
     * @param userId 用户ID
     * @return 会话数量
     */
    public int getUserSessionCount(Long userId) {
        // 新API中暂不支持单用户会话数量查询，返回在线状态
        return webSocketApi.isUserOnline(userId) ? 1 : 0;
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return webSocketApi.getOnlineUserCount();
    }

    /**
     * 获取所有在线用户ID
     *
     * @return 在线用户ID集合
     */
    public Set<Long> getOnlineUserIds() {
        return webSocketApi.getOnlineUserIds();
    }

    /**
     * 获取总会话数量
     *
     * @return 会话数量
     */
    public int getTotalSessionCount() {
        return webSocketApi.getActiveSessionCount();
    }

    /**
     * 清理无效会话
     */
    public void cleanupInvalidSessions() {
        webSocketApi.cleanInvalidSessions();
    }
}