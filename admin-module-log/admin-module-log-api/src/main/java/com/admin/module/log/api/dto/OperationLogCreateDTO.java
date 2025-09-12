package com.admin.module.log.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 操作日志创建DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "操作日志创建DTO")
@Data
public class OperationLogCreateDTO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "模块标题", example = "用户管理")
    @Size(max = 50, message = "模块标题长度不能超过50个字符")
    private String title;

    @Schema(description = "业务类型：0-其它，1-新增，2-修改，3-删除，4-授权，5-导出，6-导入，7-强退，8-生成代码，9-清空数据", example = "1")
    private Integer businessType;

    @Schema(description = "方法名称", example = "com.admin.module.system.controller.UserController.createUser")
    @Size(max = 100, message = "方法名称长度不能超过100个字符")
    private String method;

    @Schema(description = "请求方式", example = "POST")
    @Size(max = 10, message = "请求方式长度不能超过10个字符")
    private String requestMethod;

    @Schema(description = "操作类别：0-其它，1-后台用户，2-手机端用户", example = "1")
    private Integer operatorType;

    @Schema(description = "操作人员", example = "admin")
    @Size(max = 50, message = "操作人员长度不能超过50个字符")
    private String operName;

    @Schema(description = "请求URL", example = "/admin-api/system/users/create")
    @Size(max = 255, message = "请求URL长度不能超过255个字符")
    private String operUrl;

    @Schema(description = "主机地址", example = "127.0.0.1")
    @Size(max = 50, message = "主机地址长度不能超过50个字符")
    private String operIp;

    @Schema(description = "操作地点", example = "内网IP")
    @Size(max = 255, message = "操作地点长度不能超过255个字符")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态：0-正常，1-异常", example = "0")
    @NotNull(message = "操作状态不能为空")
    private Integer status;

    @Schema(description = "错误消息")
    @Size(max = 2000, message = "错误消息长度不能超过2000个字符")
    private String errorMsg;

    @Schema(description = "操作时间")
    private LocalDateTime operTime;

    @Schema(description = "消耗时间(毫秒)", example = "100")
    private Long costTime;
}