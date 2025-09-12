package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入错误详情VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入错误详情信息")
@Data
public class ImportErrorDetailVO {

    @Schema(description = "错误详情ID", example = "1")
    private Long id;

    @Schema(description = "任务ID", example = "1")
    private Long taskId;

    @Schema(description = "行号", example = "5")
    private Integer rowNumber;

    @Schema(description = "列名", example = "email")
    private String columnName;

    @Schema(description = "原始值", example = "invalid-email")
    private String originalValue;

    @Schema(description = "错误类型", example = "格式错误")
    private String errorType;

    @Schema(description = "错误信息", example = "邮箱格式不正确")
    private String errorMessage;

    @Schema(description = "创建时间", example = "2024-01-15 10:32:00")
    private LocalDateTime createTime;
}