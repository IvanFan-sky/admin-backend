package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导出配置DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导出配置")
@Data
public class ExportConfigDTO {

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "user",
            allowableValues = {"user", "role", "operation_log"})
    @NotBlank(message = "数据类型不能为空")
    private String dataType;

    @Schema(description = "文件格式", example = "xlsx", 
            allowableValues = {"xlsx", "xls", "csv"})
    private String fileFormat;

    @Schema(description = "导出列（为空则导出所有列）", example = "[\"username\", \"nickname\", \"email\"]")
    private List<String> columns;

    @Schema(description = "查询条件")
    private QueryCondition queryCondition;

    @Schema(description = "查询条件")
    @Data
    public static class QueryCondition {

        @Schema(description = "关键字查询", example = "admin")
        private String keyword;

        @Schema(description = "状态筛选", example = "1")
        private Integer status;

        @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime beginTime;

        @Schema(description = "结束时间", example = "2024-01-31 23:59:59")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        @Schema(description = "创建者", example = "admin")
        private String createBy;

        @Schema(description = "用户ID列表（精确导出指定用户）")
        private List<Long> userIds;

        @Schema(description = "角色ID列表（精确导出指定角色）")
        private List<Long> roleIds;
    }
}