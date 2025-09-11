package com.admin.framework.web.config;

import com.admin.framework.web.properties.OpenApiProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置类
 * 
 * 配置Swagger/OpenAPI文档的基础信息、安全认证、API分组等
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "admin.openapi.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {

    /**
     * 禁用默认分组，避免与自定义分组重复
     * 当定义了GroupedOpenApi时，SpringDoc会自动禁用默认的全局分组
     */

    private final OpenApiProperties openApiProperties;

    /**
     * 配置OpenAPI基础信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .components(buildComponents())
                .addSecurityItem(buildSecurityRequirement());
    }

    /**
     * 构建API基础信息
     */
    private Info buildApiInfo() {
        OpenApiProperties.ApiInfo apiInfo = openApiProperties.getInfo();
        return new Info()
                .title(apiInfo.getTitle())
                .description(apiInfo.getDescription())
                .version(apiInfo.getVersion())
                .contact(buildContact())
                .license(buildLicense());
    }

    /**
     * 构建联系信息
     */
    private Contact buildContact() {
        OpenApiProperties.ContactInfo contactInfo = openApiProperties.getInfo().getContact();
        return new Contact()
                .name(contactInfo.getName())
                .email(contactInfo.getEmail())
                .url(contactInfo.getUrl());
    }

    /**
     * 构建许可证信息
     */
    private License buildLicense() {
        OpenApiProperties.LicenseInfo licenseInfo = openApiProperties.getInfo().getLicense();
        return new License()
                .name(licenseInfo.getName())
                .url(licenseInfo.getUrl());
    }

    /**
     * 构建服务器信息
     */
    private List<Server> buildServers() {
        return openApiProperties.getServers().stream()
                .map(serverConfig -> new Server()
                        .url(serverConfig.getUrl())
                        .description(serverConfig.getDescription()))
                .toList();
    }

    /**
     * 构建组件信息（安全方案等）
     */
    private Components buildComponents() {
        OpenApiProperties.SecurityConfig securityConfig = openApiProperties.getSecurity();
        return new Components()
                .addSecuritySchemes(securityConfig.getSchemeName(), buildSecurityScheme());
    }

    /**
     * 构建安全认证方案
     */
    private SecurityScheme buildSecurityScheme() {
        OpenApiProperties.SecurityConfig securityConfig = openApiProperties.getSecurity();
        return new SecurityScheme()
                .type(SecurityScheme.Type.valueOf(securityConfig.getType().toUpperCase()))
                .scheme(securityConfig.getScheme())
                .bearerFormat(securityConfig.getBearerFormat())
                .description(securityConfig.getDescription());
    }

    /**
     * 构建安全要求
     */
    private SecurityRequirement buildSecurityRequirement() {
        OpenApiProperties.SecurityConfig securityConfig = openApiProperties.getSecurity();
        return new SecurityRequirement()
                .addList(securityConfig.getSchemeName());
    }

    /**
     * 系统管理API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("01-系统管理")
                .pathsToMatch("/system/cache/**")
                .build();
    }

    /**
     * 用户管理API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("02-用户管理")
                .pathsToMatch("/admin/system/user/**")
                .build();
    }

    /**
     * 角色权限API分组
     */
    @Bean
    public GroupedOpenApi roleApi() {
        return GroupedOpenApi.builder()
                .group("03-角色权限")
                .pathsToMatch("/system/role/**", "/system/menu/**")
                .pathsToExclude("/system/role-menu/**")
                .build();
    }

    /**
     * 用户角色关联API分组
     */
    @Bean
    public GroupedOpenApi userRoleApi() {
        return GroupedOpenApi.builder()
                .group("04-用户角色关联")
                .pathsToMatch("/system/user-role/**", "/system/role-menu/**")
                .build();
    }

    /**
     * 字典管理API分组
     */
    @Bean
    public GroupedOpenApi dictApi() {
        return GroupedOpenApi.builder()
                .group("05-字典管理")
                .pathsToMatch("/system/dict-type/**", "/system/dict-data/**")
                .build();
    }

    /**
     * 认证授权API分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("06-认证授权")
                .pathsToMatch("/auth/**")
                .build();
    }

    /**
     * 日志管理API分组
     */
    @Bean
    public GroupedOpenApi logApi() {
        return GroupedOpenApi.builder()
                .group("07-日志管理")
                .pathsToMatch("/system/login-log/**","/system/operation-log")
                .build();
    }

    /**
     * 基础设施API分组
     */
    @Bean
    public GroupedOpenApi infraApi() {
        return GroupedOpenApi.builder()
                .group("08-基础设施")
                .pathsToMatch("/admin-api/infra/**")
                .build();
    }

    /**
     * 监控运维API分组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                .group("09-监控运维")
                .pathsToMatch("/admin-api/monitor/**", "/actuator/**")
                .build();
    }
}