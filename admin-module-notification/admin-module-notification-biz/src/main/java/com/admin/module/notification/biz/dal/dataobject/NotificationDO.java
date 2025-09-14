package com.admin.module.notification.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知数据对象
 * 
 * 对应数据库notification表
 * 存储系统通知的基本信息、内容、状态等数据
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class NotificationDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知类型ID
     */
    @TableField("type_id")
    private Long typeId;

    /**
     * 通知标题
     */
    @TableField("title")
    private String title;

    /**
     * 通知内容
     */
    @TableField("content")
    private String content;

    /**
     * 通知级别：1-普通 2-重要 3-紧急
     */
    @TableField("level")
    private Integer level;

    /**
     * 发送方式：1-站内信 2-邮件 3-短信 4-推送
     */
    @TableField("send_type")
    private Integer sendType;

    /**
     * 目标用户类型：1-全部用户 2-指定用户 3-指定角色
     */
    @TableField("target_type")
    private Integer targetType;

    /**
     * 目标用户ID列表（JSON格式）
     */
    @TableField("target_users")
    private String targetUsers;

    /**
     * 目标角色ID列表（JSON格式）
     */
    @TableField("target_roles")
    private String targetRoles;

    /**
     * 发布状态：0-草稿 1-已发布 2-已撤回
     */
    @TableField("publish_status")
    private Integer publishStatus;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 撤回时间
     */
    @TableField("revoke_time")
    private LocalDateTime revokeTime;

    /**
     * 撤回原因
     */
    @TableField("revoke_reason")
    private String revokeReason;

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
     * 判断是否为草稿状态
     */
    public boolean isDraft() {
        return publishStatus != null && publishStatus == 0;
    }

    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return publishStatus != null && publishStatus == 1;
    }

    /**
     * 判断是否已撤回
     */
    public boolean isRevoked() {
        return publishStatus != null && publishStatus == 2;
    }

}