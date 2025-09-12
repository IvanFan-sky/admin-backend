package com.admin.module.infra.api.enums;

/**
 * 任务类型枚举
 * 
 * 定义导入导出任务的类型
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public enum TaskTypeEnum {

    /**
     * 导入任务
     */
    IMPORT(1, "导入"),

    /**
     * 导出任务
     */
    EXPORT(2, "导出");

    /**
     * 类型码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String description;

    TaskTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取类型码
     *
     * @return 类型码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取类型描述
     *
     * @return 类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据类型码获取枚举
     *
     * @param code 类型码
     * @return TaskTypeEnum枚举，如果未找到返回null
     */
    public static TaskTypeEnum getByCode(Integer code) {
        for (TaskTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, description);
    }
}