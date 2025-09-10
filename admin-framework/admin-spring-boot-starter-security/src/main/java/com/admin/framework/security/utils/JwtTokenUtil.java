package com.admin.framework.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌工具类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Component
public class JwtTokenUtil {

    /**
     * JWT密钥
     */
    @Value("${admin.jwt.secret:adminSecretKeyForJWTTokenGeneration}")
    private String secret;

    /**
     * 访问令牌过期时间（毫秒）
     */
    @Value("${admin.jwt.expiration:1800000}")
    private Long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    @Value("${admin.jwt.refresh-expiration:2592000000}")
    private Long refreshExpiration;

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 获取密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定声明
     * 
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * 
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查令牌是否过期
     * 
     * @param token JWT令牌
     * @return true-已过期，false-未过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问令牌
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @param authorities 权限列表
     * @return 访问令牌
     */
    public String generateAccessToken(String username, Long userId, String authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("authorities", authorities);
        claims.put("tokenType", "access");
        return createToken(claims, username, expiration);
    }

    /**
     * 生成刷新令牌
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tokenType", "refresh");
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * 创建令牌
     * 
     * @param claims 声明
     * @param subject 主题
     * @param expiration 过期时间
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌
     * 
     * @param token JWT令牌
     * @param username 用户名
     * @return true-有效，false-无效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌格式和有效性
     * 
     * @param token JWT令牌
     * @return true-有效，false-无效
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式错误: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌参数为空: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取权限信息
     * 
     * @param token JWT令牌
     * @return 权限字符串
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("authorities", String.class);
    }

    /**
     * 检查是否为刷新令牌
     * 
     * @param token JWT令牌
     * @return true-是刷新令牌，false-不是
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            log.error("检查令牌类型失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取令牌剩余有效时间（毫秒）
     * 
     * @param token JWT令牌
     * @return 剩余有效时间
     */
    public Long getRemainingTime(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            final Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            log.error("获取令牌剩余时间失败: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 从请求头中获取令牌
     * 
     * @param authHeader 认证头
     * @return JWT令牌
     */
    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}