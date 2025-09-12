package com.admin.module.infra.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 导入导出配置类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
@ConfigurationProperties(prefix = "admin.import-export")
@Data
public class ImportExportConfig {

    /**
     * 文件上传临时目录
     */
    private String tempPath = "/tmp/import-export/temp";

    /**
     * 导出文件存储目录
     */
    private String exportPath = "/tmp/import-export/export";

    /**
     * 模板文件存储目录
     */
    private String templatePath = "/tmp/import-export/template";

    /**
     * 单个文件最大大小（字节）
     */
    private Long maxFileSize = 10 * 1024 * 1024L; // 10MB

    /**
     * 单次导入最大行数
     */
    private Integer maxImportRows = 10000;

    /**
     * 单次导出最大行数
     */
    private Integer maxExportRows = 50000;

    /**
     * 并发导入任务数限制
     */
    private Integer maxConcurrentImportTasks = 3;

    /**
     * 并发导出任务数限制
     */
    private Integer maxConcurrentExportTasks = 5;

    /**
     * 任务执行超时时间（分钟）
     */
    private Integer taskTimeoutMinutes = 30;

    /**
     * 错误详情保留天数
     */
    private Integer errorDetailRetentionDays = 7;

    /**
     * 导出文件保留天数
     */
    private Integer exportFileRetentionDays = 3;

    /**
     * 是否启用异步任务处理
     */
    private Boolean enableAsyncProcessing = true;

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private Integer corePoolSize = 3;

        /**
         * 最大线程数
         */
        private Integer maximumPoolSize = 8;

        /**
         * 线程空闲时间（秒）
         */
        private Integer keepAliveTime = 60;

        /**
         * 队列容量
         */
        private Integer queueCapacity = 100;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "import-export-";
    }
}