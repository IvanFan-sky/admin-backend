package com.admin.module.log.api.enums;

import lombok.Getter;

/**
 * 日志状态枚举
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum LogStatusEnum {

    /**
     * 失败
     */
    FAILED(0, "失败"),

    /**
     * 成功
     */
    SUCCESS(1, "成功");

    private final Integer code;
    private final String description;

    LogStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}