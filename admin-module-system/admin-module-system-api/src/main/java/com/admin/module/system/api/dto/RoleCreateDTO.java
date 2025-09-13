package com.admin.module.system.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 角色创建DTO（用于导入导出）
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class RoleCreateDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z_]{2,50}$", message = "角色编码格式不正确，应为2-50位大写字母或下划线")
    private String roleCode;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单ID列表
     */
    private Long[] menuIds;
}