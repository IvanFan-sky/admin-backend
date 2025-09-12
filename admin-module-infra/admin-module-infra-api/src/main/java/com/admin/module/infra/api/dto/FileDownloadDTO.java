package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 文件下载请求 DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件下载请求")
@Data
public class FileDownloadDTO {

    @Schema(description = "文件ID", required = true)
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "是否内联显示", example = "false")
    private Boolean inline = false;

    @Schema(description = "下载文件名")
    private String downloadName;
}