package com.admin.framework.redis.constant;

/**
 * Redis键名常量类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class RedisKeyConstants {

    /**
     * 全局键前缀
     */
    public static final String GLOBAL_PREFIX = "admin:";

    // =============================认证相关=============================
    
    /**
     * JWT令牌黑名单前缀
     */
    public static final String JWT_BLACKLIST_PREFIX = GLOBAL_PREFIX + "auth:blacklist:";

    /**
     * 用户登录失败次数前缀
     */
    public static final String LOGIN_FAIL_COUNT_PREFIX = GLOBAL_PREFIX + "auth:fail_count:";

    /**
     * 用户登录锁定前缀
     */
    public static final String LOGIN_LOCK_PREFIX = GLOBAL_PREFIX + "auth:lock:";

    /**
     * 用户在线信息前缀
     */
    public static final String USER_ONLINE_PREFIX = GLOBAL_PREFIX + "auth:online:";

    /**
     * 刷新令牌前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = GLOBAL_PREFIX + "auth:refresh_token:";

    // =============================用户信息缓存=============================
    
    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_PREFIX = GLOBAL_PREFIX + "user:info:";

    /**
     * 用户权限缓存前缀
     */
    public static final String USER_PERMISSIONS_PREFIX = GLOBAL_PREFIX + "user:permissions:";

    /**
     * 用户角色缓存前缀
     */
    public static final String USER_ROLES_PREFIX = GLOBAL_PREFIX + "user:roles:";

    // =============================系统配置缓存=============================
    
    /**
     * 系统配置缓存前缀
     */
    public static final String SYS_CONFIG_PREFIX = GLOBAL_PREFIX + "config:";

    /**
     * 字典数据缓存前缀
     */
    public static final String DICT_DATA_PREFIX = GLOBAL_PREFIX + "dict:";

    /**
     * 菜单缓存前缀
     */
    public static final String MENU_PREFIX = GLOBAL_PREFIX + "menu:";

    // =============================业务缓存=============================
    
    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = GLOBAL_PREFIX + "captcha:";

    /**
     * 短信验证码前缀
     */
    public static final String SMS_CODE_PREFIX = GLOBAL_PREFIX + "sms:";

    /**
     * 邮箱验证码前缀
     */
    public static final String EMAIL_CODE_PREFIX = GLOBAL_PREFIX + "email:";

    // =============================工具方法=============================

    /**
     * 构建JWT黑名单键
     */
    public static String buildJwtBlacklistKey(String jti) {
        return JWT_BLACKLIST_PREFIX + jti;
    }

    /**
     * 构建登录失败次数键
     */
    public static String buildLoginFailCountKey(String username) {
        return LOGIN_FAIL_COUNT_PREFIX + username;
    }

    /**
     * 构建登录锁定键
     */
    public static String buildLoginLockKey(String username) {
        return LOGIN_LOCK_PREFIX + username;
    }

    /**
     * 构建用户在线信息键
     */
    public static String buildUserOnlineKey(Long userId) {
        return USER_ONLINE_PREFIX + userId;
    }

    /**
     * 构建刷新令牌键
     */
    public static String buildRefreshTokenKey(String refreshToken) {
        return REFRESH_TOKEN_PREFIX + refreshToken;
    }

    /**
     * 构建用户信息缓存键
     */
    public static String buildUserInfoKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }

    /**
     * 构建用户权限缓存键
     */
    public static String buildUserPermissionsKey(Long userId) {
        return USER_PERMISSIONS_PREFIX + userId;
    }

    /**
     * 构建用户角色缓存键
     */
    public static String buildUserRolesKey(Long userId) {
        return USER_ROLES_PREFIX + userId;
    }

    /**
     * 构建验证码键
     */
    public static String buildCaptchaKey(String uuid) {
        return CAPTCHA_PREFIX + uuid;
    }

    /**
     * 构建短信验证码键
     */
    public static String buildSmsCodeKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    /**
     * 构建邮箱验证码键
     */
    public static String buildEmailCodeKey(String email) {
        return EMAIL_CODE_PREFIX + email;
    }
}