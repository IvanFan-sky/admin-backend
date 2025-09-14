package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知发布类型枚举
 * 
 * 定义通知的发布方式和时机
 * 用于通知发布策略控制
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum NotificationPublishTypeEnum {
    
    /**
     * 立即发布
     * 创建后立即发布通知
     */
    IMMEDIATE(1, "立即发布"),
    
    /**
     * 定时发布
     * 在指定时间发布通知
     */
    SCHEDULED(2, "定时发布");
    
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
    public static NotificationPublishTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationPublishTypeEnum publishType : values()) {
            if (publishType.getCode().equals(code)) {
                return publishType;
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
        NotificationPublishTypeEnum publishType = getByCode(code);
        return publishType != null ? publishType.getName() : "";
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