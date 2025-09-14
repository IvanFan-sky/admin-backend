package com.admin.module.notification.api.dto.message;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户站内信分页查询DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户站内信分页查询DTO")
public class UserInternalMessagePageReqDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "读取状态", example = "0", allowableValues = {"0", "1"})
    private Integer readStatus; // 0-未读，1-已读

    @Schema(description = "收藏状态", example = "false")
    private Boolean favorite;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "发送者ID", example = "1")
    private Long senderId;

    @Schema(description = "关键字搜索（标题或内容）", example = "通知")
    private String keyword;

    @Schema(description = "创建时间-开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "接收时间-开始")
    private LocalDateTime receiveTimeStart;

    @Schema(description = "接收时间-结束")
    private LocalDateTime receiveTimeEnd;
}