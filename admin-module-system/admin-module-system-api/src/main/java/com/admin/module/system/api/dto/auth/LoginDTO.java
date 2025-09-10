package com.admin.module.system.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "用户登录请求")
public class LoginDTO {

    /**
     * 登录账号（用户名、邮箱或手机号）
     */
    @Schema(description = "登录账号", example = "admin")
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号长度不能超过50个字符")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description = "登录密码", example = "admin123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32位之间")
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "1234")
    private String captcha;

    /**
     * 验证码UUID
     */
    @Schema(description = "验证码UUID")
    private String uuid;

    /**
     * 是否记住我
     */
    @Schema(description = "是否记住我", example = "false")
    private Boolean rememberMe = false;
}