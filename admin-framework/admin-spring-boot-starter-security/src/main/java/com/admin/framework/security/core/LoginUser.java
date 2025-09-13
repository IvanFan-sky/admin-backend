package com.admin.framework.security.core;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 登录用户信息
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public class LoginUser implements UserDetails {

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 权限列表
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 账户是否未过期
     */
    private final boolean accountNonExpired;

    /**
     * 账户是否未锁定
     */
    private final boolean accountNonLocked;

    /**
     * 凭证是否未过期
     */
    private final boolean credentialsNonExpired;

    /**
     * 账户是否启用
     */
    private final boolean enabled;

    public LoginUser(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this(userId, username, authorities, true, true, true, true);
    }

    public LoginUser(Long userId, String username, Collection<? extends GrantedAuthority> authorities,
                    boolean accountNonExpired, boolean accountNonLocked, 
                    boolean credentialsNonExpired, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @Override
    public String getPassword() {
        return null; // JWT认证不需要密码
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 检查是否有指定权限
     */
    public boolean hasPermission(String permission) {
        if (authorities == null || permission == null) {
            return false;
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission));
    }

    /**
     * 检查是否有指定角色
     */
    public boolean hasRole(String role) {
        if (authorities == null || role == null) {
            return false;
        }
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(roleWithPrefix));
    }

    /**
     * 检查是否有任意一个指定权限
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
     * 检查是否有任意一个指定角色
     */
    public boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取显示名称（默认返回用户名）
     */
    public String getDisplayName() {
        return username;
    }

    /**
     * 检查Token是否过期（JWT认证中由外部管理）
     */
    public boolean isTokenExpired() {
        return false; // JWT认证中Token过期由JwtTokenUtil管理
    }
}