package com.admin.module.system.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "刷新令牌请求")
public class RefreshTokenDTO {

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌", required = true)
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}