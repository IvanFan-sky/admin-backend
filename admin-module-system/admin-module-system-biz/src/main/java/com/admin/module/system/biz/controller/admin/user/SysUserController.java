package com.admin.module.system.biz.controller.admin.user;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.user.SysUserCreateDTO;
import com.admin.module.system.api.dto.user.SysUserQueryDTO;
import com.admin.module.system.api.dto.user.SysUserResetPwdDTO;
import com.admin.module.system.api.dto.user.SysUserUpdateDTO;
import com.admin.module.system.api.service.user.SysUserService;
import com.admin.module.system.api.vo.user.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户管理控制器
 * 
 * 提供用户管理相关的REST API接口
 * 包括用户的增删改查、状态管理、密码重置等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 用户管理")
@RestController
@RequestMapping("/admin/system/user")
@RequiredArgsConstructor
@Validated
public class SysUserController {

    private final SysUserService userService;

    @Operation(
        summary = "获得用户分页列表", 
        description = "根据查询条件分页获取用户列表，支持按用户名、昵称、邮箱、手机号、状态等条件筛选"
    )
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<PageResult<SysUserVO>> getUserPage(@Valid SysUserQueryDTO queryDTO) {
        PageResult<SysUserVO> pageResult = userService.getUserPage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(
        summary = "获得用户列表", 
        description = "根据查询条件获取用户列表，不分页返回所有匹配的用户数据"
    )
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<List<SysUserVO>> getUserList(@Valid SysUserQueryDTO queryDTO) {
        List<SysUserVO> list = userService.getUserList(queryDTO);
        return R.ok(list);
    }

    @Operation(
        summary = "获得用户详情", 
        description = "根据用户ID获取用户的详细信息"
    )
    @Parameter(name = "id", description = "用户ID", required = true, example = "1024")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<SysUserVO> getUser(@PathVariable("id") Long id) {
        SysUserVO user = userService.getUser(id);
        return R.ok(user);
    }

    @Operation(
        summary = "创建用户", 
        description = "创建新用户，需要提供用户基本信息和角色分配"
    )
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.INSERT, description = "创建用户")
    public R<Long> createUser(@Valid @RequestBody SysUserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return R.ok(userId);
    }

    @Operation(
        summary = "更新用户信息", 
        description = "更新用户的基本信息，支持修改用户名、昵称、邮箱、手机号等"
    )
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新用户")
    public R<Boolean> updateUser(@Valid @RequestBody SysUserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return R.ok(true);
    }

    @Operation(
        summary = "删除用户", 
        description = "根据用户ID删除用户，执行逻辑删除"
    )
    @Parameter(name = "id", description = "用户ID", required = true, example = "1024")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.DELETE, description = "删除用户")
    public R<Boolean> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除用户")
    public R<Boolean> deleteUsers(@RequestBody @NotEmpty(message = "删除用户不能为空") Long[] ids) {
        userService.deleteUsers(ids);
        return R.ok(true);
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/resetPwd")
    @PreAuthorize("@ss.hasPermission('system:user:resetPwd')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE, description = "重置用户密码", recordRequestParam = false)
    public R<Boolean> resetUserPwd(@Valid @RequestBody SysUserResetPwdDTO resetPwdDTO) {
        userService.resetUserPwd(resetPwdDTO);
        return R.ok(true);
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @OperationLog(title = "用户管理", businessType = OperationLog.BusinessType.UPDATE, description = "修改用户状态")
    public R<Boolean> changeStatus(@RequestParam("id") @NotNull(message = "用户ID不能为空") Long id,
                                   @RequestParam("status") @NotNull(message = "状态不能为空") Integer status) {
        userService.updateUserStatus(id, status);
        return R.ok(true);
    }

    @Operation(summary = "校验用户名是否唯一")
    @GetMapping("/checkUsernameUnique")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> checkUsernameUnique(@RequestParam("username") String username,
                                          @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkUsernameUnique(username, id);
        return R.ok(unique);
    }


    @Operation(summary = "校验手机号是否唯一")
    @GetMapping("/checkPhoneUnique")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> checkPhoneUnique(@RequestParam("phone") String phone,
                                       @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkPhoneUnique(phone, id);
        return R.ok(unique);
    }

    @Operation(summary = "校验邮箱是否唯一")
    @GetMapping("/checkEmailUnique")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<Boolean> checkEmailUnique(@RequestParam("email") String email,
                                       @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkEmailUnique(email, id);
        return R.ok(unique);
    }
}