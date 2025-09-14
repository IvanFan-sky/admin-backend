package com.admin.module.notification.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户站内信批量已读DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信批量已读DTO")
public class UserInternalMessageBatchReadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站内信ID列表", required = true, example = "[1, 2, 3]")
    @NotEmpty(message = "站内信ID列表不能为空")
    private List<Long> ids;

    /**
     * 获取消息ID列表（兼容方法）
     * 
     * @return 消息ID列表
     */
    public List<Long> getMessageIds() {
        return this.ids;
    }
}
