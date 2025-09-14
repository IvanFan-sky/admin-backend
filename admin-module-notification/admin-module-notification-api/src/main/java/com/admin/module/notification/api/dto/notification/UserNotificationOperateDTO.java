package com.admin.module.notification.api.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户通知操作请求DTO
 * 
 * 用于接收前端用户通知操作的请求参数
 * 包含标记已读、删除等操作
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "用户通知操作请求对象")
public class UserNotificationOperateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID数组", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "通知ID不能为空")
    private Long[] notificationIds;

    @Schema(description = "操作类型", example = "read", allowableValues = {"read", "unread", "delete"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "操作类型不能为空")
    private String operationType;

}