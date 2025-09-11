package com.admin.module.system.api.dto.role;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统角色查询请求对象")
public class SysRoleQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称", example = "管理员")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Schema(description = "角色编码", example = "admin")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    @Schema(description = "角色状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}