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

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends TreeEntity<SysMenu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @TableField("menu_name")
    private String menuName;

    @NotNull(message = "菜单类型不能为空")
    @TableField("menu_type")
    private Integer menuType;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    @TableField("path")
    private String path;

    @Size(max = 255, message = "组件路径不能超过255个字符")
    @TableField("component")
    private String component;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @TableField("permission")
    private String permission;

    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    @TableField("icon")
    private String icon;

    @NotNull(message = "显示顺序不能为空")
    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Integer status;

    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

    public List<SysMenu> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}