package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知级别枚举
 * 
 * 定义通知的重要程度级别
 * 用于通知优先级排序和样式展示
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum NotificationLevelEnum {
    
    /**
     * 普通级别
     * 一般性通知消息
     */
    NORMAL(1, "普通", "#1890ff"),
    
    /**
     * 重要级别
     * 需要用户关注的重要消息
     */
    IMPORTANT(2, "重要", "#faad14"),
    
    /**
     * 紧急级别
     * 需要用户立即处理的紧急消息
     */
    URGENT(3, "紧急", "#ff4d4f");
    
    /**
     * 级别码
     */
    private final Integer code;
    
    /**
     * 级别名称
     */
    private final String name;
    
    /**
     * 显示颜色
     */
    private final String color;
    
    /**
     * 根据代码获取枚举
     *
     * @param code 级别代码
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static NotificationLevelEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取名称
     *
     * @param code 级别代码
     * @return 级别名称，如果不存在则返回空字符串
     */
    public static String getNameByCode(Integer code) {
        NotificationLevelEnum level = getByCode(code);
        return level != null ? level.getName() : "";
    }
    
    /**
     * 根据代码获取颜色
     *
     * @param code 级别代码
     * @return 颜色值，如果不存在则返回空字符串
     */
    public static String getColorByCode(Integer code) {
        NotificationLevelEnum level = getByCode(code);
        return level != null ? level.getColor() : "";
    }
    
    /**
     * 检查代码是否有效
     *
     * @param code 级别代码
     * @return 是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}