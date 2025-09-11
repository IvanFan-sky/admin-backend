package com.admin.framework.redis.service;

import com.admin.framework.redis.constant.RedisKeyConstants;
import com.admin.framework.redis.core.RedisCache;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 用户信息缓存服务
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedisCache redisCache;

    /**
     * 用户信息缓存过期时间（1小时）
     */
    private static final Duration USER_INFO_EXPIRE = Duration.ofHours(1);
    
    /**
     * 用户权限缓存过期时间（30分钟）
     */
    private static final Duration USER_PERMISSIONS_EXPIRE = Duration.ofMinutes(30);

    // =============================用户基本信息缓存=============================

    /**
     * 缓存用户信息
     */
    public void cacheUserInfo(Long userId, Object userInfo) {
        try {
            String key = RedisKeyConstants.buildUserInfoKey(userId);
            redisCache.set(key, userInfo, USER_INFO_EXPIRE);
            log.debug("缓存用户信息成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("缓存用户信息失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 获取缓存的用户信息
     */
    public <T> T getUserInfo(Long userId, Class<T> clazz) {
        try {
            String key = RedisKeyConstants.buildUserInfoKey(userId);
            T userInfo = redisCache.get(key);
            if (userInfo != null) {
                log.debug("从缓存获取用户信息成功，用户ID: {}", userId);
            }
            return userInfo;
        } catch (Exception e) {
            log.error("获取缓存用户信息失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * 删除用户信息缓存
     */
    public void evictUserInfo(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserInfoKey(userId);
            redisCache.delete(key);
            log.debug("删除用户信息缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户信息缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    // =============================用户权限缓存=============================

    /**
     * 缓存用户权限
     */
    public void cacheUserPermissions(Long userId, List<String> permissions) {
        try {
            String key = RedisKeyConstants.buildUserPermissionsKey(userId);
            redisCache.set(key, permissions, USER_PERMISSIONS_EXPIRE);
            log.debug("缓存用户权限成功，用户ID: {}, 权限数量: {}", userId, permissions.size());
        } catch (Exception e) {
            log.error("缓存用户权限失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 获取缓存的用户权限
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserPermissions(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserPermissionsKey(userId);
            List<String> permissions = redisCache.get(key);
            if (permissions != null) {
                log.debug("从缓存获取用户权限成功，用户ID: {}, 权限数量: {}", userId, permissions.size());
            }
            return permissions;
        } catch (Exception e) {
            log.error("获取缓存用户权限失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * 删除用户权限缓存
     */
    public void evictUserPermissions(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserPermissionsKey(userId);
            redisCache.delete(key);
            log.debug("删除用户权限缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户权限缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    // =============================用户角色缓存=============================

    /**
     * 缓存用户角色
     */
    public void cacheUserRoles(Long userId, List<String> roles) {
        try {
            String key = RedisKeyConstants.buildUserRolesKey(userId);
            redisCache.set(key, roles, USER_PERMISSIONS_EXPIRE);
            log.debug("缓存用户角色成功，用户ID: {}, 角色数量: {}", userId, roles.size());
        } catch (Exception e) {
            log.error("缓存用户角色失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 获取缓存的用户角色
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserRoles(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserRolesKey(userId);
            List<String> roles = redisCache.get(key);
            if (roles != null) {
                log.debug("从缓存获取用户角色成功，用户ID: {}, 角色数量: {}", userId, roles.size());
            }
            return roles;
        } catch (Exception e) {
            log.error("获取缓存用户角色失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * 删除用户角色缓存
     */
    public void evictUserRoles(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserRolesKey(userId);
            redisCache.delete(key);
            log.debug("删除用户角色缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户角色缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    // =============================用户在线状态缓存=============================

    /**
     * 设置用户在线状态
     */
    public void setUserOnline(Long userId, UserOnlineInfo onlineInfo) {
        try {
            String key = RedisKeyConstants.buildUserOnlineKey(userId);
            // 在线状态缓存时间设置为访问令牌过期时间的1.5倍
            Duration expireTime = Duration.ofMinutes(45); // 45分钟
            redisCache.set(key, onlineInfo, expireTime);
            log.debug("设置用户在线状态成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("设置用户在线状态失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 获取用户在线状态
     */
    public UserOnlineInfo getUserOnlineInfo(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserOnlineKey(userId);
            UserOnlineInfo onlineInfo = redisCache.get(key);
            if (onlineInfo != null) {
                log.debug("获取用户在线状态成功，用户ID: {}", userId);
            }
            return onlineInfo;
        } catch (Exception e) {
            log.error("获取用户在线状态失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * 设置用户离线
     */
    public void setUserOffline(Long userId) {
        try {
            String key = RedisKeyConstants.buildUserOnlineKey(userId);
            redisCache.delete(key);
            log.debug("设置用户离线状态成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("设置用户离线状态失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 获取所有在线用户ID
     */
    public Set<String> getOnlineUserIds() {
        try {
            String pattern = RedisKeyConstants.USER_ONLINE_PREFIX + "*";
            Set<String> keys = redisCache.keys(pattern);
            
            // 提取用户ID
            return keys.stream()
                    .map(key -> key.substring(RedisKeyConstants.USER_ONLINE_PREFIX.length()))
                    .collect(java.util.stream.Collectors.toSet());
        } catch (Exception e) {
            log.error("获取在线用户列表失败，错误: {}", e.getMessage());
            return Set.of();
        }
    }

    // =============================批量操作=============================

    /**
     * 清除用户所有缓存
     */
    public void evictUserAllCache(Long userId) {
        try {
            evictUserInfo(userId);
            evictUserPermissions(userId);
            evictUserRoles(userId);
            setUserOffline(userId);
            log.info("清除用户所有缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户所有缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 刷新用户权限相关缓存
     */
    public void refreshUserAuthCache(Long userId) {
        try {
            evictUserPermissions(userId);
            evictUserRoles(userId);
            log.info("刷新用户权限缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("刷新用户权限缓存失败，用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    /**
     * 用户在线信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserOnlineInfo {
        /**
         * 用户ID
         */
        private Long userId;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 登录时间
         */
        private java.util.Date loginTime;
        
        /**
         * 最后活动时间
         */
        private java.util.Date lastActiveTime;
        
        /**
         * 登录IP
         */
        private String loginIp;
        
        /**
         * 浏览器信息
         */
        private String userAgent;
        
        /**
         * 令牌ID（用于多设备登录管理）
         */
        private String tokenId;
    }
}