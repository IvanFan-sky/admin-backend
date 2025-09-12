package com.admin.module.infra.biz.ratelimit;

import java.lang.annotation.*;

/**
 * 文件操作频率限制注解
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileRateLimiter {

    /**
     * 限制类型
     */
    LimitType limitType() default LimitType.USER;

    /**
     * 限制键（支持SpEL表达式）
     */
    String key() default "";

    /**
     * 时间窗口大小（秒）
     */
    int timeWindow() default 60;

    /**
     * 时间窗口内允许的最大请求数
     */
    int maxRequests() default 10;

    /**
     * 限制提示信息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限制类型
     */
    enum LimitType {
        /**
         * 根据用户限制
         */
        USER,
        
        /**
         * 根据IP限制
         */
        IP,
        
        /**
         * 根据自定义键限制
         */
        CUSTOM,
        
        /**
         * 全局限制
         */
        GLOBAL
    }
}