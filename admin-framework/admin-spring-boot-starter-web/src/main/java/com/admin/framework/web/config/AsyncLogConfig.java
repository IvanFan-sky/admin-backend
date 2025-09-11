package com.admin.framework.web.config;

import com.admin.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步日志配置类
 * 
 * 配置异步任务执行器，支持链路追踪信息的传递
 * 确保异步日志记录时能够保持完整的追踪链路
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncLogConfig implements AsyncConfigurer {

    /**
     * 异步日志任务执行器
     */
    @Bean("asyncLogExecutor")
    public Executor asyncLogExecutor() {
        ThreadPoolTaskExecutor executor = new TraceableThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncLog-");
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncLogExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncLogExceptionHandler();
    }

    /**
     * 支持链路追踪的线程池任务执行器
     */
    public static class TraceableThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
        
        @Override
        public void execute(Runnable task) {
            // 复制当前线程的MDC上下文
            Map<String, String> contextMap = TraceContext.copyContext();
            
            super.execute(() -> {
                try {
                    // 在新线程中设置MDC上下文
                    TraceContext.setContext(contextMap);
                    task.run();
                } finally {
                    // 清理MDC上下文
                    TraceContext.clear();
                }
            });
        }
        
        @Override
        public java.util.concurrent.Future<?> submit(Runnable task) {
            Map<String, String> contextMap = TraceContext.copyContext();
            
            return super.submit(() -> {
                try {
                    TraceContext.setContext(contextMap);
                    task.run();
                } finally {
                    TraceContext.clear();
                }
            });
        }
        
        @Override
        public <T> java.util.concurrent.Future<T> submit(java.util.concurrent.Callable<T> task) {
            Map<String, String> contextMap = TraceContext.copyContext();
            
            return super.submit(() -> {
                try {
                    TraceContext.setContext(contextMap);
                    return task.call();
                } finally {
                    TraceContext.clear();
                }
            });
        }
    }

    /**
     * 异步日志异常处理器
     */
    public static class AsyncLogExceptionHandler implements AsyncUncaughtExceptionHandler {
        
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("异步日志任务执行异常 - 方法: {}.{}, 参数: {}", 
                    method.getDeclaringClass().getSimpleName(), 
                    method.getName(), 
                    params, 
                    ex);
        }
    }
}
