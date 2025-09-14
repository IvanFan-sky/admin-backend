package com.admin.module.notification.api.dto.message;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内信查询请求DTO
 * 
 * 用于接收前端查询站内信的请求参数
 * 支持多条件组合查询和分页
 *
 * @author admin
 * @version 1.0
 * @since 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "站内信查询请求对象")
public class InternalMessageQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息标题（模糊查询）", example = "系统")
    private String title;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "优先级", example = "1", allowableValues = {"1", "2", "3"})
    private Integer priority;

    @Schema(description = "发送类型", example = "1", allowableValues = {"1", "2"})
    private Integer sendType;

    @Schema(description = "消息状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    @Schema(description = "发送人ID", example = "1")
    private Long senderId;

    @Schema(description = "接收人ID", example = "1")
    private Long receiverId;

    @Schema(description = "接收人类型", example = "1", allowableValues = {"1", "2"})
    private Integer receiverType;

    @Schema(description = "创建时间范围-开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围-结束时间", example = "2025-01-31T23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "发送时间范围-开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime sendTimeStart;

    @Schema(description = "发送时间范围-结束时间", example = "2025-01-31T23:59:59")
    private LocalDateTime sendTimeEnd;
}