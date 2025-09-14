package com.admin.module.notification.api.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户站内信统计VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信统计VO")
public class UserInternalMessageStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总站内信数量", example = "100")
    private Long totalCount;

    @Schema(description = "未读站内信数量", example = "10")
    private Long unreadCount;

    @Schema(description = "已读站内信数量", example = "90")
    private Long readCount;

    @Schema(description = "收藏站内信数量", example = "5")
    private Long favoriteCount;

    @Schema(description = "草稿箱数量", example = "2")
    private Long draftCount;

    @Schema(description = "已发送数量", example = "80")
    private Long sentCount;

    @Schema(description = "已删除数量", example = "3")
    private Long deletedCount;

    @Schema(description = "已回执数量", example = "60")
    private Long receiptedCount;
}
