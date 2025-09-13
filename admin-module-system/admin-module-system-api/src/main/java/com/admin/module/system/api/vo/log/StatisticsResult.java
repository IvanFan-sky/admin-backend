package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录统计结果VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "登录统计结果")
public class StatisticsResult {

    @Schema(description = "总登录次数", example = "1000")
    private Long totalCount;

    @Schema(description = "成功登录次数", example = "950")
    private Long successCount;

    @Schema(description = "失败登录次数", example = "50")
    private Long failCount;

    @Schema(description = "在线用户数", example = "25")
    private Long onlineCount;

    @Schema(description = "平均在线时长（分钟）", example = "120.5")
    private Double avgOnlineDuration;
}