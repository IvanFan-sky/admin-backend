package com.admin.framework.websocket.core.listener;

import com.admin.framework.websocket.core.sender.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * WebSocket 事件监听器
 *
 * @author admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "admin.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketEventListener {

    private final WebSocketMessageSender messageSender;

    /**
     * 监听系统公告发布事件
     *
     * @param event 系统公告事件
     */
    @EventListener
    public void handleSystemAnnouncementEvent(SystemAnnouncementEvent event) {
        try {
            log.info("收到系统公告事件: {}", event.getTitle());
            
            // 发送系统公告
            messageSender.sendSystemAnnouncement(event.getTitle(), event.getContent());
            
        } catch (Exception e) {
            log.error("处理系统公告事件失败", e);
        }
    }

    /**
     * 监听站内信发送事件
     *
     * @param event 站内信事件
     */
    @EventListener
    public void handleInternalMessageEvent(InternalMessageEvent event) {
        try {
            log.info("收到站内信事件，用户ID: {}", event.getUserId());
            
            // 发送站内信通知
            messageSender.sendInternalMessage(event.getUserId(), event.getTitle(), event.getContent());
            
        } catch (Exception e) {
            log.error("处理站内信事件失败", e);
        }
    }

    /**
     * 监听系统通知事件
     *
     * @param event 系统通知事件
     */
    @EventListener
    public void handleSystemNotificationEvent(SystemNotificationEvent event) {
        try {
            log.info("收到系统通知事件: {}", event.getTitle());
            
            // 发送系统通知
            messageSender.sendSystemNotification(event.getUserId(), event.getTitle(), event.getContent());
            
        } catch (Exception e) {
            log.error("处理系统通知事件失败", e);
        }
    }

    /**
     * 系统公告事件
     */
    public static class SystemAnnouncementEvent {
        private final String title;
        private final Object content;

        public SystemAnnouncementEvent(String title, Object content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public Object getContent() {
            return content;
        }
    }

    /**
     * 站内信事件
     */
    public static class InternalMessageEvent {
        private final Long userId;
        private final String title;
        private final Object content;

        public InternalMessageEvent(Long userId, String title, Object content) {
            this.userId = userId;
            this.title = title;
            this.content = content;
        }

        public Long getUserId() {
            return userId;
        }

        public String getTitle() {
            return title;
        }

        public Object getContent() {
            return content;
        }
    }

    /**
     * 系统通知事件
     */
    public static class SystemNotificationEvent {
        private final Long userId;
        private final String title;
        private final Object content;

        public SystemNotificationEvent(Long userId, String title, Object content) {
            this.userId = userId;
            this.title = title;
            this.content = content;
        }

        public Long getUserId() {
            return userId;
        }

        public String getTitle() {
            return title;
        }

        public Object getContent() {
            return content;
        }
    }
}