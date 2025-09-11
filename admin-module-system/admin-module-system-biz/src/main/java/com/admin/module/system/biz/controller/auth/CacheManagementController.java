package com.admin.module.system.biz.controller.auth;

import com.admin.common.core.domain.R;
import com.admin.framework.redis.service.UserCacheService;
import com.admin.framework.security.service.JwtBlacklistService;
import com.admin.framework.security.service.LoginLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 缓存管理控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/system/cache")
@RequiredArgsConstructor
@Validated
@Tag(name = "缓存管理", description = "Redis缓存管理相关接口")
public class CacheManagementController {

    private final UserCacheService userCacheService;
    private final JwtBlacklistService jwtBlacklistService;
    private final LoginLimitService loginLimitService;

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取缓存统计信息", description = "获取JWT黑名单、登录限制、在线用户等统计信息")
    @PreAuthorize("@ss.hasPermission('system:cache:query')")
    public R<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // JWT黑名单统计
            JwtBlacklistService.JwtBlacklistStats blacklistStats = jwtBlacklistService.getBlacklistStats();
            stats.put("blacklist", blacklistStats);
            
            // 登录限制统计
            LoginLimitService.LoginLimitStats limitStats = loginLimitService.getLoginLimitStats();
            stats.put("loginLimit", limitStats);
            
            // 在线用户统计
            Set<String> onlineUserIds = userCacheService.getOnlineUserIds();
            Map<String, Object> onlineStats = new HashMap<>();
            onlineStats.put("totalOnlineUsers", onlineUserIds.size());
            onlineStats.put("onlineUserIds", onlineUserIds);
            stats.put("onlineUsers", onlineStats);
            
            log.info("获取缓存统计信息成功");
            return R.ok(stats);
            
        } catch (Exception e) {
            log.error("获取缓存统计信息失败: {}", e.getMessage(), e);
            return R.error("获取缓存统计信息失败");
        }
    }

    /**
     * 清除用户缓存
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "清除指定用户缓存", description = "清除指定用户的所有缓存信息")
    @PreAuthorize("@ss.hasPermission('system:cache:delete')")
    public R<Void> clearUserCache(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            userCacheService.evictUserAllCache(userId);
            log.info("清除用户缓存成功，用户ID: {}", userId);
            return R.ok();
            
        } catch (Exception e) {
            log.error("清除用户缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return R.error("清除用户缓存失败");
        }
    }

    /**
     * 刷新用户权限缓存
     */
    @PostMapping("/user/{userId}/refresh-auth")
    @Operation(summary = "刷新用户权限缓存", description = "刷新指定用户的角色和权限缓存")
    @PreAuthorize("@ss.hasPermission('system:cache:update')")
    public R<Void> refreshUserAuthCache(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            userCacheService.refreshUserAuthCache(userId);
            log.info("刷新用户权限缓存成功，用户ID: {}", userId);
            return R.ok();
            
        } catch (Exception e) {
            log.error("刷新用户权限缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return R.error("刷新用户权限缓存失败");
        }
    }

    /**
     * 强制用户下线
     */
    @PostMapping("/user/{userId}/force-offline")
    @Operation(summary = "强制用户下线", description = "强制指定用户下线，清除在线状态")
    @PreAuthorize("@ss.hasPermission('system:user:force-offline')")
    public R<Void> forceUserOffline(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            userCacheService.setUserOffline(userId);
            log.info("强制用户下线成功，用户ID: {}", userId);
            return R.ok();
            
        } catch (Exception e) {
            log.error("强制用户下线失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return R.error("强制用户下线失败");
        }
    }

    /**
     * 解锁账户
     */
    @PostMapping("/unlock-account")
    @Operation(summary = "解锁账户", description = "解锁被锁定的用户账户")
    @PreAuthorize("@ss.hasPermission('system:user:unlock')")
    public R<Void> unlockAccount(
            @Parameter(description = "用户名") @RequestParam String username) {
        
        try {
            loginLimitService.unlockAccount(username);
            log.info("解锁账户成功，用户名: {}", username);
            return R.ok();
            
        } catch (Exception e) {
            log.error("解锁账户失败，用户名: {}, 错误: {}", username, e.getMessage(), e);
            return R.error("解锁账户失败");
        }
    }

    /**
     * 获取账户锁定信息
     */
    @GetMapping("/lock-info")
    @Operation(summary = "获取账户锁定信息", description = "获取指定用户的锁定信息")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<LoginLimitService.AccountLockInfo> getAccountLockInfo(
            @Parameter(description = "用户名") @RequestParam String username) {
        
        try {
            LoginLimitService.AccountLockInfo lockInfo = loginLimitService.getAccountLockInfo(username);
            return R.ok(lockInfo);
            
        } catch (Exception e) {
            log.error("获取账户锁定信息失败，用户名: {}, 错误: {}", username, e.getMessage(), e);
            return R.error("获取账户锁定信息失败");
        }
    }

    /**
     * 清空JWT黑名单（危险操作）
     */
    @DeleteMapping("/blacklist/clear")
    @Operation(summary = "清空JWT黑名单", description = "清空所有JWT黑名单记录（危险操作）")
    @PreAuthorize("@ss.hasPermission('system:cache:clear')")
    public R<Void> clearJwtBlacklist() {
        
        try {
            jwtBlacklistService.clearAllBlacklist();
            log.warn("清空JWT黑名单操作执行");
            return R.ok();
            
        } catch (Exception e) {
            log.error("清空JWT黑名单失败: {}", e.getMessage(), e);
            return R.error("清空JWT黑名单失败");
        }
    }

    /**
     * 获取在线用户列表
     */
    @GetMapping("/online-users")
    @Operation(summary = "获取在线用户列表", description = "获取当前在线用户列表")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Set<String>> getOnlineUsers() {
        
        try {
            Set<String> onlineUserIds = userCacheService.getOnlineUserIds();
            return R.ok(onlineUserIds);
            
        } catch (Exception e) {
            log.error("获取在线用户列表失败: {}", e.getMessage(), e);
            return R.error("获取在线用户列表失败");
        }
    }

    /**
     * 获取用户在线信息
     */
    @GetMapping("/user/{userId}/online-info")
    @Operation(summary = "获取用户在线信息", description = "获取指定用户的在线状态信息")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<UserCacheService.UserOnlineInfo> getUserOnlineInfo(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        try {
            UserCacheService.UserOnlineInfo onlineInfo = userCacheService.getUserOnlineInfo(userId);
            return R.ok(onlineInfo);
            
        } catch (Exception e) {
            log.error("获取用户在线信息失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return R.error("获取用户在线信息失败");
        }
    }
}