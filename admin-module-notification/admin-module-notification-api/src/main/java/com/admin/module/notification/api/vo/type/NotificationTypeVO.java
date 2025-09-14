package com.admin.module.notification.api.vo.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知类型展示VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "通知类型展示对象")
public class NotificationTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型ID", example = "1")
    private Long id;

    @Schema(description = "类型名称", example = "系统消息")
    private String name;

    @Schema(description = "类型编码", example = "SYSTEM")
    private String code;

    @Schema(description = "类型描述", example = "系统相关消息")
    private String description;

    @Schema(description = "图标", example = "system-icon")
    private String icon;

    @Schema(description = "颜色", example = "#1890ff")
    private String color;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "是否系统内置", example = "true")
    private Boolean isSystem;

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

    @Schema(description = "备注信息", example = "系统内置类型")
    private String remark;
}