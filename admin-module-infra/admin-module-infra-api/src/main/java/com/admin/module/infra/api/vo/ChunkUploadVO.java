package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分片上传响应VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分片上传响应")
public class ChunkUploadVO {

    /**
     * 上传会话ID
     */
    @Schema(description = "上传会话ID", example = "upload_123456789")
    private String uploadId;

    /**
     * 分片序号
     */
    @Schema(description = "分片序号", example = "1")
    private Integer chunkNumber;

    /**
     * 分片ETag（MinIO返回的标识）
     */
    @Schema(description = "分片ETag", example = "\"d41d8cd98f00b204e9800998ecf8427e\"")
    private String etag;

    /**
     * 是否上传完成（所有分片都已上传）
     */
    @Schema(description = "是否上传完成", example = "false")
    private Boolean completed;

    /**
     * 上传进度百分比
     */
    @Schema(description = "上传进度百分比", example = "30.5")
    private Double progress;

    /**
     * 已上传分片数
     */
    @Schema(description = "已上传分片数", example = "3")
    private Integer uploadedCount;

    /**
     * 总分片数
     */
    @Schema(description = "总分片数", example = "10")
    private Integer totalCount;

    /**
     * 文件ID（上传完成后返回）
     */
    @Schema(description = "文件ID（上传完成后返回）", example = "12345")
    private Long fileId;

    /**
     * 提示信息
     */
    @Schema(description = "提示信息", example = "分片上传成功")
    private String message;

    /**
     * 创建分片上传成功响应
     */
    public static ChunkUploadVO success(String uploadId, Integer chunkNumber, String etag, 
                                       Boolean completed, Double progress, Integer uploadedCount, 
                                       Integer totalCount, String message) {
        ChunkUploadVO vo = new ChunkUploadVO();
        vo.setUploadId(uploadId);
        vo.setChunkNumber(chunkNumber);
        vo.setEtag(etag);
        vo.setCompleted(completed);
        vo.setProgress(progress);
        vo.setUploadedCount(uploadedCount);
        vo.setTotalCount(totalCount);
        vo.setMessage(message);
        return vo;
    }

    /**
     * 创建上传完成响应
     */
    public static ChunkUploadVO completed(String uploadId, Long fileId, String message) {
        ChunkUploadVO vo = new ChunkUploadVO();
        vo.setUploadId(uploadId);
        vo.setFileId(fileId);
        vo.setCompleted(true);
        vo.setProgress(100.0);
        vo.setMessage(message);
        return vo;
    }
}
