package com.admin.module.system.biz.controller.role;

import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.role.SysRoleMenuDTO;
import com.admin.module.system.api.service.role.SysRoleMenuService;
import com.admin.module.system.api.vo.menu.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

/**
 * 角色菜单关联管理控制器
 * 
 * 提供角色菜单关联相关的RESTful API接口
 * 包括权限分配、查询、移除等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 角色菜单关联管理")
@RestController
@RequestMapping("/system/role-menu")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysRoleMenuController {

    private final SysRoleMenuService roleMenuService;

    /**
     * 分配角色菜单权限
     * 
     * @param roleMenuDTO 角色菜单权限分配参数
     * @return 操作结果
     */
    @Operation(summary = "分配角色菜单权限")
    @PostMapping("/assign")
    public R<Void> assignRoleMenus(@Valid @RequestBody SysRoleMenuDTO roleMenuDTO) {
        roleMenuService.assignRoleMenus(roleMenuDTO);
        return R.ok();
    }

    /**
     * 获取角色的菜单权限列表
     * 
     * @param roleId 角色ID
     * @return 角色关联的菜单列表
     */
    @Operation(summary = "获取角色的菜单权限列表")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/role/{roleId}/menus")
    public R<List<SysMenuVO>> getRoleMenus(@PathVariable @NotNull @Positive Long roleId) {
        List<SysMenuVO> menuList = roleMenuService.getRoleMenus(roleId);
        return R.ok(menuList);
    }

    /**
     * 获取角色的菜单权限ID列表
     * 
     * @param roleId 角色ID
     * @return 角色关联的菜单ID列表
     */
    @Operation(summary = "获取角色的菜单权限ID列表")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/role/{roleId}/menu-ids")
    public R<List<Long>> getRoleMenuIds(@PathVariable @NotNull @Positive Long roleId) {
        List<Long> menuIds = roleMenuService.getRoleMenuIds(roleId);
        return R.ok(menuIds);
    }

    /**
     * 获取角色菜单权限树形结构
     * 
     * @param roleId 角色ID
     * @return 角色菜单权限树形结构列表
     */
    @Operation(summary = "获取角色菜单权限树形结构")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/role/{roleId}/menu-tree")
    public R<List<SysMenuVO>> getRoleMenuTree(@PathVariable @NotNull @Positive Long roleId) {
        List<SysMenuVO> menuTree = roleMenuService.getRoleMenuTree(roleId);
        return R.ok(menuTree);
    }

    /**
     * 移除角色菜单权限
     * 
     * @param roleId 角色ID
     * @param menuId 菜单ID
     * @return 操作结果
     */
    @Operation(summary = "移除角色菜单权限")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @Parameter(name = "menuId", description = "菜单编号", required = true, example = "1")
    @DeleteMapping("/role/{roleId}/menu/{menuId}")
    public R<Void> removeRoleMenu(@PathVariable @NotNull @Positive Long roleId,
                                  @PathVariable @NotNull @Positive Long menuId) {
        roleMenuService.removeRoleMenu(roleId, menuId);
        return R.ok();
    }

    /**
     * 移除角色的所有菜单权限
     * 
     * @param roleId 角色ID
     * @return 操作结果
     */
    @Operation(summary = "移除角色的所有菜单权限")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @DeleteMapping("/role/{roleId}/menus")
    public R<Void> removeAllRoleMenus(@PathVariable @NotNull @Positive Long roleId) {
        roleMenuService.removeAllRoleMenus(roleId);
        return R.ok();
    }

    /**
     * 批量移除角色菜单权限关联
     * 
     * @param roleIds 角色ID列表
     * @return 实际移除关联的角色数量
     */
    @Operation(summary = "批量移除角色菜单权限关联")
    @DeleteMapping("/roles/menus/batch")
    public R<Integer> removeRoleMenusBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> roleIds) {
        int removeCount = roleMenuService.removeRoleMenusBatch(roleIds);
        return R.ok(removeCount);
    }

    /**
     * 获取拥有指定菜单权限的角色ID列表
     * 
     * @param menuId 菜单ID
     * @return 拥有该菜单权限的角色ID列表
     */
    @Operation(summary = "获取拥有指定菜单权限的角色ID列表")
    @Parameter(name = "menuId", description = "菜单编号", required = true, example = "1")
    @GetMapping("/menu/{menuId}/role-ids")
    public R<List<Long>> getRoleIdsByMenuId(@PathVariable @NotNull @Positive Long menuId) {
        List<Long> roleIds = roleMenuService.getRoleIdsByMenuId(menuId);
        return R.ok(roleIds);
    }

    /**
     * 检查角色是否拥有指定菜单权限
     * 
     * @param roleId 角色ID
     * @param menuId 菜单ID
     * @return 检查结果
     */
    @Operation(summary = "检查角色是否拥有指定菜单权限")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @Parameter(name = "menuId", description = "菜单编号", required = true, example = "1")
    @GetMapping("/role/{roleId}/menu/{menuId}/exists")
    public R<Boolean> hasRoleMenu(@PathVariable @NotNull @Positive Long roleId,
                                  @PathVariable @NotNull @Positive Long menuId) {
        boolean hasMenu = roleMenuService.hasRoleMenu(roleId, menuId);
        return R.ok(hasMenu);
    }

    /**
     * 检查角色是否拥有指定菜单权限中的任意一个
     * 
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 检查结果
     */
    @Operation(summary = "检查角色是否拥有指定菜单权限中的任意一个")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @PostMapping("/role/{roleId}/menus/has-any")
    public R<Boolean> hasAnyRoleMenu(@PathVariable @NotNull @Positive Long roleId,
                                     @RequestBody @NotEmpty Set<@NotNull @Positive Long> menuIds) {
        boolean hasAnyMenu = roleMenuService.hasAnyRoleMenu(roleId, menuIds);
        return R.ok(hasAnyMenu);
    }

    /**
     * 检查角色是否拥有指定的所有菜单权限
     * 
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 检查结果
     */
    @Operation(summary = "检查角色是否拥有指定的所有菜单权限")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @PostMapping("/role/{roleId}/menus/has-all")
    public R<Boolean> hasAllRoleMenus(@PathVariable @NotNull @Positive Long roleId,
                                      @RequestBody @NotEmpty Set<@NotNull @Positive Long> menuIds) {
        boolean hasAllMenus = roleMenuService.hasAllRoleMenus(roleId, menuIds);
        return R.ok(hasAllMenus);
    }

    /**
     * 检查菜单是否被任何角色使用
     * 
     * @param menuId 菜单ID
     * @return 检查结果
     */
    @Operation(summary = "检查菜单是否被任何角色使用")
    @Parameter(name = "menuId", description = "菜单编号", required = true, example = "1")
    @GetMapping("/menu/{menuId}/used")
    public R<Boolean> isMenuUsedByAnyRole(@PathVariable @NotNull @Positive Long menuId) {
        boolean isUsed = roleMenuService.isMenuUsedByAnyRole(menuId);
        return R.ok(isUsed);
    }

    /**
     * 根据菜单ID列表批量移除相关的角色菜单权限
     * 
     * @param menuIds 菜单ID列表
     * @return 实际移除的关联数量
     */
    @Operation(summary = "根据菜单ID列表批量移除相关的角色菜单权限")
    @DeleteMapping("/menus/roles/batch")
    public R<Integer> removeRoleMenusByMenuIds(@RequestBody @NotEmpty Set<@NotNull @Positive Long> menuIds) {
        int removeCount = roleMenuService.removeRoleMenusByMenuIds(menuIds);
        return R.ok(removeCount);
    }
}