package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分片上传初始化响应VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分片上传初始化响应")
public class ChunkUploadInitVO {

    /**
     * 上传会话ID
     */
    @Schema(description = "上传会话ID", example = "upload_123456789")
    private String uploadId;

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "12345")
    private Long fileId;

    /**
     * 建议分片大小（字节）
     */
    @Schema(description = "建议分片大小（字节）", example = "5242880")
    private Long chunkSize;

    /**
     * 总分片数
     */
    @Schema(description = "总分片数", example = "10")
    private Integer totalChunks;

    /**
     * 是否需要上传（秒传检查）
     */
    @Schema(description = "是否需要上传（秒传检查）", example = "true")
    private Boolean needUpload;

    /**
     * 已上传的分片序号列表
     */
    @Schema(description = "已上传的分片序号列表", example = "[1, 3, 5]")
    private List<Integer> uploadedChunks;

    /**
     * 上传URL（预签名URL或直接上传地址）
     */
    @Schema(description = "上传URL", example = "http://localhost:9000/admin-dev/uploads/...")
    private String uploadUrl;

    /**
     * 上传过期时间（秒）
     */
    @Schema(description = "上传过期时间（秒）", example = "3600")
    private Integer expireTime;

    /**
     * 提示信息
     */
    @Schema(description = "提示信息", example = "文件已存在，支持秒传")
    private String message;

    /**
     * 创建需要上传的响应
     */
    public static ChunkUploadInitVO needUpload(String uploadId, Long fileId, Long chunkSize, 
                                              Integer totalChunks, List<Integer> uploadedChunks, 
                                              String uploadUrl, Integer expireTime) {
        ChunkUploadInitVO vo = new ChunkUploadInitVO();
        vo.setUploadId(uploadId);
        vo.setFileId(fileId);
        vo.setChunkSize(chunkSize);
        vo.setTotalChunks(totalChunks);
        vo.setNeedUpload(true);
        vo.setUploadedChunks(uploadedChunks);
        vo.setUploadUrl(uploadUrl);
        vo.setExpireTime(expireTime);
        vo.setMessage("开始分片上传");
        return vo;
    }

    /**
     * 创建秒传响应
     */
    public static ChunkUploadInitVO instantUpload(Long fileId, String message) {
        ChunkUploadInitVO vo = new ChunkUploadInitVO();
        vo.setFileId(fileId);
        vo.setNeedUpload(false);
        vo.setMessage(message);
        return vo;
    }
}
