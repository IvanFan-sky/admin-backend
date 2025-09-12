package com.admin.module.log.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "操作日志信息")
@Data
public class OperationLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "模块标题", example = "用户管理")
    private String title;

    @Schema(description = "业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "方法名称", example = "com.admin.module.system.controller.UserController.createUser")
    private String method;

    @Schema(description = "请求方式", example = "POST")
    private String requestMethod;

    @Schema(description = "操作类别", example = "1")
    private Integer operatorType;

    @Schema(description = "操作人员", example = "admin")
    private String operName;

    @Schema(description = "请求URL", example = "/admin-api/system/users/create")
    private String operUrl;

    @Schema(description = "主机地址", example = "127.0.0.1")
    private String operIp;

    @Schema(description = "操作地点", example = "内网IP")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态：0-正常，1-异常", example = "0")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "操作时间")
    private LocalDateTime operTime;

    @Schema(description = "消耗时间(毫秒)", example = "100")
    private Long costTime;
}