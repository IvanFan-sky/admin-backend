package com.admin.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.admin.common.utils.IpUtils;

/**
 * Servlet工具类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class ServletUtils {

    /**
     * 获取当前请求
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            log.warn("获取当前请求失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前响应
     */
    public static HttpServletResponse getResponse() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getResponse() : null;
        } catch (Exception e) {
            log.warn("获取当前响应失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress() {
        String ip = IpUtils.getIpAddr();
        return ip != null ? ip : "Unknown";
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "Unknown";
        }
        
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown";
    }

    /**
     * 获取请求头值
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        
        return request.getHeader(name);
    }

    /**
     * 获取请求参数
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        
        return request.getParameter(name);
    }

    /**
     * 获取请求URI
     */
    public static String getRequestURI() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        
        return request.getRequestURI();
    }

    /**
     * 获取请求方法
     */
    public static String getMethod() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        
        return request.getMethod();
    }

    /**
     * 判断是否为Ajax请求
     */
    public static boolean isAjaxRequest() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return false;
        }
        
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}