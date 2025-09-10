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
}