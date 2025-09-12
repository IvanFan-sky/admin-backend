package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件分片上传请求 DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件分片上传请求")
@Data
public class FileChunkUploadDTO {

    @Schema(description = "上传会话ID", required = true)
    @NotBlank(message = "上传会话ID不能为空")
    private String uploadId;

    @Schema(description = "分片序号（从1开始）", required = true)
    @NotNull(message = "分片序号不能为空")
    @Min(value = 1, message = "分片序号必须大于0")
    private Integer chunkNumber;

    @Schema(description = "总分片数", required = true)
    @NotNull(message = "总分片数不能为空")
    @Min(value = 1, message = "总分片数必须大于0")
    private Integer totalChunks;

    @Schema(description = "文件总大小", required = true)
    @NotNull(message = "文件总大小不能为空")
    @Min(value = 1, message = "文件总大小必须大于0")
    private Long totalSize;

    @Schema(description = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "文件完整哈希值")
    private String fileHash;

    @Schema(description = "分片哈希值")
    private String chunkHash;

    @Schema(description = "分片文件", required = true)
    @NotNull(message = "分片文件不能为空")
    private MultipartFile chunkFile;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务关联ID")
    private String businessId;
}