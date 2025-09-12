package com.admin.module.infra.api.dto;

import com.admin.common.core.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件分页查询 DTO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "文件分页查询")
@Data
@EqualsAndHashCode(callSuper = true)
public class FilePageDTO extends PageParam {

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务关联ID")
    private String businessId;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "上传状态")
    private Integer uploadStatus;

    @Schema(description = "是否公开")
    private Integer isPublic;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}