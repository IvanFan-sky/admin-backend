package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作员统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "操作员统计")
public class OperatorStatisticsVO {

    @Schema(description = "操作员ID", example = "1")
    private Long operatorId;

    @Schema(description = "操作员用户名", example = "admin")
    private String username;

    @Schema(description = "操作员昵称", example = "管理员")
    private String nickname;

    @Schema(description = "操作次数", example = "50")
    private Long count;

    @Schema(description = "占比", example = "15.5")
    private Double percentage;
}