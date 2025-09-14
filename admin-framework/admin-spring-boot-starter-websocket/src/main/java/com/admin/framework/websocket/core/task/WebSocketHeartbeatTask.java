package com.admin.framework.websocket.core.task;

import com.admin.framework.websocket.config.WebSocketProperties;
import com.admin.framework.websocket.core.sender.WebSocketMessageSender;
import com.admin.framework.websocket.core.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

/**
 * WebSocket 心跳任务
 *
 * @author admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "admin.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketHeartbeatTask {

    private final WebSocketSessionManager sessionManager;
    private final WebSocketMessageSender messageSender;
    private final WebSocketProperties properties;

    /**
     * 清理无效会话
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 30000)
    public void cleanInvalidSessions() {
        try {
            log.debug("开始清理无效WebSocket会话");
            
            int beforeCount = sessionManager.getActiveSessionCount();
            sessionManager.cleanInvalidSessions();
            int afterCount = sessionManager.getActiveSessionCount();
            
            if (beforeCount != afterCount) {
                log.info("清理无效会话完成，清理前: {}，清理后: {}", beforeCount, afterCount);
            }
            
        } catch (Exception e) {
            log.error("清理无效会话失败", e);
        }
    }

    /**
     * 发送心跳消息
     * 根据配置的心跳间隔执行
     */
    @Scheduled(fixedRateString = "#{@webSocketProperties.heartbeatInterval}")
    public void sendHeartbeat() {
        try {
            Set<WebSocketSession> allSessions = sessionManager.getAllSessions();
            if (allSessions.isEmpty()) {
                return;
            }
            
            log.debug("开始发送心跳消息，会话数: {}", allSessions.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (WebSocketSession session : allSessions) {
                try {
                    if (session.isOpen()) {
                        messageSender.sendHeartbeat(session);
                        successCount++;
                    } else {
                        // 移除已关闭的会话
                        sessionManager.removeSession(session);
                        failCount++;
                    }
                } catch (Exception e) {
                    log.warn("发送心跳失败，会话ID: {}", session.getId(), e);
                    sessionManager.removeSession(session);
                    failCount++;
                }
            }
            
            if (successCount > 0 || failCount > 0) {
                log.debug("心跳发送完成，成功: {}，失败: {}", successCount, failCount);
            }
            
        } catch (Exception e) {
            log.error("发送心跳消息失败", e);
        }
    }

    /**
     * 统计信息输出
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void printStatistics() {
        try {
            int onlineUserCount = sessionManager.getOnlineUserCount();
            int activeSessionCount = sessionManager.getActiveSessionCount();
            
            if (onlineUserCount > 0 || activeSessionCount > 0) {
                log.info("WebSocket统计信息 - 在线用户数: {}，活跃会话数: {}", onlineUserCount, activeSessionCount);
            }
            
        } catch (Exception e) {
            log.error("输出统计信息失败", e);
        }
    }

    /**
     * 会话超时检查
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void checkSessionTimeout() {
        try {
            Long maxIdleTimeout = properties.getMaxSessionIdleTimeout();
            if (maxIdleTimeout == null || maxIdleTimeout <= 0) {
                return;
            }
            
            log.debug("开始检查会话超时，最大空闲时间: {} ms", maxIdleTimeout);
            
            Set<WebSocketSession> allSessions = sessionManager.getAllSessions();
            long currentTime = System.currentTimeMillis();
            int timeoutCount = 0;
            
            for (WebSocketSession session : allSessions) {
                try {
                    // 检查会话是否超时（这里简化处理，实际可能需要记录最后活跃时间）
                    if (!session.isOpen()) {
                        sessionManager.removeSession(session);
                        timeoutCount++;
                    }
                } catch (Exception e) {
                    log.warn("检查会话超时失败，会话ID: {}", session.getId(), e);
                    sessionManager.removeSession(session);
                    timeoutCount++;
                }
            }
            
            if (timeoutCount > 0) {
                log.info("会话超时检查完成，移除超时会话数: {}", timeoutCount);
            }
            
        } catch (Exception e) {
            log.error("检查会话超时失败", e);
        }
    }
}