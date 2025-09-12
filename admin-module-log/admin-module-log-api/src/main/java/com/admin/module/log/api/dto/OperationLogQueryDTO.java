package com.admin.module.log.api.dto;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "操作日志查询DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogQueryDTO extends PageQuery {

    @Schema(description = "模块标题", example = "用户管理")
    private String title;

    @Schema(description = "业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "操作人员", example = "admin")
    private String operName;

    @Schema(description = "操作状态：0-正常，1-异常", example = "0")
    private Integer status;

    @Schema(description = "请求方式", example = "POST")
    private String requestMethod;

    @Schema(description = "操作类别：0-其它，1-后台用户，2-手机端用户", example = "1")
    private Integer operatorType;

    @Schema(description = "操作地点", example = "内网IP")
    private String operLocation;

    @Schema(description = "最小耗时（毫秒）", example = "100")
    private Long minCostTime;

    @Schema(description = "最大耗时（毫秒）", example = "5000")
    private Long maxCostTime;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;
}