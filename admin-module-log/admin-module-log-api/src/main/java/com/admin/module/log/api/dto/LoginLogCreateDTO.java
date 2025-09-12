package com.admin.module.log.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 登录日志创建DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "登录日志创建DTO")
@Data
public class LoginLogCreateDTO {

    @Schema(description = "用户账号", example = "admin")
    @Size(max = 50, message = "用户账号长度不能超过50个字符")
    private String userName;

    @Schema(description = "登录类型：1-用户名密码，2-邮箱密码，3-手机验证码，4-第三方登录", example = "1")
    private Integer loginType;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipaddr;

    @Schema(description = "登录地点", example = "内网IP")
    @Size(max = 255, message = "登录地点长度不能超过255个字符")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome")
    @Size(max = 50, message = "浏览器类型长度不能超过50个字符")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    @Size(max = 50, message = "操作系统长度不能超过50个字符")
    private String os;

    @Schema(description = "登录状态：0-成功，1-失败", example = "0")
    @NotNull(message = "登录状态不能为空")
    private Integer status;

    @Schema(description = "提示消息", example = "登录成功")
    @Size(max = 255, message = "提示消息长度不能超过255个字符")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}