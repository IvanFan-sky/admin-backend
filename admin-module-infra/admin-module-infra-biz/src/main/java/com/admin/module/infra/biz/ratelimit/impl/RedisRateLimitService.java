package com.admin.module.infra.biz.ratelimit.impl;

import com.admin.module.infra.biz.ratelimit.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * 基于Redis的频率限制服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimitService implements RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "file:rate_limit:";

    /**
     * Lua脚本实现滑动窗口限流
     */
    private static final String SLIDING_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local current = tonumber(ARGV[3])
        
        -- 移除过期的记录
        redis.call('zremrangebyscore', key, '-inf', current - window * 1000)
        
        -- 获取当前窗口内的请求数
        local currentCount = redis.call('zcard', key)
        
        if currentCount < limit then
            -- 允许请求，添加当前时间戳
            redis.call('zadd', key, current, current)
            redis.call('expire', key, window + 1)
            return {1, currentCount + 1, limit - currentCount - 1}
        else
            -- 拒绝请求
            return {0, currentCount, 0}
        end
        """;

    @Override
    public boolean isAllowed(String key, int timeWindow, int maxRequests) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            long currentTime = System.currentTimeMillis();
            
            List<Object> result = redisTemplate.execute(
                RedisScript.of(SLIDING_WINDOW_SCRIPT, List.class),
                Collections.singletonList(redisKey),
                timeWindow, maxRequests, currentTime
            );
            
            if (result != null && !result.isEmpty()) {
                Long allowed = (Long) result.get(0);
                return allowed == 1;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("检查频率限制失败，键: {}", key, e);
            // 出现异常时允许请求通过，避免影响正常业务
            return true;
        }
    }

    @Override
    public RateLimitStatus getLimitStatus(String key, int timeWindow) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - timeWindow * 1000L;
            
            // 清理过期数据
            redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
            
            // 获取当前窗口内的请求数
            Long currentCount = redisTemplate.opsForZSet().count(redisKey, windowStart, currentTime);
            int count = currentCount != null ? currentCount.intValue() : 0;
            
            // 计算重置时间（下一个窗口开始时间）
            long resetTime = currentTime + timeWindow * 1000L;
            
            return new RateLimitStatus(true, count, 0, resetTime, 0);
            
        } catch (Exception e) {
            log.error("获取限制状态失败，键: {}", key, e);
            return new RateLimitStatus(true, 0, 0, System.currentTimeMillis(), 0);
        }
    }

    @Override
    public void resetLimit(String key) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            redisTemplate.delete(redisKey);
            log.debug("重置频率限制，键: {}", key);
        } catch (Exception e) {
            log.error("重置频率限制失败，键: {}", key, e);
        }
    }

    @Override
    public int getRemainingQuota(String key, int timeWindow, int maxRequests) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - timeWindow * 1000L;
            
            // 清理过期数据
            redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
            
            // 获取当前窗口内的请求数
            Long currentCount = redisTemplate.opsForZSet().count(redisKey, windowStart, currentTime);
            int count = currentCount != null ? currentCount.intValue() : 0;
            
            return Math.max(0, maxRequests - count);
            
        } catch (Exception e) {
            log.error("获取剩余配额失败，键: {}", key, e);
            return maxRequests; // 异常时返回最大配额
        }
    }

    /**
     * 简单固定窗口限流实现（备选方案）
     */
    public boolean isAllowedSimple(String key, int timeWindow, int maxRequests) {
        try {
            String redisKey = RATE_LIMIT_KEY_PREFIX + key;
            String countStr = (String) redisTemplate.opsForValue().get(redisKey);
            
            int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
            
            if (currentCount >= maxRequests) {
                return false;
            }
            
            // 递增计数
            Long newCount = redisTemplate.opsForValue().increment(redisKey);
            
            if (newCount == 1) {
                // 设置过期时间
                redisTemplate.expire(redisKey, Duration.ofSeconds(timeWindow));
            }
            
            return newCount <= maxRequests;
            
        } catch (Exception e) {
            log.error("简单频率限制检查失败，键: {}", key, e);
            return true;
        }
    }
}