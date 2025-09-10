package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.TreeEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单表数据对象
 * 
 * 对应数据库表 sys_menu
 * 支持树形结构的菜单权限管理
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenuDO extends TreeEntity<SysMenuDO> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单类型
     * 1-目录，2-菜单，3-按钮
     */
    @TableField("menu_type")
    private Integer menuType;

    /**
     * 路由地址
     * 访问的路由地址，如：`/user`
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径
     * 组件的具体路径，如：`system/user/index`
     */
    @TableField("component")
    private String component;

    /**
     * 权限标识
     * 权限字符串，如：`system:user:view`
     */
    @TableField("permission")
    private String permission;

    /**
     * 菜单图标
     * 图标名称或图标类名
     */
    @TableField("icon")
    private String icon;

    /**
     * 显示顺序
     * 数值越小越靠前显示
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 菜单状态
     * 0-隐藏，1-显示
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否为外链
     * 0-否，1-是
     */
    @TableField("is_frame")
    private Integer isFrame;

    /**
     * 是否缓存
     * 0-不缓存，1-缓存
     */
    @TableField("is_cache")
    private Integer isCache;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 删除标识
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}