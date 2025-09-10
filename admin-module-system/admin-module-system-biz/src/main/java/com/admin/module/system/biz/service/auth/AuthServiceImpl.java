package com.admin.module.system.biz.service.auth;

import com.admin.common.exception.ServiceException;
import com.admin.framework.security.core.LoginUser;
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

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录尝试，账号: {}", loginDTO.getUsername());
        
        // 1. 参数验证
        validateLoginParams(loginDTO);
        
        // 2. 查找用户
        SysUserDO user = findUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new ServiceException("用户名或密码错误");
        }
        
        // 3. 验证用户状态
        validateUserStatus(user);
        
        // 4. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("用户密码验证失败，用户: {}", loginDTO.getUsername());
            throw new ServiceException("用户名或密码错误");
        }
        
        // 5. 生成JWT令牌
        String authorities = buildUserAuthorities(user);
        String accessToken = jwtTokenUtil.generateAccessToken(user.getUsername(), user.getId(), authorities);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername(), user.getId());
        
        // 6. 更新用户登录信息
        updateLoginInfo(user);
        
        // 7. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(1800000L); // 30分钟
        loginVO.setUserInfo(buildUserInfo(user, authorities));
        loginVO.setLoginTime(LocalDateTime.now());
        
        log.info("用户登录成功，用户: {}", user.getUsername());
        return loginVO;
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            log.info("用户退出登录，用户: {}", loginUser.getUsername());
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
     * TODO: 实际应该从用户角色和权限表中查询
     */
    private String buildUserAuthorities(SysUserDO user) {
        List<String> authorities = new ArrayList<>();
        
        // 添加角色权限（以ROLE_开头）
        if (user.isAdmin()) {
            authorities.add("ROLE_ADMIN");
        } else {
            authorities.add("ROLE_USER");
        }
        
        // 添加功能权限
        // TODO: 从数据库查询用户的实际权限
        authorities.add("system:user:query");
        authorities.add("system:user:create");
        authorities.add("system:user:update");
        authorities.add("system:user:delete");
        
        return String.join(",", authorities);
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
        
        // 解析角色和权限
        if (StringUtils.hasText(authorities)) {
            String[] authArray = authorities.split(",");
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            
            for (String auth : authArray) {
                auth = auth.trim();
                if (auth.startsWith("ROLE_")) {
                    // 去掉ROLE_前缀
                    roles.add(auth.substring(5));
                } else {
                    permissions.add(auth);
                }
            }
            
            userInfo.setRoles(roles);
            userInfo.setPermissions(permissions);
        }
        
        return userInfo;
    }

    /**
     * 更新用户登录信息
     */
    private void updateLoginInfo(SysUserDO user) {
        try {
            user.setLoginDate(LocalDateTime.now());
            // TODO: 获取实际IP地址
            user.setLoginIp("127.0.0.1");
            userMapper.updateById(user);
        } catch (Exception e) {
            log.warn("更新用户登录信息失败: {}", e.getMessage());
        }
    }
}