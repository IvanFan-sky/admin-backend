package com.admin.framework.excel.config;

import com.admin.framework.excel.service.ImportExportFileService;
import com.admin.framework.excel.service.ImportExportTaskService;
import com.admin.framework.excel.service.ExcelImportService;
import com.admin.framework.excel.service.ExcelExportService;
import com.admin.framework.excel.service.impl.ImportExportFileServiceImpl;
import com.admin.framework.excel.service.impl.ImportExportTaskServiceImpl;
import com.admin.framework.excel.service.impl.ExcelImportServiceImpl;
import com.admin.framework.excel.service.impl.ExcelExportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.validation.Validator;
import java.util.concurrent.Executor;

/**
 * 导入导出功能自动配置类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@AutoConfiguration
@EnableAsync
@ComponentScan(basePackages = "com.admin.framework.excel")
@EnableConfigurationProperties(ImportExportProperties.class)
@RequiredArgsConstructor
public class ImportExportAutoConfiguration {

    private final ImportExportProperties properties;

    /**
     * Excel导入服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelImportService excelImportService(Validator validator) {
        return new ExcelImportServiceImpl(validator);
    }

    /**
     * Excel导出服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelExportService excelExportService() {
        return new ExcelExportServiceImpl();
    }

    /**
     * 导入导出文件服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ImportExportFileService importExportFileService() {
        return new ImportExportFileServiceImpl(null); // MinioService由依赖注入提供
    }

    /**
     * 导入导出任务服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ImportExportTaskService importExportTaskService() {
        return new ImportExportTaskServiceImpl(null, null, taskExecutor()); // 依赖由注入提供
    }

    /**
     * 导入导出任务执行器
     */
    @Bean("importExportTaskExecutor")
    @ConditionalOnMissingBean(name = "importExportTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getTask().getCorePoolSize());
        executor.setMaxPoolSize(properties.getTask().getMaxPoolSize());
        executor.setQueueCapacity(properties.getTask().getQueueCapacity());
        executor.setThreadNamePrefix("ImportExport-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}