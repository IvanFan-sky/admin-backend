package com.admin.module.notification.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户通知数据对象
 * 
 * 对应数据库user_notification表
 * 存储用户与通知的关联关系、阅读状态等数据
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_notification")
public class UserNotificationDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知ID
     */
    @TableField("notification_id")
    private Long notificationId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 阅读状态：0-未读 1-已读
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 接收状态：0-未接收 1-已接收 2-接收失败
     */
    @TableField("receive_status")
    private Integer receiveStatus;

    /**
     * 接收时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 接收失败原因
     */
    @TableField("receive_error")
    private String receiveError;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 判断是否未读
     */
    public boolean isUnread() {
        return readStatus != null && readStatus == 0;
    }

    /**
     * 判断是否已读
     */
    public boolean isRead() {
        return readStatus != null && readStatus == 1;
    }

    /**
     * 判断是否接收成功
     */
    public boolean isReceived() {
        return receiveStatus != null && receiveStatus == 1;
    }

    /**
     * 判断是否接收失败
     */
    public boolean isReceiveFailed() {
        return receiveStatus != null && receiveStatus == 2;
    }

}