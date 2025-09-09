package com.admin.common.core.domain.entity;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 
 * 对应数据库sys_user表
 * 存储系统用户的基本信息、登录状态、权限相关数据
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 主键，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     * 唯一索引，长度3-30位，支持字母数字下划线
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    @TableField("username")
    private String username;

    /**
     * 用户昵称（显示名称）
     * 用于界面展示，长度限制30个字符
     */
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    @TableField("nickname")
    private String nickname;

    /**
     * 用户密码
     * 加密存储，不能直接获取明文
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱地址
     * 用于找回密码和消息通知，需符合标准邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @TableField("email")
    private String email;

    /**
     * 手机号码
     * 用于短信验证和消息通知，支持11位中国大陆手机号格式
     */
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    @TableField("phone")
    private String phone;

    /**
     * 用户性别
     * 1-男 2-女 0-未知
     */
    @TableField("sex")
    private String sex;

    /**
     * 用户头像
     * 存储头像文件的URL路径
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户状态
     * 1-正常 0-禁用 2-锁定
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标志
     * 0-正常 1-删除，用于逻辑删除
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * 最后登录IP地址
     * 用于安全审计和异常登录检测
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     * 用于统计用户活跃度和安全审计
     */
    @TableField("login_date")
    private LocalDateTime loginDate;

    /**
     * 版本号
     * 用于乐观锁控制，防止并发修改冲突
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }
}