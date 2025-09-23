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

-- 插入默认模型配置
INSERT INTO ai_model_config (model_type, model_name, display_name, api_endpoint, max_tokens, temperature, enabled, priority, cost_per_1k_input_tokens, cost_per_1k_output_tokens, create_time, update_time) VALUES
-- DeepSeek 模型（主要推荐）
('DEEPSEEK', 'deepseek-chat', 'DeepSeek V3.1 Chat', 'https://api.deepseek.com/chat/completions', 4096, 0.7, 1, 1, 0.00014, 0.00028, NOW(), NOW()),
('DEEPSEEK', 'deepseek-reasoner', 'DeepSeek V3.1 Reasoner', 'https://api.deepseek.com/chat/completions', 4096, 0.7, 1, 2, 0.00055, 0.0019, NOW(), NOW()),
-- Kimi 模型（主要推荐）
('KIMI', 'moonshot-v1-8k', 'Kimi K2 8K', 'https://api.moonshot.cn/v1/chat/completions', 8000, 0.7, 1, 3, 0.012, 0.012, NOW(), NOW()),
('KIMI', 'moonshot-v1-32k', 'Kimi K2 32K', 'https://api.moonshot.cn/v1/chat/completions', 32000, 0.7, 1, 4, 0.024, 0.024, NOW(), NOW()),
('KIMI', 'moonshot-v1-128k', 'Kimi K2 128K', 'https://api.moonshot.cn/v1/chat/completions', 128000, 0.7, 1, 5, 0.06, 0.06, NOW(), NOW()),
('KIMI', 'kimi-k2-preview', 'Kimi K2 Preview', 'https://api.moonshot.cn/v1/chat/completions', 256000, 0.7, 1, 6, 0.05, 0.05, NOW(), NOW()),
-- GLM 模型（主要推荐）
('GLM', 'glm-4.5', 'GLM-4.5', 'https://open.bigmodel.cn/api/paas/v4/chat/completions', 4096, 0.7, 1, 7, 0.002, 0.006, NOW(), NOW()),
-- OpenAI 模型（备选）
('OPENAI', 'gpt-3.5-turbo', 'GPT-3.5 Turbo', 'https://api.openai.com/v1/chat/completions', 4096, 0.7, 0, 8, 0.0015, 0.002, NOW(), NOW()),
('OPENAI', 'gpt-4', 'GPT-4', 'https://api.openai.com/v1/chat/completions', 8192, 0.7, 0, 9, 0.03, 0.06, NOW(), NOW()),
('OPENAI', 'gpt-4-turbo', 'GPT-4 Turbo', 'https://api.openai.com/v1/chat/completions', 128000, 0.7, 0, 10, 0.01, 0.03, NOW(), NOW()),
('OPENAI', 'gpt-4o', 'GPT-4o', 'https://api.openai.com/v1/chat/completions', 128000, 0.7, 0, 11, 0.005, 0.015, NOW(), NOW());

-- 插入权限数据（假设sys_menu表存在）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_time, remark) VALUES
('AI对话', 0, 8, 'ai', NULL, 1, 0, 'M', '0', '0', NULL, 'robot', 'admin', NOW(), NOW(), 'AI对话模块菜单'),
('对话管理', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话' AND menu_type = 'M'), 1, 'chat', 'ai/chat/index', 1, 0, 'C', '0', '0', 'ai:chat:list', 'message', 'admin', NOW(), NOW(), '对话管理菜单'),
('知识库管理', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话' AND menu_type = 'M'), 2, 'knowledge', 'ai/knowledge/index', 1, 0, 'C', '0', '0', 'ai:knowledge:list', 'documentation', 'admin', NOW(), NOW(), '知识库管理菜单'),
('模型配置', (SELECT id FROM sys_menu WHERE menu_name = 'AI对话' AND menu_type = 'M'), 3, 'model', 'ai/model/index', 1, 0, 'C', '0', '0', 'ai:model:list', 'cpu', 'admin', NOW(), NOW(), '模型配置菜单');

-- 插入按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_time, remark) VALUES
('对话查询', (SELECT id FROM sys_menu WHERE menu_name = '对话管理' AND menu_type = 'C'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:chat:query', '#', 'admin', NOW(), NOW(), ''),
('发送消息', (SELECT id FROM sys_menu WHERE menu_name = '对话管理' AND menu_type = 'C'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:chat:send', '#', 'admin', NOW(), NOW(), ''),
('删除对话', (SELECT id FROM sys_menu WHERE menu_name = '对话管理' AND menu_type = 'C'), 3, '', '', 1, 0, 'F', '0', '0', 'ai:chat:remove', '#', 'admin', NOW(), NOW(), ''),
('归档对话', (SELECT id FROM sys_menu WHERE menu_name = '对话管理' AND menu_type = 'C'), 4, '', '', 1, 0, 'F', '0', '0', 'ai:chat:edit', '#', 'admin', NOW(), NOW(), ''),
('知识库查询', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理' AND menu_type = 'C'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:query', '#', 'admin', NOW(), NOW(), ''),
('知识库新增', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理' AND menu_type = 'C'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:add', '#', 'admin', NOW(), NOW(), ''),
('知识库修改', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理' AND menu_type = 'C'), 3, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:edit', '#', 'admin', NOW(), NOW(), ''),
('知识库删除', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理' AND menu_type = 'C'), 4, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:remove', '#', 'admin', NOW(), NOW(), ''),
('文档上传', (SELECT id FROM sys_menu WHERE menu_name = '知识库管理' AND menu_type = 'C'), 5, '', '', 1, 0, 'F', '0', '0', 'ai:knowledge:upload', '#', 'admin', NOW(), NOW(), ''),
('模型查询', (SELECT id FROM sys_menu WHERE menu_name = '模型配置' AND menu_type = 'C'), 1, '', '', 1, 0, 'F', '0', '0', 'ai:model:query', '#', 'admin', NOW(), NOW(), ''),
('模型配置', (SELECT id FROM sys_menu WHERE menu_name = '模型配置' AND menu_type = 'C'), 2, '', '', 1, 0, 'F', '0', '0', 'ai:model:edit', '#', 'admin', NOW(), NOW(), '');