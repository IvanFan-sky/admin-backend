package com.admin.module.system.biz.controller.admin.user;

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
import lombok.RequiredArgsConstructor;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Operation(summary = "获得用户分页")
    @GetMapping("/page")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<PageResult<SysUserVO>> getUserPage(@Valid SysUserQueryDTO queryDTO) {
        PageResult<SysUserVO> pageResult = userService.getUserPage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(summary = "获得用户列表")
    @GetMapping("/list")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<List<SysUserVO>> getUserList(@Valid SysUserQueryDTO queryDTO) {
        List<SysUserVO> list = userService.getUserList(queryDTO);
        return R.ok(list);
    }

    @Operation(summary = "获得用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @GetMapping("/{id}")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<SysUserVO> getUser(@PathVariable("id") Long id) {
        SysUserVO user = userService.getUser(id);
        return R.ok(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    // @PreAuthorize("hasPermission('system:user:add')")
    public R<Long> createUser(@Valid @RequestBody SysUserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return R.ok(userId);
    }

    @Operation(summary = "修改用户")
    @PutMapping
    // @PreAuthorize("hasPermission('system:user:edit')")
    public R<Boolean> updateUser(@Valid @RequestBody SysUserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return R.ok(true);
    }

    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasPermission('system:user:remove')")
    public R<Boolean> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    // @PreAuthorize("hasPermission('system:user:remove')")
    public R<Boolean> deleteUsers(@RequestBody @NotEmpty(message = "删除用户不能为空") Long[] ids) {
        userService.deleteUsers(ids);
        return R.ok(true);
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/resetPwd")
    // @PreAuthorize("hasPermission('system:user:resetPwd')")
    public R<Boolean> resetUserPwd(@Valid @RequestBody SysUserResetPwdDTO resetPwdDTO) {
        userService.resetUserPwd(resetPwdDTO);
        return R.ok(true);
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/changeStatus")
    // @PreAuthorize("hasPermission('system:user:edit')")
    public R<Boolean> changeStatus(@RequestParam("id") @NotNull(message = "用户ID不能为空") Long id,
                                   @RequestParam("status") @NotNull(message = "状态不能为空") Integer status) {
        userService.updateUserStatus(id, status);
        return R.ok(true);
    }

    @Operation(summary = "校验用户名是否唯一")
    @GetMapping("/checkUsernameUnique")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<Boolean> checkUsernameUnique(@RequestParam("username") String username,
                                          @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkUsernameUnique(username, id);
        return R.ok(unique);
    }


    @Operation(summary = "校验手机号是否唯一")
    @GetMapping("/checkPhoneUnique")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<Boolean> checkPhoneUnique(@RequestParam("phone") String phone,
                                       @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkPhoneUnique(phone, id);
        return R.ok(unique);
    }

    @Operation(summary = "校验邮箱是否唯一")
    @GetMapping("/checkEmailUnique")
    // @PreAuthorize("hasPermission('system:user:query')")
    public R<Boolean> checkEmailUnique(@RequestParam("email") String email,
                                       @RequestParam(value = "id", required = false) Long id) {
        boolean unique = userService.checkEmailUnique(email, id);
        return R.ok(unique);
    }
}