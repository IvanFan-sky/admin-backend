package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户导入DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "用户导入数据")
@Data
public class UserImportDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "test001")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度为4-30个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "昵称", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Schema(description = "邮箱", example = "test001@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "性别", example = "男", allowableValues = {"未知", "男", "女"})
    private String gender;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "角色编码（多个用逗号分隔）", example = "user,editor")
    @Size(max = 200, message = "角色编码长度不能超过200个字符")
    private String roleCodes;

    @Schema(description = "状态", example = "启用", allowableValues = {"启用", "禁用"})
    private String status;

    @Schema(description = "备注", example = "新员工")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}