package com.admin.module.system.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户角色关联请求DTO
 * 
 * 用于接收用户角色分配的请求参数
 * 包含用户ID和角色ID列表
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统用户角色分配请求对象")
public class SysUserRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正整数")
    private Long userId;

    @Schema(description = "角色ID列表", example = "[2, 3, 4]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "角色列表不能为空")
    private Set<Long> roleIds;

    @Schema(description = "备注信息", example = "为用户分配管理员和财务角色")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}