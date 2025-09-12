package com.admin.framework.excel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Excel自动配置类
 * 
 * 只提供配置属性的自动装配，具体的服务实现由业务模块提供
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AdminExcelProperties.class)
@ConditionalOnProperty(prefix = "admin.excel", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AdminExcelAutoConfiguration {

    public AdminExcelAutoConfiguration(AdminExcelProperties properties) {
        log.info("Admin Excel组件已启用，配置: maxRows={}, batchSize={}", 
                properties.getImportConfig().getMaxRows(),
                properties.getImportConfig().getBatchSize());
    }
}
