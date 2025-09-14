package com.admin.module.notification.api.dto.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 通知类型更新DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "通知类型更新对象")
public class NotificationTypeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型ID不能为空")
    private Long id;

    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统消息")
    @NotBlank(message = "类型名称不能为空")
    @Size(max = 50, message = "类型名称长度不能超过50个字符")
    private String name;

    @Schema(description = "类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM")
    @NotBlank(message = "类型编码不能为空")
    @Size(max = 50, message = "类型编码长度不能超过50个字符")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "类型编码只能包含大写字母、数字和下划线，且必须以大写字母开头")
    private String code;

    @Schema(description = "类型描述", example = "系统相关消息")
    @Size(max = 200, message = "类型描述长度不能超过200个字符")
    private String description;

    @Schema(description = "图标", example = "system-icon")
    @Size(max = 100, message = "图标长度不能超过100个字符")
    private String icon;

    @Schema(description = "颜色", example = "#1890ff")
    @Size(max = 20, message = "颜色长度不能超过20个字符")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确，请使用十六进制颜色代码")
    private String color;

    @Schema(description = "排序", example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注信息", example = "系统内置类型")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}