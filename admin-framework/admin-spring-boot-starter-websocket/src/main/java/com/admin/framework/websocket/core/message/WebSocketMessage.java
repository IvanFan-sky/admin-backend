package com.admin.framework.websocket.core.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * WebSocket 消息实体
 *
 * @author admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * 创建消息构建器
     */
    public static WebSocketMessageBuilder builder() {
        return new WebSocketMessageBuilder();
    }

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private Object content;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 接收者ID（为空表示广播消息）
     */
    private Long receiverId;

    /**
     * 接收者类型（user: 用户, role: 角色, dept: 部门, all: 全部）
     */
    private String receiverType;

    /**
     * 消息优先级（1: 低, 2: 中, 3: 高, 4: 紧急）
     */
    private Integer priority;

    /**
     * 是否需要确认收到
     */
    private Boolean needAck;

    /**
     * 扩展数据
     */
    private Map<String, Object> extra;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 消息类型常量
     */
    public static class Type {
        /** 系统通知 */
        public static final String SYSTEM_NOTIFICATION = "system_notification";
        /** 系统公告 */
        public static final String SYSTEM_ANNOUNCEMENT = "system_announcement";
        /** 站内信 */
        public static final String INTERNAL_MESSAGE = "internal_message";
        /** 在线用户数更新 */
        public static final String ONLINE_USER_COUNT = "online_user_count";
        /** 用户上线 */
        public static final String USER_ONLINE = "user_online";
        /** 用户下线 */
        public static final String USER_OFFLINE = "user_offline";
        /** 心跳 */
        public static final String HEARTBEAT = "heartbeat";
        /** 确认收到 */
        public static final String ACK = "ack";
    }

    /**
     * 接收者类型常量
     */
    public static class ReceiverType {
        /** 用户 */
        public static final String USER = "user";
        /** 角色 */
        public static final String ROLE = "role";
        /** 部门 */
        public static final String DEPT = "dept";
        /** 全部 */
        public static final String ALL = "all";
    }

    /**
     * 优先级常量
     */
    public static class Priority {
        /** 低 */
        public static final int LOW = 1;
        /** 中 */
        public static final int MEDIUM = 2;
        /** 高 */
        public static final int HIGH = 3;
        /** 紧急 */
        public static final int URGENT = 4;
    }

    /**
     * WebSocket消息构建器
     */
    public static class WebSocketMessageBuilder {
        private String messageId;
        private String type;
        private String title;
        private Object content;
        private Long senderId;
        private String senderName;
        private Long receiverId;
        private String receiverType;
        private Integer priority;
        private Boolean needAck;
        private Map<String, Object> extra;
        private LocalDateTime createTime;

        public WebSocketMessageBuilder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public WebSocketMessageBuilder type(String type) {
            this.type = type;
            return this;
        }

        public WebSocketMessageBuilder title(String title) {
            this.title = title;
            return this;
        }

        public WebSocketMessageBuilder content(Object content) {
            this.content = content;
            return this;
        }

        public WebSocketMessageBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public WebSocketMessageBuilder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public WebSocketMessageBuilder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public WebSocketMessageBuilder receiverType(String receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        public WebSocketMessageBuilder priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public WebSocketMessageBuilder needAck(Boolean needAck) {
            this.needAck = needAck;
            return this;
        }

        public WebSocketMessageBuilder extra(Map<String, Object> extra) {
            this.extra = extra;
            return this;
        }

        public WebSocketMessageBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public WebSocketMessage build() {
            WebSocketMessage message = new WebSocketMessage();
            message.messageId = this.messageId != null ? this.messageId : UUID.randomUUID().toString();
            message.type = this.type;
            message.title = this.title;
            message.content = this.content;
            message.senderId = this.senderId;
            message.senderName = this.senderName;
            message.receiverId = this.receiverId;
            message.receiverType = this.receiverType;
            message.priority = this.priority != null ? this.priority : Priority.MEDIUM;
            message.needAck = this.needAck != null ? this.needAck : false;
            message.extra = this.extra;
            message.createTime = this.createTime != null ? this.createTime : LocalDateTime.now();
            return message;
        }
    }
}