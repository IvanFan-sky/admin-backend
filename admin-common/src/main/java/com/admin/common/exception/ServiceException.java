package com.admin.common.exception;

import com.admin.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常类
 * 
 * 用于封装业务逻辑中的异常情况
 * 提供自定义错误码和错误消息功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误状态码
     * 业务相关的自定义错误码
     */
    private Integer code;

    /**
     * 错误消息
     * 具体的错误描述信息
     */
    private String message;

    public ServiceException() {}

    public ServiceException(String message) {
        this.message = message;
    }

    /**
     * 基于ErrorCode枚举构造异常
     *
     * @param errorCode 错误码枚举
     */
    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 基于ErrorCode枚举和自定义消息构造异常
     *
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息，如果为null则使用默认消息
     */
    public ServiceException(ErrorCode errorCode, String customMessage) {
        this.code = errorCode.getCode();
        this.message = customMessage != null ? customMessage : errorCode.getMessage();
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    /**
     * 基于ErrorCode枚举和根异常构造异常
     *
     * @param errorCode 错误码枚举
     * @param e 根异常
     */
    public ServiceException(ErrorCode errorCode, Throwable e) {
        super(errorCode.getMessage(), e);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}