package com.admin.module.system.api.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 更新菜单请求DTO
 * 
 * 用于接收更新菜单的请求参数
 * 包含菜单ID和需要更新的字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统菜单更新请求对象")
public class SysMenuUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单ID不能为空")
    @Positive(message = "菜单ID必须为正整数")
    private Long id;

    @Schema(description = "父菜单ID", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "父菜单ID不能为空")
    @PositiveOrZero(message = "父菜单ID必须为非负整数")
    private Long parentId;

    @Schema(description = "菜单名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    @Schema(description = "菜单类型", example = "2", allowableValues = {"1", "2", "3"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @Schema(description = "路由地址", example = "/user")
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    @Schema(description = "组件路径", example = "system/user/index")
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    @Schema(description = "权限标识", example = "system:user:view")
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    @Schema(description = "菜单图标", example = "user")
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    private String icon;

    @Schema(description = "显示顺序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    @PositiveOrZero(message = "显示顺序必须为非负整数")
    private Integer sortOrder;

    @Schema(description = "菜单可见性", example = "1", allowableValues = {"0", "1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单状态不能为空")
    private Integer visible;

    @Schema(description = "菜单状态", example = "1", allowableValues = {"0", "1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "是否为外链", example = "0", allowableValues = {"0", "1"})
    private Integer isFrame;

    @Schema(description = "是否缓存", example = "1", allowableValues = {"0", "1"})
    private Integer isCache;

    @Schema(description = "备注信息", example = "用户管理菜单")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

    @Schema(description = "乐观锁版本号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer version;
}