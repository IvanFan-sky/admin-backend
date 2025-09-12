package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 导入导出任务更新DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出任务更新请求")
@Data
public class ImportExportTaskUpdateDTO {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "任务ID不能为空")
    private Long id;

    @Schema(description = "任务名称", example = "用户数据导入")
    @Size(max = 100, message = "任务名称长度不能超过100个字符")
    private String taskName;

    @Schema(description = "任务状态", example = "2",
            allowableValues = {"0", "1", "2", "3"})
    private Integer status;

    @Schema(description = "进度百分比", example = "75")
    private Integer progressPercent;

    @Schema(description = "成功数量", example = "150")
    private Integer successCount;

    @Schema(description = "失败数量", example = "5")
    private Integer failureCount;

    @Schema(description = "总数量", example = "155")
    private Integer totalCount;

    @Schema(description = "结果文件路径", example = "/export/users_20240115_result.xlsx")
    private String resultFilePath;

    @Schema(description = "错误信息", example = "第5行数据格式错误")
    @Size(max = 1000, message = "错误信息长度不能超过1000个字符")
    private String errorMessage;

    @Schema(description = "备注", example = "任务执行完成")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}