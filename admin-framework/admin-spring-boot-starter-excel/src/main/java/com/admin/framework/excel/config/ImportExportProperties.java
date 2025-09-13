package com.admin.framework.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 导入导出配置属性
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.import-export")
public class ImportExportProperties {

    /**
     * 是否启用导入导出功能
     */
    private boolean enabled = true;

    /**
     * 文件配置
     */
    private FileConfig file = new FileConfig();

    /**
     * 任务配置
     */
    private TaskConfig task = new TaskConfig();

    /**
     * 缓存配置
     */
    private CacheConfig cache = new CacheConfig();

    @Data
    public static class FileConfig {
        /**
         * 最大文件大小（字节）
         */
        private long maxFileSize = 10 * 1024 * 1024; // 10MB

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {"xlsx", "xls", "csv"};

        /**
         * 文件保留天数
         */
        private int retentionDays = 30;

        /**
         * 是否启用文件压缩
         */
        private boolean compressionEnabled = false;
    }

    @Data
    public static class TaskConfig {
        /**
         * 核心线程池大小
         */
        private int corePoolSize = 2;

        /**
         * 最大线程池大小
         */
        private int maxPoolSize = 5;

        /**
         * 队列容量
         */
        private int queueCapacity = 100;

        /**
         * 每个用户最大并发任务数
         */
        private int maxConcurrentTasksPerUser = 2;

        /**
         * 系统最大并发任务数
         */
        private int maxSystemConcurrentTasks = 10;

        /**
         * 批处理大小
         */
        private int batchSize = 1000;

        /**
         * 任务超时时间（分钟）
         */
        private int timeoutMinutes = 60;
    }

    @Data
    public static class CacheConfig {
        /**
         * 是否启用缓存
         */
        private boolean enabled = true;

        /**
         * 缓存过期时间（分钟）
         */
        private int expireMinutes = 30;

        /**
         * 缓存Key前缀
         */
        private String keyPrefix = "import_export:";
    }
}