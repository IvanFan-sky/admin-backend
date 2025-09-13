package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 分片上传请求DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "分片上传请求")
public class ChunkUploadDTO {

    /**
     * 上传会话ID
     */
    @Schema(description = "上传会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "upload_123456789")
    @NotBlank(message = "上传会话ID不能为空")
    @Size(max = 255, message = "上传会话ID长度不能超过255个字符")
    private String uploadId;

    /**
     * 分片序号（从1开始）
     */
    @Schema(description = "分片序号（从1开始）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分片序号不能为空")
    @Min(value = 1, message = "分片序号必须从1开始")
    private Integer chunkNumber;

    /**
     * 总分片数
     */
    @Schema(description = "总分片数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "总分片数不能为空")
    @Min(value = 1, message = "总分片数必须大于0")
    private Integer totalChunks;

    /**
     * 分片文件
     */
    @Schema(description = "分片文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分片文件不能为空")
    private MultipartFile chunkFile;

    /**
     * 分片MD5哈希值
     */
    @Schema(description = "分片MD5哈希值", example = "e99a18c428cb38d5f260853678922e03")
    @Size(min = 32, max = 32, message = "MD5哈希值长度必须为32位")
    private String chunkMd5;
}
