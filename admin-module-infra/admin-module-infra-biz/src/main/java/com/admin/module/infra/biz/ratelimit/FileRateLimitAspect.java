package com.admin.module.infra.biz.ratelimit;

import com.admin.common.core.util.SecurityUtils;
import com.admin.common.core.util.ServletUtils;
import com.admin.module.infra.api.enums.FileErrorCode;
import com.admin.module.infra.api.exception.FileBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件操作频率限制切面
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Aspect
@Component
@Order(50) // 在审计切面之前执行
@RequiredArgsConstructor
@Slf4j
public class FileRateLimitAspect {

    private final RateLimitService rateLimitService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Around("@annotation(rateLimiter)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, FileRateLimiter rateLimiter) throws Throwable {
        // 构建限制键
        String limitKey = buildLimitKey(rateLimiter, joinPoint);
        
        // 检查是否允许访问
        boolean allowed = rateLimitService.isAllowed(
            limitKey, 
            rateLimiter.timeWindow(), 
            rateLimiter.maxRequests()
        );
        
        if (!allowed) {
            // 获取剩余配额和重置时间信息
            int remainingQuota = rateLimitService.getRemainingQuota(
                limitKey, 
                rateLimiter.timeWindow(), 
                rateLimiter.maxRequests()
            );
            
            log.warn("文件操作频率限制触发，限制键: {}, 窗口: {}秒, 最大请求: {}, 剩余配额: {}", 
                    limitKey, rateLimiter.timeWindow(), rateLimiter.maxRequests(), remainingQuota);
            
            throw new FileBusinessException(
                FileErrorCode.RATE_LIMIT_EXCEEDED, 
                rateLimiter.message()
            );
        }
        
        // 记录限制状态（用于监控）
        RateLimitService.RateLimitStatus status = rateLimitService.getLimitStatus(
            limitKey, 
            rateLimiter.timeWindow()
        );
        
        log.debug("文件操作频率检查通过，限制键: {}, 当前计数: {}, 允许: {}", 
                limitKey, status.getCurrentCount(), status.isAllowed());
        
        return joinPoint.proceed();
    }

    /**
     * 构建限制键
     */
    private String buildLimitKey(FileRateLimiter rateLimiter, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder();
        
        // 方法签名
        keyBuilder.append(joinPoint.getSignature().getDeclaringTypeName())
                  .append(".")
                  .append(joinPoint.getSignature().getName());
        
        // 根据限制类型添加不同的键组件
        switch (rateLimiter.limitType()) {
            case USER:
                keyBuilder.append(":user:");
                try {
                    keyBuilder.append(SecurityUtils.getUserId());
                } catch (Exception e) {
                    keyBuilder.append("anonymous");
                }
                break;
                
            case IP:
                keyBuilder.append(":ip:");
                HttpServletRequest request = getCurrentRequest();
                if (request != null) {
                    keyBuilder.append(ServletUtils.getClientIP(request));
                } else {
                    keyBuilder.append("unknown");
                }
                break;
                
            case CUSTOM:
                keyBuilder.append(":custom:");
                if (StringUtils.hasText(rateLimiter.key())) {
                    String customKey = parseSpelExpression(rateLimiter.key(), joinPoint);
                    keyBuilder.append(customKey);
                } else {
                    keyBuilder.append("default");
                }
                break;
                
            case GLOBAL:
                keyBuilder.append(":global");
                break;
        }
        
        return keyBuilder.toString();
    }

    /**
     * 解析SpEL表达式
     */
    private String parseSpelExpression(String spelExpression, ProceedingJoinPoint joinPoint) {
        try {
            EvaluationContext context = new StandardEvaluationContext();
            
            // 设置方法参数
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                    .getParameterNames();
            
            for (int i = 0; i < paramNames.length && i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            
            // 设置用户信息
            try {
                context.setVariable("userId", SecurityUtils.getUserId());
                context.setVariable("username", SecurityUtils.getUsername());
            } catch (Exception e) {
                context.setVariable("userId", 0L);
                context.setVariable("username", "anonymous");
            }
            
            // 设置请求信息
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                context.setVariable("clientIp", ServletUtils.getClientIP(request));
                context.setVariable("userAgent", request.getHeader("User-Agent"));
            }
            
            Expression expression = expressionParser.parseExpression(spelExpression);
            Object result = expression.getValue(context);
            
            return result != null ? result.toString() : "null";
            
        } catch (Exception e) {
            log.warn("解析SpEL表达式失败，表达式: {}", spelExpression, e);
            return "parse_error";
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
            return null;
        }
    }
}