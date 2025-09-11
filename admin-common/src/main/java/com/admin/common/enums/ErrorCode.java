package com.admin.common.enums;

/**
 * 业务错误码枚举
 * 
 * 定义系统各模块的详细业务错误码和对应错误消息
 * 提供统一的错误码管理和国际化支持
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public enum ErrorCode {

    // =============================================
    // 认证授权业务错误码 (100xxx)
    // =============================================
    
    INVALID_CREDENTIALS(100001, "用户名或密码错误"),
    INVALID_TOKEN(100002, "令牌无效"),
    TOKEN_EXPIRED(100003, "令牌过期"),
    ACCOUNT_LOCKED(100004, "账户被锁定"),
    ACCOUNT_DISABLED(100005, "账户被禁用"),
    LOGIN_FAIL_COUNT_EXCEEDED(100006, "登录失败次数过多"),
    INVALID_CAPTCHA(100007, "验证码错误"),
    CAPTCHA_EXPIRED(100008, "验证码过期"),
    INVALID_REFRESH_TOKEN(100009, "刷新令牌无效"),
    USER_NOT_LOGIN(100010, "用户未登录"),
    SESSION_EXPIRED(100011, "会话过期"),
    ACCESS_DENIED(100012, "权限不足"),

    // =============================================
    // 用户管理业务错误码 (200xxx)
    // =============================================
    
    USER_NOT_FOUND(200001, "用户不存在"),
    USERNAME_ALREADY_EXISTS(200002, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(200003, "邮箱已存在"),
    PHONE_ALREADY_EXISTS(200004, "手机号已存在"),
    INVALID_USERNAME_FORMAT(200005, "用户名格式错误"),
    INVALID_PASSWORD_FORMAT(200006, "密码格式错误"),
    INVALID_EMAIL_FORMAT(200007, "邮箱格式错误"),
    INVALID_PHONE_FORMAT(200008, "手机号格式错误"),
    WRONG_OLD_PASSWORD(200009, "原密码错误"),
    NEW_PASSWORD_SAME_AS_OLD(200010, "新密码与原密码相同"),
    INVALID_USER_STATUS(200011, "用户状态异常"),
    CANNOT_DELETE_ADMIN_USER(200012, "不能删除管理员用户"),
    CANNOT_DISABLE_ADMIN_USER(200013, "不能禁用管理员用户"),
    USER_PASSWORD_NOT_SET(200014, "用户密码未设置"),
    USER_AVATAR_UPLOAD_FAILED(200015, "用户头像上传失败"),


    // =============================================
    // 角色管理业务错误码 (300xxx)
    // =============================================
    
    ROLE_NOT_FOUND(300001, "角色不存在"),
    ROLE_CODE_ALREADY_EXISTS(300002, "角色编码已存在"),
    ROLE_NAME_ALREADY_EXISTS(300003, "角色名称已存在"),
    INVALID_ROLE_CODE_FORMAT(300004, "角色编码格式错误"),
    INVALID_ROLE_STATUS(300005, "角色状态异常"),
    CANNOT_DELETE_SUPER_ADMIN_ROLE(300006, "不能删除超级管理员角色"),
    CANNOT_DISABLE_SUPER_ADMIN_ROLE(300007, "不能禁用超级管理员角色"),
    ROLE_ASSIGNED_TO_USER(300008, "角色已分配给用户，不能删除"),
    ROLE_PERMISSION_ASSIGN_FAILED(300009, "角色权限分配失败"),
    ROLE_SORT_ORDER_DUPLICATE(300010, "角色排序号重复"),
    ROLE_UPDATE_FAILED(300011, "角色更新失败，数据可能已被其他人修改，请刷新后重试"),
    ROLE_STATUS_UPDATE_FAILED(300012, "角色状态更新失败"),
    ROLE_IN_USE(300013, "角色正在被使用，无法删除"),

    // =============================================
    // 菜单权限业务错误码 (400xxx)
    // =============================================
    
    MENU_NOT_FOUND(400001, "菜单不存在"),
    MENU_NAME_ALREADY_EXISTS(400002, "菜单名称已存在"),
    PARENT_MENU_NOT_FOUND(400003, "父菜单不存在"),
    CANNOT_SELECT_SELF_AS_PARENT(400004, "不能选择自己作为父菜单"),
    CANNOT_SELECT_CHILD_AS_PARENT(400005, "不能选择子菜单作为父菜单"),
    INVALID_MENU_TYPE(400006, "菜单类型错误"),
    INVALID_MENU_STATUS(400007, "菜单状态异常"),
    MENU_HAS_CHILDREN(400008, "存在子菜单，不能删除"),
    MENU_ASSIGNED_TO_ROLE(400009, "菜单已分配给角色，不能删除"),
    PERMISSION_ALREADY_EXISTS(400010, "权限标识已存在"),
    INVALID_PERMISSION_FORMAT(400011, "权限标识格式错误"),
    MENU_PATH_ALREADY_EXISTS(400012, "菜单路由地址已存在"),
    MENU_SORT_ORDER_DUPLICATE(400013, "菜单排序号重复"),

    // =============================================
    // 字典管理业务错误码 (500xxx)
    // =============================================
    
    DICT_TYPE_NOT_FOUND(500001, "字典类型不存在"),
    DICT_TYPE_ALREADY_EXISTS(500002, "字典类型已存在"),
    DICT_DATA_NOT_FOUND(500003, "字典数据不存在"),
    DICT_DATA_ALREADY_EXISTS(500004, "字典数据已存在"),
    CANNOT_DELETE_SYSTEM_DICT(500005, "系统内置字典不能删除"),
    INVALID_DICT_STATUS(500006, "字典状态异常"),
    DICT_DATA_VALUE_DUPLICATE(500007, "字典数据值重复"),
    DICT_DATA_LABEL_DUPLICATE(500008, "字典数据标签重复"),

    // =============================================
    // 文件管理业务错误码 (600xxx)
    // =============================================
    
    FILE_NOT_FOUND(600001, "文件不存在"),
    FILE_UPLOAD_FAILED(600002, "文件上传失败"),
    FILE_FORMAT_NOT_SUPPORTED(600003, "文件格式不支持"),
    FILE_SIZE_EXCEEDED(600004, "文件大小超限"),
    FILE_READ_FAILED(600005, "文件读取失败"),
    FILE_WRITE_FAILED(600006, "文件写入失败"),
    FILE_DELETE_FAILED(600007, "文件删除失败"),

    // =============================================
    // 日志管理业务错误码 (700xxx)
    // =============================================
    
    OPERATION_LOG_RECORD_FAILED(700001, "操作日志记录失败"),
    LOGIN_LOG_RECORD_FAILED(700002, "登录日志记录失败"),
    LOG_QUERY_FAILED(700003, "日志查询失败"),
    LOG_DELETE_FAILED(700004, "日志删除失败"),
    LOG_EXPORT_FAILED(700005, "日志导出失败"),
    LOG_CLEAR_FAILED(700006, "日志清空失败"),
    LOG_STATISTICS_FAILED(700007, "日志统计失败"),
    OPERATION_LOG_NOT_FOUND(700008, "操作日志不存在"),
    LOGIN_LOG_NOT_FOUND(700009, "登录日志不存在"),
    LOG_TYPE_NOT_SUPPORTED(700010, "日志类型不支持"),
    LOG_SAVE_FAILED(700011, "日志保存失败"),
    LOGOUT_FAILED(700012, "登出失败"),

    // =============================================
    // 缓存管理业务错误码 (800xxx)
    // =============================================
    
    CACHE_CONNECTION_FAILED(800001, "缓存连接失败"),
    CACHE_READ_FAILED(800002, "缓存读取失败"),
    CACHE_WRITE_FAILED(800003, "缓存写入失败"),
    CACHE_DELETE_FAILED(800004, "缓存删除失败"),
    CACHE_CLEAR_FAILED(800005, "缓存清空失败"),

    // =============================================
    // 数据库操作业务错误码 (900xxx)
    // =============================================
    
    DATA_NOT_FOUND(900001, "数据不存在"),
    DATA_ALREADY_EXISTS(900002, "数据已存在"),
    DATA_INSERT_FAILED(900003, "数据插入失败"),
    DATA_UPDATE_FAILED(900004, "数据更新失败"),
    DATA_DELETE_FAILED(900005, "数据删除失败"),
    DATA_QUERY_FAILED(900006, "数据查询失败"),
    DATA_INTEGRITY_VIOLATION(900007, "数据完整性约束违反"),
    OPTIMISTIC_LOCK_CONFLICT(900008, "乐观锁冲突"),
    DATA_VERSION_CONFLICT(900009, "数据版本冲突"),
    INVALID_DATA_STATUS(900010, "数据状态异常"),

    // =============================================
    // 第三方服务业务错误码 (1000xxx)
    // =============================================
    
    THIRD_PARTY_SERVICE_UNAVAILABLE(1000001, "第三方服务不可用"),
    THIRD_PARTY_API_CALL_FAILED(1000002, "第三方接口调用失败"),
    THIRD_PARTY_AUTH_FAILED(1000003, "第三方认证失败"),
    THIRD_PARTY_DATA_FORMAT_ERROR(1000004, "第三方数据格式错误"),
    SMS_SEND_FAILED(1000005, "短信发送失败"),
    EMAIL_SEND_FAILED(1000006, "邮件发送失败"),
    PAYMENT_SERVICE_ERROR(1000007, "支付服务异常"),
    FILE_STORAGE_SERVICE_ERROR(1000008, "文件存储服务异常"),

    // =============================================
    // 通用业务错误码 (9999xx)
    // =============================================
    
    SYSTEM_ERROR(999999, "系统异常"),
    PARAMETER_ERROR(999998, "参数错误"),
    BUSINESS_ERROR(999997, "业务异常"),
    UNKNOWN_ERROR(999996, "未知错误");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 根据错误码获取错误信息
     *
     * @param code 错误码
     * @return ErrorCode枚举，如果未找到返回UNKNOWN_ERROR
     */
    public static ErrorCode getByCode(Integer code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, message);
    }
}