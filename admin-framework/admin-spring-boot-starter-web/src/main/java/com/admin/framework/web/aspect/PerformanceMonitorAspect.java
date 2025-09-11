package com.admin.framework.web.aspect;

import com.admin.common.log.StructuredLogger;
import com.admin.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 性能监控切面
 * 
 * 监控接口执行时间，记录慢接口日志
 * 提供性能分析数据支持
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(0) // 最高优先级，确保能准确测量时间
@Slf4j
public class PerformanceMonitorAspect {

    /**
     * 慢接口阈值（毫秒），默认2秒
     */
    @Value("${admin.logging.performance.warn-threshold:2000}")
    private long slowApiThreshold;

    /**
     * 超慢接口阈值（毫秒），默认5秒
     */
    @Value("${admin.logging.performance.error-threshold:5000}")
    private long verySlowApiThreshold;

    /**
     * 是否启用性能监控
     */
    @Value("${admin.logging.performance.enabled:true}")
    private boolean performanceMonitorEnabled;

    /**
     * 监控所有Controller方法的执行时间
     */
    @Around("execution(* com.admin.module.*.biz.controller..*.*(..))")
    public Object monitorApiPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!performanceMonitorEnabled) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String methodName = getMethodName(joinPoint);
        String traceId = TraceContext.getTraceId();
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录性能日志
            recordPerformanceLog(joinPoint, methodName, costTime, null, traceId);
            
            return result;
            
        } catch (Throwable throwable) {
            // 计算执行时间（包含异常处理时间）
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录性能日志（包含异常信息）
            recordPerformanceLog(joinPoint, methodName, costTime, throwable, traceId);
            
            throw throwable;
        }
    }

    /**
     * 监控Service方法的执行时间
     */
    @Around("execution(* com.admin.module.*.biz.service..*.*(..))")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!performanceMonitorEnabled) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String methodName = getMethodName(joinPoint);
        
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            
            // 只记录超过阈值的Service方法
            if (costTime > slowApiThreshold) {
                StructuredLogger.logPerformance(
                    "Service方法: " + methodName,
                    costTime,
                    "执行完成"
                );
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long costTime = System.currentTimeMillis() - startTime;
            
            StructuredLogger.logPerformance(
                "Service方法: " + methodName,
                costTime,
                "执行异常: " + throwable.getMessage()
            );
            
            throw throwable;
        }
    }

    /**
     * 记录性能日志
     *
     * @param joinPoint 切点
     * @param methodName 方法名
     * @param costTime 执行时间
     * @param throwable 异常（如果有）
     * @param traceId 链路追踪ID
     */
    private void recordPerformanceLog(ProceedingJoinPoint joinPoint, String methodName, 
                                    long costTime, Throwable throwable, String traceId) {
        try {
            // 获取请求信息
            HttpServletRequest request = getCurrentRequest();
            String requestInfo = buildRequestInfo(request);
            
            // 构建详细信息
            String details = String.format(
                "方法: %s, 请求: %s, 链路ID: %s%s",
                methodName,
                requestInfo,
                traceId,
                throwable != null ? ", 异常: " + throwable.getMessage() : ""
            );
            
            // 根据执行时间记录不同级别的日志
            if (throwable != null) {
                // 有异常的情况
                StructuredLogger.logPerformance(methodName, costTime, details);
                log.error("接口执行异常 - {} 耗时: {}ms, 详情: {}", methodName, costTime, details);
                
            } else if (costTime > verySlowApiThreshold) {
                // 超慢接口
                StructuredLogger.logPerformance(methodName, costTime, details);
                log.error("超慢接口告警 - {} 耗时: {}ms, 详情: {}", methodName, costTime, details);
                
            } else if (costTime > slowApiThreshold) {
                // 慢接口
                StructuredLogger.logPerformance(methodName, costTime, details);
                log.warn("慢接口告警 - {} 耗时: {}ms, 详情: {}", methodName, costTime, details);
                
            } else if (log.isDebugEnabled()) {
                // 正常接口（仅在DEBUG级别记录）
                log.debug("接口执行完成 - {} 耗时: {}ms", methodName, costTime);
            }
            
        } catch (Exception e) {
            log.error("记录性能日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取方法名称
     *
     * @param joinPoint 切点
     * @return 方法全名
     */
    private String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className + "." + method.getName();
    }

    /**
     * 获取当前HTTP请求
     *
     * @return HTTP请求对象
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 构建请求信息字符串
     *
     * @param request HTTP请求
     * @return 请求信息
     */
    private String buildRequestInfo(HttpServletRequest request) {
        if (request == null) {
            return "非HTTP请求";
        }
        
        return String.format("%s %s", 
            request.getMethod(), 
            request.getRequestURI());
    }
}
