package com.admin.module.system.api.dto.menu;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单查询请求DTO
 * 
 * 用于接收菜单查询的请求参数
 * 支持菜单名称、状态、类型等条件筛选
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统菜单查询请求对象")
public class SysMenuQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单名称", example = "用户管理")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @Schema(description = "菜单类型", example = "2", allowableValues = {"1", "2", "3"})
    private Integer menuType;

    @Schema(description = "菜单可见性", example = "1", allowableValues = {"0", "1"})
    private Integer visible;

    @Schema(description = "菜单状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "权限标识", example = "system:user:view")
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;
}