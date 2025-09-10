package com.admin.module.system.api.dto.role;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Set;

/**
 * 角色菜单权限分配请求DTO
 * 
 * 用于接收角色菜单权限分配的请求参数
 * 包含角色ID和菜单ID列表
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysRoleMenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * 要分配权限的角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正整数")
    private Long roleId;

    /**
     * 菜单ID列表
     * 分配给角色的菜单权限ID集合
     * 不能为空，至少需要分配一个菜单权限
     */
    @NotEmpty(message = "菜单权限不能为空")
    private Set<Long> menuIds;

    /**
     * 备注信息
     * 权限分配的说明
     */
    private String remark;
}