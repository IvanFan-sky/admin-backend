package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件分片上传结果 VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件分片上传结果")
@Data
public class FileChunkUploadVO {

    @Schema(description = "上传会话ID")
    private String uploadId;

    @Schema(description = "分片序号")
    private Integer chunkNumber;

    @Schema(description = "分片ETag")
    private String etag;

    @Schema(description = "是否为最后一个分片")
    private Boolean isLastChunk = false;

    @Schema(description = "分片上传状态：1-上传中，2-上传完成，3-上传失败")
    private Integer uploadStatus;

    @Schema(description = "文件信息（仅最后一片时返回）")
    private FileInfoVO fileInfo;
}