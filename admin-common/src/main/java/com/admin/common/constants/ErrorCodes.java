package com.admin.common.constants;

/**
 * 业务错误码定义
 * 
 * 定义系统各模块的详细业务错误码，便于问题定位和国际化
 * HTTP状态码采用标准200、400等，业务错误码采用整型数值
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ErrorCodes {

    // =============================================
    // 认证授权业务错误码 (A0xxxx)
    // =============================================
    
    /**
     * 用户名或密码错误
     */
    Integer INVALID_CREDENTIALS = 100001;
    
    /**
     * 令牌无效
     */
    Integer INVALID_TOKEN = 100002;
    
    /**
     * 令牌过期
     */
    Integer TOKEN_EXPIRED = 100003;
    
    /**
     * 账户被锁定
     */
    Integer ACCOUNT_LOCKED = 100004;
    
    /**
     * 账户被禁用
     */
    Integer ACCOUNT_DISABLED = 100005;
    
    /**
     * 登录失败次数过多
     */
    Integer LOGIN_FAIL_COUNT_EXCEEDED = 100006;
    
    /**
     * 验证码错误
     */
    Integer INVALID_CAPTCHA = 100007;
    
    /**
     * 验证码过期
     */
    Integer CAPTCHA_EXPIRED = 100008;
    
    /**
     * 刷新令牌无效
     */
    Integer INVALID_REFRESH_TOKEN = 100009;
    
    /**
     * 用户未登录
     */
    Integer USER_NOT_LOGIN = 100010;
    
    /**
     * 会话过期
     */
    Integer SESSION_EXPIRED = 100011;
    
    /**
     * 权限不足
     */
    Integer ACCESS_DENIED = 100012;

    // =============================================
    // 用户管理业务错误码 (200xxx)
    // =============================================
    
    /**
     * 用户不存在
     */
    Integer USER_NOT_FOUND = 200001;
    
    /**
     * 用户名已存在
     */
    Integer USERNAME_ALREADY_EXISTS = 200002;
    
    /**
     * 邮箱已存在
     */
    Integer EMAIL_ALREADY_EXISTS = 200003;
    
    /**
     * 手机号已存在
     */
    Integer PHONE_ALREADY_EXISTS = 200004;
    
    /**
     * 用户名格式错误
     */
    Integer INVALID_USERNAME_FORMAT = 200005;
    
    /**
     * 密码格式错误
     */
    Integer INVALID_PASSWORD_FORMAT = 200006;
    
    /**
     * 邮箱格式错误
     */
    Integer INVALID_EMAIL_FORMAT = 200007;
    
    /**
     * 手机号格式错误
     */
    Integer INVALID_PHONE_FORMAT = 200008;
    
    /**
     * 原密码错误
     */
    Integer WRONG_OLD_PASSWORD = 200009;
    
    /**
     * 新密码与原密码相同
     */
    Integer NEW_PASSWORD_SAME_AS_OLD = 200010;
    
    /**
     * 用户状态异常
     */
    Integer INVALID_USER_STATUS = 200011;
    
    /**
     * 不能删除管理员用户
     */
    Integer CANNOT_DELETE_ADMIN_USER = 200012;
    
    /**
     * 不能禁用管理员用户
     */
    Integer CANNOT_DISABLE_ADMIN_USER = 200013;
    
    /**
     * 用户密码未设置
     */
    Integer USER_PASSWORD_NOT_SET = 200014;
    
    /**
     * 用户头像上传失败
     */
    Integer USER_AVATAR_UPLOAD_FAILED = 200015;

    // =============================================
    // 角色管理业务错误码 (R0xxxx)
    // =============================================
    
    /**
     * 角色不存在
     */
    String ROLE_NOT_FOUND = "R00001";
    
    /**
     * 角色编码已存在
     */
    String ROLE_CODE_ALREADY_EXISTS = "R00002";
    
    /**
     * 角色名称已存在
     */
    String ROLE_NAME_ALREADY_EXISTS = "R00003";
    
    /**
     * 角色编码格式错误
     */
    String INVALID_ROLE_CODE_FORMAT = "R00004";
    
    /**
     * 角色状态异常
     */
    String INVALID_ROLE_STATUS = "R00005";
    
    /**
     * 不能删除超级管理员角色
     */
    String CANNOT_DELETE_SUPER_ADMIN_ROLE = "R00006";
    
    /**
     * 不能禁用超级管理员角色
     */
    String CANNOT_DISABLE_SUPER_ADMIN_ROLE = "R00007";
    
    /**
     * 角色已分配给用户，不能删除
     */
    String ROLE_ASSIGNED_TO_USER = "R00008";
    
    /**
     * 角色权限分配失败
     */
    String ROLE_PERMISSION_ASSIGN_FAILED = "R00009";
    
    /**
     * 角色排序号重复
     */
    String ROLE_SORT_ORDER_DUPLICATE = "R00010";

    // =============================================
    // 菜单权限业务错误码 (M0xxxx)
    // =============================================
    
    /**
     * 菜单不存在
     */
    String MENU_NOT_FOUND = "M00001";
    
    /**
     * 菜单名称已存在
     */
    String MENU_NAME_ALREADY_EXISTS = "M00002";
    
    /**
     * 父菜单不存在
     */
    String PARENT_MENU_NOT_FOUND = "M00003";
    
    /**
     * 不能选择自己作为父菜单
     */
    String CANNOT_SELECT_SELF_AS_PARENT = "M00004";
    
    /**
     * 不能选择子菜单作为父菜单
     */
    String CANNOT_SELECT_CHILD_AS_PARENT = "M00005";
    
    /**
     * 菜单类型错误
     */
    String INVALID_MENU_TYPE = "M00006";
    
    /**
     * 菜单状态异常
     */
    String INVALID_MENU_STATUS = "M00007";
    
    /**
     * 存在子菜单，不能删除
     */
    String MENU_HAS_CHILDREN = "M00008";
    
    /**
     * 菜单已分配给角色，不能删除
     */
    String MENU_ASSIGNED_TO_ROLE = "M00009";
    
    /**
     * 权限标识已存在
     */
    String PERMISSION_ALREADY_EXISTS = "M00010";
    
    /**
     * 权限标识格式错误
     */
    String INVALID_PERMISSION_FORMAT = "M00011";
    
    /**
     * 菜单路由地址已存在
     */
    String MENU_PATH_ALREADY_EXISTS = "M00012";
    
    /**
     * 菜单排序号重复
     */
    String MENU_SORT_ORDER_DUPLICATE = "M00013";

    // =============================================
    // 字典管理业务错误码 (D0xxxx)
    // =============================================
    
    /**
     * 字典类型不存在
     */
    String DICT_TYPE_NOT_FOUND = "D00001";
    
    /**
     * 字典类型已存在
     */
    String DICT_TYPE_ALREADY_EXISTS = "D00002";
    
    /**
     * 字典数据不存在
     */
    String DICT_DATA_NOT_FOUND = "D00003";
    
    /**
     * 字典数据已存在
     */
    String DICT_DATA_ALREADY_EXISTS = "D00004";
    
    /**
     * 系统内置字典不能删除
     */
    String CANNOT_DELETE_SYSTEM_DICT = "D00005";
    
    /**
     * 字典状态异常
     */
    String INVALID_DICT_STATUS = "D00006";
    
    /**
     * 字典数据值重复
     */
    String DICT_DATA_VALUE_DUPLICATE = "D00007";
    
    /**
     * 字典数据标签重复
     */
    String DICT_DATA_LABEL_DUPLICATE = "D00008";

    // =============================================
    // 文件管理业务错误码 (F0xxxx)
    // =============================================
    
    /**
     * 文件不存在
     */
    String FILE_NOT_FOUND = "F00001";
    
    /**
     * 文件上传失败
     */
    String FILE_UPLOAD_FAILED = "F00002";
    
    /**
     * 文件格式不支持
     */
    String FILE_FORMAT_NOT_SUPPORTED = "F00003";
    
    /**
     * 文件大小超限
     */
    String FILE_SIZE_EXCEEDED = "F00004";
    
    /**
     * 文件读取失败
     */
    String FILE_READ_FAILED = "F00005";
    
    /**
     * 文件写入失败
     */
    String FILE_WRITE_FAILED = "F00006";
    
    /**
     * 文件删除失败
     */
    String FILE_DELETE_FAILED = "F00007";

    // =============================================
    // 缓存管理业务错误码 (C0xxxx)
    // =============================================
    
    /**
     * 缓存连接失败
     */
    String CACHE_CONNECTION_FAILED = "C00001";
    
    /**
     * 缓存读取失败
     */
    String CACHE_READ_FAILED = "C00002";
    
    /**
     * 缓存写入失败
     */
    String CACHE_WRITE_FAILED = "C00003";
    
    /**
     * 缓存删除失败
     */
    String CACHE_DELETE_FAILED = "C00004";
    
    /**
     * 缓存清空失败
     */
    String CACHE_CLEAR_FAILED = "C00005";

    // =============================================
    // 数据库操作业务错误码 (B0xxxx)
    // =============================================
    
    /**
     * 数据不存在
     */
    String DATA_NOT_FOUND = "B00001";
    
    /**
     * 数据已存在
     */
    String DATA_ALREADY_EXISTS = "B00002";
    
    /**
     * 数据插入失败
     */
    String DATA_INSERT_FAILED = "B00003";
    
    /**
     * 数据更新失败
     */
    String DATA_UPDATE_FAILED = "B00004";
    
    /**
     * 数据删除失败
     */
    String DATA_DELETE_FAILED = "B00005";
    
    /**
     * 数据查询失败
     */
    String DATA_QUERY_FAILED = "B00006";
    
    /**
     * 数据完整性约束违反
     */
    String DATA_INTEGRITY_VIOLATION = "B00007";
    
    /**
     * 乐观锁冲突
     */
    String OPTIMISTIC_LOCK_CONFLICT = "B00008";
    
    /**
     * 数据版本冲突
     */
    String DATA_VERSION_CONFLICT = "B00009";
    
    /**
     * 数据状态异常
     */
    String INVALID_DATA_STATUS = "B00010";

    // =============================================
    // 第三方服务业务错误码 (T0xxxx)
    // =============================================
    
    /**
     * 第三方服务不可用
     */
    String THIRD_PARTY_SERVICE_UNAVAILABLE = "T00001";
    
    /**
     * 第三方接口调用失败
     */
    String THIRD_PARTY_API_CALL_FAILED = "T00002";
    
    /**
     * 第三方认证失败
     */
    String THIRD_PARTY_AUTH_FAILED = "T00003";
    
    /**
     * 第三方数据格式错误
     */
    String THIRD_PARTY_DATA_FORMAT_ERROR = "T00004";
    
    /**
     * 短信发送失败
     */
    String SMS_SEND_FAILED = "T00005";
    
    /**
     * 邮件发送失败
     */
    String EMAIL_SEND_FAILED = "T00006";
    
    /**
     * 支付服务异常
     */
    String PAYMENT_SERVICE_ERROR = "T00007";
    
    /**
     * 文件存储服务异常
     */
    String FILE_STORAGE_SERVICE_ERROR = "T00008";

    // =============================================
    // 日志管理业务错误码 (700xxx)
    // =============================================
    
    /**
     * 操作日志记录失败
     */
    Integer OPERATION_LOG_RECORD_FAILED = 700001;
    
    /**
     * 登录日志记录失败
     */
    Integer LOGIN_LOG_RECORD_FAILED = 700002;
    
    /**
     * 日志查询失败
     */
    Integer LOG_QUERY_FAILED = 700003;
    
    /**
     * 日志删除失败
     */
    Integer LOG_DELETE_FAILED = 700004;
    
    /**
     * 日志导出失败
     */
    Integer LOG_EXPORT_FAILED = 700005;
    
    /**
     * 日志清空失败
     */
    Integer LOG_CLEAR_FAILED = 700006;
    
    /**
     * 日志统计失败
     */
    Integer LOG_STATISTICS_FAILED = 700007;
    
    /**
     * 操作日志不存在
     */
    Integer OPERATION_LOG_NOT_FOUND = 700008;
    
    /**
     * 登录日志不存在
     */
    Integer LOGIN_LOG_NOT_FOUND = 700009;
    
    /**
     * 日志类型不支持
     */
    Integer LOG_TYPE_NOT_SUPPORTED = 700010;
    
    /**
     * 日志保存失败
     */
    Integer LOG_SAVE_FAILED = 700011;
    
    /**
     * 登出失败
     */
    Integer LOGOUT_FAILED = 700012;
}