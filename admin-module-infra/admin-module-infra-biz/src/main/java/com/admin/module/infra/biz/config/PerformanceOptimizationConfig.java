package com.admin.module.infra.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 性能优化相关配置
 * 支持并行处理、流式处理和分段事务优化
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
public class PerformanceOptimizationConfig {

    /**
     * 并行批处理专用线程池
     */
    @Bean("parallelBatchProcessorExecutor")
    public Executor parallelBatchProcessorExecutor() {
        int parallelism = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
        
        return new ForkJoinPool(
            parallelism,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (thread, exception) -> {
                System.err.println("并行处理线程异常: " + exception.getMessage());
            },
            true // 启用异步模式
        );
    }

    /**
     * 流式处理专用线程池
     */
    @Bean("streamProcessorExecutor")
    public Executor streamProcessorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("Stream-Processor-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * 分段事务处理专用线程池
     */
    @Bean("segmentedTransactionExecutor")
    public Executor segmentedTransactionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Segmented-Tx-");
        executor.setKeepAliveSeconds(300);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * 性能监控线程池
     */
    @Bean("performanceMonitorExecutor")
    public Executor performanceMonitorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Perf-Monitor-");
        executor.setKeepAliveSeconds(600);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }
}