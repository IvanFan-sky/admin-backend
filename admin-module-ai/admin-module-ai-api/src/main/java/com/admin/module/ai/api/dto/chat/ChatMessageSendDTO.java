package com.admin.module.ai.api.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发送消息DTO
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@Schema(description = "发送消息请求")
public class ChatMessageSendDTO {
    
    @Schema(description = "会话ID", example = "session_123456")
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    @Schema(description = "消息内容", example = "你好，请介绍一下Spring AI")
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 10000, message = "消息内容长度不能超过10000个字符")
    private String content;
    
    @Schema(description = "是否启用RAG检索", example = "true")
    private Boolean enableRag = false;
    
    @Schema(description = "知识库ID列表")
    private List<Long> knowledgeBaseIds;
    
    @Schema(description = "是否流式响应", example = "false")
    private Boolean stream = false;
}