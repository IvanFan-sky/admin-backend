package com.admin.module.ai.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI配置属性
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@ConfigurationProperties(prefix = "admin.ai")
public class AiProperties {
    
    private Rag rag = new Rag();
    private Chat chat = new Chat();
    private KnowledgeBase knowledgeBase = new KnowledgeBase();
    
    @Data
    public static class Rag {
        private Boolean enabled = true;
        private Integer chunkSize = 500;
        private Integer chunkOverlap = 100;
        private Integer topK = 5;
        private Double similarityThreshold = 0.7;
        private Integer maxDocumentSize = 10485760; // 10MB
        private String[] supportedFormats = {"pdf", "txt", "md", "docx"};
    }
    
    @Data
    public static class Chat {
        private Integer maxHistoryLength = 20;
        private Integer sessionTimeoutMinutes = 60;
        private Integer maxConcurrentSessions = 100;
        private Integer maxMessageLength = 10000;
        private Boolean enableStreaming = true;
        private Integer streamingTimeoutSeconds = 30;
    }
    
    @Data
    public static class KnowledgeBase {
        private String uploadPath = "./uploads/knowledge";
        private Integer maxFileSize = 10485760; // 10MB
        private Integer batchSize = 100;
        private Boolean autoIndex = true;
        private Integer indexingThreads = 2;
    }
}