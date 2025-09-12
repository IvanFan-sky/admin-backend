package com.admin.module.log.api.enums;

import lombok.Getter;

/**
 * 操作类别枚举
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum OperatorTypeEnum {

    /**
     * 其它
     */
    OTHER(0, "其它"),

    /**
     * 后台用户
     */
    MANAGE(1, "后台用户"),

    /**
     * 手机端用户
     */
    MOBILE(2, "手机端用户");

    private final Integer code;
    private final String description;

    OperatorTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}