package com.admin.module.infra.api.dto;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件分页查询DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件分页查询请求")
public class FilePageDTO extends PageQuery {

    /**
     * 文件名（模糊查询）
     */
    @Schema(description = "文件名，支持模糊查询", example = "用户头像")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg")
    @Size(max = 100, message = "文件类型长度不能超过100个字符")
    private String contentType;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "USER_AVATAR")
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    private String businessType;

    /**
     * 上传状态
     */
    @Schema(description = "上传状态：0-上传中，1-上传完成，2-上传失败，3-已删除", example = "1")
    private Integer uploadStatus;

    /**
     * 开始时间
     */
    @Schema(description = "查询开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "查询结束时间", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

    /**
     * 上传用户ID
     */
    @Schema(description = "上传用户ID", example = "123456")
    private Long uploadUserId;
}
