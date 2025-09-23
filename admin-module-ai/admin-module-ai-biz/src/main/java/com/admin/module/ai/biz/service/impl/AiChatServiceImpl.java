package com.admin.module.ai.biz.service.impl;

import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.PageUtils;
import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.enums.ModelType;
import com.admin.module.ai.api.service.AiChatService;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import com.admin.module.ai.biz.convert.AiChatConvert;
import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import com.admin.module.ai.biz.dal.mapper.AiChatMessageMapper;
import com.admin.module.ai.biz.dal.mapper.AiChatSessionMapper;
import com.admin.module.ai.biz.service.core.AiModelManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
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
    private final QuestionAnswerAdvisor questionAnswerAdvisor;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO createSession(ChatSessionCreateDTO createDTO) {
        // 获取当前用户ID (这里需要从SecurityContext获取，暂时模拟)
        Long userId = getCurrentUserId();
        
        // 创建会话记录
        AiChatSessionDO sessionDO = new AiChatSessionDO();
        sessionDO.setSessionId(generateSessionId());
        sessionDO.setUserId(userId);
        sessionDO.setTitle(StringUtils.hasText(createDTO.getTitle()) ? createDTO.getTitle() : "新对话");
        sessionDO.setModelType(createDTO.getModelType().name());
        sessionDO.setSystemPrompt(createDTO.getSystemPrompt());
        sessionDO.setStatus(ChatStatus.ACTIVE.getCode());
        sessionDO.setMessageCount(0);
        sessionDO.setTotalTokens(0);
        sessionDO.setTotalCost(BigDecimal.ZERO);
        
        sessionMapper.insert(sessionDO);
        
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
        
        return PageUtils.build(result, aiChatConvert::convertToVO);
    }
    
    @Override
    public ChatSessionVO getSessionDetail(String sessionId) {
        AiChatSessionDO sessionDO = sessionMapper.selectBySessionId(sessionId);
        if (sessionDO == null) {
            throw new ServiceException("会话不存在: " + sessionId);
        }
        
        // 检查权限
        Long currentUserId = getCurrentUserId();
        if (!sessionDO.getUserId().equals(currentUserId)) {
            throw new ServiceException("无权访问该会话");
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
        
        return PageUtils.build(result, aiChatConvert::convertToMessageVO);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageVO sendMessage(ChatMessageSendDTO sendDTO) {
        // 验证会话
        AiChatSessionDO session = sessionMapper.selectBySessionId(sendDTO.getSessionId());
        if (session == null) {
            throw new ServiceException("会话不存在");
        }
        
        Long startTime = System.currentTimeMillis();
        
        try {
            // 保存用户消息
            String userMessageId = saveUserMessage(sendDTO);
            
            // 获取ChatClient
            ModelType modelType = ModelType.valueOf(session.getModelType());
            ChatClient chatClient = modelManager.getChatClient(modelType);
            
            // 构建对话请求
            ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(sendDTO.getContent());
            
            // 添加系统提示词
            if (StringUtils.hasText(session.getSystemPrompt())) {
                requestSpec = requestSpec.system(session.getSystemPrompt());
            }
            
            // 如果启用RAG，添加问答顾问
            if (Boolean.TRUE.equals(sendDTO.getEnableRag())) {
                requestSpec = requestSpec.advisors(questionAnswerAdvisor);
            }
            
            // 调用AI模型
            String response = requestSpec.call().content();
            
            // 保存AI回复
            ChatMessageVO assistantMessage = saveAssistantMessage(
                sendDTO.getSessionId(),
                response,
                session.getModelType(),
                userMessageId,
                (int) (System.currentTimeMillis() - startTime)
            );
            
            // 更新会话统计
            updateSessionStats(sendDTO.getSessionId());
            
            return assistantMessage;
            
        } catch (Exception e) {
            log.error("发送消息失败", e);
            throw new ServiceException("AI服务暂时不可用，请稍后重试");
        }
    }
    
    @Override
    public Flux<String> sendMessageStream(ChatMessageSendDTO sendDTO) {
        // 验证会话
        AiChatSessionDO session = sessionMapper.selectBySessionId(sendDTO.getSessionId());
        if (session == null) {
            return Flux.error(new ServiceException("会话不存在"));
        }
        
        try {
            // 保存用户消息
            saveUserMessage(sendDTO);
            
            // 获取ChatClient
            ModelType modelType = ModelType.valueOf(session.getModelType());
            ChatClient chatClient = modelManager.getChatClient(modelType);
            
            // 构建对话请求
            ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .user(sendDTO.getContent());
            
            // 添加系统提示词
            if (StringUtils.hasText(session.getSystemPrompt())) {
                requestSpec = requestSpec.system(session.getSystemPrompt());
            }
            
            // 如果启用RAG，添加问答顾问
            if (Boolean.TRUE.equals(sendDTO.getEnableRag())) {
                requestSpec = requestSpec.advisors(questionAnswerAdvisor);
            }
            
            // 流式调用AI模型
            return requestSpec.stream().content()
                .doOnComplete(() -> {
                    // 流式响应完成后的处理
                    log.debug("流式响应完成: {}", sendDTO.getSessionId());
                    updateSessionStats(sendDTO.getSessionId());
                })
                .doOnError(error -> {
                    log.error("流式响应错误", error);
                });
            
        } catch (Exception e) {
            log.error("发送流式消息失败", e);
            return Flux.error(new ServiceException("AI服务暂时不可用，请稍后重试"));
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        // 验证会话权限
        getSessionDetail(sessionId);
        
        // 软删除会话
        AiChatSessionDO updateSession = new AiChatSessionDO();
        updateSession.setStatus(ChatStatus.DELETED.getCode());
        
        LambdaQueryWrapper<AiChatSessionDO> wrapper = new LambdaQueryWrapper<AiChatSessionDO>()
            .eq(AiChatSessionDO::getSessionId, sessionId);
        sessionMapper.update(updateSession, wrapper);
        
        log.info("删除对话会话: {}", sessionId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveSession(String sessionId) {
        // 验证会话权限
        getSessionDetail(sessionId);
        
        // 归档会话
        AiChatSessionDO updateSession = new AiChatSessionDO();
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
        String messageId = generateMessageId();
        
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
                                              String parentMessageId, int responseTime) {
        String messageId = generateMessageId();
        
        AiChatMessageDO messageDO = new AiChatMessageDO();
        messageDO.setSessionId(sessionId);
        messageDO.setMessageId(messageId);
        messageDO.setParentMessageId(parentMessageId);
        messageDO.setRole("assistant");
        messageDO.setContent(content);
        messageDO.setModelType(modelType);
        messageDO.setResponseTime(responseTime);
        
        messageMapper.insert(messageDO);
        
        return aiChatConvert.convertToMessageVO(messageDO);
    }
    
    /**
     * 更新会话统计信息
     */
    private void updateSessionStats(String sessionId) {
        sessionMapper.updateSessionStats(sessionId, 2, 0, BigDecimal.ZERO);
    }
    
    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "session_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return "msg_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 获取当前用户ID
     * 这里需要集成现有的安全框架来获取当前用户信息
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L; // 暂时返回固定值
    }
}