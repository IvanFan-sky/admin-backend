package com.admin.module.infra.biz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 导入导出定时任务配置
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
@EnableScheduling
public class ImportExportScheduleConfig {

    // 这里可以添加自定义的定时任务执行器配置
    // 如果需要单独的线程池来执行定时任务
    
    /*
    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);
        scheduler.setThreadNamePrefix("import-export-schedule-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();
        return scheduler;
    }
    */
}