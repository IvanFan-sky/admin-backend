package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 站内信状态枚举
 * 
 * 定义站内信的发送和处理状态
 * 用于站内信状态管理和流程控制
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum InternalMessageStatusEnum {
    
    /**
     * 草稿状态
     * 消息已创建但尚未发送
     */
    DRAFT(0, "草稿", "#d9d9d9"),
    
    /**
     * 已发送状态
     * 消息已成功发送给目标用户
     */
    SENT(1, "已发送", "#52c41a"),
    
    /**
     * 已撤回状态
     * 消息已被发送者撤回
     */
    REVOKED(2, "已撤回", "#faad14");
    
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
    public static InternalMessageStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (InternalMessageStatusEnum status : values()) {
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
        InternalMessageStatusEnum status = getByCode(code);
        return status != null ? status.getName() : "";
    }
    
    /**
     * 根据代码获取颜色
     *
     * @param code 状态代码
     * @return 颜色值，如果不存在则返回空字符串
     */
    public static String getColorByCode(Integer code) {
        InternalMessageStatusEnum status = getByCode(code);
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