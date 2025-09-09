package com.admin.module.system.api.dto.user;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统用户创建请求DTO
 * 
 * 用于接收前端创建用户的请求参数
 * 包含用户基本信息和角色分配信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysUserCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号（登录用户名）
     * 唯一标识，长度3-30位，支持字母数字下划线
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    private String username;

    /**
     * 用户昵称（显示名称）
     * 用于界面展示，长度限制30个字符
     */
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;

    /**
     * 用户密码
     * 明文密码，后端会进行加密处理
     * 长度6-20位，支持字母数字特殊字符
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度必须在6到20个字符之间")
    private String password;

    /**
     * 邮箱地址
     * 用于找回密码和消息通知
     * 需符合标准邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     * 用于短信验证和消息通知
     * 支持11位中国大陆手机号格式
     */
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phone;

    /**
     * 用户性别
     * 1-男 2-女 0-未知
     */
    private String sex;

    /**
     * 用户头像
     * 存储头像文件的URL路径
     */
    private String avatar;

    /**
     * 用户状态
     * 1-正常 0-禁用
     */
    private Integer status;

    /**
     * 备注信息
     * 管理员添加的用户说明信息
     */
    private String remark;

    /**
     * 角色ID数组
     * 为用户分配的角色列表
     */
    private Long[] roleIds;
}