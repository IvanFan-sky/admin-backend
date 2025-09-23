package com.admin.module.ai.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI对话会话数据对象
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_chat_session")
public class AiChatSessionDO extends BaseEntity {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("title")
    private String title;
    
    @TableField("model_type")
    private String modelType;
    
    @TableField("system_prompt")
    private String systemPrompt;
    
    @TableField("status")
    private Integer status;
    
    @TableField("message_count")
    private Integer messageCount;
    
    @TableField("total_tokens")
    private Integer totalTokens;
    
    @TableField("total_cost")
    private BigDecimal totalCost;
    
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}