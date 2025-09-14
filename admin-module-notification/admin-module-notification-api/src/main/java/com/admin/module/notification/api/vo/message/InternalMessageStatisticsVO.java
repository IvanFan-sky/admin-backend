package com.admin.module.notification.api.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 站内信统计VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "站内信统计VO")
public class InternalMessageStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总站内信数量", example = "100")
    private Long totalCount;

    @Schema(description = "已接收数量", example = "95")
    private Long receivedCount;

    @Schema(description = "发送失败数量", example = "5")
    private Long failedCount;

    @Schema(description = "已读数量", example = "80")
    private Long readCount;

    @Schema(description = "已回执数量", example = "60")
    private Long receiptCount;

    @Schema(description = "收藏数量", example = "10")
    private Long favoriteCount;

    @Schema(description = "接收成功率", example = "95.0")
    private Double receivedRate;

    @Schema(description = "发送失败率", example = "5.0")
    private Double failedRate;

    @Schema(description = "已读率", example = "84.2")
    private Double readRate;

    @Schema(description = "回执率", example = "63.2")
    private Double receiptRate;

    @Schema(description = "收藏率", example = "10.5")
    private Double favoriteRate;
}
