package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入导出模板VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出模板信息")
@Data
public class
ImportExportTemplateVO {

    @Schema(description = "模板ID", example = "1")
    private Long id;

    @Schema(description = "模板名称", example = "用户导入模板")
    private String templateName;

    @Schema(description = "数据类型", example = "user")
    private String dataType;

    @Schema(description = "数据类型描述", example = "用户数据")
    private String dataTypeDesc;

    @Schema(description = "文件格式", example = "xlsx")
    private String fileFormat;

    @Schema(description = "文件格式描述", example = "Excel 2007+")
    private String fileFormatDesc;

    @Schema(description = "模板文件路径", example = "/templates/user_import_template.xlsx")
    private String templatePath;

    @Schema(description = "模板配置", example = "{\"columns\":[{\"name\":\"username\",\"required\":true}]}")
    private String templateConfig;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    @Schema(description = "备注", example = "用户数据导入模板")
    private String remark;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 10:30:00")
    private LocalDateTime updateTime;
}