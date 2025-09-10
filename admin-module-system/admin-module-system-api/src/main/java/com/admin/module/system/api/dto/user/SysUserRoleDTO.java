package com.admin.module.system.api.dto.user;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户角色关联请求DTO
 * 
 * 用于接收用户角色分配的请求参数
 * 包含用户ID和角色ID列表
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysUserRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 要分配角色的用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正整数")
    private Long userId;

    /**
     * 角色ID列表
     * 分配给用户的角色ID集合
     */
    @NotEmpty(message = "角色列表不能为空")
    private Set<Long> roleIds;

    /**
     * 备注信息
     * 角色分配的说明
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}