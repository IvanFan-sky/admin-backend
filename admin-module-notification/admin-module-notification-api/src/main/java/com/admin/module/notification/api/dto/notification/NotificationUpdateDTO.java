package com.admin.module.notification.api.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知更新请求DTO
 * 
 * 用于接收前端更新通知的请求参数
 * 包含通知基本信息和状态更新
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "通知更新请求对象")
public class NotificationUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "通知ID不能为空")
    private Long id;

    @Schema(description = "通知类型ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "通知类型不能为空")
    private Long typeId;

    @Schema(description = "通知标题", example = "系统维护通知", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 200, message = "通知标题长度不能超过200个字符")
    private String title;

    @Schema(description = "通知内容", example = "系统将于今晚22:00-24:00进行维护，请提前保存工作")
    @Size(max = 2000, message = "通知内容长度不能超过2000个字符")
    private String content;

    @Schema(description = "通知级别", example = "1", allowableValues = {"1", "2", "3"})
    private Integer level;

    @Schema(description = "通知状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "定时推送时间", example = "2024-01-15T10:00:00")
    private LocalDateTime scheduledTime;

    @Schema(description = "扩展数据（JSON格式）", example = "{\"url\":\"https://example.com\"}")
    private String extraData;

    @Schema(description = "备注信息", example = "重要系统通知")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

}