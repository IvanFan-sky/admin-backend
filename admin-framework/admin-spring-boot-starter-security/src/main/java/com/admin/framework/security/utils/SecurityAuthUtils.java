package com.admin.framework.security.utils;

import com.admin.common.utils.AuthorityUtils;
import com.admin.common.utils.ServletUtils;
import com.admin.framework.redis.service.UserCacheService;
import com.admin.framework.security.core.LoginUser;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * 安全认证工具类 - 只依赖框架和common模块
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class SecurityAuthUtils {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getCurrentUser() {
        Authentication authentication = AuthorityUtils.getCurrentAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUsername() : null;
    }

    /**
     * 检查当前用户是否已认证
     */
    public static boolean isAuthenticated() {
        return AuthorityUtils.isAuthenticated();
    }

    /**
     * 解析权限字符串为角色和权限列表
     */
    public static AuthorityUtils.AuthorityInfo parseAuthorities(String authoritiesStr) {
        return AuthorityUtils.parseAuthorities(authoritiesStr);
    }

    /**
     * 构建权限字符串
     */
    public static String buildAuthoritiesString(java.util.List<String> roles, java.util.List<String> permissions) {
        return AuthorityUtils.buildAuthoritiesString(roles, permissions);
    }

    /**
     * 生成令牌ID（用于多设备登录管理）
     */
    public static String generateTokenId(String accessToken) {
        return AuthorityUtils.generateTokenId(accessToken);
    }

    /**
     * 创建用户在线信息
     */
    public static UserCacheService.UserOnlineInfo createUserOnlineInfo(
            Long userId, String username, String loginIp, String userAgent, String tokenId) {
        
        return UserCacheService.UserOnlineInfo.builder()
                .userId(userId)
                .username(username)
                .loginTime(new java.util.Date())
                .lastActiveTime(new java.util.Date())
                .loginIp(loginIp != null ? loginIp : "Unknown")
                .userAgent(userAgent != null ? userAgent : "Unknown")
                .tokenId(tokenId)
                .build();
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress() {
        return ServletUtils.getClientIpAddress();
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        return ServletUtils.getUserAgent();
    }

    /**
     * 验证用户状态（通用方法）
     */
    public static void validateUserStatus(Object user) {
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
    }

    /**
     * 更新Map形式的用户信息权限
     */
    public static void updateUserInfoAuthorities(Map<String, Object> userInfo, String authorities) {
        AuthorityUtils.AuthorityInfo authorityInfo = parseAuthorities(authorities);
        userInfo.put("roles", authorityInfo.getRoles());
        userInfo.put("permissions", authorityInfo.getPermissions());
    }
}