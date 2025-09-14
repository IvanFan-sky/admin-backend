package com.admin.module.notification.api.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告更新DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "系统公告更新对象")
public class SystemAnnouncementUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公告ID不能为空")
    private Long id;

    @Schema(description = "公告标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统维护通知")
    @NotBlank(message = "公告标题不能为空")
    private String title;

    @Schema(description = "公告内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统将于今晚进行维护")
    @NotBlank(message = "公告内容不能为空")
    private String content;

    @Schema(description = "公告类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    @Schema(description = "是否弹窗", example = "false")
    private Boolean isPopup;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}