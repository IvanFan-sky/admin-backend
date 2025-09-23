# AI对话模块设计方案

## 1. 方案概述

基于当前项目的Spring Boot 3.x + 多模块架构，设计一个功能完整的AI对话模块，支持多种大模型集成、RAG检索增强生成、对话记忆管理等核心功能。

### 1.1 设计目标

- **多模型支持**: 集成OpenAI、Claude、通义千问、文心一言等主流大模型
- **RAG增强**: 支持文档知识库检索增强生成
- **对话记忆**: 维护上下文对话历史和会话管理
- **流式响应**: 支持SSE流式输出提升用户体验
- **插件扩展**: 支持Function Calling和工具调用
- **权限控制**: 基于现有RBAC权限体系进行访问控制

### 1.2 技术选型

| 技术分类 | 技术选型 | 版本 | 说明 |
|---------|---------|------|------|
| **AI框架** | Spring AI | 1.0.x | Spring官方AI集成框架 |
| **向量数据库** | Chroma/Milvus | - | 向量存储和相似度检索 |
| **文档处理** | Apache Tika | 2.x | 多格式文档解析 |
| **嵌入模型** | text-embedding-ada-002 | - | OpenAI嵌入模型 |
| **流式处理** | Spring WebFlux | 6.x | 响应式编程支持SSE |
| **消息队列** | RocketMQ | 5.x | 异步任务处理 |

## 2. 架构设计

### 2.1 模块架构

```
admin-module-ai/
├── admin-module-ai-api/           # API接口定义层
│   ├── dto/                       # 数据传输对象
│   │   ├── chat/                  # 对话相关DTO
│   │   ├── knowledge/             # 知识库相关DTO
│   │   └── model/                 # 模型配置DTO
│   ├── vo/                        # 视图对象
│   │   ├── chat/                  # 对话响应VO
│   │   ├── knowledge/             # 知识库VO
│   │   └── model/                 # 模型信息VO
│   └── service/                   # 服务接口定义
│       ├── AiChatService.java     # 对话服务接口
│       ├── KnowledgeService.java  # 知识库服务接口
│       └── ModelService.java      # 模型管理服务接口
│
├── admin-module-ai-biz/           # 业务实现层
│   ├── controller/                # 控制器层
│   │   ├── AiChatController.java  # AI对话控制器
│   │   ├── KnowledgeController.java # 知识库管理控制器
│   │   └── ModelController.java   # 模型配置控制器
│   ├── service/                   # 服务实现层
│   │   ├── chat/                  # 对话服务实现
│   │   ├── knowledge/             # 知识库服务实现
│   │   ├── model/                 # 模型服务实现
│   │   └── rag/                   # RAG相关服务
│   ├── dal/                       # 数据访问层
│   │   ├── dataobject/            # 数据对象
│   │   └── mapper/                # MyBatis映射器
│   ├── convert/                   # 对象转换器
│   ├── config/                    # 配置类
│   │   ├── AiModelConfig.java     # AI模型配置
│   │   └── VectorStoreConfig.java # 向量数据库配置
│   └── enums/                     # 枚举类
│       ├── ModelType.java         # 模型类型枚举
│       └── ChatStatus.java        # 对话状态枚举
│
└── pom.xml                        # 模块依赖管理
```

### 2.2 核心组件设计

#### 2.2.1 AI模型管理器

```java
@Component
public class AiModelManager {
    private final Map<ModelType, ChatModel> chatModels;
    private final Map<ModelType, EmbeddingModel> embeddingModels;
    
    // 动态模型切换
    public ChatModel getChatModel(ModelType modelType) {
        return chatModels.get(modelType);
    }
    
    // 模型健康检查
    public boolean isModelAvailable(ModelType modelType) {
        // 实现模型可用性检查
    }
}
```

#### 2.2.2 对话会话管理器

```java
@Service
public class ChatSessionManager {
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 会话创建和管理
    public String createSession(Long userId) {
        // 创建新的对话会话
    }
    
    // 对话历史管理
    public void addMessage(String sessionId, ChatMessage message) {
        // 添加消息到会话历史
    }
    
    // 获取会话上下文
    public List<ChatMessage> getSessionHistory(String sessionId, int limit) {
        // 获取会话历史记录
    }
}
```

#### 2.2.3 RAG检索引擎

```java
@Service
public class RagRetrievalEngine {
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    
    // 文档检索
    public List<Document> retrieveRelevantDocuments(String query, int topK) {
        // 向量化查询并检索相关文档
    }
    
    // 上下文增强
    public String enhancePromptWithContext(String userQuery, List<Document> documents) {
        // 将检索到的文档内容整合到提示词中
    }
}
```

### 2.3 数据库设计

#### 2.3.1 对话会话表 (ai_chat_session)

```sql
CREATE TABLE ai_chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话标识',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '会话标题',
    model_type VARCHAR(50) NOT NULL COMMENT '使用的模型类型',
    status TINYINT DEFAULT 1 COMMENT '会话状态：1-活跃，2-归档，3-删除',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_create_time (create_time)
) COMMENT='AI对话会话表';
```

#### 2.3.2 对话消息表 (ai_chat_message)

```sql
CREATE TABLE ai_chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话标识',
    message_id VARCHAR(64) NOT NULL COMMENT '消息标识',
    parent_message_id VARCHAR(64) COMMENT '父消息ID',
    role ENUM('user', 'assistant', 'system') NOT NULL COMMENT '消息角色',
    content TEXT NOT NULL COMMENT '消息内容',
    model_type VARCHAR(50) COMMENT '使用的模型类型',
    tokens_used INT COMMENT '使用的token数量',
    cost DECIMAL(10,6) COMMENT '消息成本',
    metadata JSON COMMENT '元数据信息',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_session_id (session_id),
    INDEX idx_message_id (message_id),
    INDEX idx_create_time (create_time)
) COMMENT='AI对话消息表';
```

#### 2.3.3 知识库表 (ai_knowledge_base)

```sql
CREATE TABLE ai_knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识库ID',
    name VARCHAR(100) NOT NULL COMMENT '知识库名称',
    description TEXT COMMENT '知识库描述',
    embedding_model VARCHAR(50) NOT NULL COMMENT '嵌入模型',
    vector_dimension INT NOT NULL COMMENT '向量维度',
    document_count INT DEFAULT 0 COMMENT '文档数量',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，2-维护中，3-禁用',
    create_by BIGINT COMMENT '创建者ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_name (name),
    INDEX idx_status (status)
) COMMENT='AI知识库表';
```

#### 2.3.4 知识库文档表 (ai_knowledge_document)

```sql
CREATE TABLE ai_knowledge_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',
    knowledge_base_id BIGINT NOT NULL COMMENT '知识库ID',
    document_id VARCHAR(64) NOT NULL COMMENT '文档标识',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    content LONGTEXT NOT NULL COMMENT '文档内容',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    chunk_count INT DEFAULT 0 COMMENT '分块数量',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已索引，2-索引中，3-索引失败',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_knowledge_base_id (knowledge_base_id),
    INDEX idx_document_id (document_id),
    INDEX idx_status (status)
) COMMENT='AI知识库文档表';
```

#### 2.3.5 模型配置表 (ai_model_config)

```sql
CREATE TABLE ai_model_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    api_endpoint VARCHAR(500) NOT NULL COMMENT 'API端点',
    api_key VARCHAR(500) COMMENT 'API密钥（加密存储）',
    max_tokens INT DEFAULT 4096 COMMENT '最大token数',
    temperature DECIMAL(3,2) DEFAULT 0.7 COMMENT '温度参数',
    top_p DECIMAL(3,2) DEFAULT 1.0 COMMENT 'Top-p参数',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    priority INT DEFAULT 0 COMMENT '优先级',
    rate_limit INT DEFAULT 60 COMMENT '速率限制（每分钟请求数）',
    cost_per_1k_tokens DECIMAL(10,6) COMMENT '每1K token成本',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    UNIQUE KEY uk_model_type_name (model_type, model_name),
    INDEX idx_enabled (enabled),
    INDEX idx_priority (priority)
) COMMENT='AI模型配置表';
```

## 3. API设计

### 3.1 对话相关API

#### 3.1.1 创建对话会话

```http
POST /ai/chat/session
Content-Type: application/json

{
    "title": "新对话",
    "modelType": "OPENAI_GPT4",
    "systemPrompt": "你是一个专业的AI助手"
}
```

#### 3.1.2 发送消息

```http
POST /ai/chat/message
Content-Type: application/json

{
    "sessionId": "session_123",
    "content": "你好，请介绍一下Spring AI",
    "enableRag": true,
    "knowledgeBaseIds": [1, 2]
}
```

#### 3.1.3 流式对话

```http
POST /ai/chat/stream
Content-Type: application/json
Accept: text/event-stream

{
    "sessionId": "session_123",
    "content": "请详细解释RAG技术原理"
}
```

### 3.2 知识库相关API

#### 3.2.1 创建知识库

```http
POST /ai/knowledge/base
Content-Type: application/json

{
    "name": "技术文档库",
    "description": "存储技术相关文档",
    "embeddingModel": "text-embedding-ada-002"
}
```

#### 3.2.2 上传文档

```http
POST /ai/knowledge/document/upload
Content-Type: multipart/form-data

knowledgeBaseId: 1
file: document.pdf
```

## 4. 核心功能实现

### 4.1 多模型支持实现

```java
@Configuration
public class AiModelConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "ai.openai.enabled", havingValue = "true")
    public OpenAiChatModel openAiChatModel(
            @Value("${ai.openai.api-key}") String apiKey,
            @Value("${ai.openai.base-url}") String baseUrl) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("gpt-4")
                .temperature(0.7)
                .build();
    }
    
    @Bean
    @ConditionalOnProperty(name = "ai.claude.enabled", havingValue = "true")
    public AnthropicChatModel claudeChatModel(
            @Value("${ai.claude.api-key}") String apiKey) {
        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName("claude-3-sonnet-20240229")
                .temperature(0.7)
                .build();
    }
}
```

### 4.2 RAG检索实现

```java
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {
    
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final DocumentReader documentReader;
    
    @Override
    public String enhanceQueryWithRag(String query, List<Long> knowledgeBaseIds) {
        // 1. 向量化用户查询
        List<Double> queryEmbedding = embeddingModel.embed(query);
        
        // 2. 检索相关文档
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)
                .withSimilarityThreshold(0.7)
        );
        
        // 3. 构建增强提示词
        StringBuilder enhancedPrompt = new StringBuilder();
        enhancedPrompt.append("基于以下相关信息回答用户问题：\n\n");
        
        for (Document doc : relevantDocs) {
            enhancedPrompt.append("参考资料：\n")
                         .append(doc.getContent())
                         .append("\n\n");
        }
        
        enhancedPrompt.append("用户问题：").append(query);
        
        return enhancedPrompt.toString();
    }
    
    @Override
    @Async
    public void indexDocument(Long knowledgeBaseId, String documentContent) {
        // 1. 文档分块
        List<String> chunks = splitDocument(documentContent);
        
        // 2. 向量化并存储
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            List<Double> embedding = embeddingModel.embed(chunk);
            
            Document document = new Document(chunk);
            document.getMetadata().put("knowledgeBaseId", knowledgeBaseId);
            document.getMetadata().put("chunkIndex", i);
            documents.add(document);
        }
        
        vectorStore.add(documents);
    }
}
```

### 4.3 流式响应实现

```java
@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        return aiChatService.streamChat(request)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build())
                .doOnComplete(() -> log.info("Stream completed for session: {}", request.getSessionId()))
                .doOnError(error -> log.error("Stream error for session: {}", request.getSessionId(), error));
    }
}

@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    
    @Override
    public Flux<String> streamChat(ChatRequest request) {
        return Flux.create(sink -> {
            try {
                ChatModel chatModel = modelManager.getChatModel(request.getModelType());
                
                // 构建提示词（包含RAG增强）
                String enhancedPrompt = ragService.enhanceQueryWithRag(
                    request.getContent(), 
                    request.getKnowledgeBaseIds()
                );
                
                // 流式调用
                chatModel.stream(Prompt.from(enhancedPrompt))
                        .subscribe(
                            chatResponse -> {
                                String content = chatResponse.getResult().getOutput().getContent();
                                sink.next(content);
                            },
                            sink::error,
                            sink::complete
                        );
                        
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
}
```

## 5. 配置管理

### 5.1 应用配置 (application.yml)

```yaml
# AI模块配置
ai:
  # OpenAI配置
  openai:
    enabled: true
    api-key: ${OPENAI_API_KEY:}
    base-url: https://api.openai.com
    model: gpt-4
    max-tokens: 4096
    temperature: 0.7
    
  # Claude配置
  claude:
    enabled: false
    api-key: ${CLAUDE_API_KEY:}
    model: claude-3-sonnet-20240229
    max-tokens: 4096
    temperature: 0.7
    
  # 通义千问配置
  qwen:
    enabled: false
    api-key: ${QWEN_API_KEY:}
    model: qwen-turbo
    
  # 向量数据库配置
  vector-store:
    type: chroma  # chroma, milvus, pinecone
    chroma:
      url: http://localhost:8000
    milvus:
      host: localhost
      port: 19530
      
  # RAG配置
  rag:
    enabled: true
    chunk-size: 1000
    chunk-overlap: 200
    top-k: 5
    similarity-threshold: 0.7
    
  # 对话配置
  chat:
    max-history-length: 20
    session-timeout: 3600  # 会话超时时间（秒）
    max-concurrent-sessions: 100
```

## 6. 部署和监控

### 6.1 Docker配置

```dockerfile
# 向量数据库 - Chroma
version: '3.8'
services:
  chroma:
    image: chromadb/chroma:latest
    ports:
      - "8000:8000"
    volumes:
      - chroma_data:/chroma/chroma
    environment:
      - CHROMA_SERVER_HOST=0.0.0.0
      - CHROMA_SERVER_HTTP_PORT=8000
      
volumes:
  chroma_data:
```

### 6.2 监控指标

```java
@Component
@RequiredArgsConstructor
public class AiMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // 对话请求计数
    public void incrementChatRequests(String modelType) {
        Counter.builder("ai.chat.requests")
                .tag("model", modelType)
                .register(meterRegistry)
                .increment();
    }
    
    // Token使用统计
    public void recordTokenUsage(String modelType, int tokens) {
        Gauge.builder("ai.tokens.used")
                .tag("model", modelType)
                .register(meterRegistry, tokens, Number::doubleValue);
    }
    
    // 响应时间统计
    public void recordResponseTime(String modelType, Duration duration) {
        Timer.builder("ai.response.time")
                .tag("model", modelType)
                .register(meterRegistry)
                .record(duration);
    }
}
```

## 7. 安全考虑

### 7.1 权限控制

```java
@RestController
@PreAuthorize("hasRole('ADMIN') or hasPermission('ai:chat:use')")
public class AiChatController {
    
    @PostMapping("/message")
    @PreAuthorize("@aiPermissionService.canUseModel(authentication.name, #request.modelType)")
    public R<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        // 实现对话逻辑
    }
}

@Service
public class AiPermissionService {
    
    public boolean canUseModel(String username, ModelType modelType) {
        // 检查用户是否有权限使用特定模型
        return userService.hasModelPermission(username, modelType);
    }
    
    public boolean canAccessKnowledgeBase(String username, Long knowledgeBaseId) {
        // 检查用户是否有权限访问特定知识库
        return knowledgeBaseService.hasAccessPermission(username, knowledgeBaseId);
    }
}
```

### 7.2 API密钥管理

```java
@Component
public class ApiKeyManager {
    
    @Value("${ai.encryption.key}")
    private String encryptionKey;
    
    public String encryptApiKey(String apiKey) {
        // 使用AES加密API密钥
        return AESUtil.encrypt(apiKey, encryptionKey);
    }
    
    public String decryptApiKey(String encryptedApiKey) {
        // 解密API密钥
        return AESUtil.decrypt(encryptedApiKey, encryptionKey);
    }
}
```

## 8. 测试策略

### 8.1 单元测试

```java
@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {
    
    @Mock
    private ChatModel chatModel;
    
    @Mock
    private RagService ragService;
    
    @InjectMocks
    private AiChatServiceImpl aiChatService;
    
    @Test
    void testSendMessage() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setContent("Hello, AI!");
        request.setSessionId("test-session");
        
        // Mock行为
        when(ragService.enhanceQueryWithRag(anyString(), anyList()))
                .thenReturn("Enhanced prompt");
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(new ChatResponse(List.of(new Generation("Hello, human!"))));
        
        // 执行测试
        ChatResponse response = aiChatService.sendMessage(request);
        
        // 验证结果
        assertThat(response.getContent()).isEqualTo("Hello, human!");
        verify(ragService).enhanceQueryWithRag("Hello, AI!", Collections.emptyList());
        verify(chatModel).call(any(Prompt.class));
    }
}
```

### 8.2 集成测试

```java
@SpringBootTest
@TestPropertySource(properties = {
    "ai.openai.enabled=true",
    "ai.openai.api-key=test-key"
})
class AiChatIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testChatEndpoint() {
        ChatRequest request = new ChatRequest();
        request.setContent("Test message");
        
        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
            "/ai/chat/message", 
            request, 
            ChatResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isNotBlank();
    }
}
```

## 9. 实施计划

### 9.1 开发阶段

| 阶段 | 任务 | 预计时间 | 依赖 |
|------|------|----------|------|
| **阶段1** | 基础架构搭建 | 3天 | - |
| | - 创建模块结构 | 1天 | - |
| | - 配置Spring AI依赖 | 1天 | - |
| | - 实现基础配置类 | 1天 | - |
| **阶段2** | 核心功能开发 | 5天 | 阶段1 |
| | - 实现多模型支持 | 2天 | - |
| | - 开发对话会话管理 | 2天 | - |
| | - 实现基础API接口 | 1天 | - |
| **阶段3** | RAG功能开发 | 4天 | 阶段2 |
| | - 集成向量数据库 | 2天 | - |
| | - 实现文档处理和索引 | 2天 | - |
| **阶段4** | 高级功能开发 | 3天 | 阶段3 |
| | - 实现流式响应 | 1天 | - |
| | - 添加权限控制 | 1天 | - |
| | - 完善监控和日志 | 1天 | - |
| **阶段5** | 测试和优化 | 2天 | 阶段4 |
| | - 编写单元测试 | 1天 | - |
| | - 性能测试和优化 | 1天 | - |

### 9.2 部署计划

1. **开发环境部署** (1天)
   - 配置向量数据库
   - 部署AI模块
   - 配置API密钥

2. **测试环境部署** (1天)
   - 数据迁移
   - 功能测试
   - 性能测试

3. **生产环境部署** (1天)
   - 灰度发布
   - 监控配置
   - 备份策略

## 10. 风险评估

### 10.1 技术风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| API限制和成本 | 高 | 中 | 实现多模型切换，设置使用限额 |
| 向量数据库性能 | 中 | 低 | 选择成熟方案，做好性能测试 |
| 模型响应延迟 | 中 | 中 | 实现缓存机制，优化提示词 |

### 10.2 业务风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| 用户接受度 | 中 | 低 | 提供良好的用户体验，渐进式推广 |
| 数据安全 | 高 | 低 | 严格权限控制，数据加密 |
| 合规性问题 | 高 | 低 | 遵循相关法规，建立审计机制 |

## 11. 总结

本设计方案基于Spring AI框架，结合当前项目的多模块架构，提供了一个完整的AI对话模块解决方案。方案具有以下特点：

1. **架构清晰**: 遵循项目现有的分层架构，易于集成和维护
2. **功能完整**: 支持多模型、RAG、流式响应等核心功能
3. **扩展性强**: 模块化设计，便于后续功能扩展
4. **安全可靠**: 完善的权限控制和安全机制
5. **易于部署**: 提供详细的部署和配置指南

通过分阶段实施，可以逐步构建一个功能强大、性能优异的AI对话系统。