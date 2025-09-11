package com.admin.module.system.api.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "操作日志DTO")
public class OperationLogDTO {

    @Schema(description = "操作标题")
    private String title;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "操作方法")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "操作类别")
    private Integer operatorType;

    @Schema(description = "操作人员")
    private String operatorName;

    @Schema(description = "操作人员ID")
    private Long operatorId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "请求URL")
    private String operationUrl;

    @Schema(description = "主机地址")
    private String operationIp;

    @Schema(description = "操作地点")
    private String operationLocation;

    @Schema(description = "请求参数")
    private String operationParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "消耗时间")
    private Long costTime;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;
}