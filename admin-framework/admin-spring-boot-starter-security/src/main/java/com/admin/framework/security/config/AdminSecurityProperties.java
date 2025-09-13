package com.admin.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Admin安全组件配置属性
 * 
 * 定义安全组件的可配置参数
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.security")
public class AdminSecurityProperties {

    /**
     * 是否启用安全组件
     */
    private boolean enabled = true;

    /**
     * 是否启用安全上下文工具
     */
    private boolean contextEnabled = true;

    /**
     * 是否启用权限检查
     */
    private boolean permissionEnabled = true;

    /**
     * 匿名用户默认用户名
     */
    private String anonymousUsername = "anonymous";

    /**
     * 匿名用户默认用户ID
     */
    private Long anonymousUserId = 0L;

    /**
     * Token配置
     */
    private TokenConfig token = new TokenConfig();

    /**
     * 权限配置
     */
    private PermissionConfig permission = new PermissionConfig();

    @Data
    public static class TokenConfig {
        /**
         * Token头名称
         */
        private String header = "Authorization";

        /**
         * Token前缀
         */
        private String prefix = "Bearer ";

        /**
         * Token过期时间（小时）
         */
        private Integer expireHours = 24;

        /**
         * 刷新Token过期时间（天）
         */
        private Integer refreshExpireDays = 7;
    }

    @Data
    public static class PermissionConfig {
        /**
         * 超级管理员角色标识
         */
        private String adminRole = "ADMIN";

        /**
         * 权限缓存时间（分钟）
         */
        private Integer cacheMinutes = 30;

        /**
         * 是否启用权限缓存
         */
        private boolean cacheEnabled = true;
    }

    @Override
    public String toString() {
        return String.format("AdminSecurityProperties{enabled=%s, contextEnabled=%s, permissionEnabled=%s}", 
                enabled, contextEnabled, permissionEnabled);
    }
}