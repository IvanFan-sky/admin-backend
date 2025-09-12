package com.admin.module.log.biz.config;

import com.admin.module.log.api.enums.LogStorageTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志配置属性
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Component
@ConfigurationProperties(prefix = "admin.log")
public class LogProperties {

    /**
     * 日志存储类型
     */
    private LogStorageTypeEnum storageType = LogStorageTypeEnum.DATABASE;

    /**
     * 是否启用异步记录
     */
    private boolean asyncEnabled = true;

    /**
     * 文件存储配置
     */
    private FileConfig file = new FileConfig();

    @Data
    public static class FileConfig {
        /**
         * 文件存储基础路径
         */
        private String basePath = "./logs";

        /**
         * 是否启用文件轮转
         */
        private boolean rotationEnabled = true;

        /**
         * 文件保留天数
         */
        private int retentionDays = 30;
    }
}