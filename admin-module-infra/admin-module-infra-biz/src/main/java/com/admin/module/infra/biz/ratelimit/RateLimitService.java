package com.admin.module.infra.biz.ratelimit;

/**
 * 频率限制服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface RateLimitService {

    /**
     * 检查是否允许访问
     *
     * @param key 限制键
     * @param timeWindow 时间窗口（秒）
     * @param maxRequests 最大请求数
     * @return 是否允许
     */
    boolean isAllowed(String key, int timeWindow, int maxRequests);

    /**
     * 获取当前限制状态
     *
     * @param key 限制键
     * @param timeWindow 时间窗口（秒）
     * @return 限制状态
     */
    RateLimitStatus getLimitStatus(String key, int timeWindow);

    /**
     * 重置限制计数
     *
     * @param key 限制键
     */
    void resetLimit(String key);

    /**
     * 获取剩余配额
     *
     * @param key 限制键
     * @param timeWindow 时间窗口（秒）
     * @param maxRequests 最大请求数
     * @return 剩余配额
     */
    int getRemainingQuota(String key, int timeWindow, int maxRequests);

    /**
     * 频率限制状态
     */
    class RateLimitStatus {
        private boolean allowed;
        private int currentCount;
        private int maxRequests;
        private long resetTime;
        private int remainingQuota;

        public RateLimitStatus(boolean allowed, int currentCount, int maxRequests, long resetTime, int remainingQuota) {
            this.allowed = allowed;
            this.currentCount = currentCount;
            this.maxRequests = maxRequests;
            this.resetTime = resetTime;
            this.remainingQuota = remainingQuota;
        }

        // Getters
        public boolean isAllowed() { return allowed; }
        public int getCurrentCount() { return currentCount; }
        public int getMaxRequests() { return maxRequests; }
        public long getResetTime() { return resetTime; }
        public int getRemainingQuota() { return remainingQuota; }
    }
}