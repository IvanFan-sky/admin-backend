# AI对话模块详细实现方案

## 1. 实施准备

### 1.1 依赖管理更新

首先需要在 `admin-dependencies/pom.xml` 中添加Spring AI相关依赖：

```xml
<!-- Spring AI 相关依赖版本 -->
<spring-ai.version>1.0.0-M4</spring-ai.version>
<pgvector.version>0.1.6</pgvector.version>
<apache-tika.version>2.9.1</apache-tika.version>
<reactor-core.version>3.6.1</reactor-core.version>

<!-- Spring AI BOM -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-bom</artifactId>
    <version>${spring-ai.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- Spring AI Core -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-core</artifactId>
</dependency>

<!-- OpenAI 支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Ollama 本地模型支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>

<!-- Azure OpenAI 支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
</dependency>

<!-- 向量数据库 - PostgreSQL PGVector -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>

<!-- Spring AI Advisors for RAG -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-advisors-vector-store</artifactId>
</dependency>

<!-- 文档处理和解析 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pdf-document-reader</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-transformers</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
    <version>${apache-tika.version}</version>
</dependency>

<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>${apache-tika.version}</version>
</dependency>

<!-- PostgreSQL 驱动和 PGVector 扩展 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<dependency>
    <groupId>com.pgvector</groupId>
    <artifactId>pgvector</artifactId>
    <version>${pgvector.version}</version>
</dependency>

<!-- 响应式编程支持 -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>${reactor-core.version}</version>
</dependency>
```

### 1.2 数据库脚本

创建 `sql/ai_module_init.sql`：

```sql
-- AI对话模块初始化脚本

-- 1. 创建AI对话会话表
CREATE TABLE ai_chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话标识',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '会话标题',
    model_type VARCHAR(50) NOT NULL COMMENT '使用的模型类型',
    system_prompt TEXT COMMENT '系统提示词',
    status TINYINT DEFAULT 1 COMMENT '会话状态：1-活跃，2-归档，3-删除',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    total_tokens INT DEFAULT 0 COMMENT '总token消耗',
    total_cost DECIMAL(10,6) DEFAULT 0 COMMENT '总成本',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT='AI对话会话表';

-- 2. 创建AI对话消息表
CREATE TABLE ai_chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话标识',
    message_id VARCHAR(64) NOT NULL COMMENT '消息标识',
    parent_message_id VARCHAR(64) COMMENT '父消息ID',
    role ENUM('user', 'assistant', 'system') NOT NULL COMMENT '消息角色',
    content LONGTEXT NOT NULL COMMENT '消息内容',
    model_type VARCHAR(50) COMMENT '使用的模型类型',
    prompt_tokens INT COMMENT '提示token数量',
    completion_tokens INT COMMENT '完成token数量',
    total_tokens INT COMMENT '总token数量',
    cost DECIMAL(10,6) COMMENT '消息成本',
    response_time INT COMMENT '响应时间(毫秒)',
    metadata JSON COMMENT '元数据信息',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_session_id (session_id),
    INDEX idx_message_id (message_id),
    INDEX idx_role (role),
    INDEX idx_create_time (create_time)
) COMMENT='AI对话消息表';

-- 3. 创建AI知识库表
CREATE TABLE ai_knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识库ID',
    name VARCHAR(100) NOT NULL COMMENT '知识库名称',
    description TEXT COMMENT '知识库描述',
    embedding_model VARCHAR(50) NOT NULL COMMENT '嵌入模型',
    vector_dimension INT NOT NULL COMMENT '向量维度',
    document_count INT DEFAULT 0 COMMENT '文档数量',
    chunk_count INT DEFAULT 0 COMMENT '分块数量',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，2-维护中，3-禁用',
    create_by BIGINT COMMENT '创建者ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_create_by (create_by)
) COMMENT='AI知识库表';

-- 4. 创建AI知识库文档表
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
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    INDEX idx_knowledge_base_id (knowledge_base_id),
    INDEX idx_document_id (document_id),
    INDEX idx_status (status),
    FOREIGN KEY (knowledge_base_id) REFERENCES ai_knowledge_base(id)
) COMMENT='AI知识库文档表';

-- 5. 创建AI模型配置表
CREATE TABLE ai_model_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称',
    api_endpoint VARCHAR(500) NOT NULL COMMENT 'API端点',
    api_key VARCHAR(500) COMMENT 'API密钥（加密存储）',
    max_tokens INT DEFAULT 4096 COMMENT '最大token数',
    temperature DECIMAL(3,2) DEFAULT 0.7 COMMENT '温度参数',
    top_p DECIMAL(3,2) DEFAULT 1.0 COMMENT 'Top-p参数',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    priority INT DEFAULT 0 COMMENT '优先级',
    rate_limit INT DEFAULT 60 COMMENT '速率限制（每分钟请求数）',
    cost_per_1k_input_tokens DECIMAL(10,6) COMMENT '每1K输入token成本',
    cost_per_1k_output_tokens DECIMAL(10,6) COMMENT '每1K输出token成本',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    UNIQUE KEY uk_model_type_name (model_type, model_name),
    INDEX idx_enabled (enabled),
    INDEX idx_priority (priority)
) COMMENT='AI模型配置表';

-- 6. 创建AI用户使用统计表
CREATE TABLE ai_user_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
    date DATE NOT NULL COMMENT '统计日期',
    request_count INT DEFAULT 0 COMMENT '请求次数',
    total_tokens INT DEFAULT 0 COMMENT '总token数',
    total_cost DECIMAL(10,6) DEFAULT 0 COMMENT '总成本',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_user_model_date (user_id, model_type, date),
    INDEX idx_user_id (user_id),
    INDEX idx_date (date)
) COMMENT='AI用户使用统计表';

-- 插入默认模型配置
INSERT INTO ai_model_config (model_type, model_name, display_name, api_endpoint, max_tokens, temperature, enabled, priority, cost_per_1k_input_tokens, cost_per_1k_output_tokens, create_time, update_time) VALUES
('OPENAI', 'gpt-3.5-turbo', 'GPT-3.5 Turbo', 'https://api.openai.com/v1/chat/completions', 4096, 0.7, 1, 1, 0.0015, 0.002, NOW(), NOW()),
('OPENAI', 'gpt-4', 'GPT-4', 'https://api.openai.com/v1/chat/completions', 8192, 0.7, 1, 2, 0.03, 0.06, NOW(), NOW()),
('OPENAI', 'gpt-4-turbo', 'GPT-4 Turbo', 'https://api.openai.com/v1/chat/completions', 128000, 0.7, 1, 3, 0.01, 0.03, NOW(), NOW()),
('CLAUDE', 'claude-3-haiku-20240307', 'Claude 3 Haiku', 'https://api.anthropic.com/v1/messages', 200000, 0.7, 0, 4, 0.00025, 0.00125, NOW(), NOW()),
('CLAUDE', 'claude-3-sonnet-20240229', 'Claude 3 Sonnet', 'https://api.anthropic.com/v1/messages', 200000, 0.7, 0, 5, 0.003, 0.015, NOW(), NOW()),
('CLAUDE', 'claude-3-opus-20240229', 'Claude 3 Opus', 'https://api.anthropic.com/v1/messages', 200000, 0.7, 0, 6, 0.015, 0.075, NOW(), NOW());

-- 插入权限数据
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_time, remark) VALUES
('AI对话', 0, 8, 'ai', NULL, 1, 0, 'M', '0', '0', NULL, 'robot', 'admin', NOW(), NOW(), 'AI对话模块菜单'),
('对话管理', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话'), 1, 'chat', 'ai/chat/index', 1, 0, 'C', '0', '0', 'ai:chat:list', 'message', 'admin', NOW(), NOW(), '对话管理菜单'),
('知识库管理', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话'), 2, 'knowledge', 'ai/knowledge/index', 1, 0, 'C', '0', '0', 'ai:knowledge:list', 'documentation', 'admin', NOW(), NOW(), '知识库管理菜单'),
('模型配置', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话'), 3, 'model', 'ai/model/index', 1, 0, 'C', '0', '0', 'ai:model:list', 'cpu', 'admin', NOW(), NOW(), '模型配置菜单');

-- 插入按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_time, remark) VALUES
('对话查询', (SELECT id FROM sys_menu WHERE menu_name = '对话管理'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:chat:query', '#', 'admin', NOW(), NOW(), ''),
('发送消息', (SELECT id FROM sys_menu WHERE menu_name = '对话管理'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:chat:send', '#', 'admin', NOW(), NOW(), ''),
('删除对话', (SELECT id FROM sys_menu WHERE menu_name = '对话管理'), 3, '', '', 1, 0, 'F', '0', '0', 'ai:chat:remove', '#', 'admin', NOW(), NOW(), ''),
('知识库查询', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:query', '#', 'admin', NOW(), NOW(), ''),
('知识库新增', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:add', '#', 'admin', NOW(), NOW(), ''),
('知识库修改', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理'), 3, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:edit', '#', 'admin', NOW(), NOW(), ''),
('知识库删除', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理'), 4, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:remove', '#', 'admin', NOW(), NOW(), ''),
('文档上传', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理'), 5, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:upload', '#', 'admin', NOW(), NOW(), ''),
('模型查询', (SELECT id FROM sys_menu WHERE menu_name = '模型配置'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:model:query', '#', 'admin', NOW(), NOW(), ''),
('模型配置', (SELECT id FROM sys_menu WHERE menu_name = '模型配置'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:model:edit', '#', 'admin', NOW(), NOW(), '');
```

## 2. 模块结构创建

### 2.1 创建AI模块目录结构

```bash
# 在项目根目录下创建AI模块
mkdir -p admin-module-ai/admin-module-ai-api/src/main/java/com/admin/module/ai/api
mkdir -p admin-module-ai/admin-module-ai-biz/src/main/java/com/admin/module/ai/biz
mkdir -p admin-module-ai/admin-module-ai-biz/src/main/resources
```

### 2.2 模块POM文件

`admin-module-ai/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.admin</groupId>
        <artifactId>admin-backend</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>admin-module-ai</artifactId>
    <packaging>pom</packaging>
    
    <name>admin-module-ai</name>
    <description>AI对话模块</description>
    
    <modules>
        <module>admin-module-ai-api</module>
        <module>admin-module-ai-biz</module>
    </modules>
</project>
```

`admin-module-ai/admin-module-ai-api/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.admin</groupId>
        <artifactId>admin-module-ai</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>admin-module-ai-api</artifactId>
    
    <name>admin-module-ai-api</name>
    <description>AI对话模块 - API接口</description>
    
    <dependencies>
        <!-- 通用模块 -->
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-common</artifactId>
        </dependency>
        
        <!-- 参数校验 -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        
        <!-- Swagger注解 -->
        <dependency>
            <groupId>io.swagger.core.v3</groupId>
            <artifactId>swagger-annotations-jakarta</artifactId>
        </dependency>
    </dependencies>
</project>
```

`admin-module-ai/admin-module-ai-biz/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.admin</groupId>
        <artifactId>admin-module-ai</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>admin-module-ai-biz</artifactId>
    
    <name>admin-module-ai-biz</name>
    <description>AI对话模块 - 业务实现</description>
    
    <dependencies>
        <!-- AI API模块 -->
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-module-ai-api</artifactId>
        </dependency>
        
        <!-- 框架模块 -->
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-framework-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-framework-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-framework-starter-mybatis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.admin</groupId>
            <artifactId>admin-framework-starter-redis</artifactId>
        </dependency>
        
        <!-- Spring AI -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-advisors-vector-store</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-pdf-document-reader</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-transformers</artifactId>
        </dependency>
        
        <!-- PostgreSQL 和 PGVector -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>com.pgvector</groupId>
            <artifactId>pgvector</artifactId>
        </dependency>
        
        <!-- 文档处理 -->
        <dependency>
            <groupId>org.apache.tika</groupId>
            <artifactId>tika-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.tika</groupId>
            <artifactId>tika-parsers-standard-package</artifactId>
        </dependency>
        
        <!-- 响应式编程 -->
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webflux</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 3. 核心代码实现

### 3.1 枚举类定义

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/enums/ModelType.java`：

```java
package com.admin.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型类型枚举
 *
 * @author admin
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum ModelType {
    
    // OpenAI 模型
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
```

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/enums/ChatStatus.java`：

```java
package com.admin.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对话状态枚举
 *
 * @author admin
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum ChatStatus {
    
    ACTIVE(1, "活跃"),
    ARCHIVED(2, "归档"),
    DELETED(3, "删除");
    
    private final Integer code;
    private final String description;
    
    public static ChatStatus getByCode(Integer code) {
        for (ChatStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}
```

### 3.2 DTO类定义

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/dto/chat/ChatSessionCreateDTO.java`：

```java
package com.admin.module.ai.api.dto.chat;

import com.admin.module.ai.api.enums.ModelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    
    @Schema(description = "模型类型", example = "OPENAI_GPT4")
    @NotNull(message = "模型类型不能为空")
    private ModelType modelType;
    
    @Schema(description = "系统提示词", example = "你是一个专业的AI助手")
    @Size(max = 2000, message = "系统提示词长度不能超过2000个字符")
    private String systemPrompt;
}
```

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/dto/chat/ChatMessageSendDTO.java`：

```java
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
```

### 3.3 VO类定义

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/vo/chat/ChatSessionVO.java`：

```java
package com.admin.module.ai.api.vo.chat;

import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.enums.ModelType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对话会话VO
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@Schema(description = "对话会话信息")
public class ChatSessionVO {
    
    @Schema(description = "会话ID")
    private Long id;
    
    @Schema(description = "会话标识")
    private String sessionId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "会话标题")
    private String title;
    
    @Schema(description = "模型类型")
    private ModelType modelType;
    
    @Schema(description = "系统提示词")
    private String systemPrompt;
    
    @Schema(description = "会话状态")
    private ChatStatus status;
    
    @Schema(description = "消息数量")
    private Integer messageCount;
    
    @Schema(description = "总token消耗")
    private Integer totalTokens;
    
    @Schema(description = "总成本")
    private BigDecimal totalCost;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
```

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/vo/chat/ChatMessageVO.java`：

```java
package com.admin.module.ai.api.vo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
@Schema(description = "对话消息信息")
public class ChatMessageVO {
    
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
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

### 3.4 服务接口定义

`admin-module-ai-api/src/main/java/com/admin/module/ai/api/service/AiChatService.java`：

```java
package com.admin.module.ai.api.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI对话服务接口
 *
 * @author admin
 * @since 2024-01-15
 */
public interface AiChatService {
    
    /**
     * 创建对话会话
     *
     * @param createDTO 创建参数
     * @return 会话信息
     */
    ChatSessionVO createSession(ChatSessionCreateDTO createDTO);
    
    /**
     * 获取用户的对话会话列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 会话列表
     */
    PageResult<ChatSessionVO> getUserSessions(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    ChatSessionVO getSessionDetail(String sessionId);
    
    /**
     * 获取会话消息历史
     *
     * @param sessionId 会话ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 消息列表
     */
    PageResult<ChatMessageVO> getSessionMessages(String sessionId, Integer pageNum, Integer pageSize);
    
    /**
     * 发送消息（同步）
     *
     * @param sendDTO 发送参数
     * @return 回复消息
     */
    ChatMessageVO sendMessage(ChatMessageSendDTO sendDTO);
    
    /**
     * 发送消息（流式）
     *
     * @param sendDTO 发送参数
     * @return 流式响应
     */
    Flux<String> sendMessageStream(ChatMessageSendDTO sendDTO);
    
    /**
     * 删除对话会话
     *
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);
    
    /**
     * 归档对话会话
     *
     * @param sessionId 会话ID
     */
    void archiveSession(String sessionId);
}
```

### 3.5 数据对象定义

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/dal/dataobject/AiChatSessionDO.java`：

```java
package com.admin.module.ai.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI对话会话数据对象
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_chat_session")
public class AiChatSessionDO {
    
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
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}
```

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/dal/dataobject/AiChatMessageDO.java`：

```java
package com.admin.module.ai.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI对话消息数据对象
 *
 * @author admin
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "ai_chat_message", autoResultMap = true)
public class AiChatMessageDO {
    
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
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}
```

### 3.6 Mapper接口定义

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/dal/mapper/AiChatSessionMapper.java`：

```java
package com.admin.module.ai.biz.dal.mapper;

import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话会话Mapper接口
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSessionDO> {
    
    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiChatSessionDO> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会话ID查询会话信息
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    AiChatSessionDO selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 更新会话统计信息
     *
     * @param sessionId 会话ID
     * @param messageCount 消息数量增量
     * @param tokenCount token数量增量
     * @param cost 成本增量
     */
    void updateSessionStats(@Param("sessionId") String sessionId, 
                           @Param("messageCount") Integer messageCount,
                           @Param("tokenCount") Integer tokenCount, 
                           @Param("cost") java.math.BigDecimal cost);
}
```

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/dal/mapper/AiChatMessageMapper.java`：

```java
package com.admin.module.ai.biz.dal.mapper;

import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话消息Mapper接口
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessageDO> {
    
    /**
     * 根据会话ID查询消息列表
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<AiChatMessageDO> selectBySessionId(@Param("sessionId") String sessionId, 
                                           @Param("limit") Integer limit);
    
    /**
     * 根据会话ID查询最近的消息
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<AiChatMessageDO> selectRecentMessages(@Param("sessionId") String sessionId, 
                                              @Param("limit") Integer limit);
}
```

## 4. 配置类实现

### 4.1 AI模型配置

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/config/AiModelConfiguration.java`：

```java
package com.admin.module.ai.biz.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;

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
     * OpenAI ChatClient 配置
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.ai.openai.api-key")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("You are a helpful AI assistant.")
                .build();
    }
    
    /**
     * Ollama ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.ollama.base-url")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("You are a helpful AI assistant.")
                .build();
    }
    
    /**
     * Azure OpenAI ChatClient 配置
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.azure.openai.api-key")
    public ChatClient azureOpenAiChatClient(AzureOpenAiChatModel azureOpenAiChatModel) {
        return ChatClient.builder(azureOpenAiChatModel)
                .defaultSystem("You are a helpful AI assistant.")
                .build();
    }
    
    /**
     * 向量存储配置
     */
    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new PgVectorStore(jdbcTemplate, embeddingModel);
    }
    
    /**
     * RAG 问答顾问配置
     */
    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return new QuestionAnswerAdvisor(vectorStore);
    }
    
    /**
     * 文本分割器配置
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter(500, 100, 5, 10000, true);
    }
}
```

### 4.2 配置属性类

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/config/AiProperties.java`：

```java
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
```

## 5. 核心服务实现

### 5.1 AI模型管理器

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/service/core/AiModelManager.java`：

```java
package com.admin.module.ai.biz.service.core;

import com.admin.module.ai.api.enums.ModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI模型管理器
 *
 * @author admin
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiModelManager {
    
    private final Map<ModelType, ChatClient> chatClients = new ConcurrentHashMap<>();
    
    private final ChatClient openAiChatClient;
    private final ChatClient azureOpenAiChatClient;
    private final ChatClient ollamaChatClient;
    
    /**
     * 初始化模型映射
     */
    @PostConstruct
    public void initModels() {
        // OpenAI模型
        if (openAiChatClient != null) {
            chatClients.put(ModelType.OPENAI_GPT_4O, openAiChatClient);
            chatClients.put(ModelType.OPENAI_GPT_4O_MINI, openAiChatClient);
            chatClients.put(ModelType.OPENAI_GPT_4_TURBO, openAiChatClient);
        }
        
        // Azure OpenAI模型
        if (azureOpenAiChatClient != null) {
            chatClients.put(ModelType.AZURE_OPENAI_GPT_4O, azureOpenAiChatClient);
            chatClients.put(ModelType.AZURE_OPENAI_GPT_4_TURBO, azureOpenAiChatClient);
            chatClients.put(ModelType.AZURE_OPENAI_GPT_35_TURBO, azureOpenAiChatClient);
        }
        
        // Ollama本地模型
        if (ollamaChatClient != null) {
            chatClients.put(ModelType.OLLAMA_LLAMA2, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_LLAMA2_13B, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_CODE_LLAMA, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_MISTRAL, ollamaChatClient);
            chatClients.put(ModelType.OLLAMA_QWEN, ollamaChatClient);
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
            throw new IllegalArgumentException("不支持的模型类型: " + modelType);
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
     * 获取云端模型列表
     *
     * @return 云端模型列表
     */
    public Set<ModelType> getCloudModels() {
        return chatClients.keySet().stream()
            .filter(ModelType::isCloudModel)
            .collect(Collectors.toSet());
    }
    
    /**
     * 获取本地模型列表
     *
     * @return 本地模型列表
     */
    public Set<ModelType> getLocalModels() {
        return chatClients.keySet().stream()
            .filter(modelType -> !modelType.isCloudModel())
            .collect(Collectors.toSet());
    }
}
```

### 5.2 对话会话管理器

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/service/core/ChatSessionManager.java`：

```java
package com.admin.module.ai.biz.service.core;

import com.admin.framework.redis.constants.CacheConstants;
import com.admin.module.ai.biz.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.ChatMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对话会话管理器
 *
 * @author admin
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatSessionManager {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final AiProperties aiProperties;
    
    private static final String SESSION_KEY_PREFIX = CacheConstants.AI_CHAT_SESSION + ":";
    private static final String HISTORY_KEY_PREFIX = CacheConstants.AI_CHAT_HISTORY + ":";
    
    /**
     * 创建新的对话会话
     *
     * @param userId 用户ID
     * @return 会话ID
     */
    public String createSession(Long userId) {
        String sessionId = "session_" + UUID.randomUUID().toString().replace("-", "");
        
        // 存储会话基本信息
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, "userId", userId);
        redisTemplate.opsForHash().put(sessionKey, "createTime", System.currentTimeMillis());
        redisTemplate.expire(sessionKey, Duration.ofMinutes(aiProperties.getChat().getSessionTimeoutMinutes()));
        
        log.debug("创建对话会话: {}, 用户: {}", sessionId, userId);
        return sessionId;
    }
    
    /**
     * 添加消息到会话历史
     *
     * @param sessionId 会话ID
     * @param message 消息
     */
    public void addMessage(String sessionId, ChatMessage message) {
        String historyKey = HISTORY_KEY_PREFIX + sessionId;
        
        // 添加消息到历史记录
        redisTemplate.opsForList().rightPush(historyKey, message);
        
        // 限制历史记录长度
        Long size = redisTemplate.opsForList().size(historyKey);
        if (size != null && size > aiProperties.getChat().getMaxHistoryLength()) {
            redisTemplate.opsForList().leftPop(historyKey);
        }
        
        // 更新过期时间
        redisTemplate.expire(historyKey, Duration.ofMinutes(aiProperties.getChat().getSessionTimeoutMinutes()));
    }
    
    /**
     * 获取会话历史记录
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 历史消息列表
     */
    @SuppressWarnings("unchecked")
    public List<ChatMessage> getSessionHistory(String sessionId, int limit) {
        String historyKey = HISTORY_KEY_PREFIX + sessionId;
        
        // 获取最近的消息
        List<Object> messages = redisTemplate.opsForList().range(historyKey, -limit, -1);
        
        return messages != null ? 
            messages.stream().map(obj -> (ChatMessage) obj).collect(Collectors.toList()) :
            Collections.emptyList();
    }
    
    /**
     * 检查会话是否存在
     *
     * @param sessionId 会话ID
     * @return 是否存在
     */
    public boolean sessionExists(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }
    
    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     */
    public void deleteSession(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        String historyKey = HISTORY_KEY_PREFIX + sessionId;
        
        redisTemplate.delete(sessionKey);
        redisTemplate.delete(historyKey);
        
        log.debug("删除对话会话: {}", sessionId);
    }
    
    /**
     * 获取会话用户ID
     *
     * @param sessionId 会话ID
     * @return 用户ID
     */
    public Long getSessionUserId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object userId = redisTemplate.opsForHash().get(sessionKey, "userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
}
```

### 5.3 RAG检索引擎

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/service/core/RagRetrievalEngine.java`：

```java
package com.admin.module.ai.biz.service.core;

import com.admin.module.ai.biz.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG检索引擎
 *
 * @author admin
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RagRetrievalEngine {
    
    private final VectorStore vectorStore;
    private final AiProperties aiProperties;
    
    /**
     * 检索相关文档
     *
     * @param query 查询文本
     * @param knowledgeBaseIds 知识库ID列表
     * @return 相关文档列表
     */
    public List<Document> retrieveDocuments(String query, List<Long> knowledgeBaseIds) {
        try {
            // 构建搜索请求
            SearchRequest.Builder requestBuilder = SearchRequest.query(query)
                .withTopK(aiProperties.getRag().getTopK())
                .withSimilarityThreshold(aiProperties.getRag().getSimilarityThreshold());
            
            // 添加知识库过滤条件
            if (knowledgeBaseIds != null && !knowledgeBaseIds.isEmpty()) {
                requestBuilder.withFilterExpression(
                    "knowledge_base_id in [" + 
                    knowledgeBaseIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + 
                    "]"
                );
            }
            
            SearchRequest request = requestBuilder.build();
            
            // 执行检索
            List<Document> documents = vectorStore.similaritySearch(request);
            
            log.debug("RAG检索完成，查询: {}, 返回文档数: {}", query, documents.size());
            return documents;
            
        } catch (Exception e) {
            log.error("RAG检索失败", e);
            return List.of();
        }
    }
    
    /**
     * 构建RAG上下文
     *
     * @param documents 检索到的文档
     * @return 上下文文本
     */
    public String buildContext(List<Document> documents) {
        if (documents.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("以下是相关的背景信息：\n\n");
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append("[文档 ").append(i + 1).append("]\n");
            context.append(doc.getContent()).append("\n\n");
        }
        
        context.append("请基于以上背景信息回答用户的问题。如果背景信息不足以回答问题，请说明并提供你的最佳建议。\n\n");
        
        return context.toString();
    }
}
```

### 5.4 AI对话服务实现

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/service/impl/AiChatServiceImpl.java`：

```java
package com.admin.module.ai.biz.service.impl;

import com.admin.common.core.domain.PageResult;
import com.admin.common.utils.SecurityUtils;
import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.service.AiChatService;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import com.admin.module.ai.biz.convert.AiChatConvert;
import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import com.admin.module.ai.biz.dal.mapper.AiChatMessageMapper;
import com.admin.module.ai.biz.dal.mapper.AiChatSessionMapper;
import com.admin.module.ai.biz.service.core.AiModelManager;
import com.admin.module.ai.biz.service.core.ChatSessionManager;
import com.admin.module.ai.biz.service.core.RagRetrievalEngine;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ChatMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI对话服务实现
 *
 * @author admin
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatServiceImpl implements AiChatService {
    
    private final AiChatSessionMapper sessionMapper;
    private final AiChatMessageMapper messageMapper;
    private final AiChatConvert aiChatConvert;
    private final AiModelManager modelManager;
    private final ChatSessionManager sessionManager;
    private final RagRetrievalEngine ragEngine;
    private final VectorStore vectorStore;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO createSession(ChatSessionCreateDTO createDTO) {
        Long userId = SecurityUtils.getUserId();
        
        // 创建会话记录
        AiChatSessionDO sessionDO = new AiChatSessionDO();
        sessionDO.setSessionId(sessionManager.createSession(userId));
        sessionDO.setUserId(userId);
        sessionDO.setTitle(StringUtils.hasText(createDTO.getTitle()) ? createDTO.getTitle() : "新对话");
        sessionDO.setModelType(createDTO.getModelType().name());
        sessionDO.setSystemPrompt(createDTO.getSystemPrompt());
        sessionDO.setStatus(ChatStatus.ACTIVE.getCode());
        sessionDO.setMessageCount(0);
        sessionDO.setTotalTokens(0);
        sessionDO.setTotalCost(BigDecimal.ZERO);
        
        sessionMapper.insert(sessionDO);
        
        // 如果有系统提示词，添加到会话历史
        if (StringUtils.hasText(createDTO.getSystemPrompt())) {
            SystemMessage systemMessage = new SystemMessage(createDTO.getSystemPrompt());
            sessionManager.addMessage(sessionDO.getSessionId(), systemMessage);
        }
        
        log.info("创建对话会话成功: {}", sessionDO.getSessionId());
        return aiChatConvert.convertToVO(sessionDO);
    }
    
    @Override
    public PageResult<ChatSessionVO> getUserSessions(Long userId, Integer pageNum, Integer pageSize) {
        Page<AiChatSessionDO> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AiChatSessionDO> wrapper = new LambdaQueryWrapper<AiChatSessionDO>()
            .eq(AiChatSessionDO::getUserId, userId)
            .ne(AiChatSessionDO::getStatus, ChatStatus.DELETED.getCode())
            .orderByDesc(AiChatSessionDO::getUpdateTime);
        
        Page<AiChatSessionDO> result = sessionMapper.selectPage(page, wrapper);
        
        List<ChatSessionVO> sessionVOs = aiChatConvert.convertToVOList(result.getRecords());
        
        return new PageResult<>(sessionVOs, result.getTotal());
    }
    
    @Override
    public ChatSessionVO getSessionDetail(String sessionId) {
        AiChatSessionDO sessionDO = sessionMapper.selectBySessionId(sessionId);
        if (sessionDO == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        
        // 检查权限
        Long currentUserId = SecurityUtils.getUserId();
        if (!sessionDO.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("无权访问该会话");
        }
        
        return aiChatConvert.convertToVO(sessionDO);
    }
    
    @Override
    public PageResult<ChatMessageVO> getSessionMessages(String sessionId, Integer pageNum, Integer pageSize) {
        // 验证会话权限
        getSessionDetail(sessionId);
        
        Page<AiChatMessageDO> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AiChatMessageDO> wrapper = new LambdaQueryWrapper<AiChatMessageDO>()
            .eq(AiChatMessageDO::getSessionId, sessionId)
            .orderByAsc(AiChatMessageDO::getCreateTime);
        
        Page<AiChatMessageDO> result = messageMapper.selectPage(page, wrapper);
        
        List<ChatMessageVO> messageVOs = aiChatConvert.convertToMessageVOList(result.getRecords());
        
        return new PageResult<>(messageVOs, result.getTotal());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageVO sendMessage(ChatMessageSendDTO sendDTO) {
        // 验证会话
        AiChatSessionDO session = sessionMapper.selectBySessionId(sendDTO.getSessionId());
        if (session == null) {
            throw new IllegalArgumentException("会话不存在");
        }
        
        Long startTime = System.currentTimeMillis();
        
        try {
            // 保存用户消息
            String userMessageId = saveUserMessage(sendDTO);
            
            // 构建对话上下文
            List<ChatMessage> messages = buildChatContext(sendDTO);
            
            // 获取ChatClient
            com.admin.module.ai.api.enums.ModelType modelType = session.getModelType() != null ? 
                Enum.valueOf(com.admin.module.ai.api.enums.ModelType.class, session.getModelType()) :
                com.admin.module.ai.api.enums.ModelType.OPENAI_GPT_4O;
            
            ChatClient chatClient = modelManager.getChatClient(modelType);
            
            // 构建ChatClient请求
            ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .messages(messages);
            
            // 如果启用RAG，添加问答顾问
            if (sendDTO.getEnableRag() != null && sendDTO.getEnableRag()) {
                List<Document> documents = ragEngine.retrieveDocuments(
                    sendDTO.getContent(), 
                    sendDTO.getKnowledgeBaseIds()
                );
                
                if (!documents.isEmpty()) {
                    requestSpec = requestSpec.advisors(new QuestionAnswerAdvisor(vectorStore));
                }
            }
            
            // 调用AI模型
            ChatResponse response = requestSpec.call().chatResponse();
            
            // 保存AI回复
            String assistantContent = response.getResult().getOutput().getContent();
            ChatMessageVO assistantMessage = saveAssistantMessage(
                sendDTO.getSessionId(), 
                assistantContent, 
                session.getModelType(),
                response.getMetadata(),
                userMessageId,
                (int) (System.currentTimeMillis() - startTime)
            );
            
            // 更新会话统计
            updateSessionStats(sendDTO.getSessionId(), response.getMetadata());
            
            // 添加到会话历史
            sessionManager.addMessage(sendDTO.getSessionId(), new UserMessage(sendDTO.getContent()));
            sessionManager.addMessage(sendDTO.getSessionId(), new AssistantMessage(assistantContent));
            
            return assistantMessage;
            
        } catch (Exception e) {
            log.error("发送消息失败", e);
            throw new RuntimeException("AI服务暂时不可用，请稍后重试");
        }
    }
    
    @Override
    public Flux<String> sendMessageStream(ChatMessageSendDTO sendDTO) {
        // 验证会话
        AiChatSessionDO session = sessionMapper.selectBySessionId(sendDTO.getSessionId());
        if (session == null) {
            return Flux.error(new IllegalArgumentException("会话不存在"));
        }
        
        try {
            // 保存用户消息
            saveUserMessage(sendDTO);
            
            // 构建对话上下文
            List<ChatMessage> messages = buildChatContext(sendDTO);
            
            // 获取ChatClient
            com.admin.module.ai.api.enums.ModelType modelType = session.getModelType() != null ? 
                Enum.valueOf(com.admin.module.ai.api.enums.ModelType.class, session.getModelType()) :
                com.admin.module.ai.api.enums.ModelType.OPENAI_GPT_4O;
            
            ChatClient chatClient = modelManager.getChatClient(modelType);
            
            // 构建ChatClient请求
            ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .messages(messages);
            
            // 如果启用RAG，添加问答顾问
            if (sendDTO.getEnableRag() != null && sendDTO.getEnableRag()) {
                List<Document> documents = ragEngine.retrieveDocuments(
                    sendDTO.getContent(), 
                    sendDTO.getKnowledgeBaseIds()
                );
                
                if (!documents.isEmpty()) {
                    requestSpec = requestSpec.advisors(new QuestionAnswerAdvisor(vectorStore));
                }
            }
            
            // 流式调用AI模型
            return requestSpec.stream().content()
                .doOnComplete(() -> {
                    // 流式响应完成后的处理
                    log.debug("流式响应完成: {}", sendDTO.getSessionId());
                })
                .doOnError(error -> {
                    log.error("流式响应错误", error);
                });
            
        } catch (Exception e) {
            log.error("发送流式消息失败", e);
            return Flux.error(new RuntimeException("AI服务暂时不可用，请稍后重试"));
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        // 验证会话权限
        getSessionDetail(sessionId);
        
        // 软删除会话
        AiChatSessionDO updateSession = new AiChatSessionDO();
        updateSession.setSessionId(sessionId);
        updateSession.setStatus(ChatStatus.DELETED.getCode());
        
        LambdaQueryWrapper<AiChatSessionDO> wrapper = new LambdaQueryWrapper<AiChatSessionDO>()
            .eq(AiChatSessionDO::getSessionId, sessionId);
        sessionMapper.update(updateSession, wrapper);
        
        // 删除Redis中的会话数据
        sessionManager.deleteSession(sessionId);
        
        log.info("删除对话会话: {}", sessionId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveSession(String sessionId) {
        // 验证会话权限
        getSessionDetail(sessionId);
        
        // 归档会话
        AiChatSessionDO updateSession = new AiChatSessionDO();
        updateSession.setSessionId(sessionId);
        updateSession.setStatus(ChatStatus.ARCHIVED.getCode());
        
        LambdaQueryWrapper<AiChatSessionDO> wrapper = new LambdaQueryWrapper<AiChatSessionDO>()
            .eq(AiChatSessionDO::getSessionId, sessionId);
        sessionMapper.update(updateSession, wrapper);
        
        log.info("归档对话会话: {}", sessionId);
    }
    
    /**
     * 保存用户消息
     */
    private String saveUserMessage(ChatMessageSendDTO sendDTO) {
        String messageId = "msg_" + UUID.randomUUID().toString().replace("-", "");
        
        AiChatMessageDO messageDO = new AiChatMessageDO();
        messageDO.setSessionId(sendDTO.getSessionId());
        messageDO.setMessageId(messageId);
        messageDO.setRole("user");
        messageDO.setContent(sendDTO.getContent());
        
        messageMapper.insert(messageDO);
        
        return messageId;
    }
    
    /**
     * 保存AI回复消息
     */
    private ChatMessageVO saveAssistantMessage(String sessionId, String content, String modelType, 
                                              Object metadata, String parentMessageId, int responseTime) {
        String messageId = "msg_" + UUID.randomUUID().toString().replace("-", "");
        
        AiChatMessageDO messageDO = new AiChatMessageDO();
        messageDO.setSessionId(sessionId);
        messageDO.setMessageId(messageId);
        messageDO.setParentMessageId(parentMessageId);
        messageDO.setRole("assistant");
        messageDO.setContent(content);
        messageDO.setModelType(modelType);
        messageDO.setResponseTime(responseTime);
        
        // 处理token统计和成本计算
        if (metadata != null) {
            // 这里需要根据实际的metadata结构来提取token信息
            // messageDO.setPromptTokens(...);
            // messageDO.setCompletionTokens(...);
            // messageDO.setTotalTokens(...);
            // messageDO.setCost(...);
        }
        
        messageMapper.insert(messageDO);
        
        return aiChatConvert.convertToMessageVO(messageDO);
    }
    
    /**
     * 构建对话上下文
     */
    private List<ChatMessage> buildChatContext(ChatMessageSendDTO sendDTO) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // 获取历史消息
        List<ChatMessage> history = sessionManager.getSessionHistory(sendDTO.getSessionId(), 10);
        messages.addAll(history);
        
        // 如果启用RAG，添加检索到的上下文
        if (Boolean.TRUE.equals(sendDTO.getEnableRag()) && 
            sendDTO.getKnowledgeBaseIds() != null && !sendDTO.getKnowledgeBaseIds().isEmpty()) {
            
            List<Document> documents = ragEngine.retrieveDocuments(
                sendDTO.getContent(), 
                sendDTO.getKnowledgeBaseIds()
            );
            
            if (!documents.isEmpty()) {
                String context = ragEngine.buildContext(documents);
                messages.add(new SystemMessage(context));
            }
        }
        
        // 添加当前用户消息
        messages.add(new UserMessage(sendDTO.getContent()));
        
        return messages;
    }
    
    /**
     * 更新会话统计信息
     */
    private void updateSessionStats(String sessionId, Object metadata) {
        // 这里需要根据实际的metadata结构来更新统计信息
        // sessionMapper.updateSessionStats(sessionId, 2, totalTokens, cost);
    }
}
```

## 6. 控制器实现

### 6.1 AI对话控制器

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/controller/AiChatController.java`：

```java
package com.admin.module.ai.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.framework.security.utils.SecurityUtils;
import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.service.AiChatService;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI对话控制器
 *
 * @author admin
 * @since 2024-01-15
 */
@Tag(name = "AI对话管理", description = "AI对话相关接口")
@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    @Operation(summary = "创建对话会话")
    @PostMapping("/session")
    @PreAuthorize("@ss.hasPermi('ai:chat:send')")
    public R<ChatSessionVO> createSession(@Valid @RequestBody ChatSessionCreateDTO createDTO) {
        ChatSessionVO session = aiChatService.createSession(createDTO);
        return R.ok(session);
    }
    
    @Operation(summary = "获取用户对话会话列表")
    @GetMapping("/sessions")
    @PreAuthorize("@ss.hasPermi('ai:chat:query')")
    public R<PageResult<ChatSessionVO>> getUserSessions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long userId = SecurityUtils.getUserId();
        PageResult<ChatSessionVO> result = aiChatService.getUserSessions(userId, pageNum, pageSize);
        return R.ok(result);
    }
    
    @Operation(summary = "获取会话详情")
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("@ss.hasPermi('ai:chat:query')")
    public R<ChatSessionVO> getSessionDetail(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        
        ChatSessionVO session = aiChatService.getSessionDetail(sessionId);
        return R.ok(session);
    }
    
    @Operation(summary = "获取会话消息历史")
    @GetMapping("/session/{sessionId}/messages")
    @PreAuthorize("@ss.hasPermi('ai:chat:query')")
    public R<PageResult<ChatMessageVO>> getSessionMessages(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResult<ChatMessageVO> result = aiChatService.getSessionMessages(sessionId, pageNum, pageSize);
        return R.ok(result);
    }
    
    @Operation(summary = "发送消息")
    @PostMapping("/message")
    @PreAuthorize("@ss.hasPermi('ai:chat:send')")
    public R<ChatMessageVO> sendMessage(@Valid @RequestBody ChatMessageSendDTO sendDTO) {
        ChatMessageVO message = aiChatService.sendMessage(sendDTO);
        return R.ok(message);
    }
    
    @Operation(summary = "发送消息（流式响应）")
    @PostMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("@ss.hasPermi('ai:chat:send')")
    public Flux<String> sendMessageStream(@Valid @RequestBody ChatMessageSendDTO sendDTO) {
        return aiChatService.sendMessageStream(sendDTO)
            .map(content -> "data: " + content + "\n\n")
            .onErrorReturn("data: [ERROR] AI服务暂时不可用\n\n");
    }
    
    @Operation(summary = "删除对话会话")
    @DeleteMapping("/session/{sessionId}")
    @PreAuthorize("@ss.hasPermi('ai:chat:remove')")
    public R<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        
        aiChatService.deleteSession(sessionId);
        return R.ok();
    }
    
    @Operation(summary = "归档对话会话")
    @PutMapping("/session/{sessionId}/archive")
    @PreAuthorize("@ss.hasPermi('ai:chat:edit')")
    public R<Void> archiveSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        
        aiChatService.archiveSession(sessionId);
        return R.ok();
    }
}
```

## 7. 转换器实现

### 7.1 AI对话转换器

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/convert/AiChatConvert.java`：

```java
package com.admin.module.ai.biz.convert;

import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.enums.ModelType;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * AI对话转换器
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper
public interface AiChatConvert {
    
    AiChatConvert INSTANCE = Mappers.getMapper(AiChatConvert.class);
    
    @Mapping(source = "modelType", target = "modelType", qualifiedByName = "stringToModelType")
    @Mapping(source = "status", target = "status", qualifiedByName = "integerToChatStatus")
    ChatSessionVO convertToVO(AiChatSessionDO sessionDO);
    
    List<ChatSessionVO> convertToVOList(List<AiChatSessionDO> sessionDOList);
    
    ChatMessageVO convertToMessageVO(AiChatMessageDO messageDO);
    
    List<ChatMessageVO> convertToMessageVOList(List<AiChatMessageDO> messageDOList);
    
    @Named("stringToModelType")
    default ModelType stringToModelType(String modelType) {
        if (modelType == null) {
            return null;
        }
        try {
            return ModelType.valueOf(modelType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("integerToChatStatus")
    default ChatStatus integerToChatStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return ChatStatus.getByCode(status);
    }
}
```

## 8. 配置文件

### 8.1 应用配置

在 `admin-module-ai-biz/src/main/resources/application-ai.yml` 中添加：

```yaml
# 管理后台AI模块配置
admin:
  ai:
    # RAG配置
    rag:
      enabled: true
      chunk-size: 500
      chunk-overlap: 100
      top-k: 5
      similarity-threshold: 0.7
      max-document-size: 10485760 # 10MB
      supported-formats: ["pdf", "txt", "md", "docx"]
    
    # 对话配置
    chat:
      max-history-length: 20
      session-timeout-minutes: 60
      max-concurrent-sessions: 100
      max-message-length: 10000
      enable-streaming: true
      streaming-timeout-seconds: 30
    
    # 知识库配置
    knowledge-base:
      upload-path: "./uploads/knowledge"
      max-file-size: 10485760 # 10MB
      batch-size: 100
      auto-index: true
      indexing-threads: 2

# Spring AI配置
spring:
  ai:
    # OpenAI配置
    openai:
      api-key: ${AI_OPENAI_API_KEY:your-openai-api-key}
      base-url: ${AI_OPENAI_BASE_URL:https://api.openai.com}
      chat:
        options:
          model: gpt-4o
          max-tokens: 4096
          temperature: 0.7
    
    # Azure OpenAI配置
    azure:
      openai:
        api-key: ${AI_AZURE_OPENAI_API_KEY:your-azure-openai-api-key}
        endpoint: ${AI_AZURE_OPENAI_ENDPOINT:https://your-resource.openai.azure.com/}
        chat:
          options:
            deployment-name: ${AI_AZURE_OPENAI_DEPLOYMENT:gpt-4o}
            max-tokens: 4096
            temperature: 0.7
    
    # Ollama配置
    ollama:
      base-url: ${AI_OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        options:
          model: llama2
          temperature: 0.7
    
    # 向量存储配置（PostgreSQL PGVector）
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
        
# PostgreSQL数据库配置（用于向量存储）
spring:
  datasource:
    # 如果使用单独的向量数据库
    vector:
      url: ${AI_VECTOR_DB_URL:jdbc:postgresql://localhost:5432/admin_ai_vector}
      username: ${AI_VECTOR_DB_USERNAME:postgres}
      password: ${AI_VECTOR_DB_PASSWORD:password}
      driver-class-name: org.postgresql.Driver
```

### 8.2 Redis缓存配置

在 `admin-framework/admin-framework-starter-redis/src/main/java/com/admin/framework/redis/constants/CacheConstants.java` 中添加：

```java
/**
 * AI模块缓存常量
 */
public static final String AI_CHAT_SESSION = "ai:chat:session";
public static final String AI_CHAT_HISTORY = "ai:chat:history";
public static final String AI_MODEL_CONFIG = "ai:model:config";
public static final String AI_KNOWLEDGE_BASE = "ai:knowledge:base";
```

## 9. 测试实现

### 9.1 单元测试

`admin-module-ai-biz/src/test/java/com/admin/module/ai/biz/service/AiChatServiceTest.java`：

```java
package com.admin.module.ai.biz.service;

import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.enums.ModelType;
import com.admin.module.ai.api.service.AiChatService;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI对话服务测试
 *
 * @author admin
 * @since 2024-01-15
 */
@SpringBootTest
@ActiveProfiles("test")
class AiChatServiceTest {
    
    @Resource
    private AiChatService aiChatService;
    
    @Test
    void testCreateSession() {
        ChatSessionCreateDTO createDTO = new ChatSessionCreateDTO();
        createDTO.setTitle("测试对话");
        createDTO.setModelType(ModelType.OPENAI_GPT35_TURBO);
        createDTO.setSystemPrompt("你是一个专业的AI助手");
        
        ChatSessionVO session = aiChatService.createSession(createDTO);
        
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals("测试对话", session.getTitle());
        assertEquals(ModelType.OPENAI_GPT35_TURBO, session.getModelType());
    }
    
    @Test
    void testSendMessage() {
        // 先创建会话
        ChatSessionCreateDTO createDTO = new ChatSessionCreateDTO();
        createDTO.setTitle("测试对话");
        createDTO.setModelType(ModelType.OPENAI_GPT35_TURBO);
        
        ChatSessionVO session = aiChatService.createSession(createDTO);
        
        // 发送消息
        ChatMessageSendDTO sendDTO = new ChatMessageSendDTO();
        sendDTO.setSessionId(session.getSessionId());
        sendDTO.setContent("你好，请介绍一下自己");
        sendDTO.setEnableRag(false);
        sendDTO.setStream(false);
        
        ChatMessageVO message = aiChatService.sendMessage(sendDTO);
        
        assertNotNull(message);
        assertNotNull(message.getContent());
        assertEquals("assistant", message.getRole());
    }
}
```

## 10. 部署说明

### 10.1 Docker Compose配置

创建 `docker/ai-services/docker-compose.yml`：

```yaml
version: '3.8'

services:
  # PostgreSQL数据库（支持PGVector扩展）
  postgres-vector:
    image: pgvector/pgvector:pg16
    container_name: admin-postgres-vector
    ports:
      - "5433:5432"
    environment:
      - POSTGRES_DB=admin_ai_vector
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_vector_data:/var/lib/postgresql/data
      - ./init-vector-db.sql:/docker-entrypoint-initdb.d/init-vector-db.sql
    networks:
      - ai-network
  
  # Ollama本地模型服务
  ollama:
    image: ollama/ollama:latest
    container_name: admin-ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    environment:
      - OLLAMA_HOST=0.0.0.0
    networks:
      - ai-network
  
  # Redis（如果需要独立部署）
  redis:
    image: redis:7-alpine
    container_name: admin-redis-ai
    ports:
      - "6380:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    networks:
      - ai-network

volumes:
  postgres_vector_data:
  ollama_data:
  redis_data:

networks:
  ai-network:
    driver: bridge
```

### 10.2 数据库初始化脚本

创建 `docker/ai-services/init-vector-db.sql`：

```sql
-- 启用PGVector扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量存储表
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(1536),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建向量索引
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx 
ON vector_store USING hnsw (embedding vector_cosine_ops);
```

### 10.3 启动脚本

创建 `scripts/start-ai-services.sh`：

```bash
#!/bin/bash

# 启动AI相关服务
echo "启动AI服务..."

# 启动服务
cd docker/ai-services
docker-compose up -d

echo "等待服务启动..."
sleep 30

# 检查PostgreSQL服务状态
echo "检查PostgreSQL向量数据库状态..."
docker exec admin-postgres-vector pg_isready -U postgres || echo "PostgreSQL服务启动失败"

# 检查Ollama服务状态
echo "检查Ollama服务状态..."
curl -f http://localhost:11434/api/version || echo "Ollama服务启动失败"

# 拉取Ollama模型
echo "拉取Llama2模型..."
docker exec admin-ollama ollama pull llama2

echo "AI服务启动完成！"
```

## 11. 监控和日志

### 11.1 监控指标

`admin-module-ai-biz/src/main/java/com/admin/module/ai/biz/metrics/AiMetrics.java`：

```java
package com.admin.module.ai.biz.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * AI模块监控指标
 *
 * @author admin
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
public class AiMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // 对话请求计数器
    private final Counter chatRequestCounter = Counter.builder("ai.chat.requests")
        .description("AI对话请求总数")
        .register(meterRegistry);
    
    // 对话响应时间
    private final Timer chatResponseTimer = Timer.builder("ai.chat.response.time")
        .description("AI对话响应时间")
        .register(meterRegistry);
    
    // Token使用计数器
    private final Counter tokenUsageCounter = Counter.builder("ai.token.usage")
        .description("Token使用总数")
        .register(meterRegistry);
    
    // 错误计数器
    private final Counter errorCounter = Counter.builder("ai.errors")
        .description("AI服务错误总数")
        .register(meterRegistry);
    
    public void incrementChatRequest() {
        chatRequestCounter.increment();
    }
    
    public Timer.Sample startChatTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordChatResponse(Timer.Sample sample) {
        sample.stop(chatResponseTimer);
    }
    
    public void incrementTokenUsage(int tokens) {
        tokenUsageCounter.increment(tokens);
    }
    
    public void incrementError(String errorType) {
        errorCounter.increment("type", errorType);
    }
}
```

## 12. 性能优化建议

### 12.1 缓存策略

1. **模型响应缓存**：对相同问题的回答进行缓存
2. **向量检索缓存**：缓存热门文档的向量检索结果
3. **会话状态缓存**：使用Redis缓存活跃会话状态

### 12.2 并发优化

1. **异步处理**：使用`@Async`注解处理耗时操作
2. **连接池配置**：优化HTTP客户端连接池设置
3. **限流控制**：实现基于用户和IP的请求限流

### 12.3 资源管理

1. **内存管理**：合理设置JVM堆内存和向量存储缓存
2. **线程池配置**：优化Spring AI的线程池配置
3. **数据库连接**：配置合适的数据库连接池大小

## 13. 总结

本实现方案基于Spring AI框架提供了一个完整的AI对话模块，具备以下特性：

1. **多模型支持**：支持OpenAI GPT-4o、Azure OpenAI、Ollama本地模型等
2. **RAG集成**：基于PGVector的向量存储，支持知识库检索增强生成
3. **流式响应**：支持实时流式对话体验，提升用户交互感受
4. **会话管理**：完整的对话会话生命周期管理和历史记录
5. **权限控制**：集成现有Spring Security权限体系
6. **监控告警**：完善的监控指标和日志记录
7. **高可用性**：支持分布式部署和负载均衡
8. **扩展性强**：基于Spring AI的模块化设计，易于扩展新功能
9. **标准化架构**：遵循Spring AI最佳实践和项目现有架构模式

该方案采用了最新的Spring AI技术栈，提供了生产级别的AI对话能力，可以无缝集成到现有的管理后台系统中。通过合理的架构设计和性能优化，能够支撑大规模的AI对话应用场景。