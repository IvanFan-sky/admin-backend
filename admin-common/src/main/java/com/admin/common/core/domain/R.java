package com.admin.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * 
 * 用于封装所有API接口的响应数据
 * 提供统一的响应格式，包含状态码、消息和数据
 *
 * @param <T> 响应数据的类型
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;
    
    /**
     * 错误状态码
     */
    public static final int ERROR = 500;

    /**
     * 响应状态码
     * 200-成功 其他-失败
     */
    @JsonProperty("code")
    private int code;

    /**
     * 响应消息
     * 成功或失败的描述信息
     */
    @JsonProperty("message")
    private String message;

    /**
     * 响应数据
     * 具体的业务数据内容
     */
    @JsonProperty("data")
    private T data;

    public R() {}

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功结果
     *
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    /**
     * 返回成功结果（带数据）
     *
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    /**
     * 返回成功结果（自定义消息）
     *
     * @param <T> 数据类型
     * @param message 自定义成功消息
     * @return 成功响应对象
     */
    public static <T> R<T> ok(String message) {
        return restResult(null, SUCCESS, message);
    }

    /**
     * 返回成功结果（自定义消息和数据）
     *
     * @param <T> 数据类型
     * @param message 自定义成功消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> R<T> ok(String message, T data) {
        return restResult(data, SUCCESS, message);
    }

    /**
     * 返回错误结果
     *
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> R<T> error() {
        return restResult(null, ERROR, "操作失败");
    }

    /**
     * 返回错误结果（自定义消息）
     *
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> R<T> error(String message) {
        return restResult(null, ERROR, message);
    }

    /**
     * 返回错误结果（自定义状态码和消息）
     *
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> R<T> error(int code, String message) {
        return restResult(null, code, message);
    }

    /**
     * 返回错误结果（自定义消息和数据）
     *
     * @param <T> 数据类型
     * @param message 错误消息
     * @param data 响应数据
     * @return 错误响应对象
     */
    public static <T> R<T> error(String message, T data) {
        return restResult(data, ERROR, message);
    }

    /**
     * 返回错误结果（自定义状态码、消息和数据）
     *
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @param data 响应数据
     * @return 错误响应对象
     */
    public static <T> R<T> error(int code, String message, T data) {
        return restResult(data, code, message);
    }

    private static <T> R<T> restResult(T data, int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMessage(message);
        return r;
    }

    public boolean isSuccess() {
        return SUCCESS == this.code;
    }

    public boolean isError() {
        return !isSuccess();
    }
}