package com.admin.framework.redis.constants;

/**
 * 缓存常量定义
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface CacheConstants {
    
    /**
     * 系统用户缓存
     */
    String SYS_USER_CACHE = "sys_user";
    
    /**
     * 系统角色缓存
     */
    String SYS_ROLE_CACHE = "sys_role";
    
    /**
     * 系统菜单缓存
     */
    String SYS_MENU_CACHE = "sys_menu";
    
    /**
     * 用户权限缓存
     */
    String USER_PERMISSION_CACHE = "user_permission";
    
    /**
     * 用户角色缓存
     */
    String USER_ROLE_CACHE = "user_role";
    
    /**
     * 字典缓存
     */
    String SYS_DICT_CACHE = "sys_dict";
    
    /**
     * 操作日志缓存
     */
    String OPERATION_LOG_CACHE = "operation_log";
    
    /**
     * 登录日志缓存
     */
    String LOGIN_LOG_CACHE = "login_log";
    
    /**
     * 日志统计缓存
     */
    String LOG_STATISTICS_CACHE = "log_statistics";
}