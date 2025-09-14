package com.admin.module.notification.api.dto.announcement;

import com.admin.common.core.page.PageQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统公告分页查询DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统公告分页查询对象")
public class SystemAnnouncementPageDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告标题", example = "系统维护")
    private String title;

    @Schema(description = "公告类型", example = "1")
    private Integer type;

    @Schema(description = "公告状态", example = "1")
    private Integer status;

    @Schema(description = "优先级", example = "1")
    private Integer priority;

    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    @Schema(description = "是否弹窗", example = "false")
    private Boolean isPopup;

    @Schema(description = "创建时间范围-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] createTime;

    @Schema(description = "发布时间范围-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] publishTime;
}