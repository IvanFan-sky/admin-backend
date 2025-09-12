package com.admin.module.log.api.dto;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录日志查询DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "登录日志查询DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogQueryDTO extends PageQuery {

    @Schema(description = "用户账号", example = "admin")
    private String userName;

    @Schema(description = "登录类型", example = "1")
    private Integer loginType;

    @Schema(description = "登录IP地址", example = "192.168.1.100")
    private String ipaddr;

    @Schema(description = "登录状态：0-成功，1-失败", example = "0")
    private Integer status;

    @Schema(description = "浏览器类型", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;
}