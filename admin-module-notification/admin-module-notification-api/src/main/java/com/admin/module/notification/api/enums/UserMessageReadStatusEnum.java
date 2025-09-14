package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户消息读取状态枚举
 * 
 * 定义用户对消息的读取状态
 * 用于消息已读未读管理
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum UserMessageReadStatusEnum {
    
    /**
     * 未读状态
     * 用户尚未阅读该消息
     */
    UNREAD(0, "未读", "#faad14"),
    
    /**
     * 已读状态
     * 用户已经阅读该消息
     */
    READ(1, "已读", "#52c41a");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态名称
     */
    private final String name;
    
    /**
     * 显示颜色
     */
    private final String color;
    
    /**
     * 根据代码获取枚举
     *
     * @param code 状态代码
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static UserMessageReadStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserMessageReadStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取名称
     *
     * @param code 状态代码
     * @return 状态名称，如果不存在则返回空字符串
     */
    public static String getNameByCode(Integer code) {
        UserMessageReadStatusEnum status = getByCode(code);
        return status != null ? status.getName() : "";
    }
    
    /**
     * 根据代码获取颜色
     *
     * @param code 状态代码
     * @return 颜色值，如果不存在则返回空字符串
     */
    public static String getColorByCode(Integer code) {
        UserMessageReadStatusEnum status = getByCode(code);
        return status != null ? status.getColor() : "";
    }
    
    /**
     * 检查代码是否有效
     *
     * @param code 状态代码
     * @return 是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}