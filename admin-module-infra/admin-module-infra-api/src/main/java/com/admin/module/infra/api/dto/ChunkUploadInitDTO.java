package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分片上传初始化请求DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "分片上传初始化请求")
public class ChunkUploadInitDTO {

    /**
     * 文件名
     */
    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "large-video.mp4")
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "104857600")
    @NotNull(message = "文件大小不能为空")
    @Min(value = 1, message = "文件大小必须大于0")
    private Long fileSize;

    /**
     * 文件MD5哈希值
     */
    @Schema(description = "文件MD5哈希值", requiredMode = Schema.RequiredMode.REQUIRED, example = "d41d8cd98f00b204e9800998ecf8427e")
    @NotBlank(message = "文件MD5不能为空")
    @Size(min = 32, max = 32, message = "MD5哈希值长度必须为32位")
    private String fileMd5;

    /**
     * 文件MIME类型
     */
    @Schema(description = "文件MIME类型", example = "video/mp4")
    @Size(max = 100, message = "MIME类型长度不能超过100个字符")
    private String contentType;

    /**
     * 分片大小（字节）
     */
    @Schema(description = "分片大小（字节）", example = "5242880")
    @Min(value = 1024 * 1024, message = "分片大小不能小于1MB")
    private Long chunkSize;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "USER_AVATAR")
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    private String businessType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "12345")
    @Size(max = 100, message = "业务ID长度不能超过100个字符")
    private String businessId;

    /**
     * 文件标签
     */
    @Schema(description = "文件标签", example = "重要,临时")
    @Size(max = 200, message = "文件标签长度不能超过200个字符")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "用户上传的大文件")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
