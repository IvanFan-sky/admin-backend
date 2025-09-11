package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "管理后台 - 操作日志信息")
@Data
public class SysOperationLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "模块标题", example = "用户管理")
    private String title;

    @Schema(description = "业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "业务类型名称", example = "新增")
    private String businessTypeName;

    @Schema(description = "方法名称", example = "com.admin.module.system.biz.controller.admin.user.SysUserController.createUser()")
    private String method;

    @Schema(description = "请求方式", example = "POST")
    private String requestMethod;

    @Schema(description = "操作类别", example = "1")
    private Integer operatorType;

    @Schema(description = "操作类别名称", example = "后台用户")
    private String operatorTypeName;

    @Schema(description = "操作人员", example = "admin")
    private String operatorName;

    @Schema(description = "操作人员ID", example = "1")
    private Long operatorId;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "请求URL", example = "/system/user")
    private String operationUrl;

    @Schema(description = "主机地址", example = "192.168.1.100")
    private String operationIp;

    @Schema(description = "操作地点", example = "内网IP")
    private String operationLocation;

    @Schema(description = "请求参数", example = "{\"username\":\"test\",\"nickname\":\"测试用户\"}")
    private String operationParam;

    @Schema(description = "返回参数", example = "{\"code\":200,\"data\":123}")
    private String jsonResult;

    @Schema(description = "操作状态", example = "1")
    private Integer status;

    @Schema(description = "操作状态名称", example = "正常")
    private String statusName;

    @Schema(description = "错误消息", example = "")
    private String errorMsg;

    @Schema(description = "操作时间", example = "2024-01-15 10:30:00")
    private LocalDateTime operationTime;

    @Schema(description = "消耗时间（毫秒）", example = "150")
    private Long costTime;

    @Schema(description = "用户代理", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    private String userAgent;

    @Schema(description = "浏览器类型", example = "Chrome 120.0")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "创建时间", example = "2024-01-15 10:30:00")
    private LocalDateTime createTime;
}