package com.admin.framework.web.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.admin.common.annotation.OperationLog;
import com.admin.common.constants.ErrorCodes;
import com.admin.common.constants.SystemConstants;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.ServletUtils;
import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.module.system.api.dto.log.OperationLogDTO;
import com.admin.module.system.api.service.log.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 操作日志AOP切面
 * 
 * 拦截标记@OperationLog注解的方法，自动记录操作日志
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    /**
     * 操作开始时间
     */
    private static final ThreadLocal<Long> OPERATION_START_TIME = new ThreadLocal<>();

    private final SysOperationLogService operationLogService;

    /**
     * 处理请求前执行
     */
    @Before("@annotation(operationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog operationLog) {
        // 记录开始时间
        OPERATION_START_TIME.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     * @param operationLog 操作日志注解
     * @param jsonResult 返回结果
     */
    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog operationLog, Object jsonResult) {
        handleLog(joinPoint, operationLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param operationLog 操作日志注解
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(operationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception e) {
        handleLog(joinPoint, operationLog, e, null);
    }

    /**
     * 处理日志记录
     */
    protected void handleLog(final JoinPoint joinPoint, OperationLog operationLog, final Exception e, Object jsonResult) {
        try {
            // 获取当前用户信息
            String operatorName = SecurityAuthUtils.getCurrentUsername();
            Long operatorId = SecurityAuthUtils.getCurrentUserId();

            // 构建操作日志对象
            OperationLogInfo logInfo = buildOperationLogInfo(joinPoint, operationLog, e, jsonResult, operatorName, operatorId);

            // 异步保存日志
            saveOperationLog(logInfo);

        } catch (Exception ex) {
            log.error("记录操作日志异常: {}", ex.getMessage(), ex);
        } finally {
            // 清理ThreadLocal
            OPERATION_START_TIME.remove();
        }
    }

    /**
     * 构建操作日志信息
     */
    private OperationLogInfo buildOperationLogInfo(JoinPoint joinPoint, OperationLog operationLog,
                                                 Exception e, Object jsonResult,
                                                 String operatorName, Long operatorId) {
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();

        // 获取请求信息
        HttpServletRequest request = ServletUtils.getRequest();
        String userAgent = ServletUtils.getUserAgent();
        UserAgent ua = UserAgentUtil.parse(userAgent);

        // 计算执行时间
        Long startTime = OPERATION_START_TIME.get();
        long costTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

        // 构建日志对象
        OperationLogInfo logInfo = new OperationLogInfo();
        
        // 基本信息
        logInfo.setTitle(operationLog.title());
        logInfo.setBusinessType(operationLog.businessType().getCode());
        logInfo.setMethod(className + "." + methodName + "()");
        logInfo.setRequestMethod(request.getMethod());
        logInfo.setOperatorType(operationLog.operatorType().getCode());
        logInfo.setOperatorName(StrUtil.isNotBlank(operatorName) ? operatorName : "匿名用户");
        logInfo.setOperatorId(operatorId);
        
        // 请求信息
        logInfo.setOperationUrl(request.getRequestURI());
        logInfo.setOperationIp(SecurityAuthUtils.getClientIpAddress());
        logInfo.setOperationLocation(getIpLocation(logInfo.getOperationIp()));
        logInfo.setUserAgent(userAgent);
        logInfo.setBrowser(ua.getBrowser().getName() + " " + ua.getVersion());
        logInfo.setOs(ua.getOs().getName());
        
        // 参数和结果
        if (operationLog.recordRequestParam()) {
            logInfo.setOperationParam(getRequestParam(joinPoint));
        }
        if (operationLog.recordResponseResult() && jsonResult != null) {
            logInfo.setJsonResult(formatJsonResult(jsonResult));
        }
        
        // 状态和异常信息
        if (e != null) {
            logInfo.setStatus(SystemConstants.LOG_STATUS_FAILED);
            if (operationLog.recordException()) {
                logInfo.setErrorMsg(formatException(e));
            }
        } else {
            logInfo.setStatus(SystemConstants.LOG_STATUS_SUCCESS);
        }
        
        // 执行时间
        logInfo.setCostTime(costTime);
        logInfo.setOperationTime(LocalDateTime.now());
        
        return logInfo;
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
                    // 限制参数长度
                    if (jsonStr.length() > SystemConstants.REQUEST_PARAM_MAX_LENGTH) {
                        jsonStr = jsonStr.substring(0, SystemConstants.REQUEST_PARAM_MAX_LENGTH) + "...";
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
            // 限制结果长度
            if (result.length() > SystemConstants.RESPONSE_RESULT_MAX_LENGTH) {
                result = result.substring(0, SystemConstants.RESPONSE_RESULT_MAX_LENGTH) + "...";
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
            // 限制错误信息长度
            if (errorMsg.length() > SystemConstants.ERROR_MESSAGE_MAX_LENGTH) {
                errorMsg = errorMsg.substring(0, SystemConstants.ERROR_MESSAGE_MAX_LENGTH) + "...";
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
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

    /**
     * 获取IP地址归属地
     */
    private String getIpLocation(String ip) {
        try {
            // 这里可以集成第三方IP定位服务
            if (StrUtil.isBlank(ip) || "127.0.0.1".equals(ip) || "localhost".equals(ip)) {
                return "内网IP";
            }
            return "未知位置";
        } catch (Exception e) {
            log.warn("获取IP归属地异常: {}", e.getMessage());
            return "未知位置";
        }
    }

    /**
     * 异步保存操作日志
     */
    @Async
    protected void saveOperationLog(OperationLogInfo logInfo) {
        try {
            // 转换为DTO
            OperationLogDTO logDTO = convertToDTO(logInfo);
            operationLogService.saveOperationLog(logDTO);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.OPERATION_LOG_RECORD_FAILED, "操作日志记录失败");
        }
    }

    /**
     * 转换OperationLogInfo为OperationLogDTO
     */
    private OperationLogDTO convertToDTO(OperationLogInfo logInfo) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setTitle(logInfo.getTitle());
        dto.setBusinessType(logInfo.getBusinessType());
        dto.setMethod(logInfo.getMethod());
        dto.setRequestMethod(logInfo.getRequestMethod());
        dto.setOperatorType(logInfo.getOperatorType());
        dto.setOperatorName(logInfo.getOperatorName());
        dto.setOperatorId(logInfo.getOperatorId());
        dto.setDeptName(logInfo.getDeptName());
        dto.setOperationUrl(logInfo.getOperationUrl());
        dto.setOperationIp(logInfo.getOperationIp());
        dto.setOperationLocation(logInfo.getOperationLocation());
        dto.setOperationParam(logInfo.getOperationParam());
        dto.setJsonResult(logInfo.getJsonResult());
        dto.setStatus(logInfo.getStatus());
        dto.setErrorMsg(logInfo.getErrorMsg());
        dto.setOperationTime(logInfo.getOperationTime());
        dto.setCostTime(logInfo.getCostTime());
        dto.setUserAgent(logInfo.getUserAgent());
        dto.setBrowser(logInfo.getBrowser());
        dto.setOs(logInfo.getOs());
        return dto;
    }

    /**
     * 操作日志信息内部类
     */
    public static class OperationLogInfo {
        private String title;
        private Integer businessType;
        private String method;
        private String requestMethod;
        private Integer operatorType;
        private String operatorName;
        private Long operatorId;
        private String deptName;
        private String operationUrl;
        private String operationIp;
        private String operationLocation;
        private String operationParam;
        private String jsonResult;
        private Integer status;
        private String errorMsg;
        private LocalDateTime operationTime;
        private Long costTime;
        private String userAgent;
        private String browser;
        private String os;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getBusinessType() { return businessType; }
        public void setBusinessType(Integer businessType) { this.businessType = businessType; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getRequestMethod() { return requestMethod; }
        public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

        public Integer getOperatorType() { return operatorType; }
        public void setOperatorType(Integer operatorType) { this.operatorType = operatorType; }

        public String getOperatorName() { return operatorName; }
        public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

        public Long getOperatorId() { return operatorId; }
        public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }

        public String getOperationUrl() { return operationUrl; }
        public void setOperationUrl(String operationUrl) { this.operationUrl = operationUrl; }

        public String getOperationIp() { return operationIp; }
        public void setOperationIp(String operationIp) { this.operationIp = operationIp; }

        public String getOperationLocation() { return operationLocation; }
        public void setOperationLocation(String operationLocation) { this.operationLocation = operationLocation; }

        public String getOperationParam() { return operationParam; }
        public void setOperationParam(String operationParam) { this.operationParam = operationParam; }

        public String getJsonResult() { return jsonResult; }
        public void setJsonResult(String jsonResult) { this.jsonResult = jsonResult; }

        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }

        public String getErrorMsg() { return errorMsg; }
        public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }

        public Long getCostTime() { return costTime; }
        public void setCostTime(Long costTime) { this.costTime = costTime; }

        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public String getBrowser() { return browser; }
        public void setBrowser(String browser) { this.browser = browser; }

        public String getOs() { return os; }
        public void setOs(String os) { this.os = os; }
    }
}