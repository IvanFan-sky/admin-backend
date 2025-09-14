package com.admin.module.notification.api.vo.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告展示VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "系统公告展示对象")
public class SystemAnnouncementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID", example = "1")
    private Long id;

    @Schema(description = "公告标题", example = "系统维护通知")
    private String title;

    @Schema(description = "公告内容", example = "系统将于今晚进行维护")
    private String content;

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

    @Schema(description = "阅读次数", example = "100")
    private Integer readCount;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "是否删除")
    private Boolean deleted;
}