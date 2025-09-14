package com.admin.module.notification.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统公告数据对象
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("system_announcement")
public class SystemAnnouncementDO extends BaseEntity {

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型（1-系统维护 2-功能更新 3-重要通知 4-其他）
     */
    @TableField("type")
    private Integer type;

    /**
     * 优先级（1-低 2-中 3-高 4-紧急）
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 发布状态（0-草稿 1-已发布 2-已撤回）
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否置顶（0-否 1-是）
     */
    @TableField("is_top")
    private Boolean isTop;

    /**
     * 是否弹窗显示（0-否 1-是）
     */
    @TableField("is_popup")
    private Boolean isPopup;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 生效时间
     */
    @TableField("effective_time")
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 发布人ID
     */
    @TableField("publisher_id")
    private Long publisherId;

    /**
     * 发布人姓名
     */
    @TableField("publisher_name")
    private String publisherName;

    /**
     * 阅读次数
     */
    @TableField("read_count")
    private Integer readCount;


    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    // ========== 业务方法 ==========

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否草稿状态
     */
    public boolean isDraft() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否已撤回
     */
    public boolean isWithdrawn() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否在有效期内
     */
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        return (effectiveTime == null || !now.isBefore(effectiveTime)) &&
               (expireTime == null || !now.isAfter(expireTime));
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 是否高优先级
     */
    public boolean isHighPriority() {
        return priority != null && priority >= 3;
    }
}