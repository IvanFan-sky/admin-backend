package com.admin.module.system.api.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "角色菜单权限分配请求对象")
public class SysRoleMenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正整数")
    private Long roleId;

    @Schema(description = "菜单权限ID集合", example = "[1, 2, 3, 4]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "菜单权限不能为空")
    private Set<Long> menuIds;

    @Schema(description = "权限分配说明", example = "为普通用户角色分配基础菜单权限")
    private String remark;
}