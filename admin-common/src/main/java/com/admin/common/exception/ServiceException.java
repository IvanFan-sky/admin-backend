package com.admin.common.exception;

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

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}