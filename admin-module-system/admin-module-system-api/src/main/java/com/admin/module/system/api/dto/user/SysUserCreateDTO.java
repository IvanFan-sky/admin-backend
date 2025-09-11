package com.admin.module.system.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统用户创建请求对象")
public class SysUserCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号（登录用户名）", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    private String username;

    @Schema(description = "用户昵称（显示名称）", example = "张三")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;

    @Schema(description = "用户密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度必须在6到20个字符之间")
    private String password;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @Schema(description = "手机号码", example = "13888888888")
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phone;

    @Schema(description = "用户性别", example = "1", allowableValues = {"0", "1", "2"})
    private String sex;

    @Schema(description = "用户头像URL", example = "https://example.com/avatar/1.jpg")
    private String avatar;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "备注信息", example = "普通用户")
    private String remark;

    @Schema(description = "角色ID数组", example = "[2, 3]")
    private Long[] roleIds;
}