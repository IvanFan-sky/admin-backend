package com.admin.module.system.api.service.auth;

import java.util.List;

/**
 * 权限查询服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface PermissionQueryService {

    /**
     * 获取用户的角色列表
     * 
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户的权限列表
     * 
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 检查用户是否为管理员
     * 
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 检查用户是否具有指定权限
     * 
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否具有权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 检查用户是否具有指定角色
     * 
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否具有角色
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 获取用户完整的权限信息
     * 
     * @param userId 用户ID
     * @return 权限信息
     */
    UserPermissionInfo getUserPermissionInfo(Long userId);

    /**
     * 用户权限信息
     */
    class UserPermissionInfo {
        private Long userId;
        private boolean admin;
        private List<String> roles;
        private List<String> permissions;

        public UserPermissionInfo() {}

        public UserPermissionInfo(Long userId, boolean admin, List<String> roles, List<String> permissions) {
            this.userId = userId;
            this.admin = admin;
            this.roles = roles;
            this.permissions = permissions;
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}