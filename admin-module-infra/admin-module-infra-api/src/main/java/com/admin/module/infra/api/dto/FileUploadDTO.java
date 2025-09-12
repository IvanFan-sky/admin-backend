package com.admin.module.infra.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件上传请求 DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件上传请求")
@Data
public class FileUploadDTO {

    @Schema(description = "上传的文件", required = true)
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    @Schema(description = "业务类型", example = "avatar", required = true)
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "业务关联ID", example = "123")
    private String businessId;

    @Schema(description = "是否公开", example = "0")
    private Integer isPublic = 0;

    @Schema(description = "存储类型", example = "minio")
    private String storageType;

    @Schema(description = "备注")
    private String remark;
}