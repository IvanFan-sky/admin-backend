package com.admin.module.notification.api.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户站内信类型统计VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信类型统计VO")
public class UserInternalMessageTypeCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息类型", example = "1")
    private Integer type;

    @Schema(description = "消息类型名称", example = "系统消息")
    private String typeName;

    @Schema(description = "总数量", example = "100")
    private Long totalCount;

    @Schema(description = "未读数量", example = "15")
    private Long unreadCount;

    @Schema(description = "已读数量", example = "85")
    private Long readCount;

    @Schema(description = "收藏数量", example = "10")
    private Long favoriteCount;

    @Schema(description = "已删除数量", example = "3")
    private Long deletedCount;
}