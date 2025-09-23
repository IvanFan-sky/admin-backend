package com.admin.module.ai.api.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.ai.api.dto.chat.ChatMessageSendDTO;
import com.admin.module.ai.api.dto.chat.ChatSessionCreateDTO;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import reactor.core.publisher.Flux;

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