package com.admin.module.notification.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知类型数据对象
 * 
 * 用于存储系统通知类型的基本信息
 * 包括类型名称、描述、图标、颜色等配置信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@TableName("notification_type")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeDO extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 类型名称
     */
    @TableField("name")
    private String name;

    /**
     * 类型编码
     */
    @TableField("code")
    private String code;

    /**
     * 类型描述
     */
    @TableField("description")
    private String description;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 颜色
     */
    @TableField("color")
    private String color;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否系统内置：0-否，1-是
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    // ========== 业务方法 ==========

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否禁用
     */
    public boolean isDisabled() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否系统内置
     */
    public boolean isSystemBuiltIn() {
        return Integer.valueOf(1).equals(this.isSystem);
    }
}