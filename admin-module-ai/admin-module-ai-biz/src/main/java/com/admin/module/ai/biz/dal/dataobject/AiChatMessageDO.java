package com.admin.module.ai.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * AI对话消息数据对象
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_chat_message", autoResultMap = true)
public class AiChatMessageDO extends BaseEntity {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField("message_id")
    private String messageId;
    
    @TableField("parent_message_id")
    private String parentMessageId;
    
    @TableField("role")
    private String role;
    
    @TableField("content")
    private String content;
    
    @TableField("model_type")
    private String modelType;
    
    @TableField("prompt_tokens")
    private Integer promptTokens;
    
    @TableField("completion_tokens")
    private Integer completionTokens;
    
    @TableField("total_tokens")
    private Integer totalTokens;
    
    @TableField("cost")
    private BigDecimal cost;
    
    @TableField("response_time")
    private Integer responseTime;
    
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
    
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}