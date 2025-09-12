package com.admin.framework.web.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.admin.common.annotation.LoginLog;
import com.admin.common.annotation.OperationLog;
import com.admin.common.trace.TraceContext;
import com.admin.common.utils.ServletUtils;
import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.framework.web.config.LogAspectProperties;
import com.admin.framework.web.service.AsyncLogProcessor;
import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 统一日志切面
 * 
 * 统一处理操作日志和登录日志的记录，支持异步处理和异常隔离
 * 
 * @author admin
 * @version 2.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final AsyncLogProcessor asyncLogProcessor;
    private final LogAspectProperties logProperties;

    /**
     * 操作日志切入点
     */
    @Pointcut("@annotation(com.admin.common.annotation.OperationLog)")
    public void operationLogPointcut() {}

    /**
     * 登录日志切入点 - 支持注解和方法名匹配
     */
    @Pointcut("@annotation(com.admin.common.annotation.LoginLog) || " +
              "execution(* *..*Controller.login(..)) || " +
              "execution(* *..*Controller.logout(..))")
    public void loginLogPointcut() {}

    /**
     * 环绕操作日志处理
     */
    @Around("operationLogPointcut()")
    public Object doAroundOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!logProperties.getEnabled()) {
            return joinPoint.proceed();
        }

        Long startTime = System.currentTimeMillis();
        LogContextHolder.setStartTime(startTime);
        
        Object result = null;
        Exception exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                OperationLogCreateDTO logDTO = buildOperationLogContext(joinPoint, result, exception, startTime);
                if (logDTO != null) {
                    // 优先使用异步处理，失败时降级到同步处理
                    try {
                        asyncLogProcessor.processOperationLog(logDTO);
                    } catch (Exception e) {
                        log.warn("异步日志处理失败，降级到同步处理: {}", e.getMessage());
                        asyncLogProcessor.processOperationLogSync(logDTO);
                    }
                }
            } catch (Exception e) {
                log.error("记录操作日志失败: {}", e.getMessage(), e);
            } finally {
                LogContextHolder.clear();
            }
        }
    }

    /**
     * 登录成功后记录日志
     */
    @AfterReturning(pointcut = "loginLogPointcut()", returning = "result")
    public void doAfterLoginSuccess(JoinPoint joinPoint, Object result) {
        if (!logProperties.getEnabled()) {
            return;
        }
        
        try {
            String methodName = joinPoint.getSignature().getName();
            if ("login".equals(methodName)) {
                handleLoginSuccess(joinPoint, result);
            } else if ("logout".equals(methodName)) {
                handleLogout(joinPoint);
            }
        } catch (Exception e) {
            log.error("记录登录成功日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 登录失败后记录日志
     */
    @AfterThrowing(pointcut = "loginLogPointcut()", throwing = "exception")
    public void doAfterLoginFailure(JoinPoint joinPoint, Exception exception) {
        if (!logProperties.getEnabled()) {
            return;
        }
        
        try {
            String methodName = joinPoint.getSignature().getName();
            if ("login".equals(methodName)) {
                handleLoginFailure(joinPoint, exception);
            }
        } catch (Exception e) {
            log.error("记录登录失败日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理登录成功
     */
    private void handleLoginSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            LoginLogCreateDTO loginLog = buildLoginLogContext(args[0], true, "登录成功");
            asyncLogProcessor.processLoginLog(loginLog);
        }
    }

    /**
     * 处理登录失败
     */
    private void handleLoginFailure(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            String errorMsg = exception.getMessage();
            if (StrUtil.isBlank(errorMsg)) {
                errorMsg = "登录失败";
            }
            LoginLogCreateDTO loginLog = buildLoginLogContext(args[0], false, errorMsg);
            asyncLogProcessor.processLoginLog(loginLog);
        }
    }

    /**
     * 处理登出
     */
    private void handleLogout(JoinPoint joinPoint) {
        String username = SecurityAuthUtils.getCurrentUsername();
        if (StrUtil.isNotBlank(username)) {
            LoginLogCreateDTO loginLog = new LoginLogCreateDTO();
            loginLog.setUserName(username);  // 注意：这里使用的是 userName 而不是 username
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setStatus(1);
            loginLog.setMsg("登出成功");
            
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                loginLog.setIpaddr(getClientIpAddress(request));
                loginLog.setLoginLocation(getIpLocation(loginLog.getIpaddr()));
                
                // 浏览器和操作系统信息
                String userAgent = request.getHeader("User-Agent");
                if (StrUtil.isNotBlank(userAgent)) {
                    UserAgent ua = UserAgentUtil.parse(userAgent);
                    loginLog.setBrowser(ua.getBrowser().getName() + " " + ua.getVersion());
                    loginLog.setOs(ua.getOs().getName());
                }
            }
            
            asyncLogProcessor.processLoginLog(loginLog);
        }
    }

    /**
     * 构建操作日志上下文
     */
    private OperationLogCreateDTO buildOperationLogContext(JoinPoint joinPoint, Object result, 
                                                          Exception exception, Long startTime) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        if (operationLog == null) {
            return null;
        }

        OperationLogCreateDTO logDTO = new OperationLogCreateDTO();
        
        // 基本信息
        logDTO.setTitle(operationLog.title());
        logDTO.setBusinessType(operationLog.businessType().getCode());
        logDTO.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName() + "()");
        logDTO.setOperTime(LocalDateTime.now());
        
        // 操作人员信息
        String username = SecurityAuthUtils.getCurrentUsername();
        Long userId = SecurityAuthUtils.getCurrentUserId();
        logDTO.setOperName(StrUtil.isNotBlank(username) ? username : "匿名用户");
        logDTO.setId(userId);
        
        // 请求信息
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            logDTO.setRequestMethod(request.getMethod());
            logDTO.setOperUrl(request.getRequestURI());
            logDTO.setOperIp(getClientIpAddress(request));
            logDTO.setOperLocation(getIpLocation(logDTO.getOperIp()));
            
            // 用户代理信息（可以在后续版本中添加 browser 和 os 字段到 DTO）
            // String userAgent = request.getHeader("User-Agent");
            // 当前版本的 OperationLogCreateDTO 不包含 browser 和 os 字段
        }
        
        // 参数和结果
        if (operationLog.recordRequestParam()) {
            logDTO.setOperParam(getRequestParam(joinPoint));
        }
        if (operationLog.recordResponseResult() && result != null) {
            logDTO.setJsonResult(formatJsonResult(result));
        }
        
        // 状态和异常信息
        if (exception != null) {
            logDTO.setStatus(0);
            if (operationLog.recordException()) {
                logDTO.setErrorMsg(formatException(exception));
            }
        } else {
            logDTO.setStatus(1);
        }
        
        // 执行时间
        long costTime = System.currentTimeMillis() - startTime;
        logDTO.setCostTime(costTime);
        
        return logDTO;
    }

    /**
     * 构建登录日志上下文
     */
    private LoginLogCreateDTO buildLoginLogContext(Object loginParam, boolean success, String message) {
        LoginLogCreateDTO loginLog = new LoginLogCreateDTO();
        
        // 提取用户名
        String username = extractUsername(loginParam);
        loginLog.setUserName(username);  // 注意：这里使用的是 userName 而不是 username
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setStatus(success ? 1 : 0);
        loginLog.setMsg(message);
        
        // 请求信息
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            loginLog.setIpaddr(getClientIpAddress(request));
            loginLog.setLoginLocation(getIpLocation(loginLog.getIpaddr()));
            
            // 浏览器和操作系统信息
            String userAgent = request.getHeader("User-Agent");
            if (StrUtil.isNotBlank(userAgent)) {
                UserAgent ua = UserAgentUtil.parse(userAgent);
                loginLog.setBrowser(ua.getBrowser().getName() + " " + ua.getVersion());
                loginLog.setOs(ua.getOs().getName());
            }
        }
        
        return loginLog;
    }

    /**
     * 提取用户名
     */
    private String extractUsername(Object loginParam) {
        if (loginParam == null) {
            return "unknown";
        }
        
        try {
            // 通过反射获取username字段
            java.lang.reflect.Field usernameField = loginParam.getClass().getDeclaredField("username");
            usernameField.setAccessible(true);
            Object username = usernameField.get(loginParam);
            return username != null ? username.toString() : "unknown";
        } catch (Exception e) {
            log.warn("提取用户名失败: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParam(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return "";
            }

            StringBuilder params = new StringBuilder();
            for (Object arg : args) {
                if (arg != null && !isFilterObject(arg)) {
                    String jsonStr = JSONUtil.toJsonStr(arg);
                    if (jsonStr.length() > logProperties.getMaxParamLength()) {
                        jsonStr = jsonStr.substring(0, logProperties.getMaxParamLength()) + "...";
                    }
                    params.append(jsonStr).append(" ");
                }
            }
            return params.toString().trim();
        } catch (Exception e) {
            log.warn("获取请求参数异常: {}", e.getMessage());
            return "解析参数异常";
        }
    }

    /**
     * 格式化响应结果
     */
    private String formatJsonResult(Object jsonResult) {
        try {
            String result = JSONUtil.toJsonStr(jsonResult);
            if (result.length() > logProperties.getMaxResultLength()) {
                result = result.substring(0, logProperties.getMaxResultLength()) + "...";
            }
            return result;
        } catch (Exception e) {
            log.warn("格式化响应结果异常: {}", e.getMessage());
            return "格式化结果异常";
        }
    }

    /**
     * 格式化异常信息
     */
    private String formatException(Exception e) {
        try {
            String errorMsg = e.getMessage();
            if (StrUtil.isBlank(errorMsg)) {
                errorMsg = e.getClass().getSimpleName();
            }
            if (errorMsg.length() > logProperties.getMaxErrorLength()) {
                errorMsg = errorMsg.substring(0, logProperties.getMaxErrorLength()) + "...";
            }
            return errorMsg;
        } catch (Exception ex) {
            return "格式化异常信息失败";
        }
    }

    /**
     * 判断是否需要过滤的对象
     */
    private boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest 
               || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

    /**
     * 获取客户端IP地址
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
     */
    private String getIpLocation(String ip) {
        try {
            if (StrUtil.isBlank(ip) || "127.0.0.1".equals(ip) || "localhost".equals(ip)) {
                return "内网IP";
            }
            return "未知位置";
        } catch (Exception e) {
            log.warn("获取IP归属地异常: {}", e.getMessage());
            return "未知位置";
        }
    }
}