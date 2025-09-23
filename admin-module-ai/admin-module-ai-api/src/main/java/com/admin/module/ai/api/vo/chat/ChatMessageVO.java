package com.admin.module.ai.api.vo.chat;

import com.admin.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 对话消息VO
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话消息信息")
public class ChatMessageVO extends BaseEntity {
    
    @Schema(description = "消息ID")
    private Long id;
    
    @Schema(description = "消息标识")
    private String messageId;
    
    @Schema(description = "父消息ID")
    private String parentMessageId;
    
    @Schema(description = "消息角色")
    private String role;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "模型类型")
    private String modelType;
    
    @Schema(description = "提示token数量")
    private Integer promptTokens;
    
    @Schema(description = "完成token数量")
    private Integer completionTokens;
    
    @Schema(description = "总token数量")
    private Integer totalTokens;
    
    @Schema(description = "消息成本")
    private BigDecimal cost;
    
    @Schema(description = "响应时间(毫秒)")
    private Integer responseTime;
    
    @Schema(description = "元数据信息")
    private Map<String, Object> metadata;
}