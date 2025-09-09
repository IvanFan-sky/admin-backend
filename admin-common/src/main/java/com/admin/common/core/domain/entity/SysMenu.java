package com.admin.common.core.domain.entity;

import com.admin.common.core.domain.TreeEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单实体类
 * 
 * 对应数据库sys_menu表
 * 存储系统菜单和权限信息，支持树形结构
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends TreeEntity<SysMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     * 主键，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID
     * 指向父级菜单的主键，根菜单的父ID为0
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 菜单名称
     * 显示在界面上的菜单标题
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单类型
     * 1-目录 2-菜单 3-按钮
     */
    @NotNull(message = "菜单类型不能为空")
    @TableField("menu_type")
    private Integer menuType;

    /**
     * 路由地址
     * 前端路由路径，用于页面跳转
     */
    @Size(max = 200, message = "路由地址不能超过200个字符")
    @TableField("path")
    private String path;

    /**
     * 组件路径
     * 前端组件的文件路径，用于动态加载组件
     */
    @Size(max = 255, message = "组件路径不能超过255个字符")
    @TableField("component")
    private String component;

    /**
     * 权限标识
     * 用于后端权限控制的标识符，如"system:user:list"
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @TableField("permission")
    private String permission;

    /**
     * 菜单图标
     * 显示在菜单前的图标名称或图标类名
     */
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    @TableField("icon")
    private String icon;

    /**
     * 显示顺序
     * 同级菜单的排序权重，数字越小越靠前
     */
    @NotNull(message = "显示顺序不能为空")
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 菜单状态
     * 1-显示 0-隐藏
     */
    @TableField("status")
    private Integer status;

    /**
     * 版本号
     * 用于乐观锁控制，防止并发修改冲突
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 子菜单列表
     * 非数据库字段，用于构建菜单树形结构
     */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

    public List<SysMenu> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}