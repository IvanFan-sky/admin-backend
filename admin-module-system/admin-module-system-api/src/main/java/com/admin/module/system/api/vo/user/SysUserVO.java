package com.admin.module.system.api.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户展示VO
 * 
 * 用于返回给前端的用户信息
 * 不包含敏感信息如密码等
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统用户展示对象")
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户账号（登录用户名）", example = "admin")
    private String username;

    @Schema(description = "用户昵称（显示名称）", example = "系统管理员")
    private String nickname;

    @Schema(description = "邮箱地址", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13888888888")
    private String phone;

    @Schema(description = "用户性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "用户头像URL", example = "https://example.com/avatar/1.jpg")
    private String avatar;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    @Schema(description = "最后登录IP地址", example = "192.168.1.100")
    private String loginIp;

    @Schema(description = "最后登录时间", example = "2024-01-15 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;

    @Schema(description = "创建人", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注信息", example = "系统管理员账号")
    private String remark;

    @Schema(description = "版本号（乐观锁）", example = "1")
    private Integer version;
}