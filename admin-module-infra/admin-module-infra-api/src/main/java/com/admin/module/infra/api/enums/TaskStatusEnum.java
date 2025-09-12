package com.admin.module.infra.api.enums;

/**
 * 任务状态枚举
 * 
 * 定义导入导出任务的各种状态
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public enum TaskStatusEnum {

    /**
     * 待处理
     * 任务已创建，等待处理
     */
    PENDING(0, "待处理"),

    /**
     * 处理中
     * 任务正在执行中
     */
    PROCESSING(1, "处理中"),

    /**
     * 已完成
     * 任务执行成功完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败
     * 任务执行失败
     */
    FAILED(3, "失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    TaskStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return TaskStatusEnum枚举，如果未找到返回null
     */
    public static TaskStatusEnum getByCode(Integer code) {
        for (TaskStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为最终状态（已完成或失败）
     *
     * @return true-最终状态，false-非最终状态
     */
    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, description);
    }
}