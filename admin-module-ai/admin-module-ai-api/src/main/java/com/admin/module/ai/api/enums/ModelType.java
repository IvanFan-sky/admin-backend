package com.admin.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI模型类型枚举
 *
 * @author admin
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum ModelType {
    
    // DeepSeek 模型
    DEEPSEEK_V31_CHAT("DEEPSEEK", "deepseek-chat", "DeepSeek V3.1 Chat", true),
    DEEPSEEK_V31_REASONER("DEEPSEEK", "deepseek-reasoner", "DeepSeek V3.1 Reasoner", true),
    
    // Kimi 模型
    KIMI_K2_8K("KIMI", "moonshot-v1-8k", "Kimi K2 8K", true),
    KIMI_K2_32K("KIMI", "moonshot-v1-32k", "Kimi K2 32K", true),
    KIMI_K2_128K("KIMI", "moonshot-v1-128k", "Kimi K2 128K", true),
    KIMI_K2_PREVIEW("KIMI", "kimi-k2-preview", "Kimi K2 Preview", true),
    
    // GLM 模型
    GLM_4_5("GLM", "glm-4.5", "GLM-4.5", true),
    
    // OpenAI 模型（作为备选）
    OPENAI_GPT35_TURBO("OPENAI", "gpt-3.5-turbo", "GPT-3.5 Turbo", true),
    OPENAI_GPT4("OPENAI", "gpt-4", "GPT-4", true),
    OPENAI_GPT4_TURBO("OPENAI", "gpt-4-turbo", "GPT-4 Turbo", true),
    OPENAI_GPT4O("OPENAI", "gpt-4o", "GPT-4o", true),
    
    // Azure OpenAI 模型
    AZURE_GPT35_TURBO("AZURE_OPENAI", "gpt-35-turbo", "Azure GPT-3.5 Turbo", true),
    AZURE_GPT4("AZURE_OPENAI", "gpt-4", "Azure GPT-4", true),
    AZURE_GPT4_TURBO("AZURE_OPENAI", "gpt-4-turbo", "Azure GPT-4 Turbo", true),
    
    // Ollama 本地模型
    OLLAMA_LLAMA2("OLLAMA", "llama2", "Llama 2", false),
    OLLAMA_LLAMA2_13B("OLLAMA", "llama2:13b", "Llama 2 13B", false),
    OLLAMA_CODELLAMA("OLLAMA", "codellama", "Code Llama", false),
    OLLAMA_MISTRAL("OLLAMA", "mistral", "Mistral 7B", false),
    OLLAMA_QWEN("OLLAMA", "qwen:7b", "通义千问 7B", false);
    
    private final String provider;
    private final String modelName;
    private final String displayName;
    private final Boolean isCloudModel;
    
    /**
     * 根据提供商和模型名称获取模型类型
     */
    public static ModelType getByProviderAndModel(String provider, String modelName) {
        for (ModelType type : values()) {
            if (type.provider.equals(provider) && type.modelName.equals(modelName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown model: " + provider + "/" + modelName);
    }
    
    /**
     * 获取所有云端模型
     */
    public static List<ModelType> getCloudModels() {
        return Arrays.stream(values())
                .filter(ModelType::getIsCloudModel)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有本地模型
     */
    public static List<ModelType> getLocalModels() {
        return Arrays.stream(values())
                .filter(type -> !type.getIsCloudModel())
                .collect(Collectors.toList());
    }
}