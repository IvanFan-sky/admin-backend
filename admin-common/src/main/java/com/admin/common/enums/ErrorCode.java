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
    // 通知管理业务错误码 (1100xxx)
    // =============================================
    
    NOTIFICATION_NOT_FOUND(1100001, "通知不存在"),
    NOTIFICATION_TITLE_EMPTY(1100002, "通知标题不能为空"),
    NOTIFICATION_CONTENT_EMPTY(1100003, "通知内容不能为空"),
    NOTIFICATION_ALREADY_PUBLISHED(1100004, "通知已发布，不能修改"),
    NOTIFICATION_ALREADY_WITHDRAWN(1100005, "通知已撤回，不能操作"),
    NOTIFICATION_PUBLISH_FAILED(1100006, "通知发布失败"),
    NOTIFICATION_WITHDRAW_FAILED(1100007, "通知撤回失败"),

    // =============================================
    // 通知类型管理业务错误码 (1101xxx)
    // =============================================
    
    NOTIFICATION_TYPE_NOT_FOUND(1101001, "通知类型不存在"),
    NOTIFICATION_TYPE_CODE_EXISTS(1101002, "通知类型编码已存在"),
    NOTIFICATION_TYPE_NAME_EXISTS(1101003, "通知类型名称已存在"),
    NOTIFICATION_TYPE_IN_USE(1101004, "通知类型已被使用，不能删除"),
    NOTIFICATION_TYPE_SYSTEM_CANNOT_DELETE(1101005, "系统内置通知类型不能删除"),

    // =============================================
    // 站内信管理业务错误码 (1102xxx)
    // =============================================
    
    INTERNAL_MESSAGE_NOT_FOUND(1102001, "站内信不存在"),
    INTERNAL_MESSAGE_RECEIVER_EMPTY(1102002, "站内信接收者不能为空"),
    INTERNAL_MESSAGE_ALREADY_SENT(1102003, "站内信已发送，不能修改"),
    INTERNAL_MESSAGE_SEND_FAILED(1102004, "站内信发送失败"),
    INTERNAL_MESSAGE_WITHDRAW_FAILED(1102005, "站内信撤回失败"),
    INTERNAL_MESSAGE_NOT_DRAFT(1102006, "站内信不是草稿状态"),
    INTERNAL_MESSAGE_SCHEDULED(1102007, "站内信已设置定时发送"),
    INTERNAL_MESSAGE_NOT_SENT(1102008, "站内信未发送状态"),
    INTERNAL_MESSAGE_NO_RECEIVERS(1102009, "站内信没有接收人"),

    // =============================================
    // 用户站内信管理业务错误码 (1102xxx)
    // =============================================
    
    USER_INTERNAL_MESSAGE_NOT_FOUND(1102010, "用户站内信不存在"),

    // =============================================
    // 通知管理业务错误码 (1104xxx)
    // =============================================
    
    NOTIFICATION_NOT_FOUND(1104001, "通知不存在"),
    NOTIFICATION_ALREADY_PUBLISHED(1104002, "通知已发布"),
    NOTIFICATION_ALREADY_WITHDRAWN(1104003, "通知已撤回"),
    NOTIFICATION_PUBLISH_FAILED(1104004, "通知发布失败"),

    // =============================================
    // 系统公告管理业务错误码 (1103xxx)
    // =============================================
    
    SYSTEM_ANNOUNCEMENT_NOT_FOUND(1103001, "系统公告不存在"),
    SYSTEM_ANNOUNCEMENT_TITLE_EMPTY(1103002, "系统公告标题不能为空"),
    SYSTEM_ANNOUNCEMENT_CONTENT_EMPTY(1103003, "系统公告内容不能为空"),
    SYSTEM_ANNOUNCEMENT_ALREADY_PUBLISHED(1103004, "系统公告已发布，不能修改"),
    SYSTEM_ANNOUNCEMENT_PUBLISH_FAILED(1103005, "系统公告发布失败"),
    SYSTEM_ANNOUNCEMENT_PUBLISHED_CANNOT_DELETE(1103006, "已发布的公告不能删除"),
    SYSTEM_ANNOUNCEMENT_NOT_DRAFT_CANNOT_PUBLISH(1103007, "非草稿状态的公告不能发布"),
    SYSTEM_ANNOUNCEMENT_NOT_PUBLISHED_CANNOT_WITHDRAW(1103008, "非已发布状态的公告不能撤回"),

    // =============================================
    // 用户通知管理业务错误码 (1104xxx)
    // =============================================
    
    USER_NOTIFICATION_NOT_FOUND(1104001, "用户通知不存在"),
    USER_NOTIFICATION_ALREADY_READ(1104002, "用户通知已读，不能重复操作"),
    USER_NOTIFICATION_MARK_READ_FAILED(1104003, "用户通知标记已读失败"),
    USER_NOTIFICATION_DELETE_FAILED(1104004, "用户通知删除失败"),

    // =============================================
    // 通知推送业务错误码 (1105xxx)
    // =============================================
    
    NOTIFICATION_PUSH_FAILED(1105001, "通知推送失败"),
    WEBSOCKET_CONNECTION_NOT_FOUND(1105002, "WebSocket连接不存在"),

    // =============================================
    // 支付模块业务错误码 (120xxxx)
    // =============================================
    
    PAYMENT_ORDER_NOT_FOUND(1200001, "支付订单不存在"),
    PAYMENT_ORDER_EXISTS(1200002, "支付订单已存在"),
    PAYMENT_ORDER_STATUS_ERROR(1200003, "支付订单状态错误"),
    PAYMENT_ORDER_EXPIRED(1200004, "支付订单已过期"),
    PAYMENT_METHOD_NOT_SUPPORTED(1200005, "不支持的支付方式"),
    PAYMENT_CHANNEL_NOT_FOUND(1200006, "支付渠道不存在"),
    PAYMENT_CHANNEL_SERVICE_NOT_FOUND(1200007, "支付渠道服务不存在"),
    PAYMENT_CHANNEL_CONFIG_NOT_FOUND(1200008, "支付渠道配置不存在"),
    PAYMENT_CREATE_FAILED(1200009, "创建支付订单失败"),
    PAYMENT_AMOUNT_ERROR(1200010, "支付金额错误"),

    // =============================================
    // 系统通用业务错误码 (999xxx)
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