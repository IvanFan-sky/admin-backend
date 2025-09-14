package com.admin.module.notification.api.vo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 站内信详情展示VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "站内信详情展示对象")
public class InternalMessageDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "消息标题", example = "系统通知")
    private String title;

    @Schema(description = "消息内容", example = "这是一条重要通知")
    private String content;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "消息类型名称", example = "系统消息")
    private String typeName;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "优先级名称", example = "普通")
    private String priorityName;

    @Schema(description = "消息状态", example = "1")
    private Integer status;

    @Schema(description = "消息状态名称", example = "已发送")
    private String statusName;

    @Schema(description = "发送者ID", example = "1")
    private Long senderId;

    @Schema(description = "发送者名称", example = "系统管理员")
    private String senderName;

    @Schema(description = "接收用户数量", example = "100")
    private Integer receiverCount;

    @Schema(description = "已读用户数量", example = "50")
    private Integer readCount;

    @Schema(description = "未读用户数量", example = "50")
    private Integer unreadCount;

    @Schema(description = "是否需要回执", example = "false")
    private Boolean needReceipt;

    @Schema(description = "回执数量", example = "30")
    private Integer receiptCount;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    @Schema(description = "定时发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    @Schema(description = "扩展数据（JSON格式）", example = "{\"url\":\"https://example.com\"}")
    private String extraData;

    @Schema(description = "接收用户列表")
    private List<ReceiverInfo> receivers;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "备注信息", example = "重要消息")
    private String remark;

    /**
     * 接收者信息
     */
    @Data
    @Schema(description = "接收者信息")
    public static class ReceiverInfo implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @Schema(description = "用户ID", example = "1")
        private Long userId;
        
        @Schema(description = "用户名", example = "张三")
        private String username;
        
        @Schema(description = "是否已读", example = "true")
        private Boolean isRead;
        
        @Schema(description = "阅读时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime readTime;
        
        @Schema(description = "是否已回执", example = "false")
        private Boolean hasReceipt;
        
        @Schema(description = "回执时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime receiptTime;
    }
}