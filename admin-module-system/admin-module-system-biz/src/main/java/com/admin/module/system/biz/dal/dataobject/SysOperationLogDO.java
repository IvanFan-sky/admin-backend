package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统操作日志数据对象
 * 
 * 对应数据库sys_operation_log表
 * 记录用户的各种操作行为日志
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
public class SysOperationLogDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块标题
     */
    @TableField("title")
    private String title;

    /**
     * 业务类型（0其他 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8生成代码 9清空数据）
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 方法名称
     */
    @TableField("method")
    private String method;

    /**
     * 请求方式
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 操作类别（0其他 1后台用户 2手机端用户）
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 操作人员ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 请求URL
     */
    @TableField("operation_url")
    private String operationUrl;

    /**
     * 主机地址
     */
    @TableField("operation_ip")
    private String operationIp;

    /**
     * 操作地点
     */
    @TableField("operation_location")
    private String operationLocation;

    /**
     * 请求参数
     */
    @TableField("operation_param")
    private String operationParam;

    /**
     * 返回参数
     */
    @TableField("json_result")
    private String jsonResult;

    /**
     * 操作状态（1正常 0异常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField("operation_time")
    private LocalDateTime operationTime;

    /**
     * 消耗时间（毫秒）
     */
    @TableField("cost_time")
    private Long costTime;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 浏览器类型
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;
}