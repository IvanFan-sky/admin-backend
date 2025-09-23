package com.admin.module.ai.api.dto.chat;

import com.admin.module.ai.api.enums.ModelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建对话会话DTO
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@Schema(description = "创建对话会话请求")
public class ChatSessionCreateDTO {
    
    @Schema(description = "会话标题", example = "新对话")
    @Size(max = 200, message = "会话标题长度不能超过200个字符")
    private String title;
    
    @Schema(description = "模型类型", example = "DEEPSEEK_V31_CHAT")
    @NotNull(message = "模型类型不能为空")
    private ModelType modelType;
    
    @Schema(description = "系统提示词", example = "你是一个专业的AI助手")
    @Size(max = 2000, message = "系统提示词长度不能超过2000个字符")
    private String systemPrompt;
}