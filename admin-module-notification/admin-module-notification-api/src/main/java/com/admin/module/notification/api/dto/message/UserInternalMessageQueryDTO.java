package com.admin.module.notification.api.dto.message;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户站内信查询请求DTO
 * 
 * 用于接收前端查询用户站内信的请求参数
 * 支持多条件组合查询和分页
 *
 * @author admin
 * @version 1.0
 * @since 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户站内信查询请求对象")
public class UserInternalMessageQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "消息ID", example = "1")
    private Long messageId;

    @Schema(description = "消息标题（模糊查询）", example = "系统")
    private String title;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "优先级", example = "1", allowableValues = {"1", "2", "3"})
    private Integer priority;

    @Schema(description = "阅读状态", example = "0", allowableValues = {"0", "1"})
    private Integer readStatus;

    @Schema(description = "收藏状态", example = "0", allowableValues = {"0", "1"})
    private Integer favoriteStatus;

    @Schema(description = "接收状态", example = "1", allowableValues = {"0", "1"})
    private Integer receiveStatus;

    @Schema(description = "删除状态", example = "0", allowableValues = {"0", "1"})
    private Integer deleteStatus;

    @Schema(description = "创建时间范围-开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围-结束时间", example = "2025-01-31T23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "阅读时间范围-开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime readTimeStart;

    @Schema(description = "阅读时间范围-结束时间", example = "2025-01-31T23:59:59")
    private LocalDateTime readTimeEnd;
}