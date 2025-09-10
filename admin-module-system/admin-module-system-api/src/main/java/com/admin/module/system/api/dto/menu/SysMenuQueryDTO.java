package com.admin.module.system.api.dto.menu;

import com.admin.common.core.page.PageQuery;
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
public class SysMenuQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     * 支持模糊查询
     */
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 菜单类型
     * 1-目录，2-菜单，3-按钮
     */
    private Integer menuType;

    /**
     * 菜单状态
     * 0-隐藏，1-显示
     */
    private Integer visible;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 权限标识
     * 支持模糊查询
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    /**
     * 父菜单ID
     * 用于查询指定父菜单下的子菜单
     */
    private Long parentId;
}