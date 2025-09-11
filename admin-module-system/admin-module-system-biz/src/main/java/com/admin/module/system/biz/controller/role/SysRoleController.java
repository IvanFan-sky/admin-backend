package com.admin.module.system.biz.controller.role;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.role.*;
import com.admin.module.system.api.service.role.SysRoleService;
import com.admin.module.system.api.vo.role.SysRoleVO;
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
 * 系统角色管理控制器
 * 
 * 提供角色管理相关的RESTful API接口
 * 包括角色的增删改查、权限分配等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysRoleController {

    private final SysRoleService roleService;

    /**
     * 创建角色
     * 
     * @param createDTO 创建角色请求参数
     * @return 新创建的角色ID
     */
    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.INSERT, description = "创建角色")
    public R<Long> createRole(@Valid @RequestBody SysRoleCreateDTO createDTO) {
        Long roleId = roleService.createRole(createDTO);
        return R.ok(roleId);
    }

    /**
     * 更新角色
     * 
     * @param updateDTO 更新角色请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新角色")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新角色")
    public R<Void> updateRole(@Valid @RequestBody SysRoleUpdateDTO updateDTO) {
        roleService.updateRole(updateDTO);
        return R.ok();
    }

    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 操作结果
     */
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.DELETE, description = "删除角色")
    public R<Void> deleteRole(@PathVariable @NotNull @Positive Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    /**
     * 批量删除角色
     * 
     * @param ids 角色ID列表
     * @return 实际删除的角色数量
     */
    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除角色")
    public R<Integer> deleteRolesBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = roleService.deleteRolesBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 获取角色详情
     * 
     * @param id 角色ID
     * @return 角色详情信息
     */
    @Operation(summary = "获取角色详情")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<SysRoleVO> getRole(@PathVariable @NotNull @Positive Long id) {
        SysRoleVO roleVO = roleService.getRole(id);
        return R.ok(roleVO);
    }

    /**
     * 根据角色编码获取角色详情
     * 
     * @param roleCode 角色编码
     * @return 角色详情信息
     */
    @Operation(summary = "根据角色编码获取角色详情")
    @Parameter(name = "roleCode", description = "角色编码", required = true, example = "ADMIN")
    @GetMapping("/code/{roleCode}")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<SysRoleVO> getRoleByCode(@PathVariable String roleCode) {
        SysRoleVO roleVO = roleService.getRoleByCode(roleCode);
        return R.ok(roleVO);
    }

    /**
     * 获取角色分页列表
     * 
     * @param queryDTO 查询条件
     * @return 角色分页结果
     */
    @Operation(summary = "获取角色分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<PageResult<SysRoleVO>> getRolePage(@Valid SysRoleQueryDTO queryDTO) {
        PageResult<SysRoleVO> pageResult = roleService.getRolePage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取所有启用状态的角色列表
     * 
     * @return 启用状态的角色列表
     */
    @Operation(summary = "获取启用状态的角色列表")
    @GetMapping("/enabled")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<List<SysRoleVO>> getEnabledRoleList() {
        List<SysRoleVO> roleList = roleService.getEnabledRoleList();
        return R.ok(roleList);
    }

    /**
     * 根据用户ID获取角色列表
     * 
     * @param userId 用户ID
     * @return 用户关联的角色列表
     */
    @Operation(summary = "根据用户ID获取角色列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<List<SysRoleVO>> getRolesByUserId(@PathVariable @NotNull @Positive Long userId) {
        List<SysRoleVO> roleList = roleService.getRolesByUserId(userId);
        return R.ok(roleList);
    }

    /**
     * 分配角色菜单权限
     * 
     * @param roleMenuDTO 角色菜单权限分配参数
     * @return 操作结果
     */
    @Operation(summary = "分配角色菜单权限")
    @PostMapping("/menu/assign")
    @PreAuthorize("@ss.hasPermission('system:role:assign')")
    @OperationLog(title = "角色管理", businessType = OperationLog.BusinessType.GRANT, description = "分配角色菜单权限")
    public R<Void> assignRoleMenus(@Valid @RequestBody SysRoleMenuDTO roleMenuDTO) {
        roleService.assignRoleMenus(roleMenuDTO);
        return R.ok();
    }

    /**
     * 获取角色的菜单权限ID列表
     * 
     * @param roleId 角色ID
     * @return 角色关联的菜单ID列表
     */
    @Operation(summary = "获取角色的菜单权限ID列表")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/{roleId}/menus")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<List<Long>> getRoleMenuIds(@PathVariable @NotNull @Positive Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return R.ok(menuIds);
    }

    /**
     * 更新角色状态
     * 
     * @param id 角色ID
     * @param status 新的状态值
     * @return 操作结果
     */
    @Operation(summary = "更新角色状态")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1")
    @Parameter(name = "status", description = "角色状态", required = true, example = "1")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public R<Void> updateRoleStatus(@PathVariable @NotNull @Positive Long id, 
                                    @PathVariable @NotNull Integer status) {
        roleService.updateRoleStatus(id, status);
        return R.ok();
    }
}