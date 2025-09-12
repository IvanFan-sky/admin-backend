package com.admin.module.log.api.constants;

/**
 * 日志常量定义
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogConstants {

    /**
     * 日志状态 - 成功
     */
    Integer LOG_STATUS_SUCCESS = 1;

    /**
     * 日志状态 - 失败
     */
    Integer LOG_STATUS_FAILED = 0;

    /**
     * 请求参数最大长度
     */
    Integer REQUEST_PARAM_MAX_LENGTH = 2000;

    /**
     * 响应结果最大长度
     */
    Integer RESPONSE_RESULT_MAX_LENGTH = 2000;

    /**
     * 错误消息最大长度
     */
    Integer ERROR_MESSAGE_MAX_LENGTH = 2000;

    /**
     * 默认操作类型 - 其他
     */
    Integer DEFAULT_OPERATOR_TYPE = 1;

    /**
     * 默认业务类型 - 其他
     */
    Integer DEFAULT_BUSINESS_TYPE = 0;

    /**
     * 日志类型 - 操作日志
     */
    String LOG_TYPE_OPERATION = "operation";

    /**
     * 日志类型 - 登录日志
     */
    String LOG_TYPE_LOGIN = "login";
}