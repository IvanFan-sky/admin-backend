package com.admin.module.system.api.dto.role;

import com.admin.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

/**
 * 系统角色查询请求DTO
 * 
 * 用于接收角色分页查询的请求参数
 * 继承PageQuery获得分页参数
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     * 支持模糊查询
     * 可选，最大50个字符
     */
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色编码
     * 支持模糊查询
     * 可选，最大50个字符
     */
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    /**
     * 角色状态
     * 0-禁用，1-启用
     * 可选，用于筛选特定状态的角色
     */
    private Integer status;
}