package com.admin.module.system.biz.service.auth;

import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.module.system.api.service.auth.PermissionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 安全权限服务 - 用于@PreAuthorize注解的权限检查
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Component("ss")
@RequiredArgsConstructor
public class SecurityPermissionService {

    private final PermissionQueryService permissionQueryService;

    /**
     * 检查当前用户是否具有指定权限
     * 
     * @param permission 权限标识
     * @return true-有权限，false-无权限
     */
    public boolean hasPermission(String permission) {
        try {
            // 获取当前用户ID
            Long currentUserId = SecurityAuthUtils.getCurrentUserId();
            if (currentUserId == null) {
                log.debug("当前用户未认证，权限检查失败: {}", permission);
                return false;
            }

            // 检查用户权限
            boolean hasPermission = permissionQueryService.hasPermission(currentUserId, permission);
            log.debug("用户 {} 权限检查 {} 结果: {}", currentUserId, permission, hasPermission);
            
            return hasPermission;
            
        } catch (Exception e) {
            log.error("权限检查异常，权限: {}, 错误: {}", permission, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查当前用户是否具有指定角色
     * 
     * @param roleCode 角色编码
     * @return true-有角色，false-无角色
     */
    public boolean hasRole(String roleCode) {
        try {
            // 获取当前用户ID
            Long currentUserId = SecurityAuthUtils.getCurrentUserId();
            if (currentUserId == null) {
                log.debug("当前用户未认证，角色检查失败: {}", roleCode);
                return false;
            }

            // 检查用户角色
            boolean hasRole = permissionQueryService.hasRole(currentUserId, roleCode);
            log.debug("用户 {} 角色检查 {} 结果: {}", currentUserId, roleCode, hasRole);
            
            return hasRole;
            
        } catch (Exception e) {
            log.error("角色检查异常，角色: {}, 错误: {}", roleCode, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查当前用户是否为管理员
     * 
     * @return true-是管理员，false-不是管理员
     */
    public boolean isAdmin() {
        try {
            // 获取当前用户ID
            Long currentUserId = SecurityAuthUtils.getCurrentUserId();
            if (currentUserId == null) {
                log.debug("当前用户未认证，管理员检查失败");
                return false;
            }

            // 检查是否为管理员
            boolean isAdmin = permissionQueryService.isAdmin(currentUserId);
            log.debug("用户 {} 管理员检查结果: {}", currentUserId, isAdmin);
            
            return isAdmin;
            
        } catch (Exception e) {
            log.error("管理员检查异常，用户ID: {}, 错误: {}", SecurityAuthUtils.getCurrentUserId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查当前用户是否已认证
     * 
     * @return true-已认证，false-未认证
     */
    public boolean isAuthenticated() {
        return SecurityAuthUtils.isAuthenticated() && SecurityAuthUtils.getCurrentUserId() != null;
    }

    /**
     * 检查当前用户是否具有任意一个指定权限
     * 
     * @param permissions 权限标识数组
     * @return true-有任意权限，false-无任意权限
     */
    public boolean hasAnyPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有所有指定权限
     * 
     * @param permissions 权限标识数组
     * @return true-有所有权限，false-缺少权限
     */
    public boolean hasAllPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否具有任意一个指定角色
     * 
     * @param roleCodes 角色编码数组
     * @return true-有任意角色，false-无任意角色
     */
    public boolean hasAnyRole(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return false;
        }

        for (String roleCode : roleCodes) {
            if (hasRole(roleCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有所有指定角色
     * 
     * @param roleCodes 角色编码数组
     * @return true-有所有角色，false-缺少角色
     */
    public boolean hasAllRoles(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return true;
        }

        for (String roleCode : roleCodes) {
            if (!hasRole(roleCode)) {
                return false;
            }
        }
        return true;
    }
}