package com.admin.module.system.biz.controller.menu;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.menu.SysMenuCreateDTO;
import com.admin.module.system.api.dto.menu.SysMenuQueryDTO;
import com.admin.module.system.api.dto.menu.SysMenuUpdateDTO;
import com.admin.module.system.api.service.menu.SysMenuService;
import com.admin.module.system.api.vo.menu.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

/**
 * 系统菜单管理控制器
 * 
 * 提供菜单管理相关的RESTful API接口
 * 包括菜单的增删改查、树形结构查询、权限管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysMenuController {

    private final SysMenuService menuService;

    /**
     * 创建菜单
     * 
     * @param createDTO 创建菜单请求参数
     * @return 新创建的菜单ID
     */
    @Operation(summary = "创建菜单")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:menu:create')")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.INSERT, description = "创建菜单")
    public R<Long> createMenu(@Valid @RequestBody SysMenuCreateDTO createDTO) {
        Long menuId = menuService.createMenu(createDTO);
        return R.ok(menuId);
    }

    /**
     * 更新菜单
     * 
     * @param updateDTO 更新菜单请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新菜单")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新菜单")
    public R<Void> updateMenu(@Valid @RequestBody SysMenuUpdateDTO updateDTO) {
        menuService.updateMenu(updateDTO);
        return R.ok();
    }

    /**
     * 删除菜单
     * 
     * @param id 菜单ID
     * @return 操作结果
     */
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "菜单编号", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.DELETE, description = "删除菜单")
    public R<Void> deleteMenu(@PathVariable @NotNull @Positive Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }

    /**
     * 批量删除菜单
     * 
     * @param ids 菜单ID列表
     * @return 实际删除的菜单数量
     */
    @Operation(summary = "批量删除菜单")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    @OperationLog(title = "菜单管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除菜单")
    public R<Integer> deleteMenusBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = menuService.deleteMenusBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 获取菜单详情
     * 
     * @param id 菜单ID
     * @return 菜单详情信息
     */
    @Operation(summary = "获取菜单详情")
    @Parameter(name = "id", description = "菜单编号", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<SysMenuVO> getMenu(@PathVariable @NotNull @Positive Long id) {
        SysMenuVO menuVO = menuService.getMenu(id);
        return R.ok(menuVO);
    }

    /**
     * 获取菜单分页列表
     * 
     * @param queryDTO 查询条件
     * @return 菜单分页结果
     */
    @Operation(summary = "获取菜单分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<PageResult<SysMenuVO>> getMenuPage(@Valid SysMenuQueryDTO queryDTO) {
        PageResult<SysMenuVO> pageResult = menuService.getMenuPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取菜单树形列表
     * 
     * @param queryDTO 查询条件
     * @return 菜单树形结构列表
     */
    @Operation(summary = "获取菜单树形列表")
    @GetMapping("/tree")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<SysMenuVO>> getMenuTree(@Valid SysMenuQueryDTO queryDTO) {
        List<SysMenuVO> menuTree = menuService.getMenuTree(queryDTO);
        return R.ok(menuTree);
    }

    /**
     * 获取启用状态的菜单树形列表
     * 
     * @return 启用状态的菜单树形结构列表
     */
    @Operation(summary = "获取启用状态的菜单树形列表")
    @GetMapping("/tree/enabled")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<SysMenuVO>> getEnabledMenuTree() {
        List<SysMenuVO> menuTree = menuService.getEnabledMenuTree();
        return R.ok(menuTree);
    }

    /**
     * 根据用户ID获取菜单树形列表
     * 
     * @param userId 用户ID
     * @return 用户权限内的菜单树形结构列表
     */
    @Operation(summary = "根据用户ID获取菜单树形列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @GetMapping("/tree/user/{userId}")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<SysMenuVO>> getMenuTreeByUserId(@PathVariable @NotNull @Positive Long userId) {
        List<SysMenuVO> menuTree = menuService.getMenuTreeByUserId(userId);
        return R.ok(menuTree);
    }

    /**
     * 根据角色ID获取菜单列表
     * 
     * @param roleId 角色ID
     * @return 角色关联的菜单列表
     */
    @Operation(summary = "根据角色ID获取菜单列表")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/role/{roleId}")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<SysMenuVO>> getMenusByRoleId(@PathVariable @NotNull @Positive Long roleId) {
        List<SysMenuVO> menuList = menuService.getMenusByRoleId(roleId);
        return R.ok(menuList);
    }

    /**
     * 获取父菜单下拉选项列表
     * 
     * @param excludeId 需要排除的菜单ID（编辑时排除自身及其子菜单）
     * @return 父菜单选项列表
     */
    @Operation(summary = "获取父菜单下拉选项列表")
    @Parameter(name = "excludeId", description = "排除的菜单编号", example = "1")
    @GetMapping("/parent/options")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<SysMenuVO>> getParentMenuOptions(@RequestParam(required = false) Long excludeId) {
        List<SysMenuVO> menuOptions = menuService.getParentMenuOptions(excludeId);
        return R.ok(menuOptions);
    }

    /**
     * 更新菜单状态
     * 
     * @param id 菜单ID
     * @param status 新的状态值
     * @return 操作结果
     */
    @Operation(summary = "更新菜单状态")
    @Parameter(name = "id", description = "菜单编号", required = true, example = "1")
    @Parameter(name = "status", description = "菜单状态", required = true, example = "1")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public R<Void> updateMenuStatus(@PathVariable @NotNull @Positive Long id, 
                                    @PathVariable @NotNull Integer status) {
        menuService.updateMenuStatus(id, status);
        return R.ok();
    }

    /**
     * 刷新菜单缓存
     * 
     * @return 操作结果
     */
    @Operation(summary = "刷新菜单缓存")
    @PostMapping("/cache/refresh")
    @PreAuthorize("@ss.hasPermission('system:menu:refresh')")
    public R<Void> refreshMenuCache() {
        menuService.refreshMenuCache();
        return R.ok();
    }
}