package com.admin.framework.web.examples;

import com.admin.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * OpenAPI 注解使用示例
 * 
 * 展示如何正确使用OpenAPI 3.0注解来描述API接口
 * 这个Controller仅用于展示，实际项目中可以删除
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/admin-api/examples/openapi")
@Tag(name = "OpenAPI示例", description = "展示OpenAPI注解的正确使用方式")
public class OpenApiExampleController {

    @GetMapping("/simple")
    @Operation(
            summary = "简单查询示例",
            description = "展示基础的GET请求API文档注解使用方式"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = R.class)
                    )
            )
    })
    public R<String> simpleGet() {
        return R.ok("这是一个简单的GET请求示例");
    }

    @GetMapping("/with-params")
    @Operation(
            summary = "带参数查询示例",
            description = "展示如何为查询参数添加详细的文档说明"
    )
    public R<ExampleResponse> getWithParams(
            @Parameter(
                    name = "name",
                    description = "用户名称，支持模糊查询",
                    example = "张三",
                    required = false
            )
            @RequestParam(required = false) String name,
            
            @Parameter(
                    name = "status",
                    description = "用户状态：1-启用，0-禁用",
                    example = "1",
                    required = false
            )
            @RequestParam(required = false) Integer status,
            
            @Parameter(
                    name = "pageNum",
                    description = "页码，从1开始",
                    example = "1",
                    required = false
            )
            @RequestParam(defaultValue = "1") Integer pageNum
    ) {
        ExampleResponse response = new ExampleResponse();
        response.setName(name);
        response.setStatus(status);
        response.setPageNum(pageNum);
        response.setMessage("参数查询示例");
        
        return R.ok(response);
    }

    @PostMapping("/create")
    @Operation(
            summary = "创建资源示例",
            description = "展示POST请求的API文档注解，包括请求体和响应的详细说明"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "创建成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = R.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = R.class)
                    )
            )
    })
    public R<Long> create(
            @Parameter(
                    description = "创建请求体",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleCreateRequest.class)
                    )
            )
            @Valid @RequestBody ExampleCreateRequest request
    ) {
        // 模拟创建逻辑
        Long id = System.currentTimeMillis();
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "更新资源示例",
            description = "展示PUT请求的API文档注解，包括路径参数的使用"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public R<Void> update(
            @Parameter(
                    name = "id",
                    description = "资源ID",
                    example = "1",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,
            
            @Parameter(
                    description = "更新请求体",
                    required = true
            )
            @Valid @RequestBody ExampleUpdateRequest request
    ) {
        // 模拟更新逻辑
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "删除资源示例",
            description = "展示DELETE请求的API文档注解"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public R<Void> delete(
            @Parameter(
                    name = "id",
                    description = "要删除的资源ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        // 模拟删除逻辑
        return R.ok();
    }

    /**
     * 示例响应对象
     */
    @Data
    @Schema(description = "示例响应对象")
    public static class ExampleResponse {
        @Schema(description = "名称", example = "张三")
        private String name;
        
        @Schema(description = "状态", example = "1")
        private Integer status;
        
        @Schema(description = "页码", example = "1")
        private Integer pageNum;
        
        @Schema(description = "消息", example = "操作成功")
        private String message;
    }

    /**
     * 示例创建请求对象
     */
    @Data
    @Schema(description = "创建请求对象")
    public static class ExampleCreateRequest {
        @Schema(description = "名称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "名称不能为空")
        private String name;
        
        @Schema(description = "年龄", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "年龄不能为空")
        private Integer age;
        
        @Schema(description = "邮箱", example = "zhangsan@example.com")
        private String email;
        
        @Schema(description = "状态：1-启用，0-禁用", example = "1", defaultValue = "1")
        private Integer status = 1;
    }

    /**
     * 示例更新请求对象
     */
    @Data
    @Schema(description = "更新请求对象")
    public static class ExampleUpdateRequest {
        @Schema(description = "名称", example = "李四")
        private String name;
        
        @Schema(description = "年龄", example = "30")
        private Integer age;
        
        @Schema(description = "邮箱", example = "lisi@example.com")
        private String email;
        
        @Schema(description = "状态：1-启用，0-禁用", example = "1")
        private Integer status;
    }
}