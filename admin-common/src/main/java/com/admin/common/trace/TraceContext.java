package com.admin.common.trace;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * 链路追踪上下文
 * 
 * 提供TraceId的生成、传递和清理功能
 * 支持跨线程传递和异步任务追踪
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class TraceContext {

    /**
     * TraceId的MDC键名
     */
    public static final String TRACE_ID_KEY = "traceId";
    
    /**
     * SpanId的MDC键名
     */
    public static final String SPAN_ID_KEY = "spanId";
    
    /**
     * 用户ID的MDC键名
     */
    public static final String USER_ID_KEY = "userId";
    
    /**
     * 用户名的MDC键名
     */
    public static final String USERNAME_KEY = "username";

    /**
     * 生成新的TraceId
     *
     * @return TraceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成新的SpanId
     *
     * @return SpanId
     */
    public static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 设置TraceId
     *
     * @param traceId TraceId
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取当前TraceId
     *
     * @return TraceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 设置SpanId
     *
     * @param spanId SpanId
     */
    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID_KEY, spanId);
    }

    /**
     * 获取当前SpanId
     *
     * @return SpanId
     */
    public static String getSpanId() {
        return MDC.get(SPAN_ID_KEY);
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, String.valueOf(userId));
        }
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public static void setUsername(String username) {
        if (username != null) {
            MDC.put(USERNAME_KEY, username);
        }
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        return MDC.get(USERNAME_KEY);
    }

    /**
     * 初始化新的链路追踪
     *
     * @return TraceId
     */
    public static String initTrace() {
        String traceId = generateTraceId();
        setTraceId(traceId);
        setSpanId(generateSpanId());
        return traceId;
    }

    /**
     * 初始化新的链路追踪（带用户信息）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return TraceId
     */
    public static String initTrace(Long userId, String username) {
        String traceId = initTrace();
        setUserId(userId);
        setUsername(username);
        return traceId;
    }

    /**
     * 设置已存在的TraceId（用于跨服务调用）
     *
     * @param traceId 已存在的TraceId
     */
    public static void setExistingTraceId(String traceId) {
        setTraceId(traceId);
        setSpanId(generateSpanId());
    }

    /**
     * 清理当前线程的追踪信息
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 清理指定的MDC键
     *
     * @param key MDC键
     */
    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * 复制当前线程的MDC到新线程
     * 用于异步任务的链路追踪传递
     *
     * @return MDC副本
     */
    public static java.util.Map<String, String> copyContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 设置MDC上下文
     * 用于异步任务中恢复链路追踪信息
     *
     * @param contextMap MDC上下文映射
     */
    public static void setContext(java.util.Map<String, String> contextMap) {
        if (contextMap != null && !contextMap.isEmpty()) {
            MDC.setContextMap(contextMap);
        }
    }

    /**
     * 执行带链路追踪的任务
     *
     * @param task 要执行的任务
     */
    public static void runWithTrace(Runnable task) {
        String traceId = getTraceId();
        if (traceId == null) {
            initTrace();
        }
        
        try {
            task.run();
        } finally {
            // 不清理MDC，保持链路追踪信息
        }
    }

    /**
     * 执行带链路追踪的任务（带返回值）
     *
     * @param task 要执行的任务
     * @param <T>  返回值类型
     * @return 任务执行结果
     */
    public static <T> T callWithTrace(java.util.concurrent.Callable<T> task) throws Exception {
        String traceId = getTraceId();
        if (traceId == null) {
            initTrace();
        }
        
        try {
            return task.call();
        } finally {
            // 不清理MDC，保持链路追踪信息
        }
    }
}
