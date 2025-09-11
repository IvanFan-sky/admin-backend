package com.admin.module.system.api.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 系统角色更新请求DTO
 * 
 * 用于接收更新角色的请求参数
 * 包含完整的参数校验和字段说明
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统角色更新请求对象")
public class SysRoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正整数")
    private Long id;

    @Schema(description = "角色名称", example = "普通用户", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    private String roleName;

    @Schema(description = "角色编码", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 50, message = "角色编码长度必须在2-50个字符之间")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "角色编码只能包含大写字母、数字和下划线")
    private String roleCode;

    @Schema(description = "角色描述", example = "系统普通用户，拥有基本操作权限")
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String roleDesc;

    @Schema(description = "显示顺序", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    @Min(value = 0, message = "显示顺序必须为非负整数")
    private Integer sortOrder;

    @Schema(description = "角色状态", example = "1", allowableValues = {"0", "1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色状态不能为空")
    @Min(value = 0, message = "角色状态值不正确")
    @Max(value = 1, message = "角色状态值不正确")
    private Integer status;

    @Schema(description = "备注信息", example = "普通用户角色")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "乐观锁版本号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    @Min(value = 0, message = "版本号必须为非负整数")
    private Integer version;
}