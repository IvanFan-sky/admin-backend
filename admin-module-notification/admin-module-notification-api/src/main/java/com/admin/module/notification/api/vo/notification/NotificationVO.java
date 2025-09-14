package com.admin.module.notification.api.vo.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知展示VO
 * 
 * 用于返回给前端的通知信息
 * 包含通知详细信息和状态
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "通知展示对象")
public class NotificationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID", example = "1")
    private Long id;

    @Schema(description = "通知类型ID", example = "1")
    private Long typeId;

    @Schema(description = "通知类型名称", example = "系统公告")
    private String typeName;

    @Schema(description = "通知标题", example = "系统维护通知")
    private String title;

    @Schema(description = "通知内容", example = "系统将于今晚22:00-24:00进行维护，请提前保存工作")
    private String content;

    @Schema(description = "通知级别", example = "1", allowableValues = {"1", "2", "3"})
    private Integer level;

    @Schema(description = "通知级别名称", example = "普通")
    private String levelName;

    @Schema(description = "通知状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "通知状态名称", example = "已发布")
    private String statusName;

    @Schema(description = "推送时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pushTime;

    @Schema(description = "定时推送时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;

    @Schema(description = "目标用户数量", example = "100")
    private Integer targetUserCount;

    @Schema(description = "已读用户数量", example = "50")
    private Integer readUserCount;

    @Schema(description = "扩展数据（JSON格式）", example = "{\"url\":\"https://example.com\"}")
    private String extraData;

    @Schema(description = "创建人", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注信息", example = "重要系统通知")
    private String remark;

    @Schema(description = "版本号（乐观锁）", example = "1")
    private Integer version;

}