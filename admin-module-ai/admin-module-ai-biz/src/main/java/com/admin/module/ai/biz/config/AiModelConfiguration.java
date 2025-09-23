package com.admin.module.ai.biz.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.pdf.PdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * AI模型配置类
 *
 * @author admin
 * @since 2024-01-15
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiModelConfiguration {
    
    /**
     * DeepSeek ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.deepseek.api-key")
    public ChatClient deepSeekChatClient() {
        return ChatClient.builder()
                .defaultSystem("你是一个专业的AI助手，请用中文回答问题。")
                .build();
    }
    
    /**
     * Kimi ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.moonshot.api-key")
    public ChatClient kimiChatClient() {
        return ChatClient.builder()
                .defaultSystem("你是一个专业的AI助手，请用中文回答问题。")
                .build();
    }
    
    /**
     * GLM ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.zhipu.api-key")
    public ChatClient glmChatClient() {
        return ChatClient.builder()
                .defaultSystem("你是一个专业的AI助手，请用中文回答问题。")
                .build();
    }
    
    /**
     * OpenAI ChatClient 配置（作为备选）
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.openai.api-key")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个专业的AI助手，请用中文回答问题。")
                .build();
    }
    
    /**
     * Ollama ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.ollama.base-url")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个专业的AI助手，请用中文回答问题。")
                .build();
    }
    
    /**
     * 向量存储配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.vectorstore.pgvector.enabled", havingValue = "true", matchIfMissing = true)
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new PgVectorStore(jdbcTemplate, embeddingModel);
    }
    
    /**
     * RAG 问答顾问配置
     */
    @Bean
    @ConditionalOnProperty(name = "admin.ai.rag.enabled", havingValue = "true", matchIfMissing = true)
    public QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return new QuestionAnswerAdvisor(vectorStore);
    }
    
    /**
     * 文本分割器配置
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter(AiProperties aiProperties) {
        AiProperties.Rag ragConfig = aiProperties.getRag();
        return new TokenTextSplitter(
                ragConfig.getChunkSize(), 
                ragConfig.getChunkOverlap(), 
                5, 
                10000, 
                true
        );
    }
}