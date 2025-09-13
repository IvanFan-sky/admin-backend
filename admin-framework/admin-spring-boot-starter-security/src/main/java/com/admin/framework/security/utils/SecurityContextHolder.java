package com.admin.framework.security.utils;

import cn.hutool.core.util.StrUtil;
import com.admin.framework.security.core.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全上下文工具类
 * 
 * 提供当前登录用户信息的获取功能
 * 兼容Spring Security和自定义认证体系
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class SecurityContextHolder {

    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        try {
            Authentication authentication = getCurrentAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            
            // 处理LoginUser类型（自定义用户主体）
            if (principal instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) principal;
                return loginUser.getUserId();
            }
            
            // 处理UserDetails类型
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                // 如果用户名是数字，尝试解析为用户ID
                if (StrUtil.isNotBlank(username) && username.matches("\\d+")) {
                    return Long.parseLong(username);
                }
            }
            
            // 处理字符串类型的主体
            if (principal instanceof String) {
                String username = (String) principal;
                if (StrUtil.isNotBlank(username) && username.matches("\\d+")) {
                    return Long.parseLong(username);
                }
            }
            
            log.debug("无法从认证信息中获取用户ID，principal类型: {}", 
                    principal != null ? principal.getClass().getSimpleName() : "null");
            
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
        }
        
        return null;
    }

    /**
     * 获取当前登录用户名
     * 
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        try {
            Authentication authentication = getCurrentAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            
            // 处理LoginUser类型
            if (principal instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) principal;
                return loginUser.getUsername();
            }
            
            // 处理UserDetails类型
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return userDetails.getUsername();
            }
            
            // 处理字符串类型
            if (principal instanceof String) {
                return (String) principal;
            }
            
            log.debug("无法从认证信息中获取用户名，principal类型: {}", 
                    principal != null ? principal.getClass().getSimpleName() : "null");
            
        } catch (Exception e) {
            log.warn("获取当前用户名失败", e);
        }
        
        return null;
    }

    /**
     * 获取当前登录用户完整信息
     * 
     * @return LoginUser对象，未登录或类型不匹配返回null
     */
    public static LoginUser getCurrentUser() {
        try {
            Authentication authentication = getCurrentAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
            
        } catch (Exception e) {
            log.warn("获取当前用户信息失败", e);
        }
        
        return null;
    }

    /**
     * 检查当前是否有已认证的用户
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        try {
            Authentication authentication = getCurrentAuthentication();
            return authentication != null && authentication.isAuthenticated();
        } catch (Exception e) {
            log.warn("检查认证状态失败", e);
            return false;
        }
    }

    /**
     * 获取当前用户的角色列表
     * 
     * @return 角色列表，未登录返回空数组
     */
    public static String[] getCurrentUserRoles() {
        try {
            Authentication authentication = getCurrentAuthentication();
            if (authentication == null) {
                return new String[0];
            }

            return authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring(5)) // 移除"ROLE_"前缀
                    .toArray(String[]::new);
                    
        } catch (Exception e) {
            log.warn("获取当前用户角色失败", e);
            return new String[0];
        }
    }

    /**
     * 获取当前用户的权限列表
     * 
     * @return 权限列表，未登录返回空数组
     */
    public static String[] getCurrentUserPermissions() {
        try {
            Authentication authentication = getCurrentAuthentication();
            if (authentication == null) {
                return new String[0];
            }

            return authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(authority -> !authority.startsWith("ROLE_"))
                    .toArray(String[]::new);
                    
        } catch (Exception e) {
            log.warn("获取当前用户权限失败", e);
            return new String[0];
        }
    }

    /**
     * 获取当前认证对象
     * 
     * @return Authentication对象，未认证返回null
     */
    private static Authentication getCurrentAuthentication() {
        try {
            SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
            if (context == null) {
                return null;
            }
            
            Authentication authentication = context.getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            
            // 排除匿名用户
            if ("anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            
            return authentication;
            
        } catch (Exception e) {
            log.debug("获取当前认证对象失败", e);
            return null;
        }
    }

    /**
     * 获取当前用户ID，如果未登录则返回默认值
     * 
     * @param defaultUserId 默认用户ID
     * @return 用户ID
     */
    public static Long getCurrentUserIdOrDefault(Long defaultUserId) {
        Long userId = getCurrentUserId();
        return userId != null ? userId : defaultUserId;
    }

    /**
     * 获取当前用户名，如果未登录则返回默认值
     * 
     * @param defaultUsername 默认用户名
     * @return 用户名
     */
    public static String getCurrentUsernameOrDefault(String defaultUsername) {
        String username = getCurrentUsername();
        return StrUtil.isNotBlank(username) ? username : defaultUsername;
    }

    /**
     * 检查当前用户是否有指定权限
     * 
     * @param permission 权限标识
     * @return 是否有权限
     */
    public static boolean hasPermission(String permission) {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasPermission(permission);
    }

    /**
     * 检查当前用户是否有指定角色
     * 
     * @param role 角色标识
     * @return 是否有角色
     */
    public static boolean hasRole(String role) {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasRole(role);
    }

    /**
     * 检查当前用户是否有任意一个指定权限
     * 
     * @param permissions 权限列表
     * @return 是否有权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasAnyPermission(permissions);
    }

    /**
     * 检查当前用户是否有任意一个指定角色
     * 
     * @param roles 角色列表
     * @return 是否有角色
     */
    public static boolean hasAnyRole(String... roles) {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.hasAnyRole(roles);
    }

    /**
     * 获取当前用户显示名称
     * 
     * @return 显示名称
     */
    public static String getCurrentUserDisplayName() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getDisplayName() : null;
    }

    /**
     * 检查当前用户Token是否过期
     * 
     * @return 是否过期
     */
    public static boolean isCurrentUserTokenExpired() {
        LoginUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.isTokenExpired();
    }
}