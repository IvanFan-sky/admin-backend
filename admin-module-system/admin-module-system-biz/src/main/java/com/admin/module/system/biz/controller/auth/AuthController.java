package com.admin.module.system.biz.controller.auth;

import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.auth.LoginDTO;
import com.admin.module.system.api.dto.auth.RefreshTokenDTO;
import com.admin.module.system.api.service.auth.AuthService;
import com.admin.module.system.api.vo.auth.LoginVO;
import com.admin.module.system.api.vo.auth.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名密码进行登录认证")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());
        
        LoginVO loginVO = authService.login(loginDTO);
        return R.ok(loginVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录，清除认证信息")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public R<LoginVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO loginVO = authService.refreshToken(refreshTokenDTO);
        return R.ok(loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user-info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public R<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfo = authService.getCurrentUserInfo();
        return R.ok(userInfo);
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate-token")
    @Operation(summary = "验证令牌", description = "验证当前令牌是否有效")
    public R<Boolean> validateToken() {
        // 如果能到达这里，说明令牌是有效的（通过了JWT过滤器）
        return R.ok(true);
    }
}