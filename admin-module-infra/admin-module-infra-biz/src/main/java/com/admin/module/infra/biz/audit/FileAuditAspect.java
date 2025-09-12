package com.admin.module.infra.biz.audit;

import com.admin.common.core.util.SecurityUtils;
import com.admin.common.core.util.ServletUtils;
import com.admin.module.infra.biz.dal.dataobject.FileAuditLogDO;
import com.admin.module.infra.biz.service.FileAuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件访问审计切面
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(100)
@RequiredArgsConstructor
@Slf4j
public class FileAuditAspect {

    private final FileAuditService fileAuditService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(fileAudit)")
    public Object auditFileOperation(ProceedingJoinPoint joinPoint, FileAudit fileAudit) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        // 获取请求信息
        HttpServletRequest request = getCurrentRequest();
        FileAuditLogDO auditLog = buildBaseAuditLog(fileAudit, request, requestId, startTime);
        
        // 记录请求参数
        if (fileAudit.logParams()) {
            auditLog.setOperationDetails(extractOperationDetails(joinPoint, fileAudit));
        }
        
        Object result = null;
        Throwable exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            auditLog.setResult("SUCCESS");
            
            // 提取文件ID（如果返回结果中包含）
            extractFileIdFromResult(result, auditLog);
            
        } catch (Throwable e) {
            exception = e;
            auditLog.setResult("FAILED");
            auditLog.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long duration = System.currentTimeMillis() - startTime;
            auditLog.setDuration(duration);
            auditLog.setOperationTime(LocalDateTime.now());
            
            // 异步或同步记录审计日志
            if (fileAudit.async()) {
                fileAuditService.recordAuditLogAsync(auditLog);
            } else {
                fileAuditService.recordAuditLog(auditLog);
            }
            
            log.debug("文件操作审计完成，操作: {}, 结果: {}, 耗时: {}ms, 请求ID: {}", 
                    fileAudit.operation().name(), auditLog.getResult(), duration, requestId);
        }
        
        return result;
    }

    /**
     * 构建基础审计日志
     */
    private FileAuditLogDO buildBaseAuditLog(FileAudit fileAudit, HttpServletRequest request, 
                                           String requestId, long startTime) {
        FileAuditLogDO auditLog = new FileAuditLogDO();
        
        // 基础信息
        auditLog.setRequestId(requestId);
        auditLog.setOperation(fileAudit.operation().name());
        
        // 用户信息
        try {
            auditLog.setUserId(SecurityUtils.getUserId());
            auditLog.setUsername(SecurityUtils.getUsername());
        } catch (Exception e) {
            auditLog.setUserId(0L);
            auditLog.setUsername("anonymous");
        }
        
        // 请求信息
        if (request != null) {
            auditLog.setClientIp(ServletUtils.getClientIP(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }
        
        auditLog.setCreateTime(LocalDateTime.now());
        
        return auditLog;
    }

    /**
     * 提取操作详情
     */
    private String extractOperationDetails(ProceedingJoinPoint joinPoint, FileAudit fileAudit) {
        try {
            Map<String, Object> details = new HashMap<>();
            
            // 方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            details.put("method", method.getName());
            details.put("class", method.getDeclaringClass().getSimpleName());
            details.put("operation", fileAudit.operation().getDescription());
            details.put("description", fileAudit.description());
            
            // 参数信息
            if (fileAudit.logParams()) {
                String[] paramNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                
                Map<String, Object> params = new HashMap<>();
                for (int i = 0; i < paramNames.length && i < args.length; i++) {
                    Object arg = args[i];
                    // 过滤敏感参数（如文件流）
                    if (arg instanceof java.io.InputStream || 
                        arg instanceof javax.servlet.http.HttpServletRequest ||
                        arg instanceof javax.servlet.http.HttpServletResponse) {
                        params.put(paramNames[i], "[FILTERED]");
                    } else if (arg != null) {
                        params.put(paramNames[i], arg.toString());
                    }
                }
                details.put("parameters", params);
            }
            
            return objectMapper.writeValueAsString(details);
            
        } catch (JsonProcessingException e) {
            log.warn("序列化操作详情失败", e);
            return "{}";
        }
    }

    /**
     * 从结果中提取文件ID
     */
    private void extractFileIdFromResult(Object result, FileAuditLogDO auditLog) {
        if (result == null) return;
        
        try {
            // 如果返回结果中包含fileId字段
            if (result instanceof com.admin.module.infra.api.vo.FileUploadVO) {
                com.admin.module.infra.api.vo.FileUploadVO uploadVO = 
                    (com.admin.module.infra.api.vo.FileUploadVO) result;
                auditLog.setFileId(uploadVO.getFileId());
                auditLog.setFileSize(uploadVO.getFileSize());
                auditLog.setBusinessType(uploadVO.getBusinessType());
            } else if (result instanceof com.admin.module.infra.api.vo.FileInfoVO) {
                com.admin.module.infra.api.vo.FileInfoVO fileVO = 
                    (com.admin.module.infra.api.vo.FileInfoVO) result;
                auditLog.setFileId(fileVO.getId());
                auditLog.setFileSize(fileVO.getFileSize());
                auditLog.setBusinessType(fileVO.getBusinessType());
            }
        } catch (Exception e) {
            log.debug("提取文件ID失败", e);
        }
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            log.debug("获取当前请求失败", e);
            return null;
        }
    }
}