package com.admin.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对话状态枚举
 *
 * @author admin
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum ChatStatus {
    
    ACTIVE(1, "活跃"),
    ARCHIVED(2, "归档"),
    DELETED(3, "删除");
    
    private final Integer code;
    private final String description;
    
    public static ChatStatus getByCode(Integer code) {
        for (ChatStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}