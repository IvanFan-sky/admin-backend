package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 导入导出模板创建DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "导入导出模板创建请求")
@Data
public class ImportExportTemplateCreateDTO {

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户导入模板")
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称长度不能超过100个字符")
    private String templateName;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "user",
            allowableValues = {"user", "role", "operation_log"})
    @NotBlank(message = "数据类型不能为空")
    private String dataType;

    @Schema(description = "文件格式", example = "xlsx", 
            allowableValues = {"xlsx", "xls", "csv"})
    private String fileFormat;

    @Schema(description = "模板文件路径", example = "/templates/user_import_template.xlsx")
    @Size(max = 500, message = "模板文件路径长度不能超过500个字符")
    private String templatePath;

    @Schema(description = "模板配置", example = "{\"columns\":[{\"name\":\"username\",\"required\":true,\"type\":\"string\"}]}")
    private String templateConfig;

    @Schema(description = "备注", example = "用户数据导入模板")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}