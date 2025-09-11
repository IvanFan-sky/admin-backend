package com.admin.framework.web.config;

import com.admin.framework.web.properties.OpenApiProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Web框架自动配置类
 * 
 * 自动配置OpenAPI、全局异常处理、跨域等Web相关功能
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@EnableConfigurationProperties(OpenApiProperties.class)
@Import({
    OpenApiConfig.class,
    OpenApiResponseConfig.class
})
public class AdminWebAutoConfiguration {
    
    // 自动配置类，主要用于导入其他配置类
    // 未来可以在这里添加更多Web相关的自动配置
    
}