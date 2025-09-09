package com.admin.common.core.domain.entity;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 系统角色实体类
 * 
 * 对应数据库sys_role表
 * 存储系统角色信息，用于权限控制和用户分组管理
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * 主键，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     * 角色的显示名称，如"超级管理员"、"普通用户"
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @TableField("role_name")
    private String roleName;

    /**
     * 权限字符（角色标识）
     * 角色的唯一标识码，如"SUPER_ADMIN"、"USER"
     * 用于程序中的权限判断
     */
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色描述
     * 详细说明角色的用途和权限范围
     */
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    @TableField("role_desc")
    private String roleDesc;

    /**
     * 显示顺序
     * 用于角色列表的排序，数字越小越靠前
     */
    @NotNull(message = "显示顺序不能为空")
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 角色状态
     * 1-正常 0-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标志
     * 0-正常 1-删除，用于逻辑删除
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * 版本号
     * 用于乐观锁控制，防止并发修改冲突
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 用户角色选择标志
     * 非数据库字段，用于前端显示用户是否拥有该角色
     */
    @TableField(exist = false)
    private boolean flag = false;

    /**
     * 菜单ID数组
     * 非数据库字段，用于角色菜单权限分配
     */
    @TableField(exist = false)
    private Long[] menuIds;

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }
}