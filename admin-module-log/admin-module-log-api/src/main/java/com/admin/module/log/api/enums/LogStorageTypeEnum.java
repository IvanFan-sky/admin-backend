package com.admin.module.log.api.enums;

import lombok.Getter;

/**
 * 日志存储类型枚举
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum LogStorageTypeEnum {

    /**
     * 数据库存储
     */
    DATABASE("database", "数据库存储"),

    /**
     * 文件存储
     */
    FILE("file", "文件存储");

    private final String code;
    private final String description;

    LogStorageTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}