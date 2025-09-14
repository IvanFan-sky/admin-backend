package com.admin.module.notification.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内信更新DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "站内信更新对象")
public class InternalMessageUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消息ID不能为空")
    private Long id;

    @Schema(description = "消息标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统通知")
    @NotBlank(message = "消息标题不能为空")
    @Size(max = 200, message = "消息标题长度不能超过200个字符")
    private String title;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是一条重要通知")
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容长度不能超过2000个字符")
    private String content;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "消息类型不能为空")
    private Integer type;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "消息状态", example = "1")
    private Integer status;

    @Schema(description = "定时发送时间", example = "2025-01-15T10:00:00")
    private LocalDateTime scheduledTime;

    @Schema(description = "是否需要回执", example = "false")
    private Boolean needReceipt;

    @Schema(description = "扩展数据（JSON格式）", example = "{\"url\":\"https://example.com\"}")
    private String extraData;

    @Schema(description = "备注信息", example = "重要消息")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}