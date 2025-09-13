package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传结果VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "文件上传结果响应")
public class
FileUploadVO {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "123456")
    private Long fileId;

    /**
     * 文件名
     */
    @Schema(description = "生成的文件名", example = "avatar_20240115_123456.jpg")
    private String fileName;

    /**
     * 文件原始名称
     */
    @Schema(description = "文件原始名称", example = "用户头像.jpg")
    private String originalFileName;

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
     * 文件MD5哈希值
     */
    @Schema(description = "文件MD5哈希值", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String fileHash;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL", example = "http://localhost:9000/default/2024/01/15/avatar_20240115_123456.jpg")
    private String accessUrl;

    /**
     * 是否为重复文件
     */
    @Schema(description = "是否为重复文件（去重功能）", example = "false")
    private Boolean isDuplicate;

    /**
     * 上传状态
     */
    @Schema(description = "上传状态：0-上传中，1-上传完成，2-上传失败", example = "1")
    private Integer uploadStatus;

    /**
     * 上传状态描述
     */
    @Schema(description = "上传状态描述", example = "上传完成")
    private String uploadStatusDesc;

    /**
     * 上传耗时（毫秒）
     */
    @Schema(description = "上传耗时（毫秒）", example = "1500")
    private Long uploadTime;

    /**
     * 预签名URL（可选）
     */
    @Schema(description = "预签名URL，用于直接访问", example = "http://localhost:9000/default/2024/01/15/avatar_20240115_123456.jpg?X-Amz-Algorithm=...")
    private String presignedUrl;

    /**
     * 预签名URL过期时间（秒）
     */
    @Schema(description = "预签名URL过期时间（秒）", example = "3600")
    private Integer presignedUrlExpire;
}
