package com.admin.module.infra.api.constants;

/**
 * 导入导出常量类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class ImportExportConstants {

    /**
     * 缓存键前缀
     */
    public static class CacheKey {
        public static final String IMPORT_TASK_PREFIX = "import_task:";
        public static final String EXPORT_TASK_PREFIX = "export_task:";
        public static final String TEMPLATE_PREFIX = "template:";
        public static final String USER_TASK_LIMIT_PREFIX = "user_task_limit:";
    }

    /**
     * 任务执行状态
     */
    public static class TaskStatus {
        public static final Integer PENDING = 0;
        public static final Integer PROCESSING = 1;
        public static final Integer COMPLETED = 2;
        public static final Integer FAILED = 3;
    }

    /**
     * 任务类型
     */
    public static class TaskType {
        public static final Integer IMPORT = 1;
        public static final Integer EXPORT = 2;
    }

    /**
     * 数据类型
     */
    public static class DataType {
        public static final String USER = "user";
        public static final String ROLE = "role";
        public static final String OPERATION_LOG = "operation_log";
    }

    /**
     * 文件格式
     */
    public static class FileFormat {
        public static final String XLSX = "xlsx";
        public static final String XLS = "xls";
        public static final String CSV = "csv";
    }

    /**
     * 错误类型
     */
    public static class ErrorType {
        public static final String FORMAT_ERROR = "格式错误";
        public static final String VALIDATION_ERROR = "校验错误";
        public static final String DUPLICATE_ERROR = "重复数据";
        public static final String CONSTRAINT_ERROR = "约束违反";
        public static final String BUSINESS_ERROR = "业务错误";
    }

    /**
     * 文件大小限制
     */
    public static class FileSize {
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
        public static final long MAX_TEMPLATE_SIZE = 5 * 1024 * 1024L; // 5MB
    }

    /**
     * 行数限制
     */
    public static class RowLimit {
        public static final int MAX_IMPORT_ROWS = 10000;
        public static final int MAX_EXPORT_ROWS = 50000;
        public static final int BATCH_PROCESS_SIZE = 1000;
    }

    /**
     * 默认配置
     */
    public static class DefaultConfig {
        public static final int TASK_TIMEOUT_MINUTES = 30;
        public static final int ERROR_DETAIL_RETENTION_DAYS = 7;
        public static final int EXPORT_FILE_RETENTION_DAYS = 3;
        public static final int MAX_CONCURRENT_TASKS = 5;
    }

    /**
     * Excel相关常量
     */
    public static class Excel {
        public static final String SHEET_NAME_USERS = "用户数据";
        public static final String SHEET_NAME_ROLES = "角色数据";
        public static final String SHEET_NAME_OPERATION_LOGS = "操作日志";
        public static final String SHEET_NAME_ERRORS = "错误详情";
    }

    /**
     * 权限标识
     */
    public static class Permission {
        public static final String IMPORT_EXPORT_QUERY = "infra:import-export:query";
        public static final String IMPORT_EXPORT_CREATE = "infra:import-export:create";
        public static final String IMPORT_EXPORT_UPDATE = "infra:import-export:update";
        public static final String IMPORT_EXPORT_DELETE = "infra:import-export:delete";
        public static final String IMPORT_EXPORT_EXECUTE = "infra:import-export:execute";
        public static final String IMPORT_EXPORT_UPLOAD = "infra:import-export:upload";
        public static final String IMPORT_EXPORT_EXPORT = "infra:import-export:export";
        public static final String IMPORT_EXPORT_DOWNLOAD = "infra:import-export:download";
        public static final String IMPORT_EXPORT_VALIDATE = "infra:import-export:validate";
    }
}