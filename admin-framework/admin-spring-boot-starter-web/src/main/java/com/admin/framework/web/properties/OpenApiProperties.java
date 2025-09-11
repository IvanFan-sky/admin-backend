package com.admin.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OpenAPI 配置属性
 * 
 * 用于从配置文件读取OpenAPI相关设置
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.openapi")
public class OpenApiProperties {

    /**
     * 是否启用OpenAPI文档
     */
    private Boolean enabled = true;

    /**
     * API信息配置
     */
    private ApiInfo info = new ApiInfo();

    /**
     * 服务器配置
     */
    private List<ServerConfig> servers = List.of(
            new ServerConfig("http://localhost:8080", "开发环境")
    );

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * API信息配置
     */
    @Data
    public static class ApiInfo {
        /**
         * API标题
         */
        private String title = "Admin Management System API";

        /**
         * API描述
         */
        private String description = "基于Spring Boot 3.x的现代化后台管理系统API文档";

        /**
         * API版本
         */
        private String version = "1.0.0";

        /**
         * 联系信息
         */
        private ContactInfo contact = new ContactInfo();

        /**
         * 许可证信息
         */
        private LicenseInfo license = new LicenseInfo();
    }

    /**
     * 联系信息
     */
    @Data
    public static class ContactInfo {
        /**
         * 联系人姓名
         */
        private String name = "Admin Team";

        /**
         * 联系邮箱
         */
        private String email = "admin@example.com";

        /**
         * 联系地址
         */
        private String url = "https://github.com/admin/admin-backend";
    }

    /**
     * 许可证信息
     */
    @Data
    public static class LicenseInfo {
        /**
         * 许可证名称
         */
        private String name = "MIT License";

        /**
         * 许可证地址
         */
        private String url = "https://opensource.org/licenses/MIT";
    }

    /**
     * 服务器配置
     */
    @Data
    public static class ServerConfig {
        /**
         * 服务器地址
         */
        private String url;

        /**
         * 服务器描述
         */
        private String description;

        public ServerConfig() {}

        public ServerConfig(String url, String description) {
            this.url = url;
            this.description = description;
        }
    }

    /**
     * 安全配置
     */
    @Data
    public static class SecurityConfig {
        /**
         * 安全方案名称
         */
        private String schemeName = "Bearer Authentication";

        /**
         * 认证类型
         */
        private String type = "http";

        /**
         * 认证方案
         */
        private String scheme = "bearer";

        /**
         * Bearer格式
         */
        private String bearerFormat = "JWT";

        /**
         * 描述信息
         */
        private String description = "请输入JWT Token，格式：Bearer {token}";
    }
}