package com.admin.framework.security.filter;

import com.admin.framework.security.utils.JwtTokenUtil;
import com.admin.framework.security.core.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 拦截请求，验证JWT令牌，设置认证信息
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 从请求头获取JWT令牌
        String token = getTokenFromRequest(request);
        
        // 如果令牌存在且当前无认证信息，则进行认证
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证令牌
                if (jwtTokenUtil.validateToken(token)) {
                    // 获取用户信息
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    Long userId = jwtTokenUtil.getUserIdFromToken(token);
                    String authoritiesStr = jwtTokenUtil.getAuthoritiesFromToken(token);
                    
                    // 解析权限
                    List<SimpleGrantedAuthority> authorities = parseAuthorities(authoritiesStr);
                    
                    // 创建登录用户对象
                    LoginUser loginUser = new LoginUser(userId, username, authorities);
                    
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("JWT认证成功，用户: {}", username);
                }
            } catch (Exception e) {
                log.error("JWT令牌处理失败: {}", e.getMessage());
                // 清除可能的认证信息
                SecurityContextHolder.clearContext();
            }
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT令牌
     * 
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头获取
        String authHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        return jwtTokenUtil.getTokenFromAuthHeader(authHeader);
    }

    /**
     * 解析权限字符串为权限列表
     * 
     * @param authoritiesStr 权限字符串，用逗号分隔
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String authoritiesStr) {
        if (!StringUtils.hasText(authoritiesStr)) {
            return List.of();
        }
        
        return Arrays.stream(authoritiesStr.split(","))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}