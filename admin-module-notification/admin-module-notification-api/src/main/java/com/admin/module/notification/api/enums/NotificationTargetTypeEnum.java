package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知目标类型枚举
 * 
 * 定义通知推送的目标用户类型
 * 用于通知推送范围控制
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum NotificationTargetTypeEnum {
    
    /**
     * 全部用户
     * 推送给系统中的所有用户
     */
    ALL_USERS(1, "全部用户"),
    
    /**
     * 指定用户
     * 推送给特定的用户列表
     */
    SPECIFIC_USERS(2, "指定用户"),
    
    /**
     * 指定角色
     * 推送给特定角色的所有用户
     */
    SPECIFIC_ROLES(3, "指定角色");
    
    /**
     * 类型代码
     */
    private final Integer code;
    
    /**
     * 类型名称
     */
    private final String name;
    
    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static NotificationTargetTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationTargetTypeEnum targetType : values()) {
            if (targetType.getCode().equals(code)) {
                return targetType;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取名称
     *
     * @param code 类型代码
     * @return 类型名称，如果不存在则返回空字符串
     */
    public static String getNameByCode(Integer code) {
        NotificationTargetTypeEnum targetType = getByCode(code);
        return targetType != null ? targetType.getName() : "";
    }
    
    /**
     * 检查代码是否有效
     *
     * @param code 类型代码
     * @return 是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}