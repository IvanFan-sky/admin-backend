package com.admin.module.notification.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统公告状态枚举
 * 
 * 定义系统公告的发布和管理状态
 * 用于公告状态管理和流程控制
 *
 * @author admin
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum SystemAnnouncementStatusEnum {
    
    /**
     * 草稿状态
     * 公告已创建但尚未发布
     */
    DRAFT(0, "草稿", "#d9d9d9"),
    
    /**
     * 已发布状态
     * 公告已成功发布并对用户可见
     */
    PUBLISHED(1, "已发布", "#52c41a"),
    
    /**
     * 已下线状态
     * 公告已被管理员下线或过期
     */
    OFFLINE(2, "已下线", "#ff4d4f");
    
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
    public static SystemAnnouncementStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SystemAnnouncementStatusEnum status : values()) {
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
        SystemAnnouncementStatusEnum status = getByCode(code);
        return status != null ? status.getName() : "";
    }
    
    /**
     * 根据代码获取颜色
     *
     * @param code 状态代码
     * @return 颜色值，如果不存在则返回空字符串
     */
    public static String getColorByCode(Integer code) {
        SystemAnnouncementStatusEnum status = getByCode(code);
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