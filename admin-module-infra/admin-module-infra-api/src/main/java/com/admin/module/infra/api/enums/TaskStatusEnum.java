package com.admin.module.infra.api.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum TaskStatusEnum {

    /**
     * 待处理
     */
    PENDING("PENDING", "待处理"),

    /**
     * 处理中
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 失败
     */
    FAILED("FAILED", "失败"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String message;

    TaskStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据代码获取枚举
     * 
     * @param code 状态代码
     * @return 枚举值
     */
    public static TaskStatusEnum getByCode(String code) {
        for (TaskStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否为最终状态
     * 
     * @return true-最终状态 false-中间状态
     */
    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * 是否为运行状态
     * 
     * @return true-运行中 false-非运行状态
     */
    public boolean isRunning() {
        return this == PENDING || this == PROCESSING;
    }
}
