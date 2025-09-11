package com.admin.common.constants;

/**
 * 业务错误码定义
 * 
 * 定义系统各模块的详细业务错误码，便于问题定位和国际化
 * HTTP状态码采用标准200、400等，业务错误码格式：模块代码(2位) + 具体错误(4位)
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
    String INVALID_CREDENTIALS = "A00001";
    
    /**
     * 令牌无效
     */
    String INVALID_TOKEN = "A00002";
    
    /**
     * 令牌过期
     */
    String TOKEN_EXPIRED = "A00003";
    
    /**
     * 账户被锁定
     */
    String ACCOUNT_LOCKED = "A00004";
    
    /**
     * 账户被禁用
     */
    String ACCOUNT_DISABLED = "A00005";
    
    /**
     * 登录失败次数过多
     */
    String LOGIN_FAIL_COUNT_EXCEEDED = "A00006";
    
    /**
     * 验证码错误
     */
    String INVALID_CAPTCHA = "A00007";
    
    /**
     * 验证码过期
     */
    String CAPTCHA_EXPIRED = "A00008";
    
    /**
     * 刷新令牌无效
     */
    String INVALID_REFRESH_TOKEN = "A00009";
    
    /**
     * 用户未登录
     */
    String USER_NOT_LOGIN = "A00010";
    
    /**
     * 会话过期
     */
    String SESSION_EXPIRED = "A00011";
    
    /**
     * 权限不足
     */
    String ACCESS_DENIED = "A00012";

    // =============================================
    // 用户管理业务错误码 (U0xxxx)
    // =============================================
    
    /**
     * 用户不存在
     */
    String USER_NOT_FOUND = "U00001";
    
    /**
     * 用户名已存在
     */
    String USERNAME_ALREADY_EXISTS = "U00002";
    
    /**
     * 邮箱已存在
     */
    String EMAIL_ALREADY_EXISTS = "U00003";
    
    /**
     * 手机号已存在
     */
    String PHONE_ALREADY_EXISTS = "U00004";
    
    /**
     * 用户名格式错误
     */
    String INVALID_USERNAME_FORMAT = "U00005";
    
    /**
     * 密码格式错误
     */
    String INVALID_PASSWORD_FORMAT = "U00006";
    
    /**
     * 邮箱格式错误
     */
    String INVALID_EMAIL_FORMAT = "U00007";
    
    /**
     * 手机号格式错误
     */
    String INVALID_PHONE_FORMAT = "U00008";
    
    /**
     * 原密码错误
     */
    String WRONG_OLD_PASSWORD = "U00009";
    
    /**
     * 新密码与原密码相同
     */
    String NEW_PASSWORD_SAME_AS_OLD = "U00010";
    
    /**
     * 用户状态异常
     */
    String INVALID_USER_STATUS = "U00011";
    
    /**
     * 不能删除管理员用户
     */
    String CANNOT_DELETE_ADMIN_USER = "U00012";
    
    /**
     * 不能禁用管理员用户
     */
    String CANNOT_DISABLE_ADMIN_USER = "U00013";
    
    /**
     * 用户密码未设置
     */
    String USER_PASSWORD_NOT_SET = "U00014";
    
    /**
     * 用户头像上传失败
     */
    String USER_AVATAR_UPLOAD_FAILED = "U00015";

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
}