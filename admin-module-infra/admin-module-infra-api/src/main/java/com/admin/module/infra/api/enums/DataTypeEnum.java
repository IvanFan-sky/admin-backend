package com.admin.module.infra.api.enums;

/**
 * 数据类型枚举
 * 
 * 定义导入导出支持的数据类型
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public enum DataTypeEnum {

    /**
     * 用户数据
     */
    USER("user", "用户数据"),

    /**
     * 角色数据
     */
    ROLE("role", "角色数据"),

    /**
     * 操作日志
     */
    OPERATION_LOG("operation_log", "操作日志");

    /**
     * 数据类型代码
     */
    private final String code;

    /**
     * 数据类型描述
     */
    private final String description;

    DataTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取数据类型代码
     *
     * @return 数据类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取数据类型描述
     *
     * @return 数据类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 数据类型代码
     * @return DataTypeEnum枚举，如果未找到返回null
     */
    public static DataTypeEnum getByCode(String code) {
        for (DataTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为支持的数据类型
     *
     * @param code 数据类型代码
     * @return true-支持，false-不支持
     */
    public static boolean isSupported(String code) {
        return getByCode(code) != null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, description);
    }
}