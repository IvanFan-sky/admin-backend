package com.admin.module.system.biz.service.auth;

import com.admin.common.exception.ServiceException;
import com.admin.common.utils.ServletUtils;
import com.admin.framework.redis.service.UserCacheService;
import com.admin.framework.security.core.LoginUser;
import com.admin.framework.security.service.JwtBlacklistService;
import com.admin.framework.security.service.LoginLimitService;
import com.admin.module.system.api.service.auth.PermissionQueryService;
import com.admin.common.utils.AuthorityUtils;
import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.framework.security.utils.JwtTokenUtil;
import com.admin.module.system.api.dto.auth.LoginDTO;
import com.admin.module.system.api.dto.auth.RefreshTokenDTO;
import com.admin.module.system.api.service.auth.AuthService;
import com.admin.module.system.api.vo.auth.LoginVO;
import com.admin.module.system.api.vo.auth.UserInfoVO;
import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import com.admin.module.system.biz.dal.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务实现类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final UserCacheService userCacheService;
    private final LoginLimitService loginLimitService;
    private final PermissionQueryService permissionQueryService;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录尝试，账号: {}", loginDTO.getUsername());
        
        try {
            // 1. 参数验证
            validateLoginParams(loginDTO);
            
            // 2. 检查账户是否被锁定
            if (loginLimitService.isAccountLocked(loginDTO.getUsername())) {
                long remainingTime = loginLimitService.getRemainingLockTime(loginDTO.getUsername());
                throw new ServiceException(String.format("账户已被锁定，请 %d 分钟后重试", remainingTime / 60));
            }
            
            // 3. 查找用户（先从缓存查找）
            SysUserDO user = findUserByUsernameWithCache(loginDTO.getUsername());
            if (user == null) {
                // 记录登录失败
                loginLimitService.recordLoginFail(loginDTO.getUsername());
                throw new ServiceException("用户名或密码错误");
            }
            
            // 4. 验证用户状态
            validateUserStatus(user);
            
            // 5. 验证密码
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                log.warn("用户密码验证失败，用户: {}", loginDTO.getUsername());
                // 记录登录失败
                loginLimitService.recordLoginFail(loginDTO.getUsername());
                throw new ServiceException("用户名或密码错误");
            }
            
            // 6. 登录成功，清除失败记录
            loginLimitService.clearLoginFailCount(loginDTO.getUsername());
            
            // 7. 生成JWT令牌
            String authorities = buildUserAuthoritiesWithCache(user);
            String accessToken = jwtTokenUtil.generateAccessToken(user.getUsername(), user.getId(), authorities);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername(), user.getId());
            
            // 8. 更新用户登录信息
            updateLoginInfo(user);
            
            // 9. 设置用户在线状态
            setUserOnlineStatus(user, accessToken);
            
            // 10. 构建返回结果
            LoginVO loginVO = new LoginVO();
            loginVO.setAccessToken(accessToken);
            loginVO.setRefreshToken(refreshToken);
            loginVO.setTokenType("Bearer");
            loginVO.setExpiresIn(1800000L); // 30分钟
            loginVO.setUserInfo(buildUserInfoWithCache(user, authorities));
            loginVO.setLoginTime(LocalDateTime.now());
            
            log.info("用户登录成功，用户: {}", user.getUsername());
            return loginVO;
            
        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("用户登录异常，用户: {}, 错误: {}", loginDTO.getUsername(), e.getMessage(), e);
            throw new ServiceException("登录失败，请稍后重试");
        }
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            log.info("用户退出登录，用户: {}", loginUser.getUsername());
            
            // 从请求头获取当前令牌并加入黑名单
            // TODO: 这里需要从当前请求中获取令牌，可以通过RequestContextHolder或者传参方式
            // 暂时先清除认证信息
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * 登出并将令牌加入黑名单
     */
    public void logout(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            log.info("用户退出登录，用户: {}", loginUser.getUsername());
            
            // 将令牌加入黑名单
            if (token != null && !token.isEmpty()) {
                jwtBlacklistService.addToBlacklist(token, "用户主动登出");
            }
            
            // 设置用户离线状态
            userCacheService.setUserOffline(loginUser.getUserId());
            
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public LoginVO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        
        // 1. 验证刷新令牌
        if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
            throw new ServiceException("刷新令牌无效");
        }
        
        // 2. 获取用户信息
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        
        SysUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 3. 验证用户状态
        validateUserStatus(user);
        
        // 4. 生成新的访问令牌
        String authorities = buildUserAuthorities(user);
        String newAccessToken = jwtTokenUtil.generateAccessToken(username, userId, authorities);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(username, userId);
        
        // 5. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(newAccessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(1800000L); // 30分钟
        loginVO.setUserInfo(buildUserInfo(user, authorities));
        loginVO.setLoginTime(LocalDateTime.now());
        
        log.info("令牌刷新成功，用户: {}", username);
        return loginVO;
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new ServiceException("未找到当前登录用户信息");
        }
        
        SysUserDO user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        String authorities = buildUserAuthorities(user);
        return buildUserInfo(user, authorities);
    }

    /**
     * 验证登录参数
     */
    private void validateLoginParams(LoginDTO loginDTO) {
        if (!StringUtils.hasText(loginDTO.getUsername())) {
            throw new ServiceException("用户名不能为空");
        }
        if (!StringUtils.hasText(loginDTO.getPassword())) {
            throw new ServiceException("密码不能为空");
        }
    }

    /**
     * 根据用户名查找用户
     * 支持用户名、邮箱、手机号登录
     */
    private SysUserDO findUserByUsername(String username) {
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDO::getUsername, username)
               .or()
               .eq(SysUserDO::getEmail, username)
               .or()
               .eq(SysUserDO::getPhone, username);
        // 未删除
        wrapper.eq(SysUserDO::getDeleted, 0);
        
        return userMapper.selectOne(wrapper);
    }

    /**
     * 验证用户状态
     */
    private void validateUserStatus(SysUserDO user) {
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new ServiceException("账户已被禁用，请联系管理员");
        }
    }

    /**
     * 构建用户权限字符串
     */
    private String buildUserAuthorities(SysUserDO user) {
        try {
            // 获取用户的角色和权限
            PermissionQueryService.UserPermissionInfo permissionInfo = 
                    permissionQueryService.getUserPermissionInfo(user.getId());
            
            // 使用工具类构建权限字符串
            return AuthorityUtils.buildAuthoritiesString(
                    permissionInfo.getRoles(), 
                    permissionInfo.getPermissions());
                    
        } catch (Exception e) {
            log.error("构建用户权限失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage(), e);
            
            // 降级处理：返回基础权限
            List<String> fallbackRoles = new ArrayList<>();
            List<String> fallbackPermissions = new ArrayList<>();
            
            if (user.isAdmin()) {
                fallbackRoles.add("ADMIN");
            } else {
                fallbackRoles.add("USER");
            }
            
            // 基础权限
            fallbackPermissions.add("system:user:query");
            
            return AuthorityUtils.buildAuthoritiesString(fallbackRoles, fallbackPermissions);
        }
    }

    /**
     * 构建用户信息VO
     */
    private UserInfoVO buildUserInfo(SysUserDO user, String authorities) {
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setSex(user.getSex());
        userInfo.setStatus(user.getStatus());
        
        // 使用工具类解析权限信息
        updateUserInfoAuthorities(userInfo, authorities);
        
        return userInfo;
    }

    /**
     * 更新用户登录信息
     */
    private void updateLoginInfo(SysUserDO user) {
        try {
            user.setLoginDate(LocalDateTime.now());
            // 获取客户端IP地址
            user.setLoginIp(getClientIpAddress());
            userMapper.updateById(user);
        } catch (Exception e) {
            log.warn("更新用户登录信息失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        return SecurityAuthUtils.getClientIpAddress();
    }

    /**
     * 更新用户信息中的权限数据
     */
    private void updateUserInfoAuthorities(UserInfoVO userInfo, String authorities) {
        AuthorityUtils.AuthorityInfo authorityInfo = AuthorityUtils.parseAuthorities(authorities);
        userInfo.setRoles(authorityInfo.getRoles());
        userInfo.setPermissions(authorityInfo.getPermissions());
    }

    /**
     * 从缓存中查找用户（优先缓存）
     */
    private SysUserDO findUserByUsernameWithCache(String username) {
        // 先从数据库查找，后续可以考虑添加用户基本信息缓存
        return findUserByUsername(username);
    }

    /**
     * 构建用户权限字符串（使用缓存）
     */
    private String buildUserAuthoritiesWithCache(SysUserDO user) {
        // 尝试从缓存获取权限信息
        List<String> cachedPermissions = userCacheService.getUserPermissions(user.getId());
        List<String> cachedRoles = userCacheService.getUserRoles(user.getId());
        
        if (cachedPermissions != null && cachedRoles != null) {
            // 从缓存构建权限字符串
            return AuthorityUtils.buildAuthoritiesString(cachedRoles, cachedPermissions);
        }
        
        // 缓存未命中，重新构建并缓存
        String authorities = buildUserAuthorities(user);
        
        // 解析并缓存权限信息
        AuthorityUtils.AuthorityInfo authorityInfo = AuthorityUtils.parseAuthorities(authorities);
        
        // 缓存权限信息
        userCacheService.cacheUserRoles(user.getId(), authorityInfo.getRoles());
        userCacheService.cacheUserPermissions(user.getId(), authorityInfo.getPermissions());
        
        return authorities;
    }

    /**
     * 构建用户信息VO（使用缓存）
     */
    private UserInfoVO buildUserInfoWithCache(SysUserDO user, String authorities) {
        // 尝试从缓存获取用户信息
        UserInfoVO cachedUserInfo = userCacheService.getUserInfo(user.getId(), UserInfoVO.class);
        
        if (cachedUserInfo != null) {
            // 更新权限信息（权限可能会变化）
            updateUserInfoAuthorities(cachedUserInfo, authorities);
            return cachedUserInfo;
        }
        
        // 缓存未命中，重新构建并缓存
        UserInfoVO userInfo = buildUserInfo(user, authorities);
        userCacheService.cacheUserInfo(user.getId(), userInfo);
        
        return userInfo;
    }


    /**
     * 设置用户在线状态
     */
    private void setUserOnlineStatus(SysUserDO user, String accessToken) {
        try {
            String tokenId = SecurityAuthUtils.generateTokenId(accessToken);
            UserCacheService.UserOnlineInfo onlineInfo = SecurityAuthUtils.createUserOnlineInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getLoginIp(),
                    getUserAgent(), // 获取User-Agent
                    tokenId
            );
            
            userCacheService.setUserOnline(user.getId(), onlineInfo);
        } catch (Exception e) {
            log.warn("设置用户在线状态失败: {}", e.getMessage());
        }
    }

    /**
     * 获取用户代理信息
     */
    private String getUserAgent() {
        return SecurityAuthUtils.getUserAgent();
    }
}