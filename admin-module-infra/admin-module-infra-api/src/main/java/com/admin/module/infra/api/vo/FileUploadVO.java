package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传结果 VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件上传结果")
@Data
public class FileUploadVO {

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "文件哈希值")
    private String fileHash;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务关联ID")
    private String businessId;

    @Schema(description = "是否去重文件")
    private Boolean isDuplicate = false;

    @Schema(description = "上传状态：1-上传中，2-上传完成，3-上传失败")
    private Integer uploadStatus;
}