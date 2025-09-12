package com.admin.framework.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Excel配置属性
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.excel")
public class AdminExcelProperties {

    /**
     * 是否启用Excel功能
     */
    private Boolean enabled = true;

    /**
     * 导入配置
     */
    private Import importConfig = new Import();

    /**
     * 导出配置
     */
    private Export exportConfig = new Export();

    /**
     * 导入配置
     */
    @Data
    public static class Import {
        /**
         * 单次导入最大行数
         */
        private Integer maxRows = 10000;

        /**
         * 批处理大小
         */
        private Integer batchSize = 1000;

        /**
         * 异步处理阈值
         */
        private Integer asyncThreshold = 5000;

        /**
         * 是否跳过空行
         */
        private Boolean skipEmptyRows = true;

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {"xlsx", "xls", "csv"};

        /**
         * 最大文件大小（MB）
         */
        private Long maxFileSize = 10L;
    }

    /**
     * 导出配置
     */
    @Data
    public static class Export {
        /**
         * 单次导出最大行数
         */
        private Integer maxRows = 100000;

        /**
         * 内存阈值，超过此阈值使用磁盘缓存
         */
        private Integer memoryThreshold = 50000;

        /**
         * 默认工作表名称
         */
        private String defaultSheetName = "数据";

        /**
         * 是否自动调整列宽
         */
        private Boolean autoColumnWidth = true;

        /**
         * 临时文件保存路径
         */
        private String tempPath = System.getProperty("java.io.tmpdir") + "/excel/";

        /**
         * 临时文件保留时间（小时）
         */
        private Integer tempFileRetentionHours = 24;
    }
}
