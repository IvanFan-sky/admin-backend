package com.admin.module.system.api.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统登录日志创建DTO
 * 
 * 用于创建登录日志的请求参数
 * 包含登录相关的基本信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统登录日志创建请求对象")
public class SysLoginLogCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 50, message = "用户账号长度不能超过50个字符")
    private String username;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipAddress;

    @Schema(description = "登录地点", example = "北京市")
    @Size(max = 100, message = "登录地点长度不能超过100个字符")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome")
    @Size(max = 50, message = "浏览器类型长度不能超过50个字符")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    @Size(max = 50, message = "操作系统长度不能超过50个字符")
    private String os;

    @Schema(description = "登录状态（1成功 0失败）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "登录状态不能为空")
    private Integer status;

    @Schema(description = "提示消息", example = "登录成功")
    @Size(max = 255, message = "提示消息长度不能超过255个字符")
    private String msg;

    @Schema(description = "登录时间", example = "2024-01-15 10:30:00")
    private LocalDateTime loginTime;

    @Schema(description = "登录方式", example = "password")
    @Size(max = 20, message = "登录方式长度不能超过20个字符")
    private String loginType;

    @Schema(description = "用户代理")
    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    private String userAgent;

    @Schema(description = "登录令牌ID")
    @Size(max = 100, message = "令牌ID长度不能超过100个字符")
    private String tokenId;

    @Schema(description = "会话超时时间")
    private LocalDateTime sessionTimeout;
}