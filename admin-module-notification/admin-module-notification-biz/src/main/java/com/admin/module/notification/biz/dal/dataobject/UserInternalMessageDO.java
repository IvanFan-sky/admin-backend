package com.admin.module.notification.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户站内信DO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_internal_message")
public class UserInternalMessageDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户站内信ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站内信ID
     */
    @TableField("message_id")
    private Long messageId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

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
     * 消息类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 消息类型名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 优先级名称
     */
    @TableField("priority_name")
    private String priorityName;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 发送者名称
     */
    @TableField("sender_name")
    private String senderName;

    /**
     * 读取状态：0-未读，1-已读
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 读取时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 收藏状态：0-未收藏，1-已收藏
     */
    @TableField("favorite_status")
    private Integer favoriteStatus;

    /**
     * 收藏时间
     */
    @TableField("favorite_time")
    private LocalDateTime favoriteTime;

    /**
     * 接收状态：0-未接收，1-已接收
     */
    @TableField("receive_status")
    private Integer receiveStatus;

    /**
     * 接收时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 回执状态：0-未回执，1-已回执
     */
    @TableField("receipt_status")
    private Integer receiptStatus;

    /**
     * 回执内容
     */
    @TableField("receipt_content")
    private String receiptContent;

    /**
     * 回执时间
     */
    @TableField("receipt_time")
    private LocalDateTime receiptTime;

    /**
     * 删除状态：0-未删除，1-已删除
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private LocalDateTime deleteTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 扩展数据
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 创建者
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 获取收藏状态（兼容方法）
     */
    public Boolean getFavorite() {
        return favoriteStatus != null && favoriteStatus == 1;
    }

    /**
     * 设置收藏状态（兼容方法）
     */
    public void setFavorite(Boolean favorite) {
        this.favoriteStatus = (favorite != null && favorite) ? 1 : 0;
    }

    /**
     * 是否已读
     */
    public boolean isRead() {
        return readStatus != null && readStatus == 1;
    }

    /**
     * 是否收藏
     */
    public boolean isFavorite() {
        return favoriteStatus != null && favoriteStatus == 1;
    }
}