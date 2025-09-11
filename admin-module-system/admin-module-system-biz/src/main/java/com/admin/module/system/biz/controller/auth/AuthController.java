package com.admin.module.system.biz.controller.auth;

import com.admin.common.core.domain.R;
import com.admin.framework.redis.service.UserCacheService;
import com.admin.framework.security.service.LoginLimitService;
import com.admin.framework.security.utils.JwtTokenUtil;
import com.admin.module.system.api.dto.auth.LoginDTO;
import com.admin.module.system.api.dto.auth.RefreshTokenDTO;
import com.admin.module.system.api.service.auth.AuthService;
import com.admin.module.system.api.vo.auth.LoginStatusVO;
import com.admin.module.system.api.vo.auth.LoginVO;
import com.admin.module.system.api.vo.auth.UserInfoVO;
import com.admin.module.system.biz.service.auth.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新等认证相关接口")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserCacheService userCacheService;
    private final LoginLimitService loginLimitService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(
        summary = "用户登录", 
        description = "用户通过用户名和密码进行身份认证，成功后返回访问令牌和用户信息"
    )
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());
        
        LoginVO loginVO = authService.login(loginDTO);
        return R.ok(loginVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(
        summary = "用户登出", 
        description = "用户退出登录，清除认证信息并将令牌加入黑名单，防止令牌被滥用"
    )
    public R<Void> logout(HttpServletRequest request) {
        // 从请求头获取令牌
        String authHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        String token = jwtTokenUtil.getTokenFromAuthHeader(authHeader);
        
        // 调用带令牌的登出方法
        if (authService instanceof AuthServiceImpl authServiceImpl) {
            authServiceImpl.logout(token);
        } else {
            authService.logout();
        }
        
        return R.ok();
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    @Operation(
        summary = "刷新访问令牌", 
        description = "使用刷新令牌获取新的访问令牌，用于延续用户会话"
    )
    public R<LoginVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO loginVO = authService.refreshToken(refreshTokenDTO);
        return R.ok(loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user-info")
    @Operation(
        summary = "获取当前用户信息", 
        description = "获取当前登录用户的详细信息，包括基本信息和权限信息"
    )
    public R<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfo = authService.getCurrentUserInfo();
        return R.ok(userInfo);
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate-token")
    @Operation(
        summary = "验证令牌有效性", 
        description = "检查当前访问令牌是否有效且未过期"
    )
    public R<Boolean> validateToken() {
        // 如果能到达这里，说明令牌是有效的（通过了JWT过滤器）
        return R.ok(true);
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/login-status")
    @Operation(
        summary = "检查用户登录状态", 
        description = "检查指定用户名的登录状态，包括是否被锁定、失败次数等信息"
    )
    public R<LoginStatusVO> checkLoginStatus(@RequestParam String username) {
        try {
            LoginStatusVO statusVO = new LoginStatusVO();
            
            // 检查账户是否被锁定
            boolean isLocked = loginLimitService.isAccountLocked(username);
            statusVO.setLocked(isLocked);
            
            if (isLocked) {
                LoginLimitService.AccountLockInfo lockInfo = loginLimitService.getAccountLockInfo(username);
                statusVO.setLockInfo(lockInfo);
                statusVO.setRemainingLockTime(loginLimitService.getRemainingLockTime(username));
            }
            
            // 获取登录失败次数
            int failCount = loginLimitService.getLoginFailCount(username);
            statusVO.setFailCount(failCount);
            
            // 检查是否需要验证码
            boolean needCaptcha = loginLimitService.needCaptcha(username);
            statusVO.setNeedCaptcha(needCaptcha);
            
            return R.ok(statusVO);
            
        } catch (Exception e) {
            log.error("检查登录状态失败，用户名: {}, 错误: {}", username, e.getMessage());
            return R.error("检查登录状态失败");
        }
    }

}