package com.admin.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

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

    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(String message) {
        return restResult(null, SUCCESS, message);
    }

    public static <T> R<T> ok(String message, T data) {
        return restResult(data, SUCCESS, message);
    }

    public static <T> R<T> error() {
        return restResult(null, ERROR, "操作失败");
    }

    public static <T> R<T> error(String message) {
        return restResult(null, ERROR, message);
    }

    public static <T> R<T> error(int code, String message) {
        return restResult(null, code, message);
    }

    public static <T> R<T> error(String message, T data) {
        return restResult(data, ERROR, message);
    }

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