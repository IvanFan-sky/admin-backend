package com.admin.module.system.api.dto.user;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统用户重置密码请求DTO
 * 
 * 用于管理员重置用户密码的请求参数
 * 包含用户ID、新密码和版本号
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysUserResetPwdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 必填，标识要重置密码的用户
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 新密码
     * 明文密码，后端会进行加密处理
     * 长度6-20位，支持字母数字特殊字符
     */
    @Size(min = 6, max = 20, message = "用户密码长度必须在6到20个字符之间")
    private String password;

    /**
     * 版本号
     * 用于乐观锁控制，防止并发操作冲突
     */
    private Integer version;
}