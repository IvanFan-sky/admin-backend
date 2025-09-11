package com.admin.module.system.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统用户重置密码请求对象")
public class SysUserResetPwdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "新密码", example = "newPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, max = 20, message = "用户密码长度必须在6到20个字符之间")
    private String password;

    @Schema(description = "乐观锁版本号", example = "1")
    private Integer version;
}