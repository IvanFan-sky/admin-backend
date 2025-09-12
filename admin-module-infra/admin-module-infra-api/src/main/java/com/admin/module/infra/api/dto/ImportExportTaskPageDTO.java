package com.admin.module.infra.api.dto;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 导入导出任务分页查询DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出任务分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportExportTaskPageDTO extends PageQuery {

    @Schema(description = "任务名称（模糊查询）", example = "用户数据")
    private String taskName;

    @Schema(description = "任务类型", example = "1", 
            allowableValues = {"1", "2"})
    private Integer taskType;

    @Schema(description = "数据类型", example = "user",
            allowableValues = {"user", "role", "operation_log"})
    private String dataType;

    @Schema(description = "任务状态", example = "2",
            allowableValues = {"0", "1", "2", "3"})
    private Integer status;

    @Schema(description = "创建者（模糊查询）", example = "admin")
    private String createBy;

    @Schema(description = "创建开始时间", example = "2024-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginCreateTime;

    @Schema(description = "创建结束时间", example = "2024-01-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endCreateTime;
}