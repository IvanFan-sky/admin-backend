package com.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 登录日志注解
 * 
 * 用于标记需要记录登录日志的方法
 * 支持自动识别登录、登出操作
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLog {

    /**
     * 日志标题
     */
    String title() default "";

    /**
     * 日志描述
     */
    String description() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordRequestData() default true;

    /**
     * 是否记录响应结果
     */
    boolean recordResponseData() default false;

    /**
     * 登录操作类型
     */
    LoginType loginType() default LoginType.AUTO;

    /**
     * 登录操作类型枚举
     */
    enum LoginType {
        AUTO,    // 自动识别
        LOGIN,   // 登录
        LOGOUT   // 登出
    }
}