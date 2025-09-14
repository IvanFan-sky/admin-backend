package com.admin.module.notification.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 站内信数据对象
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("internal_message")
public class InternalMessageDO extends BaseEntity {

    /**
     * 站内信ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型（1-系统消息 2-业务消息 3-提醒消息 4-其他）
     */
    @TableField("type")
    private Integer type;

    /**
     * 优先级（1-低 2-中 3-高 4-紧急）
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 发送方式（1-单发 2-群发 3-广播）
     */
    @TableField("send_type")
    private Integer sendType;

    /**
     * 发送状态（0-草稿 1-已发送 2-发送失败 3-已撤回）
     */
    @TableField("status")
    private Integer status;

    /**
     * 发送人ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 发送人姓名
     */
    @TableField("sender_name")
    private String senderName;

    /**
     * 接收人ID（单发时使用）
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 接收人姓名（单发时使用）
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 接收人类型（1-指定用户 2-指定角色 3-指定部门 4-全体用户）
     */
    @TableField("receiver_type")
    private Integer receiverType;

    /**
     * 接收人ID列表（JSON格式，群发时使用）
     */
    @TableField("receiver_ids")
    private String receiverIds;

    /**
     * 定时发送时间
     */
    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;

    /**
     * 实际发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否需要回执（0-否 1-是）
     */
    @TableField("need_receipt")
    private Boolean needReceipt;

    /**
     * 附件信息（JSON格式）
     */
    @TableField("attachments")
    private String attachments;

    /**
     * 扩展信息（JSON格式）
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 发送成功数量
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 发送失败数量
     */
    @TableField("failure_count")
    private Integer failureCount;

    /**
     * 已读数量
     */
    @TableField("read_count")
    private Integer readCount;

    /**
     * 回执数量
     */
    @TableField("receipt_count")
    private Integer receiptCount;


    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    // ========== 业务方法 ==========

    /**
     * 是否草稿状态
     */
    public boolean isDraft() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否已发送
     */
    public boolean isSent() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否发送失败
     */
    public boolean isFailed() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否已撤回
     */
    public boolean isWithdrawn() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 是否单发
     */
    public boolean isSingleSend() {
        return Integer.valueOf(1).equals(this.sendType);
    }

    /**
     * 是否群发
     */
    public boolean isGroupSend() {
        return Integer.valueOf(2).equals(this.sendType);
    }

    /**
     * 是否广播
     */
    public boolean isBroadcast() {
        return Integer.valueOf(3).equals(this.sendType);
    }

    /**
     * 是否高优先级
     */
    public boolean isHighPriority() {
        return priority != null && priority >= 3;
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 是否定时发送
     */
    public boolean isScheduled() {
        return scheduledTime != null && LocalDateTime.now().isBefore(scheduledTime);
    }

    /**
     * 获取发送成功率
     */
    public double getSuccessRate() {
        int total = (successCount != null ? successCount : 0) + (failureCount != null ? failureCount : 0);
        if (total == 0) {
            return 0.0;
        }
        return (double) (successCount != null ? successCount : 0) / total * 100;
    }

    /**
     * 获取阅读率
     */
    public double getReadRate() {
        if (successCount == null || successCount == 0) {
            return 0.0;
        }
        return (double) (readCount != null ? readCount : 0) / successCount * 100;
    }
}