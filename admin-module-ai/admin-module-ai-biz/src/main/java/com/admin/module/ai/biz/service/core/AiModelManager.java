package com.admin.module.ai.biz.service.core;

import com.admin.module.ai.api.enums.ModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI模型管理器
 * 
 * 管理不同类型的AI模型ChatClient，支持动态模型切换
 *
 * @author admin
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiModelManager {
    
    private final Map<ModelType, ChatClient> chatClients = new ConcurrentHashMap<>();
    
    // 注入不同的ChatClient Bean
    private final ChatClient deepSeekChatClient;
    private final ChatClient kimiChatClient;
    private final ChatClient glmChatClient;
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    
    /**
     * 初始化模型映射
     */
    @PostConstruct
    public void initModels() {
        // DeepSeek模型
        if (deepSeekChatClient != null) {
            chatClients.put(ModelType.DEEPSEEK_V31_CHAT, deepSeekChatClient);
            chatClients.put(ModelType.DEEPSEEK_V31_REASONER, deepSeekChatClient);
            log.info("DeepSeek模型初始化完成");
        }
        
        // Kimi模型
        if (kimiChatClient != null) {
            chatClients.put(ModelType.KIMI_K2_8K, kimiChatClient);
            chatClients.put(ModelType.KIMI_K2_32K, kimiChatClient);
            chatClients.put(ModelType.KIMI_K2_128K, kimiChatClient);
            chatClients.put(ModelType.KIMI_K2_PREVIEW, kimiChatClient);
            log.info("Kimi模型初始化完成");
        }
        
        // GLM模型
        if (glmChatClient != null) {
            chatClients.put(ModelType.GLM_4_5, glmChatClient);
            log.info("GLM模型初始化完成");
        }
        
        // OpenAI模型（备选）
        if (openAiChatClient != null) {
            chatClients.put(ModelType.OPENAI_GPT35_TURBO, openAiChatClient);
            chatClients.put(ModelType.OPENAI_GPT4, openAiChatClient);
            chatClients.put(ModelType.OPENAI_GPT4_TURBO, openAiChatClient);
            chatClients.put(ModelType.OPENAI_GPT4O, openAiChatClient);
            log.info("OpenAI模型初始化完成");
        }
        
        // Ollama本地模型
        if (ollamaChatClient != null) {
            chatClients.put(ModelType.OLLAMA_LLAMA2, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_LLAMA2_13B, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_CODELLAMA, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_MISTRAL, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_QWEN, ollamaChatClient);
            log.info("Ollama模型初始化完成");
        }
        
        log.info("AI模型初始化完成，可用模型: {}", chatClients.keySet());
    }
    
    /**
     * 获取聊天客户端
     *
     * @param modelType 模型类型
     * @return 聊天客户端
     */
    public ChatClient getChatClient(ModelType modelType) {
        ChatClient client = chatClients.get(modelType);
        if (client == null) {
            // 如果指定模型不可用，返回默认可用模型
            return getDefaultAvailableClient();
        }
        return client;
    }
    
    /**
     * 检查模型是否可用
     *
     * @param modelType 模型类型
     * @return 是否可用
     */
    public boolean isModelAvailable(ModelType modelType) {
        return chatClients.containsKey(modelType);
    }
    
    /**
     * 获取所有可用模型
     *
     * @return 可用模型列表
     */
    public Set<ModelType> getAvailableModels() {
        return chatClients.keySet();
    }
    
    /**
     * 获取默认可用的ChatClient
     * 优先级：DeepSeek > Kimi > GLM > OpenAI > Ollama
     */
    private ChatClient getDefaultAvailableClient() {
        // 按优先级尝试获取可用的客户端
        if (chatClients.containsKey(ModelType.DEEPSEEK_V31_CHAT)) {
            return chatClients.get(ModelType.DEEPSEEK_V31_CHAT);
        }
        if (chatClients.containsKey(ModelType.KIMI_K2_32K)) {
            return chatClients.get(ModelType.KIMI_K2_32K);
        }
        if (chatClients.containsKey(ModelType.GLM_4_5)) {
            return chatClients.get(ModelType.GLM_4_5);
        }
        if (chatClients.containsKey(ModelType.OPENAI_GPT4O)) {
            return chatClients.get(ModelType.OPENAI_GPT4O);
        }
        
        // 如果都不可用，抛出异常
        throw new IllegalStateException("没有可用的AI模型，请检查配置");
    }
    
    /**
     * 获取推荐的模型列表（按优先级排序）
     */
    public ModelType[] getRecommendedModels() {
        return new ModelType[]{
            ModelType.DEEPSEEK_V31_CHAT,
            ModelType.KIMI_K2_32K,
            ModelType.GLM_4_5,
            ModelType.DEEPSEEK_V31_REASONER,
            ModelType.KIMI_K2_128K,
            ModelType.KIMI_K2_8K
        };
    }
}