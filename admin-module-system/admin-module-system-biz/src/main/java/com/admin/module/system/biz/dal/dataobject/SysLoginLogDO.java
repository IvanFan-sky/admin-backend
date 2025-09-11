package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统登录日志数据对象
 * 
 * 对应数据库sys_login_log表
 * 记录用户登录、登出等认证行为日志
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
public class SysLoginLogDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 登录IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 登录状态（1成功 0失败）
     */
    @TableField("status")
    private Integer status;

    /**
     * 提示消息
     */
    @TableField("msg")
    private String msg;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 登录方式（password 密码登录、phone 手机登录、third_party 第三方登录）
     */
    @TableField("login_type")
    private String loginType;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 登录令牌ID（用于关联在线用户）
     */
    @TableField("token_id")
    private String tokenId;

    /**
     * 会话超时时间
     */
    @TableField("session_timeout")
    private LocalDateTime sessionTimeout;

    /**
     * 登出时间
     */
    @TableField("logout_time")
    private LocalDateTime logoutTime;

    /**
     * 登出方式（normal 正常登出、timeout 超时登出、forced 强制登出）
     */
    @TableField("logout_type")
    private String logoutType;

    /**
     * 在线时长（分钟）
     */
    @TableField("online_duration")
    private Long onlineDuration;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;
}