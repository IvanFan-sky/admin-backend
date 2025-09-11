package com.admin.framework.web.filter;

import com.admin.common.trace.TraceContext;
import com.admin.framework.security.utils.SecurityAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 链路追踪过滤器
 * 
 * 为每个HTTP请求生成或传递TraceId，实现分布式链路追踪
 * 支持从请求头中获取已存在的TraceId，也可以生成新的TraceId
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter implements Filter {

    /**
     * TraceId请求头名称
     */
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    
    /**
     * SpanId请求头名称
     */
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TraceFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // 初始化链路追踪
            initTraceContext(httpRequest);
            
            // 设置响应头
            setResponseHeaders(httpResponse);
            
            // 继续执行请求
            chain.doFilter(request, response);
            
        } finally {
            // 清理MDC上下文
            TraceContext.clear();
        }
    }

    @Override
    public void destroy() {
        log.info("TraceFilter destroyed");
    }

    /**
     * 初始化链路追踪上下文
     *
     * @param request HTTP请求
     */
    private void initTraceContext(HttpServletRequest request) {
        // 尝试从请求头获取TraceId
        String traceId = request.getHeader(TRACE_ID_HEADER);
        String spanId = request.getHeader(SPAN_ID_HEADER);
        
        if (StringUtils.hasText(traceId)) {
            // 使用已存在的TraceId
            TraceContext.setTraceId(traceId);
            if (StringUtils.hasText(spanId)) {
                TraceContext.setSpanId(spanId);
            } else {
                TraceContext.setSpanId(TraceContext.generateSpanId());
            }
            log.debug("继续链路追踪: traceId={}, spanId={}", traceId, TraceContext.getSpanId());
        } else {
            // 生成新的TraceId
            traceId = TraceContext.initTrace();
            log.debug("开始新的链路追踪: traceId={}, spanId={}", traceId, TraceContext.getSpanId());
        }
        
        // 尝试设置用户信息到MDC
        try {
            Long userId = SecurityAuthUtils.getCurrentUserId();
            String username = SecurityAuthUtils.getCurrentUsername();
            
            if (userId != null) {
                TraceContext.setUserId(userId);
            }
            if (StringUtils.hasText(username)) {
                TraceContext.setUsername(username);
            }
        } catch (Exception e) {
            // 忽略获取用户信息失败的异常，可能是未登录的请求
            log.debug("获取用户信息失败: {}", e.getMessage());
        }
        
        // 记录请求信息
        logRequestInfo(request, traceId);
    }

    /**
     * 设置响应头
     *
     * @param response HTTP响应
     */
    private void setResponseHeaders(HttpServletResponse response) {
        String traceId = TraceContext.getTraceId();
        String spanId = TraceContext.getSpanId();
        
        if (StringUtils.hasText(traceId)) {
            response.setHeader(TRACE_ID_HEADER, traceId);
        }
        if (StringUtils.hasText(spanId)) {
            response.setHeader(SPAN_ID_HEADER, spanId);
        }
    }

    /**
     * 记录请求信息
     *
     * @param request HTTP请求
     * @param traceId 链路追踪ID
     */
    private void logRequestInfo(HttpServletRequest request, String traceId) {
        if (log.isDebugEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String userAgent = request.getHeader("User-Agent");
            String clientIp = getClientIpAddress(request);
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("HTTP请求开始 - ")
                     .append("Method: ").append(method)
                     .append(", URI: ").append(uri);
            
            if (StringUtils.hasText(queryString)) {
                logMessage.append("?").append(queryString);
            }
            
            logMessage.append(", IP: ").append(clientIp)
                     .append(", UserAgent: ").append(userAgent);
            
            log.debug(logMessage.toString());
        }
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
            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
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
}
