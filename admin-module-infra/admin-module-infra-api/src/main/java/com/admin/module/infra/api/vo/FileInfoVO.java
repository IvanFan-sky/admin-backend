package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息 VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件信息")
@Data
public class
FileInfoVO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件原始名称")
    private String fileName;

    @Schema(description = "文件存储键")
    private String fileKey;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件MIME类型")
    private String contentType;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "文件哈希值")
    private String fileHash;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "存储桶名称")
    private String storageBucket;

    @Schema(description = "存储路径")
    private String storagePath;

    @Schema(description = "上传状态：1-上传中，2-上传完成，3-上传失败")
    private Integer uploadStatus;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务关联ID")
    private String businessId;

    @Schema(description = "是否公开：0-私有，1-公开")
    private Integer isPublic;

    @Schema(description = "下载次数")
    private Integer downloadCount;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}