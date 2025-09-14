package com.admin.module.notification.api.dto.notification;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户通知查询请求DTO
 * 
 * 用于接收前端查询用户通知的请求参数
 * 支持多条件组合查询和分页
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户通知查询请求对象")
public class UserNotificationQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "通知ID", example = "1")
    private Long notificationId;

    @Schema(description = "通知类型ID", example = "1")
    private Long typeId;

    @Schema(description = "通知标题（模糊查询）", example = "系统")
    private String title;

    @Schema(description = "通知级别", example = "1", allowableValues = {"1", "2", "3"})
    private Integer level;

    @Schema(description = "阅读状态", example = "0", allowableValues = {"0", "1"})
    private Integer readStatus;

    @Schema(description = "删除状态", example = "0", allowableValues = {"0", "1"})
    private Integer deleteStatus;

    @Schema(description = "创建时间范围-开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围-结束时间", example = "2024-01-31T23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "阅读时间范围-开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime readTimeStart;

    @Schema(description = "阅读时间范围-结束时间", example = "2024-01-31T23:59:59")
    private LocalDateTime readTimeEnd;
}