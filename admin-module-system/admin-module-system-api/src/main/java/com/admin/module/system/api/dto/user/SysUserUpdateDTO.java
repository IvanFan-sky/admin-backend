package com.admin.module.system.api.dto.user;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统用户更新请求DTO
 * 
 * 用于接收前端更新用户的请求参数
 * 不包含用户名和密码字段，这些需要单独接口处理
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysUserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 必填，用于标识要更新的用户
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 用户昵称
     * 可选，用于界面展示
     */
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;

    /**
     * 邮箱地址
     * 可选，需符合邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     * 可选，支持11位手机号格式
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
     * 头像文件的URL路径
     */
    private String avatar;

    /**
     * 用户状态
     * 1-正常 0-禁用
     */
    private Integer status;

    /**
     * 备注信息
     * 管理员添加的用户说明
     */
    private String remark;

    /**
     * 角色ID数组
     * 重新分配用户角色
     */
    private Long[] roleIds;

    /**
     * 版本号
     * 用于乐观锁控制
     */
    private Integer version;
}