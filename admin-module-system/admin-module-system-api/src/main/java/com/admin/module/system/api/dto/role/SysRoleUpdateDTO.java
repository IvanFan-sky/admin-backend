package com.admin.module.system.api.dto.role;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 系统角色更新请求DTO
 * 
 * 用于接收更新角色的请求参数
 * 包含完整的参数校验和字段说明
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysRoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * 更新时必须提供角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正整数")
    private Long id;

    /**
     * 角色名称
     * 显示给用户的角色名称，如"管理员"、"普通用户"
     * 必填，长度2-50个字符
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    private String roleName;

    /**
     * 角色编码
     * 系统内部使用的角色标识，如"ADMIN"、"USER"
     * 必填，长度2-50个字符，只能包含大写字母、数字和下划线
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 50, message = "角色编码长度必须在2-50个字符之间")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "角色编码只能包含大写字母、数字和下划线")
    private String roleCode;

    /**
     * 角色描述
     * 详细说明该角色的职责和权限范围
     * 可选，最大200个字符
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String roleDesc;

    /**
     * 显示顺序
     * 用于角色列表的显示排序，数值越小越靠前
     * 必须为非负整数，默认为0
     */
    @NotNull(message = "显示顺序不能为空")
    @Min(value = 0, message = "显示顺序必须为非负整数")
    private Integer sortOrder;

    /**
     * 角色状态
     * 0-禁用，1-启用
     * 必填
     */
    @NotNull(message = "角色状态不能为空")
    @Min(value = 0, message = "角色状态值不正确")
    @Max(value = 1, message = "角色状态值不正确")
    private Integer status;

    /**
     * 备注信息
     * 额外的说明信息
     * 可选，最大500个字符
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 乐观锁版本号
     * 用于并发控制，更新时必须提供
     */
    @NotNull(message = "版本号不能为空")
    @Min(value = 0, message = "版本号必须为非负整数")
    private Integer version;
}