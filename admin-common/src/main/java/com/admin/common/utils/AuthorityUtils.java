package com.admin.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 权限工具类 - 不依赖任何业务模块
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class AuthorityUtils {

    /**
     * 解析权限字符串为角色和权限列表
     */
    public static AuthorityInfo parseAuthorities(String authoritiesStr) {
        AuthorityInfo authorityInfo = new AuthorityInfo();
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        
        if (StringUtils.hasText(authoritiesStr)) {
            String[] authArray = authoritiesStr.split(",");
            
            for (String auth : authArray) {
                auth = auth.trim();
                if (auth.startsWith("ROLE_")) {
                    // 去掉ROLE_前缀
                    roles.add(auth.substring(5));
                } else {
                    permissions.add(auth);
                }
            }
        }
        
        authorityInfo.setRoles(roles);
        authorityInfo.setPermissions(permissions);
        return authorityInfo;
    }

    /**
     * 构建权限字符串
     */
    public static String buildAuthoritiesString(List<String> roles, List<String> permissions) {
        List<String> authorities = new ArrayList<>();
        
        // 添加角色权限（以ROLE_开头）
        if (roles != null) {
            roles.forEach(role -> authorities.add("ROLE_" + role));
        }
        
        // 添加功能权限
        if (permissions != null) {
            authorities.addAll(permissions);
        }
        
        return String.join(",", authorities);
    }

    /**
     * 生成令牌ID（用于多设备登录管理）
     */
    public static String generateTokenId(String accessToken) {
        // 使用UUID基于token生成唯一ID
        return UUID.nameUUIDFromBytes(accessToken.getBytes()).toString();
    }

    /**
     * 获取当前认证信息
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 检查当前用户是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && authentication.getPrincipal() != null;
    }

    /**
     * 权限信息类
     */
    public static class AuthorityInfo {
        private List<String> roles = new ArrayList<>();
        private List<String> permissions = new ArrayList<>();

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles != null ? roles : new ArrayList<>();
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions != null ? permissions : new ArrayList<>();
        }
    }

    /**
     * 用户在线信息构建器
     */
    public static class OnlineInfoBuilder {
        private final Map<String, Object> info = new HashMap<>();

        public OnlineInfoBuilder userId(Long userId) {
            info.put("userId", userId);
            return this;
        }

        public OnlineInfoBuilder username(String username) {
            info.put("username", username);
            return this;
        }

        public OnlineInfoBuilder loginTime(java.util.Date loginTime) {
            info.put("loginTime", loginTime);
            return this;
        }

        public OnlineInfoBuilder lastActiveTime(java.util.Date lastActiveTime) {
            info.put("lastActiveTime", lastActiveTime);
            return this;
        }

        public OnlineInfoBuilder loginIp(String loginIp) {
            info.put("loginIp", loginIp);
            return this;
        }

        public OnlineInfoBuilder userAgent(String userAgent) {
            info.put("userAgent", userAgent);
            return this;
        }

        public OnlineInfoBuilder tokenId(String tokenId) {
            info.put("tokenId", tokenId);
            return this;
        }

        public Map<String, Object> build() {
            return new HashMap<>(info);
        }

        public static OnlineInfoBuilder create() {
            return new OnlineInfoBuilder();
        }
    }
}