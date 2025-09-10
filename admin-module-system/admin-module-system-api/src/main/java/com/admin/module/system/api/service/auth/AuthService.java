package com.admin.module.system.api.service.auth;

import com.admin.module.system.api.dto.auth.LoginDTO;
import com.admin.module.system.api.dto.auth.RefreshTokenDTO;
import com.admin.module.system.api.vo.auth.LoginVO;

/**
 * 认证服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginDTO 登录请求参数
     * @return 登录响应信息
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新访问令牌
     * 
     * @param refreshTokenDTO 刷新令牌请求参数
     * @return 新的令牌信息
     */
    LoginVO refreshToken(RefreshTokenDTO refreshTokenDTO);

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    com.admin.module.system.api.vo.auth.UserInfoVO getCurrentUserInfo();
}