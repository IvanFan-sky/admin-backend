# AI对话模块 - 模型配置指南

## 🚀 支持的AI模型

本系统现已支持以下主流AI模型，按推荐程度排序：

### 1. DeepSeek V3.1 ⭐⭐⭐⭐⭐
- **模型名称**: `deepseek-chat` (非思考模式), `deepseek-reasoner` (思考模式)
- **特点**: 性价比极高，编程能力强，中文支持优秀
- **上下文长度**: 4K tokens
- **成本**: 输入 ¥0.00014/1K, 输出 ¥0.00028/1K

### 2. Kimi K2 ⭐⭐⭐⭐⭐
- **模型名称**: `moonshot-v1-8k`, `moonshot-v1-32k`, `moonshot-v1-128k`, `kimi-k2-preview`
- **特点**: 超长上下文，支持256K，中文理解能力强
- **上下文长度**: 8K/32K/128K/256K tokens
- **成本**: ¥0.012/1K - ¥0.06/1K

### 3. GLM-4.5 ⭐⭐⭐⭐
- **模型名称**: `glm-4.5`
- **特点**: 智谱AI出品，兼容Claude协议，中文优化
- **上下文长度**: 4K tokens
- **成本**: 输入 ¥0.002/1K, 输出 ¥0.006/1K

### 4. OpenAI GPT系列 (备选)
- **模型名称**: `gpt-3.5-turbo`, `gpt-4`, `gpt-4-turbo`, `gpt-4o`
- **特点**: 行业标杆，但成本较高，需海外网络
- **成本**: ¥0.005/1K - ¥0.06/1K

## 🛠️ 配置方法

### 环境变量配置

在你的环境中设置以下API密钥：

```bash
# DeepSeek (推荐)
export AI_DEEPSEEK_API_KEY="your-deepseek-api-key"

# Kimi (推荐)
export AI_KIMI_API_KEY="your-kimi-api-key"

# GLM (推荐)
export AI_GLM_API_KEY="your-glm-api-key"

# OpenAI (备选)
export AI_OPENAI_API_KEY="your-openai-api-key"
```

### 申请API密钥

#### DeepSeek API
1. 访问 [DeepSeek开放平台](https://api-docs.deepseek.com/)
2. 注册账号并实名认证
3. 在控制台创建API密钥
4. 新用户赠送500万免费tokens

#### Kimi API  
1. 访问 [Moonshot开放平台](https://platform.moonshot.cn/)
2. 注册账号并完成认证
3. 在API管理中创建密钥
4. 享受长上下文能力

#### GLM API
1. 访问 [智谱AI开放平台](https://open.bigmodel.cn/)
2. 注册并实名认证
3. 在个人中心-API Keys创建密钥
4. 兼容Claude协议，迁移便捷

### 配置文件说明

系统会自动根据可用的API密钥选择模型，优先级如下：

1. **DeepSeek V3.1** - 极致性价比，编程首选
2. **Kimi K2** - 长文本处理专家
3. **GLM-4.5** - 中文理解优秀
4. **OpenAI GPT** - 备选方案

## 📊 模型选择建议

### 编程任务
- **首选**: DeepSeek V3.1 (deepseek-chat)
- **复杂推理**: DeepSeek V3.1 (deepseek-reasoner)

### 长文档分析
- **首选**: Kimi K2 128K (moonshot-v1-128k)
- **超长文档**: Kimi K2 Preview (kimi-k2-preview)

### 中文对话
- **首选**: GLM-4.5 或 Kimi K2
- **备选**: DeepSeek V3.1

### 成本敏感场景
- **首选**: DeepSeek V3.1
- **次选**: GLM-4.5

## 🔧 使用示例

### 创建对话会话

```bash
curl -X POST http://localhost:8080/ai/chat/session \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "title": "编程助手",
    "modelType": "DEEPSEEK_V31_CHAT",
    "systemPrompt": "你是一个专业的编程助手"
  }'
```

### 发送消息

```bash
curl -X POST http://localhost:8080/ai/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "sessionId": "session_xxx",
    "content": "请帮我写一个Spring Boot的REST API",
    "enableRag": false
  }'
```

### 流式对话

```bash
curl -X POST http://localhost:8080/ai/chat/message/stream \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "sessionId": "session_xxx",
    "content": "详细解释Spring AI的工作原理"
  }'
```

## 🎯 最佳实践

1. **多模型配置**: 建议同时配置2-3个模型作为备选
2. **成本控制**: 优先使用DeepSeek和GLM，成本最低
3. **长文本任务**: 使用Kimi K2系列处理长文档
4. **编程任务**: DeepSeek V3.1在代码生成方面表现最佳
5. **中文场景**: 国产模型在中文理解上有明显优势

## 🔍 故障排除

### 常见问题

1. **模型不可用**
   - 检查API密钥是否正确
   - 确认账户余额是否充足
   - 验证网络连接是否正常

2. **响应慢**
   - 尝试切换到其他可用模型
   - 检查网络延迟
   - 减少单次请求的token数量

3. **成本过高**
   - 优先使用DeepSeek V3.1
   - 控制对话历史长度
   - 合理设置max_tokens参数

### 监控和日志

系统会自动记录：
- 模型使用情况
- Token消耗统计
- 成本分析
- 响应时间监控

查看日志：
```bash
tail -f logs/ai-module.log
```

## 📈 性能对比

| 模型 | 编程能力 | 中文理解 | 成本效益 | 推荐度 |
|------|----------|----------|----------|--------|
| DeepSeek V3.1 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Kimi K2 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| GLM-4.5 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| GPT-4o | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |

---

**提示**: 配置完成后，系统会自动选择最优的可用模型。建议先配置DeepSeek和Kimi，获得最佳的性价比体验！