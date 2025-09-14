package com.admin.module.notification.api.vo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内信简单VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "站内信简单VO")
public class InternalMessageSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站内信ID", example = "1")
    private Long id;

    @Schema(description = "消息标题", example = "系统通知")
    private String title;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "消息类型名称", example = "系统消息")
    private String typeName;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "优先级名称", example = "普通")
    private String priorityName;

    @Schema(description = "发送状态", example = "1", allowableValues = {"0", "1", "2", "3"})
    private Integer status; // 0-草稿，1-已发送，2-发送失败，3-已撤回

    @Schema(description = "发送状态名称", example = "已发送")
    private String statusName;

    @Schema(description = "发送者ID", example = "1")
    private Long senderId;

    @Schema(description = "发送者名称", example = "系统管理员")
    private String senderName;

    @Schema(description = "成功发送数量", example = "100")
    private Integer successCount;

    @Schema(description = "发送失败数量", example = "0")
    private Integer failureCount;

    @Schema(description = "已读数量", example = "85")
    private Integer readCount;

    @Schema(description = "回执数量", example = "60")
    private Integer receiptCount;

    @Schema(description = "定时发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;
}
