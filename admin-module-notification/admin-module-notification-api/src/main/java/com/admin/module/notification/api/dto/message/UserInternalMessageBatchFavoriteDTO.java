package com.admin.module.notification.api.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户站内信批量收藏DTO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@Schema(description = "用户站内信批量收藏DTO")
public class UserInternalMessageBatchFavoriteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站内信ID列表", required = true, example = "[1, 2, 3]")
    @NotEmpty(message = "站内信ID列表不能为空")
    private List<Long> messageIds;

    @Schema(description = "收藏状态", required = true, example = "true")
    private Boolean favorite;
}
