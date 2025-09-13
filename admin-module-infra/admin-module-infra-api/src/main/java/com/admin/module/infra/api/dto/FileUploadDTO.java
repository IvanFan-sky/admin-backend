package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 文件上传DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadDTO {

    /**
     * 上传文件
     */
    @Schema(description = "上传文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "USER_AVATAR")
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    private String businessType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "123456")
    @Size(max = 100, message = "业务ID长度不能超过100个字符")
    private String businessId;

    /**
     * 文件标签
     */
    @Schema(description = "文件标签，多个标签用逗号分隔", example = "头像,用户")
    @Size(max = 200, message = "文件标签长度不能超过200个字符")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注信息", example = "用户头像上传")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
