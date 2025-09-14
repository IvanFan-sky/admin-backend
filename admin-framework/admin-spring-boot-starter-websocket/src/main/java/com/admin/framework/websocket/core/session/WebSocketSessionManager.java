package com.admin.framework.websocket.core.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 会话管理器
 *
 * @author admin
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    /**
     * 用户ID -> WebSocket会话集合
     */
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    /**
     * 会话ID -> 用户ID
     */
    private final Map<String, Long> sessionUsers = new ConcurrentHashMap<>();

    /**
     * 所有活跃会话
     */
    private final Set<WebSocketSession> allSessions = new CopyOnWriteArraySet<>();

    /**
     * 添加会话
     *
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public void addSession(Long userId, WebSocketSession session) {
        if (userId == null || session == null) {
            return;
        }

        // 添加到用户会话映射
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        
        // 添加到会话用户映射
        sessionUsers.put(session.getId(), userId);
        
        // 添加到所有会话集合
        allSessions.add(session);
        
        log.debug("用户 {} 的会话 {} 已添加，当前在线用户数: {}", userId, session.getId(), userSessions.size());
    }

    /**
     * 移除会话
     *
     * @param session WebSocket会话
     */
    public void removeSession(WebSocketSession session) {
        if (session == null) {
            return;
        }

        String sessionId = session.getId();
        Long userId = sessionUsers.remove(sessionId);
        
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                // 如果用户没有其他会话，移除用户映射
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        
        allSessions.remove(session);
        
        log.debug("会话 {} 已移除，用户: {}，当前在线用户数: {}", sessionId, userId, userSessions.size());
    }

    /**
     * 获取用户的所有会话
     *
     * @param userId 用户ID
     * @return 会话集合
     */
    public Set<WebSocketSession> getUserSessions(Long userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    /**
     * 获取会话对应的用户ID
     *
     * @param session WebSocket会话
     * @return 用户ID
     */
    public Long getSessionUserId(WebSocketSession session) {
        return sessionUsers.get(session.getId());
    }

    /**
     * 获取所有活跃会话
     *
     * @return 会话集合
     */
    public Set<WebSocketSession> getAllSessions() {
        return new HashSet<>(allSessions);
    }

    /**
     * 获取在线用户ID列表
     *
     * @return 用户ID集合
     */
    public Set<Long> getOnlineUserIds() {
        return new HashSet<>(userSessions.keySet());
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 获取活跃会话数量
     *
     * @return 活跃会话数量
     */
    public int getActiveSessionCount() {
        return allSessions.size();
    }

    /**
     * 清理无效会话
     */
    public void cleanInvalidSessions() {
        Iterator<WebSocketSession> iterator = allSessions.iterator();
        while (iterator.hasNext()) {
            WebSocketSession session = iterator.next();
            if (!session.isOpen()) {
                removeSession(session);
            }
        }
    }

    /**
     * 获取会话统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getSessionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("onlineUserCount", getOnlineUserCount());
        stats.put("activeSessionCount", getActiveSessionCount());
        stats.put("avgSessionsPerUser", getOnlineUserCount() > 0 ? 
            (double) getActiveSessionCount() / getOnlineUserCount() : 0);
        return stats;
    }
}