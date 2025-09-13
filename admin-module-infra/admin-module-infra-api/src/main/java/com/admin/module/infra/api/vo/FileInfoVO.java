package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "文件信息响应")
public class FileInfoVO {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "123456")
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名", example = "avatar_20240115_123456.jpg")
    private String fileName;

    /**
     * 文件原始名称
     */
    @Schema(description = "文件原始名称", example = "用户头像.jpg")
    private String originalFileName;

    /**
     * 文件路径
     */
    @Schema(description = "文件存储路径", example = "2024/01/15/avatar_20240115_123456.jpg")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long fileSize;

    /**
     * 文件大小（格式化）
     */
    @Schema(description = "文件大小（格式化）", example = "1.0 MB")
    private String fileSizeFormatted;

    /**
     * 文件类型（MIME类型）
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg")
    private String contentType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExtension;

    /**
     * 文件MD5哈希值
     */
    @Schema(description = "文件MD5哈希值", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String fileHash;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型", example = "MINIO")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "default")
    private String bucketName;

    /**
     * 上传状态
     */
    @Schema(description = "上传状态：0-上传中，1-上传完成，2-上传失败，3-已删除", example = "1")
    private Integer uploadStatus;

    /**
     * 上传状态描述
     */
    @Schema(description = "上传状态描述", example = "上传完成")
    private String uploadStatusDesc;

    /**
     * 是否为分片上传
     */
    @Schema(description = "是否为分片上传", example = "false")
    private Boolean isChunked;

    /**
     * 总分片数
     */
    @Schema(description = "总分片数", example = "0")
    private Integer totalChunks;

    /**
     * 访问URL
     */
    @Schema(description = "文件访问URL", example = "http://localhost:9000/default/2024/01/15/avatar_20240115_123456.jpg")
    private String accessUrl;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "USER_AVATAR")
    private String businessType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "123456")
    private String businessId;

    /**
     * 上传用户ID
     */
    @Schema(description = "上传用户ID", example = "123456")
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    @Schema(description = "上传用户名", example = "admin")
    private String uploadUserName;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数", example = "10")
    private Integer downloadCount;

    /**
     * 最后下载时间
     */
    @Schema(description = "最后下载时间", example = "2024-01-15T10:30:00")
    private LocalDateTime lastDownloadTime;

    /**
     * 文件标签
     */
    @Schema(description = "文件标签", example = "头像,用户")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注信息", example = "用户头像上传")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-15T09:30:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-15T09:30:00")
    private LocalDateTime updateTime;
}
