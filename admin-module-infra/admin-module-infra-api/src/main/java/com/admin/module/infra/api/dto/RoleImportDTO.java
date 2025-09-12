package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色导入DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "角色导入数据")
@Data
public class RoleImportDTO {

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "普通用户")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "角色编码只能包含字母、数字和下划线")
    private String roleCode;

    @Schema(description = "角色描述", example = "普通用户角色")
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String roleDesc;

    @Schema(description = "显示顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态", example = "启用", allowableValues = {"启用", "禁用"})
    private String status;

    @Schema(description = "备注", example = "系统默认角色")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}