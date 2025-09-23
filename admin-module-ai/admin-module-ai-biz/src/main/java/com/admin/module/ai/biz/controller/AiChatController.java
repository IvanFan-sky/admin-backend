package com.admin.module.ai.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
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
        
        // TODO: 从SecurityContext获取当前用户ID
        Long userId = 1L;
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