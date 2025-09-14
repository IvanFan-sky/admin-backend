package com.admin.module.notification.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户站内信回执DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信回执DTO")
public class UserInternalMessageReceiptDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户站内信ID", required = true, example = "1024")
    @NotNull(message = "用户站内信ID不能为空")
    private Long userMessageId;

    @Schema(description = "回执内容", example = "已收到并阅读")
    private String receiptContent;

    /**
     * 获取ID（兼容方法）
     */
    public Long getId() {
        return this.userMessageId;
    }
}