package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统登录日志VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "管理后台 - 登录日志信息")
@Data
public class SysLoginLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "用户账号", example = "admin")
    private String username;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "登录地点", example = "内网IP")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome 120.0")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "登录状态", example = "1")
    private Integer status;

    @Schema(description = "登录状态名称", example = "成功")
    private String statusName;

    @Schema(description = "提示消息", example = "登录成功")
    private String msg;

    @Schema(description = "登录时间", example = "2024-01-15 09:00:00")
    private LocalDateTime loginTime;

    @Schema(description = "登录方式", example = "password")
    private String loginType;

    @Schema(description = "登录方式名称", example = "密码登录")
    private String loginTypeName;

    @Schema(description = "用户代理", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    private String userAgent;

    @Schema(description = "登录令牌ID", example = "abc123def456")
    private String tokenId;

    @Schema(description = "会话超时时间", example = "2024-01-15 17:00:00")
    private LocalDateTime sessionTimeout;

    @Schema(description = "登出时间", example = "2024-01-15 17:30:00")
    private LocalDateTime logoutTime;

    @Schema(description = "登出方式", example = "normal")
    private String logoutType;

    @Schema(description = "登出方式名称", example = "正常登出")
    private String logoutTypeName;

    @Schema(description = "在线时长（分钟）", example = "510")
    private Long onlineDuration;

    @Schema(description = "在线时长（格式化）", example = "8小时30分钟")
    private String onlineDurationFormat;

    @Schema(description = "创建时间", example = "2024-01-15 09:00:00")
    private LocalDateTime createTime;
}