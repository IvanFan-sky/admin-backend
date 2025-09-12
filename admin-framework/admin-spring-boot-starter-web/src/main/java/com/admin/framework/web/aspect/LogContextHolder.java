package com.admin.framework.web.aspect;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志上下文管理器
 * 
 * 使用ThreadLocal管理日志处理过程中的上下文信息
 * 避免在方法间传递参数，提供线程安全的上下文存储
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class LogContextHolder {

    private static final ThreadLocal<LogContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 日志上下文
     */
    public static class LogContext {
        private Long startTime;
        private String traceId;
        private String operatorName;
        private Long operatorId;

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public Long getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(Long operatorId) {
            this.operatorId = operatorId;
        }
    }

    /**
     * 获取当前线程的日志上下文
     */
    public static LogContext getCurrentContext() {
        LogContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = new LogContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    /**
     * 设置开始时间
     */
    public static void setStartTime(Long startTime) {
        getCurrentContext().setStartTime(startTime);
    }

    /**
     * 获取开始时间
     */
    public static Long getStartTime() {
        LogContext context = CONTEXT_HOLDER.get();
        return context != null ? context.getStartTime() : null;
    }

    /**
     * 设置链路追踪ID
     */
    public static void setTraceId(String traceId) {
        getCurrentContext().setTraceId(traceId);
    }

    /**
     * 获取链路追踪ID
     */
    public static String getTraceId() {
        LogContext context = CONTEXT_HOLDER.get();
        return context != null ? context.getTraceId() : null;
    }

    /**
     * 设置操作人员信息
     */
    public static void setOperator(String operatorName, Long operatorId) {
        LogContext context = getCurrentContext();
        context.setOperatorName(operatorName);
        context.setOperatorId(operatorId);
    }

    /**
     * 获取操作人员名称
     */
    public static String getOperatorName() {
        LogContext context = CONTEXT_HOLDER.get();
        return context != null ? context.getOperatorName() : null;
    }

    /**
     * 获取操作人员ID
     */
    public static Long getOperatorId() {
        LogContext context = CONTEXT_HOLDER.get();
        return context != null ? context.getOperatorId() : null;
    }

    /**
     * 清理当前线程的上下文
     */
    public static void clear() {
        try {
            CONTEXT_HOLDER.remove();
        } catch (Exception e) {
            log.warn("清理日志上下文异常: {}", e.getMessage());
        }
    }

    /**
     * 判断当前线程是否有上下文
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }
}