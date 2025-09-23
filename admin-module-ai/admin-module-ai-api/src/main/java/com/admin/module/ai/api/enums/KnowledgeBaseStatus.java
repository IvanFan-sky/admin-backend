package com.admin.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 知识库状态枚举
 *
 * @author admin
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum KnowledgeBaseStatus {
    
    NORMAL(1, "正常"),
    MAINTENANCE(2, "维护中"),
    DISABLED(3, "禁用");
    
    private final Integer code;
    private final String description;
    
    public static KnowledgeBaseStatus getByCode(Integer code) {
        for (KnowledgeBaseStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}