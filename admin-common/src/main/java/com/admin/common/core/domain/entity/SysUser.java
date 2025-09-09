package com.admin.common.core.domain.entity;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    @TableField("username")
    private String username;

    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    @TableField("nickname")
    private String nickname;

    @TableField("password")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @TableField("email")
    private String email;

    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    @TableField("phone")
    private String phone;

    @TableField("sex")
    private String sex;

    @TableField("avatar")
    private String avatar;

    @TableField("status")
    private Integer status;

    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;

    @TableField("login_ip")
    private String loginIp;

    @TableField("login_date")
    private LocalDateTime loginDate;

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