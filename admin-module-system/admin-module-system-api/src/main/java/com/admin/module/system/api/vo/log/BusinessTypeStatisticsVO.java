package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务类型统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "业务类型统计")
public class BusinessTypeStatisticsVO {

    @Schema(description = "业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "业务类型名称", example = "用户管理")
    private String businessTypeName;

    @Schema(description = "操作次数", example = "100")
    private Long count;

    @Schema(description = "占比", example = "25.5")
    private Double percentage;
}