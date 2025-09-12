package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 导入导出任务创建DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出任务创建请求")
@Data
public class ImportExportTaskCreateDTO {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户数据导入")
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100个字符")
    private String taskName;

    @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", 
            allowableValues = {"1", "2"})
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "user",
            allowableValues = {"user", "role", "operation_log"})
    @NotBlank(message = "数据类型不能为空")
    private String dataType;

    @Schema(description = "文件格式", example = "xlsx", 
            allowableValues = {"xlsx", "xls", "csv"})
    private String fileFormat;

    @Schema(description = "文件路径（导入任务可选）", example = "/upload/users_20240115.xlsx")
    private String filePath;

    @Schema(description = "文件ID（导入任务可选，与filePath二选一）", example = "123")
    private Long fileId;

    @Schema(description = "导出配置（JSON格式，导出任务使用）", 
            example = "{\"columns\":[\"username\",\"email\"],\"dateRange\":{\"start\":\"2024-01-01\",\"end\":\"2024-01-31\"}}")
    private String exportConfig;

    @Schema(description = "备注", example = "批量导入用户数据")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}