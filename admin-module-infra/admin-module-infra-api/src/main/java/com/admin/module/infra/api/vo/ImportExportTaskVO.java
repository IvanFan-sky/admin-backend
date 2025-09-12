package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入导出任务VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出任务信息")
@Data
public class ImportExportTaskVO {

    @Schema(description = "任务ID", example = "1")
    private Long id;

    @Schema(description = "任务名称", example = "用户数据导入")
    private String taskName;

    @Schema(description = "任务类型", example = "1")
    private Integer taskType;

    @Schema(description = "任务类型描述", example = "导入")
    private String taskTypeDesc;

    @Schema(description = "数据类型", example = "user")
    private String dataType;

    @Schema(description = "数据类型描述", example = "用户数据")
    private String dataTypeDesc;

    @Schema(description = "文件格式", example = "xlsx")
    private String fileFormat;

    @Schema(description = "文件格式描述", example = "Excel 2007+")
    private String fileFormatDesc;

    @Schema(description = "文件路径", example = "/upload/users_20240115.xlsx")
    private String filePath;

    @Schema(description = "任务状态", example = "2")
    private Integer status;

    @Schema(description = "任务状态描述", example = "已完成")
    private String statusDesc;

    @Schema(description = "进度百分比", example = "100")
    private Integer progressPercent;

    @Schema(description = "成功数量", example = "150")
    private Integer successCount;

    @Schema(description = "失败数量", example = "5")
    private Integer failureCount;

    @Schema(description = "总数量", example = "155")
    private Integer totalCount;

    @Schema(description = "导出配置", example = "{\"columns\":[\"username\",\"email\"]}")
    private String exportConfig;

    @Schema(description = "结果文件路径", example = "/export/users_20240115_result.xlsx")
    private String resultFilePath;

    @Schema(description = "错误信息", example = "第5行数据格式错误")
    private String errorMessage;

    @Schema(description = "开始时间", example = "2024-01-15 10:30:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15 10:35:00")
    private LocalDateTime endTime;

    @Schema(description = "备注", example = "批量导入用户数据")
    private String remark;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 10:35:00")
    private LocalDateTime updateTime;
}