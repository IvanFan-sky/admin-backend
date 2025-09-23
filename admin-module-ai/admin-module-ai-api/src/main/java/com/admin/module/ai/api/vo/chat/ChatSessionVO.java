package com.admin.module.ai.api.vo.chat;

import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.enums.ModelType;
import com.admin.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对话会话VO
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话会话信息")
public class ChatSessionVO extends BaseEntity {
    
    @Schema(description = "会话ID")
    private Long id;
    
    @Schema(description = "会话标识")
    private String sessionId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "会话标题")
    private String title;
    
    @Schema(description = "模型类型")
    private ModelType modelType;
    
    @Schema(description = "系统提示词")
    private String systemPrompt;
    
    @Schema(description = "会话状态")
    private ChatStatus status;
    
    @Schema(description = "消息数量")
    private Integer messageCount;
    
    @Schema(description = "总token消耗")
    private Integer totalTokens;
    
    @Schema(description = "总成本")
    private BigDecimal totalCost;
}