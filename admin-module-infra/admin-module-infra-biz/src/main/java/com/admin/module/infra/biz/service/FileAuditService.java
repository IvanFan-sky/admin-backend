package com.admin.module.infra.biz.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.biz.dal.dataobject.FileAuditLogDO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文件审计服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileAuditService {

    /**
     * 记录审计日志（同步）
     *
     * @param auditLog 审计日志
     */
    void recordAuditLog(FileAuditLogDO auditLog);

    /**
     * 记录审计日志（异步）
     *
     * @param auditLog 审计日志
     */
    void recordAuditLogAsync(FileAuditLogDO auditLog);

    /**
     * 分页查询审计日志
     *
     * @param pageDTO 查询参数
     * @return 分页结果
     */
    PageResult<FileAuditLogDO> getAuditLogPage(FileAuditLogPageDTO pageDTO);

    /**
     * 根据文件ID查询审计日志
     *
     * @param fileId 文件ID
     * @return 审计日志列表
     */
    List<FileAuditLogDO> getAuditLogsByFileId(Long fileId);

    /**
     * 根据用户ID查询审计日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<FileAuditLogDO> getAuditLogsByUserId(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取操作统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Object> getOperationStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户活跃度统计
     *
     * @param days 统计天数
     * @return 统计结果
     */
    Map<String, Object> getUserActivityStatistics(int days);

    /**
     * 清理过期审计日志
     *
     * @param retentionDays 保留天数
     * @return 清理数量
     */
    int cleanupExpiredAuditLogs(int retentionDays);

    /**
     * 检测异常操作
     *
     * @param hours 检测时间范围（小时）
     * @return 异常操作列表
     */
    List<FileAuditLogDO> detectAnomalousOperations(int hours);

    /**
     * 审计日志分页查询DTO
     */
    class FileAuditLogPageDTO extends FilePageDTO {
        private Long fileId;
        private Long userId;
        private String username;
        private String operation;
        private String result;
        private String clientIp;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        // Getters and Setters
        public Long getFileId() { return fileId; }
        public void setFileId(Long fileId) { this.fileId = fileId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getClientIp() { return clientIp; }
        public void setClientIp(String clientIp) { this.clientIp = clientIp; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }
}