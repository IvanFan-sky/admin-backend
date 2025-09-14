package com.admin.module.notification.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内信导出请求VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "站内信导出请求VO")
public class InternalMessageExportReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息标题", example = "系统通知")
    private String title;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "发送状态", example = "1")
    private Integer status;

    @Schema(description = "发送者ID", example = "1")
    private Long senderId;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "接收者类型", example = "1")
    private Integer receiverType;

    @Schema(description = "创建时间-开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "发送时间-开始")
    private LocalDateTime sendTimeStart;

    @Schema(description = "发送时间-结束")
    private LocalDateTime sendTimeEnd;
}
