package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件访问审计日志 DO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@TableName("sys_file_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class FileAuditLogDO extends BaseDO {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 操作类型：UPLOAD, DOWNLOAD, DELETE, VIEW, SHARE
     */
    private String operation;

    /**
     * 操作结果：SUCCESS, FAILED
     */
    private String result;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作耗时（毫秒）
     */
    private Long duration;

    /**
     * 文件大小（用于统计）
     */
    private Long fileSize;

    /**
     * 错误信息（操作失败时）
     */
    private String errorMessage;

    /**
     * 操作详情（JSON格式）
     */
    private String operationDetails;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 请求ID（用于关联请求链路）
     */
    private String requestId;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
}