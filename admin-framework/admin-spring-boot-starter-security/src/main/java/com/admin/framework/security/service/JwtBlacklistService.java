package com.admin.framework.security.service;

import com.admin.framework.redis.constant.RedisKeyConstants;
import com.admin.framework.redis.core.RedisCache;
import com.admin.framework.security.utils.JwtTokenUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * JWT令牌黑名单服务
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisCache redisCache;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 将令牌加入黑名单
     * 
     * @param token JWT令牌
     * @param reason 加入黑名单的原因
     */
    public void addToBlacklist(String token, String reason) {
        try {
            // 验证令牌格式
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("令牌格式无效，无需加入黑名单");
                return;
            }

            // 生成唯一标识符（如果令牌中没有jti，使用token的hash）
            String jti = generateJti(token);
            
            // 计算剩余有效时间
            Long remainingTime = jwtTokenUtil.getRemainingTime(token);
            if (remainingTime <= 0) {
                log.debug("令牌已过期，无需加入黑名单");
                return;
            }

            // 构建黑名单缓存键
            String blacklistKey = RedisKeyConstants.buildJwtBlacklistKey(jti);
            
            // 创建黑名单信息
            JwtBlacklistInfo blacklistInfo = JwtBlacklistInfo.builder()
                    .jti(jti)
                    .token(token)
                    .reason(reason)
                    .blacklistTime(new Date())
                    .expireTime(new Date(System.currentTimeMillis() + remainingTime))
                    .build();

            // 将令牌加入黑名单，设置过期时间为令牌剩余时间
            redisCache.set(blacklistKey, blacklistInfo, Duration.ofMillis(remainingTime));
            
            log.info("令牌已加入黑名单，JTI: {}, 原因: {}, 剩余时间: {}ms", jti, reason, remainingTime);
            
        } catch (Exception e) {
            log.error("将令牌加入黑名单失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查令牌是否在黑名单中
     * 
     * @param token JWT令牌
     * @return true-在黑名单中，false-不在黑名单中
     */
    public boolean isBlacklisted(String token) {
        try {
            String jti = generateJti(token);
            String blacklistKey = RedisKeyConstants.buildJwtBlacklistKey(jti);
            
            JwtBlacklistInfo blacklistInfo = redisCache.get(blacklistKey);
            boolean isBlacklisted = blacklistInfo != null;
            
            if (isBlacklisted) {
                log.debug("令牌在黑名单中，JTI: {}, 原因: {}", jti, blacklistInfo.getReason());
            }
            
            return isBlacklisted;
            
        } catch (Exception e) {
            log.error("检查令牌黑名单状态失败: {}", e.getMessage());
            // 出现异常时，为了安全考虑，认为令牌在黑名单中
            return true;
        }
    }

    /**
     * 从黑名单中移除令牌（通常不需要，因为会自动过期）
     * 
     * @param token JWT令牌
     */
    public void removeFromBlacklist(String token) {
        try {
            String jti = generateJti(token);
            String blacklistKey = RedisKeyConstants.buildJwtBlacklistKey(jti);
            
            boolean removed = Boolean.TRUE.equals(redisCache.delete(blacklistKey));
            if (removed) {
                log.info("令牌已从黑名单中移除，JTI: {}", jti);
            }
            
        } catch (Exception e) {
            log.error("从黑名单中移除令牌失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 清空所有黑名单（慎用）
     */
    public void clearAllBlacklist() {
        try {
            String pattern = RedisKeyConstants.JWT_BLACKLIST_PREFIX + "*";
            var keys = redisCache.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisCache.delete(keys);
                log.info("已清空JWT黑名单，删除数量: {}", deletedCount);
            }
            
        } catch (Exception e) {
            log.error("清空JWT黑名单失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取黑名单统计信息
     */
    public JwtBlacklistStats getBlacklistStats() {
        try {
            String pattern = RedisKeyConstants.JWT_BLACKLIST_PREFIX + "*";
            var keys = redisCache.keys(pattern);
            
            int totalCount = keys != null ? keys.size() : 0;
            
            return JwtBlacklistStats.builder()
                    .totalCount(totalCount)
                    .pattern(pattern)
                    .build();
                    
        } catch (Exception e) {
            log.error("获取黑名单统计信息失败: {}", e.getMessage());
            return JwtBlacklistStats.builder().totalCount(0).build();
        }
    }

    /**
     * 生成令牌的唯一标识符
     * 如果令牌中包含jti声明则使用jti，否则使用token的hash值
     */
    private String generateJti(String token) {
        try {
            // 尝试从令牌中获取JTI
            // TODO: 如果需要JTI支持，需要在JwtTokenUtil中添加getJtiFromToken方法
            // String jti = jwtTokenUtil.getJtiFromToken(token);
            // if (StringUtils.hasText(jti)) {
            //     return jti;
            // }
            
            // 使用token的hash值作为唯一标识符
            return UUID.nameUUIDFromBytes(token.getBytes()).toString();
            
        } catch (Exception e) {
            // 如果token解析失败，使用简单的hash
            return String.valueOf(token.hashCode());
        }
    }

    /**
     * JWT黑名单信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtBlacklistInfo {
        /**
         * 令牌唯一标识符
         */
        private String jti;
        
        /**
         * 原始令牌（可选，用于调试）
         */
        private String token;
        
        /**
         * 加入黑名单的原因
         */
        private String reason;
        
        /**
         * 加入黑名单的时间
         */
        private Date blacklistTime;
        
        /**
         * 令牌过期时间
         */
        private Date expireTime;
    }

    /**
     * JWT黑名单统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtBlacklistStats {
        /**
         * 黑名单令牌总数
         */
        private int totalCount;
        
        /**
         * 查询模式
         */
        private String pattern;
    }
}