package com.admin.module.system.biz.controller.user;

import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.user.SysUserRoleDTO;
import com.admin.module.system.api.service.user.SysUserRoleService;
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
 * 用户角色关联管理控制器
 * 
 * 提供用户角色关联相关的RESTful API接口
 * 包括角色分配、查询、移除等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 用户角色关联管理")
@RestController
@RequestMapping("/system/user-role")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysUserRoleController {

    private final SysUserRoleService userRoleService;

    /**
     * 分配用户角色
     * 
     * @param userRoleDTO 用户角色分配参数
     * @return 操作结果
     */
    @Operation(summary = "分配用户角色")
    @PostMapping("/assign")
    @PreAuthorize("@ss.hasPermission('system:user:assign')")
    public R<Void> assignUserRoles(@Valid @RequestBody SysUserRoleDTO userRoleDTO) {
        userRoleService.assignUserRoles(userRoleDTO);
        return R.ok();
    }

    /**
     * 获取用户的角色列表
     * 
     * @param userId 用户ID
     * @return 用户关联的角色列表
     */
    @Operation(summary = "获取用户的角色列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @GetMapping("/user/{userId}/roles")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<List<SysRoleVO>> getUserRoles(@PathVariable @NotNull @Positive Long userId) {
        List<SysRoleVO> roleList = userRoleService.getUserRoles(userId);
        return R.ok(roleList);
    }

    /**
     * 获取用户的角色ID列表
     * 
     * @param userId 用户ID
     * @return 用户关联的角色ID列表
     */
    @Operation(summary = "获取用户的角色ID列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @GetMapping("/user/{userId}/role-ids")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<List<Long>> getUserRoleIds(@PathVariable @NotNull @Positive Long userId) {
        List<Long> roleIds = userRoleService.getUserRoleIds(userId);
        return R.ok(roleIds);
    }

    /**
     * 移除用户角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 操作结果
     */
    @Operation(summary = "移除用户角色")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @DeleteMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize("@ss.hasPermission('system:user:assign')")
    public R<Void> removeUserRole(@PathVariable @NotNull @Positive Long userId,
                                  @PathVariable @NotNull @Positive Long roleId) {
        userRoleService.removeUserRole(userId, roleId);
        return R.ok();
    }

    /**
     * 移除用户的所有角色
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @Operation(summary = "移除用户的所有角色")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @DeleteMapping("/user/{userId}/roles")
    @PreAuthorize("@ss.hasPermission('system:user:assign')")
    public R<Void> removeAllUserRoles(@PathVariable @NotNull @Positive Long userId) {
        userRoleService.removeAllUserRoles(userId);
        return R.ok();
    }

    /**
     * 批量移除用户角色关联
     * 
     * @param userIds 用户ID列表
     * @return 实际移除关联的用户数量
     */
    @Operation(summary = "批量移除用户角色关联")
    @DeleteMapping("/users/roles/batch")
    @PreAuthorize("@ss.hasPermission('system:user:assign')")
    public R<Integer> removeUserRolesBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> userIds) {
        int removeCount = userRoleService.removeUserRolesBatch(userIds);
        return R.ok(removeCount);
    }

    /**
     * 获取拥有指定角色的用户ID列表
     * 
     * @param roleId 角色ID
     * @return 拥有该角色的用户ID列表
     */
    @Operation(summary = "获取拥有指定角色的用户ID列表")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/role/{roleId}/user-ids")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<List<Long>> getUserIdsByRoleId(@PathVariable @NotNull @Positive Long roleId) {
        List<Long> userIds = userRoleService.getUserIdsByRoleId(roleId);
        return R.ok(userIds);
    }

    /**
     * 检查用户是否拥有指定角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 检查结果
     */
    @Operation(summary = "检查用户是否拥有指定角色")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @Parameter(name = "roleId", description = "角色编号", required = true, example = "1")
    @GetMapping("/user/{userId}/role/{roleId}/exists")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> hasUserRole(@PathVariable @NotNull @Positive Long userId,
                                  @PathVariable @NotNull @Positive Long roleId) {
        boolean hasRole = userRoleService.hasUserRole(userId, roleId);
        return R.ok(hasRole);
    }

    /**
     * 检查用户是否拥有指定角色中的任意一个
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 检查结果
     */
    @Operation(summary = "检查用户是否拥有指定角色中的任意一个")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @PostMapping("/user/{userId}/roles/has-any")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> hasAnyUserRole(@PathVariable @NotNull @Positive Long userId,
                                     @RequestBody @NotEmpty Set<@NotNull @Positive Long> roleIds) {
        boolean hasAnyRole = userRoleService.hasAnyUserRole(userId, roleIds);
        return R.ok(hasAnyRole);
    }

    /**
     * 检查用户是否拥有指定的所有角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 检查结果
     */
    @Operation(summary = "检查用户是否拥有指定的所有角色")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @PostMapping("/user/{userId}/roles/has-all")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> hasAllUserRoles(@PathVariable @NotNull @Positive Long userId,
                                      @RequestBody @NotEmpty Set<@NotNull @Positive Long> roleIds) {
        boolean hasAllRoles = userRoleService.hasAllUserRoles(userId, roleIds);
        return R.ok(hasAllRoles);
    }
}