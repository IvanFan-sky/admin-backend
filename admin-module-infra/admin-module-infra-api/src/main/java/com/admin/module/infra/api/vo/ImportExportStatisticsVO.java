package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 导入导出统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出统计信息")
@Data
public class ImportExportStatisticsVO {

    @Schema(description = "今日导入任务数", example = "5")
    private Long todayImportCount;

    @Schema(description = "今日导出任务数", example = "8")
    private Long todayExportCount;

    @Schema(description = "本月导入任务数", example = "120")
    private Long monthImportCount;

    @Schema(description = "本月导出任务数", example = "180")
    private Long monthExportCount;

    @Schema(description = "处理中的任务数", example = "3")
    private Long processingCount;

    @Schema(description = "待处理的任务数", example = "12")
    private Long pendingCount;

    @Schema(description = "今日成功率", example = "95.5")
    private Double todaySuccessRate;

    @Schema(description = "本月成功率", example = "97.8")
    private Double monthSuccessRate;
}