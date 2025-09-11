package com.admin.common.constants;

/**
 * 系统业务常量定义
 * 
 * 包含用户、角色、菜单、权限等业务模块的常量定义
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SystemConstants {

    // =============================================
    // 用户状态相关常量
    // =============================================
    
    /**
     * 用户状态 - 正常
     */
    Integer USER_STATUS_NORMAL = 1;
    
    /**
     * 用户状态 - 禁用
     */
    Integer USER_STATUS_DISABLED = 0;
    
    /**
     * 用户状态 - 锁定
     */
    Integer USER_STATUS_LOCKED = 2;
    
    /**
     * 用户默认密码
     */
    String USER_DEFAULT_PASSWORD = "123456";
    
    /**
     * 用户性别 - 未知
     */
    String USER_GENDER_UNKNOWN = "0";
    
    /**
     * 用户性别 - 男性
     */
    String USER_GENDER_MALE = "1";
    
    /**
     * 用户性别 - 女性
     */
    String USER_GENDER_FEMALE = "2";
    
    /**
     * 管理员用户ID
     */
    Long ADMIN_USER_ID = 1L;
    
    /**
     * 管理员用户名
     */
    String ADMIN_USERNAME = "admin";

    // =============================================
    // 角色相关常量
    // =============================================
    
    /**
     * 角色状态 - 正常
     */
    Integer ROLE_STATUS_NORMAL = 1;
    
    /**
     * 角色状态 - 停用
     */
    Integer ROLE_STATUS_DISABLED = 0;
    
    /**
     * 超级管理员角色ID
     */
    Long SUPER_ADMIN_ROLE_ID = 1L;
    
    /**
     * 超级管理员角色编码
     */
    String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";
    
    /**
     * 普通用户角色编码
     */
    String USER_ROLE_CODE = "USER";
    
    /**
     * 角色编码最大长度
     */
    int ROLE_CODE_MAX_LENGTH = 100;
    
    /**
     * 角色名称最大长度
     */
    int ROLE_NAME_MAX_LENGTH = 30;

    // =============================================
    // 菜单权限相关常量
    // =============================================
    
    /**
     * 菜单类型 - 目录
     */
    Integer MENU_TYPE_DIRECTORY = 1;
    
    /**
     * 菜单类型 - 菜单
     */
    Integer MENU_TYPE_MENU = 2;
    
    /**
     * 菜单类型 - 按钮
     */
    Integer MENU_TYPE_BUTTON = 3;
    
    /**
     * 菜单状态 - 显示
     */
    Integer MENU_STATUS_SHOW = 1;
    
    /**
     * 菜单状态 - 隐藏
     */
    Integer MENU_STATUS_HIDE = 0;
    
    /**
     * 根菜单的父ID
     */
    Long ROOT_MENU_PARENT_ID = 0L;
    
    /**
     * 菜单名称最大长度
     */
    int MENU_NAME_MAX_LENGTH = 50;
    
    /**
     * 权限标识最大长度
     */
    int PERMISSION_MAX_LENGTH = 100;

    // =============================================
    // 字典相关常量
    // =============================================
    
    /**
     * 字典状态 - 正常
     */
    Integer DICT_STATUS_NORMAL = 1;
    
    /**
     * 字典状态 - 停用
     */
    Integer DICT_STATUS_DISABLED = 0;
    
    /**
     * 字典类型 - 系统内置
     */
    String DICT_TYPE_SYSTEM = "Y";
    
    /**
     * 字典类型 - 用户定义
     */
    String DICT_TYPE_USER = "N";

    // =============================================
    // 登录认证相关常量
    // =============================================
    
    /**
     * 登录失败最大次数
     */
    int LOGIN_MAX_FAIL_COUNT = 5;
    
    /**
     * 账户锁定时间（分钟）
     */
    int ACCOUNT_LOCK_TIME_MINUTES = 30;
    
    /**
     * 验证码长度
     */
    int CAPTCHA_LENGTH = 4;
    
    /**
     * 验证码有效期（分钟）
     */
    int CAPTCHA_EXPIRATION_MINUTES = 5;
    
    /**
     * 密码最小长度
     */
    int PASSWORD_MIN_LENGTH = 6;
    
    /**
     * 密码最大长度
     */
    int PASSWORD_MAX_LENGTH = 20;
    
    /**
     * 用户名最小长度
     */
    int USERNAME_MIN_LENGTH = 3;
    
    /**
     * 用户名最大长度
     */
    int USERNAME_MAX_LENGTH = 30;

    // =============================================
    // 删除标识相关常量
    // =============================================
    
    /**
     * 删除标识 - 未删除
     */
    Integer DEL_FLAG_NORMAL = 0;
    
    /**
     * 删除标识 - 已删除
     */
    Integer DEL_FLAG_DELETED = 1;

    // =============================================
    // 操作类型常量
    // =============================================
    
    /**
     * 操作类型 - 创建
     */
    String OPERATION_CREATE = "CREATE";
    
    /**
     * 操作类型 - 更新
     */
    String OPERATION_UPDATE = "UPDATE";
    
    /**
     * 操作类型 - 删除
     */
    String OPERATION_DELETE = "DELETE";
    
    /**
     * 操作类型 - 查询
     */
    String OPERATION_QUERY = "QUERY";
    
    /**
     * 操作类型 - 导出
     */
    String OPERATION_EXPORT = "EXPORT";
    
    /**
     * 操作类型 - 导入
     */
    String OPERATION_IMPORT = "IMPORT";

    // =============================================
    // 缓存Key前缀
    // =============================================
    
    /**
     * 用户缓存Key前缀
     */
    String USER_CACHE_KEY_PREFIX = "user:";
    
    /**
     * 角色缓存Key前缀
     */
    String ROLE_CACHE_KEY_PREFIX = "role:";
    
    /**
     * 菜单缓存Key前缀
     */
    String MENU_CACHE_KEY_PREFIX = "menu:";
    
    /**
     * 权限缓存Key前缀
     */
    String PERMISSION_CACHE_KEY_PREFIX = "permission:";
    
    /**
     * 字典缓存Key前缀
     */
    String DICT_CACHE_KEY_PREFIX = "dict:";

    // =============================================
    // 排序相关常量
    // =============================================
    
    /**
     * 排序方向 - 升序
     */
    String SORT_ASC = "asc";
    
    /**
     * 排序方向 - 降序
     */
    String SORT_DESC = "desc";
    
    /**
     * 默认排序字段
     */
    String DEFAULT_SORT_FIELD = "createTime";
    
    /**
     * 默认排序方向
     */
    String DEFAULT_SORT_DIRECTION = SORT_DESC;

    // =============================================
    // 分页相关常量
    // =============================================
    
    /**
     * 默认页码
     */
    int DEFAULT_PAGE_NUM = 1;
    
    /**
     * 默认页大小
     */
    int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大页大小
     */
    int MAX_PAGE_SIZE = 500;

    // =============================================
    // 校验相关常量
    // =============================================
    
    /**
     * 校验结果 - 唯一
     */
    String UNIQUE = "0";
    
    /**
     * 校验结果 - 不唯一
     */
    String NOT_UNIQUE = "1";
    
    /**
     * 数据范围 - 全部数据权限
     */
    String DATA_SCOPE_ALL = "1";
    
    /**
     * 数据范围 - 自定义数据权限
     */
    String DATA_SCOPE_CUSTOM = "2";
    
    /**
     * 数据范围 - 部门数据权限
     */
    String DATA_SCOPE_DEPT = "3";
    
    /**
     * 数据范围 - 部门及以下数据权限
     */
    String DATA_SCOPE_DEPT_AND_CHILD = "4";
    
    /**
     * 数据范围 - 仅本人数据权限
     */
    String DATA_SCOPE_SELF = "5";
}