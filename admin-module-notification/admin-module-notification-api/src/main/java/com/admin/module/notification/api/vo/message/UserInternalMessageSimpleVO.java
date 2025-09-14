package com.admin.module.notification.api.vo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户站内信简单VO
 * 
 * 用于列表展示的简化版本，包含基本信息
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信简单VO")
public class UserInternalMessageSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户站内信ID", example = "1")
    private Long id;

    @Schema(description = "站内信ID", example = "101")
    private Long messageId;

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

    @Schema(description = "发送者名称", example = "系统管理员")
    private String senderName;

    @Schema(description = "读取状态", example = "0", allowableValues = {"0", "1"})
    private Integer readStatus; // 0-未读，1-已读

    @Schema(description = "读取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Schema(description = "是否收藏", example = "false")
    private Boolean favorite;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
