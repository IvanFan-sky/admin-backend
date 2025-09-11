package com.admin.framework.security.service;

import com.admin.framework.redis.constant.RedisKeyConstants;
import com.admin.framework.redis.core.RedisCache;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 登录限制和防暴力破解服务
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLimitService {

    private final RedisCache redisCache;

    /**
     * 最大失败次数
     */
    private static final int MAX_FAIL_COUNT = 5;
    
    /**
     * 失败次数重置时间（15分钟）
     */
    private static final Duration FAIL_COUNT_EXPIRE = Duration.ofMinutes(15);
    
    /**
     * 账户锁定时间（30分钟）
     */
    private static final Duration LOCK_EXPIRE = Duration.ofMinutes(30);

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     * @return 当前失败次数
     */
    public int recordLoginFail(String username) {
        try {
            String key = RedisKeyConstants.buildLoginFailCountKey(username);
            
            // 获取当前失败次数
            Integer currentCount = redisCache.get(key, 0);
            int newCount = currentCount + 1;
            
            // 更新失败次数，设置过期时间
            redisCache.set(key, newCount, FAIL_COUNT_EXPIRE);
            
            log.warn("记录用户登录失败，用户: {}, 失败次数: {}", username, newCount);
            
            // 如果达到最大失败次数，锁定账户
            if (newCount >= MAX_FAIL_COUNT) {
                lockAccount(username);
                log.warn("用户登录失败次数达到上限，账户已锁定，用户: {}", username);
            }
            
            return newCount;
            
        } catch (Exception e) {
            log.error("记录登录失败信息异常，用户: {}, 错误: {}", username, e.getMessage());
            return 0;
        }
    }

    /**
     * 清除登录失败记录
     * 
     * @param username 用户名
     */
    public void clearLoginFailCount(String username) {
        try {
            String key = RedisKeyConstants.buildLoginFailCountKey(username);
            redisCache.delete(key);
            log.debug("清除用户登录失败记录，用户: {}", username);
        } catch (Exception e) {
            log.error("清除登录失败记录异常，用户: {}, 错误: {}", username, e.getMessage());
        }
    }

    /**
     * 获取登录失败次数
     * 
     * @param username 用户名
     * @return 失败次数
     */
    public int getLoginFailCount(String username) {
        try {
            String key = RedisKeyConstants.buildLoginFailCountKey(username);
            return redisCache.get(key, 0);
        } catch (Exception e) {
            log.error("获取登录失败次数异常，用户: {}, 错误: {}", username, e.getMessage());
            return 0;
        }
    }

    /**
     * 锁定账户
     * 
     * @param username 用户名
     */
    public void lockAccount(String username) {
        try {
            String lockKey = RedisKeyConstants.buildLoginLockKey(username);
            
            AccountLockInfo lockInfo = AccountLockInfo.builder()
                    .username(username)
                    .lockTime(new java.util.Date())
                    .lockReason("登录失败次数过多")
                    .failCount(getLoginFailCount(username))
                    .build();
            
            redisCache.set(lockKey, lockInfo, LOCK_EXPIRE);
            log.warn("账户已锁定，用户: {}, 锁定时间: {} 分钟", username, LOCK_EXPIRE.toMinutes());
            
        } catch (Exception e) {
            log.error("锁定账户异常，用户: {}, 错误: {}", username, e.getMessage());
        }
    }

    /**
     * 解锁账户
     * 
     * @param username 用户名
     */
    public void unlockAccount(String username) {
        try {
            String lockKey = RedisKeyConstants.buildLoginLockKey(username);
            redisCache.delete(lockKey);
            
            // 同时清除失败次数记录
            clearLoginFailCount(username);
            
            log.info("账户已解锁，用户: {}", username);
            
        } catch (Exception e) {
            log.error("解锁账户异常，用户: {}, 错误: {}", username, e.getMessage());
        }
    }

    /**
     * 检查账户是否被锁定
     * 
     * @param username 用户名
     * @return true-已锁定，false-未锁定
     */
    public boolean isAccountLocked(String username) {
        try {
            String lockKey = RedisKeyConstants.buildLoginLockKey(username);
            AccountLockInfo lockInfo = redisCache.get(lockKey);
            
            boolean isLocked = lockInfo != null;
            if (isLocked) {
                log.debug("账户处于锁定状态，用户: {}, 锁定原因: {}", username, lockInfo.getLockReason());
            }
            
            return isLocked;
            
        } catch (Exception e) {
            log.error("检查账户锁定状态异常，用户: {}, 错误: {}", username, e.getMessage());
            // 异常情况下，为了安全考虑，认为账户未锁定（避免影响正常用户）
            return false;
        }
    }

    /**
     * 获取账户锁定信息
     * 
     * @param username 用户名
     * @return 锁定信息
     */
    public AccountLockInfo getAccountLockInfo(String username) {
        try {
            String lockKey = RedisKeyConstants.buildLoginLockKey(username);
            return redisCache.get(lockKey);
        } catch (Exception e) {
            log.error("获取账户锁定信息异常，用户: {}, 错误: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * 检查是否需要验证码
     * 当失败次数达到一定阈值时，要求输入验证码
     * 
     * @param username 用户名
     * @return true-需要验证码，false-不需要验证码
     */
    public boolean needCaptcha(String username) {
        int failCount = getLoginFailCount(username);
        // 失败3次后需要验证码
        return failCount >= 3;
    }

    /**
     * 获取剩余锁定时间（秒）
     * 
     * @param username 用户名
     * @return 剩余锁定时间，-1表示未锁定
     */
    public long getRemainingLockTime(String username) {
        try {
            String lockKey = RedisKeyConstants.buildLoginLockKey(username);
            Long expireTime = redisCache.getExpire(lockKey);
            return expireTime != null ? expireTime : -1;
        } catch (Exception e) {
            log.error("获取剩余锁定时间异常，用户: {}, 错误: {}", username, e.getMessage());
            return -1;
        }
    }

    /**
     * 获取登录限制统计信息
     */
    public LoginLimitStats getLoginLimitStats() {
        try {
            // 统计失败次数记录
            String failPattern = RedisKeyConstants.LOGIN_FAIL_COUNT_PREFIX + "*";
            var failKeys = redisCache.keys(failPattern);
            int failCount = failKeys != null ? failKeys.size() : 0;
            
            // 统计锁定账户数量
            String lockPattern = RedisKeyConstants.LOGIN_LOCK_PREFIX + "*";
            var lockKeys = redisCache.keys(lockPattern);
            int lockCount = lockKeys != null ? lockKeys.size() : 0;
            
            return LoginLimitStats.builder()
                    .totalFailRecords(failCount)
                    .totalLockedAccounts(lockCount)
                    .maxFailCount(MAX_FAIL_COUNT)
                    .lockDurationMinutes((int) LOCK_EXPIRE.toMinutes())
                    .build();
                    
        } catch (Exception e) {
            log.error("获取登录限制统计信息失败: {}", e.getMessage());
            return LoginLimitStats.builder().build();
        }
    }

    /**
     * 账户锁定信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountLockInfo {
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 锁定时间
         */
        private java.util.Date lockTime;
        
        /**
         * 锁定原因
         */
        private String lockReason;
        
        /**
         * 失败次数
         */
        private int failCount;
    }

    /**
     * 登录限制统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginLimitStats {
        /**
         * 失败记录总数
         */
        private int totalFailRecords;
        
        /**
         * 锁定账户总数
         */
        private int totalLockedAccounts;
        
        /**
         * 最大失败次数
         */
        private int maxFailCount;
        
        /**
         * 锁定时长（分钟）
         */
        private int lockDurationMinutes;
    }
}