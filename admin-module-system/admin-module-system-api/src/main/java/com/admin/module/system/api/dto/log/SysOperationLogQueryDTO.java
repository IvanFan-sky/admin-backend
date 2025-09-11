package com.admin.module.system.api.dto.log;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统操作日志查询DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "管理后台 - 操作日志查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperationLogQueryDTO extends PageQuery {

    @Schema(description = "模块标题", example = "用户管理")
    private String title;

    @Schema(description = "业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "操作人员", example = "admin")
    private String operatorName;

    @Schema(description = "操作人员ID", example = "1")
    private Long operatorId;

    @Schema(description = "操作状态（1正常 0异常）", example = "1")
    private Integer status;

    @Schema(description = "操作IP地址", example = "192.168.1.100")
    private String operationIp;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "请求方式", example = "GET")
    private String requestMethod;

    @Schema(description = "操作类别（0其他 1后台用户 2手机端用户）", example = "1")
    private Integer operatorType;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "最小耗时（毫秒）", example = "100")
    private Long minCostTime;

    @Schema(description = "最大耗时（毫秒）", example = "5000")
    private Long maxCostTime;
}