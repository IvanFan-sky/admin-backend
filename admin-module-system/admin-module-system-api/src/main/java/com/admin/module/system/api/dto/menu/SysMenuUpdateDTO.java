package com.admin.module.system.api.dto.menu;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 更新菜单请求DTO
 * 
 * 用于接收更新菜单的请求参数
 * 包含菜单ID和需要更新的字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysMenuUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @NotNull(message = "菜单ID不能为空")
    @Positive(message = "菜单ID必须为正整数")
    private Long id;

    /**
     * 父菜单ID
     * 0表示顶级菜单
     */
    @NotNull(message = "父菜单ID不能为空")
    @PositiveOrZero(message = "父菜单ID必须为非负整数")
    private Long parentId;

    /**
     * 菜单名称
     * 显示在界面上的菜单标题
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 菜单类型
     * 1-目录，2-菜单，3-按钮
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * 路由地址
     * 访问的路由地址，如：`/user`
     */
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    /**
     * 组件路径
     * 组件的具体路径，如：`system/user/index`
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    /**
     * 权限标识
     * 权限字符串，如：`system:user:view`
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    /**
     * 菜单图标
     * 图标名称或图标类名
     */
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    private String icon;

    /**
     * 显示顺序
     * 数值越小越靠前显示
     */
    @NotNull(message = "显示顺序不能为空")
    @PositiveOrZero(message = "显示顺序必须为非负整数")
    private Integer sortOrder;

    /**
     * 菜单状态
     * 0-隐藏，1-显示
     */
    @NotNull(message = "菜单状态不能为空")
    private Integer visible;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 是否为外链
     * 0-否，1-是
     */
    private Integer isFrame;

    /**
     * 是否缓存
     * 0-不缓存，1-缓存
     */
    private Integer isCache;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

    /**
     * 乐观锁版本号
     * 用于并发控制
     */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}