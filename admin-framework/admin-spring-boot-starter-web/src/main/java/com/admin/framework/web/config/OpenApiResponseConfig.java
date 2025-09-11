package com.admin.framework.web.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

/**
 * OpenAPI 全局响应配置
 * 
 * 为所有API接口添加通用的响应状态码和异常处理说明
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Configuration
public class OpenApiResponseConfig {

    /**
     * 全局操作定制器 - 添加通用响应状态码
     */
    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiResponses apiResponses = operation.getResponses();
            
            // 添加通用成功响应
            if (!apiResponses.containsKey("200")) {
                apiResponses.addApiResponse("200", createSuccessResponse());
            }
            
            // 添加通用错误响应
            addCommonErrorResponses(apiResponses);
            
            return operation;
        };
    }

    /**
     * 创建成功响应
     */
    private ApiResponse createSuccessResponse() {
        return new ApiResponse()
                .description("操作成功")
                .content(new Content()
                        .addMediaType("application/json", 
                                new MediaType()
                                        .schema(createResponseSchema("操作成功时返回的数据"))));
    }

    /**
     * 添加通用错误响应
     */
    private void addCommonErrorResponses(ApiResponses apiResponses) {
        // 400 请求参数错误
        if (!apiResponses.containsKey("400")) {
            apiResponses.addApiResponse("400", new ApiResponse()
                    .description("请求参数错误")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(createErrorSchema("请求参数验证失败")))));
        }

        // 401 未授权
        if (!apiResponses.containsKey("401")) {
            apiResponses.addApiResponse("401", new ApiResponse()
                    .description("未授权访问")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(createErrorSchema("Token无效或已过期")))));
        }

        // 403 权限不足
        if (!apiResponses.containsKey("403")) {
            apiResponses.addApiResponse("403", new ApiResponse()
                    .description("权限不足")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(createErrorSchema("当前用户权限不足")))));
        }

        // 404 资源不存在
        if (!apiResponses.containsKey("404")) {
            apiResponses.addApiResponse("404", new ApiResponse()
                    .description("资源不存在")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(createErrorSchema("请求的资源不存在")))));
        }

        // 500 服务器内部错误
        if (!apiResponses.containsKey("500")) {
            apiResponses.addApiResponse("500", new ApiResponse()
                    .description("服务器内部错误")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(createErrorSchema("服务器处理异常")))));
        }
    }

    /**
     * 创建通用响应Schema
     */
    private Schema<?> createResponseSchema(String description) {
        return new Schema<>()
                .type("object")
                .description("统一响应格式")
                .addProperty("code", new Schema<>()
                        .type("integer")
                        .description("响应状态码")
                        .example(200))
                .addProperty("message", new Schema<>()
                        .type("string")
                        .description("响应消息")
                        .example("操作成功"))
                .addProperty("data", new Schema<>()
                        .description(description))
                .addProperty("timestamp", new Schema<>()
                        .type("string")
                        .format("date-time")
                        .description("响应时间戳")
                        .example("2024-01-15T10:30:00Z"));
    }

    /**
     * 创建错误响应Schema
     */
    private Schema<?> createErrorSchema(String description) {
        return new Schema<>()
                .type("object")
                .description("错误响应格式")
                .addProperty("code", new Schema<>()
                        .type("integer")
                        .description("错误状态码")
                        .example(400))
                .addProperty("message", new Schema<>()
                        .type("string")
                        .description("错误消息")
                        .example(description))
                .addProperty("data", new Schema<>()
                        .nullable(true)
                        .description("错误详情数据"))
                .addProperty("timestamp", new Schema<>()
                        .type("string")
                        .format("date-time")
                        .description("错误发生时间")
                        .example("2024-01-15T10:30:00Z"))
                .addProperty("path", new Schema<>()
                        .type("string")
                        .description("请求路径")
                        .example("/admin-api/system/users"));
    }
}