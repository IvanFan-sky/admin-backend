package com.admin.module.system.api.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户信息VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "用户信息")
public class UserInfoVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "admin@example.com")
    private String email;

    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号", example = "13800138000")
    private String phone;

    /**
     * 用户性别
     */
    @Schema(description = "用户性别", example = "1")
    private Integer gender;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态", example = "1")
    private Integer status;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<String> roles;

    /**
     * 用户权限列表
     */
    @Schema(description = "用户权限列表")
    private List<String> permissions;
}