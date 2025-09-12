package com.admin.module.log.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "登录日志信息")
@Data
public class LoginLogVO {

    @Schema(description = "访问ID", example = "1")
    private Long id;

    @Schema(description = "用户账号", example = "admin")
    private String userName;

    @Schema(description = "登录类型：1-用户名密码，2-邮箱密码，3-手机验证码，4-第三方登录", example = "1")
    private Integer loginType;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    private String ipaddr;

    @Schema(description = "登录地点", example = "内网IP")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "登录状态：0-成功，1-失败", example = "0")
    private Integer status;

    @Schema(description = "提示消息", example = "登录成功")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}