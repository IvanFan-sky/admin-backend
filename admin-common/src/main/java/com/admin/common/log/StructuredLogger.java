package com.admin.common.log;

import com.admin.common.trace.TraceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 结构化日志工具类
 * 
 * 提供结构化的日志记录功能，支持业务日志、性能日志、安全日志等
 * 所有日志都包含链路追踪信息，便于问题定位和分析
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class StructuredLogger {

    private static final Logger BUSINESS_LOGGER = LoggerFactory.getLogger("BUSINESS");
    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger("PERFORMANCE");
    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger("SECURITY");
    private static final Logger API_LOGGER = LoggerFactory.getLogger("API");
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 记录业务日志
     *
     * @param action     业务动作
     * @param resource   操作资源
     * @param result     操作结果
     * @param details    详细信息
     */
    public static void logBusiness(String action, String resource, String result, Object details) {
        Map<String, Object> logData = createBaseLogData("BUSINESS");
        logData.put("action", action);
        logData.put("resource", resource);
        logData.put("result", result);
        logData.put("details", details);
        
        BUSINESS_LOGGER.info(toJsonString(logData));
    }

    /**
     * 记录业务日志（简化版）
     *
     * @param action   业务动作
     * @param resource 操作资源
     * @param result   操作结果
     */
    public static void logBusiness(String action, String resource, String result) {
        logBusiness(action, resource, result, null);
    }

    /**
     * 记录性能日志
     *
     * @param operation 操作名称
     * @param costTime  耗时（毫秒）
     * @param details   详细信息
     */
    public static void logPerformance(String operation, long costTime, Object details) {
        Map<String, Object> logData = createBaseLogData("PERFORMANCE");
        logData.put("operation", operation);
        logData.put("costTime", costTime);
        logData.put("details", details);
        
        // 根据耗时判断日志级别
        if (costTime > 5000) {
            PERFORMANCE_LOGGER.error(toJsonString(logData));
        } else if (costTime > 2000) {
            PERFORMANCE_LOGGER.warn(toJsonString(logData));
        } else {
            PERFORMANCE_LOGGER.info(toJsonString(logData));
        }
    }

    /**
     * 记录性能日志（简化版）
     *
     * @param operation 操作名称
     * @param costTime  耗时（毫秒）
     */
    public static void logPerformance(String operation, long costTime) {
        logPerformance(operation, costTime, null);
    }

    /**
     * 记录安全日志
     *
     * @param event       安全事件
     * @param level       安全级别（INFO/WARN/ERROR）
     * @param description 事件描述
     * @param details     详细信息
     */
    public static void logSecurity(String event, String level, String description, Object details) {
        Map<String, Object> logData = createBaseLogData("SECURITY");
        logData.put("event", event);
        logData.put("level", level);
        logData.put("description", description);
        logData.put("details", details);
        
        String logMessage = toJsonString(logData);
        
        switch (level.toUpperCase()) {
            case "ERROR":
                SECURITY_LOGGER.error(logMessage);
                break;
            case "WARN":
                SECURITY_LOGGER.warn(logMessage);
                break;
            default:
                SECURITY_LOGGER.info(logMessage);
                break;
        }
    }

    /**
     * 记录API调用日志
     *
     * @param method     HTTP方法
     * @param url        请求URL
     * @param statusCode 响应状态码
     * @param costTime   耗时（毫秒）
     * @param details    详细信息
     */
    public static void logApi(String method, String url, int statusCode, long costTime, Object details) {
        Map<String, Object> logData = createBaseLogData("API");
        logData.put("method", method);
        logData.put("url", url);
        logData.put("statusCode", statusCode);
        logData.put("costTime", costTime);
        logData.put("details", details);
        
        String logMessage = toJsonString(logData);
        
        if (statusCode >= 500) {
            API_LOGGER.error(logMessage);
        } else if (statusCode >= 400) {
            API_LOGGER.warn(logMessage);
        } else {
            API_LOGGER.info(logMessage);
        }
    }

    /**
     * 记录登录日志
     *
     * @param username    用户名
     * @param success     是否成功
     * @param ip          客户端IP
     * @param userAgent   用户代理
     * @param failReason  失败原因（成功时为null）
     */
    public static void logLogin(String username, boolean success, String ip, String userAgent, String failReason) {
        Map<String, Object> logData = createBaseLogData("LOGIN");
        logData.put("username", username);
        logData.put("success", success);
        logData.put("ip", ip);
        logData.put("userAgent", userAgent);
        if (!success && failReason != null) {
            logData.put("failReason", failReason);
        }
        
        String logMessage = toJsonString(logData);
        
        if (success) {
            SECURITY_LOGGER.info(logMessage);
        } else {
            SECURITY_LOGGER.warn(logMessage);
        }
    }

    /**
     * 记录数据库操作日志
     *
     * @param operation 操作类型（SELECT/INSERT/UPDATE/DELETE）
     * @param table     表名
     * @param costTime  耗时（毫秒）
     * @param rowCount  影响行数
     */
    public static void logDatabase(String operation, String table, long costTime, int rowCount) {
        Map<String, Object> logData = createBaseLogData("DATABASE");
        logData.put("operation", operation);
        logData.put("table", table);
        logData.put("costTime", costTime);
        logData.put("rowCount", rowCount);
        
        String logMessage = toJsonString(logData);
        
        // 慢查询记录为警告
        if (costTime > 1000) {
            PERFORMANCE_LOGGER.warn(logMessage);
        } else {
            PERFORMANCE_LOGGER.debug(logMessage);
        }
    }

    /**
     * 记录异常日志
     *
     * @param operation 操作名称
     * @param exception 异常信息
     * @param details   详细信息
     */
    public static void logException(String operation, Throwable exception, Object details) {
        Map<String, Object> logData = createBaseLogData("EXCEPTION");
        logData.put("operation", operation);
        logData.put("exceptionClass", exception.getClass().getSimpleName());
        logData.put("exceptionMessage", exception.getMessage());
        logData.put("details", details);
        
        log.error(toJsonString(logData), exception);
    }

    /**
     * 创建基础日志数据
     *
     * @param logType 日志类型
     * @return 基础日志数据Map
     */
    private static Map<String, Object> createBaseLogData(String logType) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(formatter));
        logData.put("logType", logType);
        logData.put("traceId", TraceContext.getTraceId());
        logData.put("spanId", TraceContext.getSpanId());
        logData.put("userId", TraceContext.getUserId());
        logData.put("username", TraceContext.getUsername());
        logData.put("thread", Thread.currentThread().getName());
        
        return logData;
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 要转换的对象
     * @return JSON字符串
     */
    private static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.warn("转换日志对象为JSON失败: {}", e.getMessage());
            return object.toString();
        }
    }

    /**
     * 执行带性能监控的操作
     *
     * @param operationName 操作名称
     * @param operation     要执行的操作
     * @param <T>           返回值类型
     * @return 操作结果
     */
    public static <T> T executeWithPerformanceLog(String operationName, java.util.function.Supplier<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            long costTime = System.currentTimeMillis() - startTime;
            logPerformance(operationName, costTime, "执行成功");
            return result;
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            logPerformance(operationName, costTime, "执行失败: " + e.getMessage());
            logException(operationName, e, null);
            throw e;
        }
    }

    /**
     * 执行带性能监控的操作（无返回值）
     *
     * @param operationName 操作名称
     * @param operation     要执行的操作
     */
    public static void executeWithPerformanceLog(String operationName, Runnable operation) {
        executeWithPerformanceLog(operationName, () -> {
            operation.run();
            return null;
        });
    }
}
