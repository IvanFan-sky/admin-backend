package com.admin.framework.web.aspect;

import com.admin.common.log.StructuredLogger;
import com.admin.common.trace.TraceContext;
import com.admin.common.utils.ServletUtils;
import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.module.system.api.dto.auth.LoginDTO;
import com.admin.module.system.api.service.log.SysLoginLogService;
import com.admin.module.system.api.vo.log.LoginLogInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 登录日志切面
 * 
 * 自动记录用户登录、登出等认证相关操作的日志
 * 包括登录成功、登录失败、登出等事件
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class LoginLogAspect {

    private final SysLoginLogService loginLogService;

    /**
     * 登录成功后记录日志
     */
    @AfterReturning(
        pointcut = "execution(* com.admin.module.system.biz.controller.auth.AuthController.login(..))",
        returning = "result"
    )
    public void doAfterLoginSuccess(JoinPoint joinPoint, Object result) {
        try {
            // 获取登录参数
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof LoginDTO loginDTO) {
                // 异步记录登录成功日志
                saveLoginSuccessLog(loginDTO);
            }
        } catch (Exception e) {
            log.error("记录登录成功日志失败", e);
        }
    }

    /**
     * 登录失败后记录日志
     */
    @AfterThrowing(
        pointcut = "execution(* com.admin.module.system.biz.controller.auth.AuthController.login(..))",
        throwing = "exception"
    )
    public void doAfterLoginFailure(JoinPoint joinPoint, Exception exception) {
        try {
            // 获取登录参数
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof LoginDTO loginDTO) {
                // 异步记录登录失败日志
                saveLoginFailureLog(loginDTO, exception.getMessage());
            }
        } catch (Exception e) {
            log.error("记录登录失败日志失败", e);
        }
    }

    /**
     * 登出成功后记录日志
     */
    @AfterReturning(
        pointcut = "execution(* com.admin.module.system.biz.controller.auth.AuthController.logout(..))"
    )
    public void doAfterLogout(JoinPoint joinPoint) {
        try {
            // 异步记录登出日志
            saveLogoutLog();
        } catch (Exception e) {
            log.error("记录登出日志失败", e);
        }
    }

    /**
     * 异步保存登录成功日志
     *
     * @param loginDTO 登录参数
     */
    @Async("asyncLogExecutor")
    protected void saveLoginSuccessLog(LoginDTO loginDTO) {
        try {
            LoginLogInfoVO loginLogInfo = buildLoginLogInfo(loginDTO, true, null);
            loginLogService.saveLoginLog(loginLogInfo);
            
            // 记录结构化日志
            StructuredLogger.logLogin(
                loginDTO.getUsername(), 
                true, 
                loginLogInfo.getIpAddress(), 
                loginLogInfo.getUserAgent(), 
                null
            );
            
            log.info("登录成功日志已记录: 用户={}, IP={}", loginDTO.getUsername(), loginLogInfo.getIpAddress());
            
        } catch (Exception e) {
            log.error("保存登录成功日志失败: 用户={}, 错误={}", loginDTO.getUsername(), e.getMessage(), e);
        }
    }

    /**
     * 异步保存登录失败日志
     *
     * @param loginDTO 登录参数
     * @param failReason 失败原因
     */
    @Async("asyncLogExecutor")
    protected void saveLoginFailureLog(LoginDTO loginDTO, String failReason) {
        try {
            LoginLogInfoVO loginLogInfo = buildLoginLogInfo(loginDTO, false, failReason);
            loginLogService.saveLoginLog(loginLogInfo);
            
            // 记录结构化日志
            StructuredLogger.logLogin(
                loginDTO.getUsername(), 
                false, 
                loginLogInfo.getIpAddress(), 
                loginLogInfo.getUserAgent(), 
                failReason
            );
            
            log.warn("登录失败日志已记录: 用户={}, IP={}, 原因={}", 
                loginDTO.getUsername(), loginLogInfo.getIpAddress(), failReason);
            
        } catch (Exception e) {
            log.error("保存登录失败日志失败: 用户={}, 错误={}", loginDTO.getUsername(), e.getMessage(), e);
        }
    }

    /**
     * 异步保存登出日志
     */
    @Async("asyncLogExecutor")
    protected void saveLogoutLog() {
        try {
            String username = SecurityAuthUtils.getCurrentUsername();
            String tokenId = TraceContext.getTraceId(); // 使用TraceId作为临时TokenId
            
            if (username != null) {
                loginLogService.updateLogoutInfo(tokenId, "正常登出");
                
                // 记录结构化日志
                StructuredLogger.logSecurity("USER_LOGOUT", "INFO", "用户登出", username);
                
                log.info("登出日志已记录: 用户={}", username);
            }
            
        } catch (Exception e) {
            log.error("保存登出日志失败: 错误={}", e.getMessage(), e);
        }
    }

    /**
     * 构建登录日志信息
     *
     * @param loginDTO 登录参数
     * @param success 是否成功
     * @param failReason 失败原因
     * @return 登录日志信息
     */
    private LoginLogInfoVO buildLoginLogInfo(LoginDTO loginDTO, boolean success, String failReason) {
        LoginLogInfoVO loginLogInfo = new LoginLogInfoVO();
        
        // 基本信息
        loginLogInfo.setUsername(loginDTO.getUsername());
        loginLogInfo.setLoginTime(LocalDateTime.now());
        loginLogInfo.setStatus(success ? 1 : 0);
        loginLogInfo.setMsg(success ? "登录成功" : failReason);
        
        // 请求信息
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            loginLogInfo.setIpAddress(getClientIpAddress(request));
            loginLogInfo.setUserAgent(request.getHeader("User-Agent"));
            loginLogInfo.setLoginLocation(getIpLocation(loginLogInfo.getIpAddress()));
        }
        
        // 链路追踪信息
        loginLogInfo.setTokenId(TraceContext.getTraceId());
        
        return loginLogInfo;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 多级代理的情况，取第一个IP
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip = ip.substring(0, index);
                }
                return ip.trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取IP地址归属地
     *
     * @param ip IP地址
     * @return 归属地
     */
    private String getIpLocation(String ip) {
        try {
            // 这里可以集成第三方IP定位服务
            if (ip == null || ip.isEmpty() || "127.0.0.1".equals(ip) || "localhost".equals(ip)) {
                return "内网IP";
            }
            return "未知位置";
        } catch (Exception e) {
            log.warn("获取IP归属地异常: {}", e.getMessage());
            return "未知位置";
        }
    }
}
