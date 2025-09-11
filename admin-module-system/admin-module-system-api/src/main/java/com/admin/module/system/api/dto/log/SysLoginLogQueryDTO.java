package com.admin.module.system.api.dto.log;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统登录日志查询DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "管理后台 - 登录日志查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLoginLogQueryDTO extends PageQuery {

    @Schema(description = "用户账号", example = "admin")
    private String username;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "登录状态（1成功 0失败）", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "登录方式", example = "password")
    private String loginType;

    @Schema(description = "登录地点", example = "北京市")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "最小在线时长（分钟）", example = "10")
    private Long minOnlineDuration;

    @Schema(description = "最大在线时长（分钟）", example = "480")
    private Long maxOnlineDuration;
}