package com.admin.module.notification.api.dto.type;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知类型查询DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知类型查询对象")
public class NotificationTypeQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型名称（模糊查询）", example = "系统")
    private String name;

    @Schema(description = "类型编码", example = "SYSTEM")
    private String code;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "创建时间范围-开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围-结束时间", example = "2024-01-31T23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "更新时间范围-开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime updateTimeStart;

    @Schema(description = "更新时间范围-结束时间", example = "2024-01-31T23:59:59")
    private LocalDateTime updateTimeEnd;
}