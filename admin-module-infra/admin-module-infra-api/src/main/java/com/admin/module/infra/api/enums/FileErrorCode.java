package com.admin.module.infra.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件错误码枚举
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum FileErrorCode {

    // 基础文件错误 F001-F099
    FILE_NOT_FOUND("F001", "文件不存在"),
    FILE_NOT_READABLE("F002", "文件不可读"),
    FILE_ALREADY_EXISTS("F003", "文件已存在"),
    FILE_IS_EMPTY("F004", "文件为空"),
    FILE_TOO_LARGE("F005", "文件过大，超出最大限制"),
    FILE_TOO_SMALL("F006", "文件过小"),

    // 文件类型错误 F101-F199
    UNSUPPORTED_FILE_TYPE("F101", "不支持的文件类型"),
    INVALID_FILE_FORMAT("F102", "无效的文件格式"),
    FILE_TYPE_MISMATCH("F103", "文件类型与扩展名不匹配"),
    CORRUPTED_FILE("F104", "文件已损坏"),

    // 存储相关错误 F201-F299
    STORAGE_FAILURE("F201", "存储服务异常"),
    STORAGE_NOT_AVAILABLE("F202", "存储服务不可用"),
    STORAGE_QUOTA_EXCEEDED("F203", "存储配额已满"),
    STORAGE_PERMISSION_DENIED("F204", "存储权限不足"),
    STORAGE_CONNECTION_TIMEOUT("F205", "存储连接超时"),

    // 上传相关错误 F301-F399
    UPLOAD_FAILURE("F301", "文件上传失败"),
    UPLOAD_INTERRUPTED("F302", "文件上传中断"),
    CHUNK_UPLOAD_FAILURE("F303", "分片上传失败"),
    CHUNK_MISSING("F304", "文件分片缺失"),
    CHUNK_ORDER_ERROR("F305", "文件分片顺序错误"),
    UPLOAD_SESSION_EXPIRED("F306", "上传会话已过期"),
    UPLOAD_SESSION_NOT_FOUND("F307", "上传会话不存在"),

    // 下载相关错误 F401-F499
    DOWNLOAD_FAILURE("F401", "文件下载失败"),
    DOWNLOAD_PERMISSION_DENIED("F402", "文件下载权限不足"),
    DOWNLOAD_LINK_EXPIRED("F403", "下载链接已过期"),
    DOWNLOAD_INTERRUPTED("F404", "文件下载中断"),

    // 安全相关错误 F501-F599
    FILE_SECURITY_SCAN_FAILED("F501", "文件安全扫描失败"),
    MALICIOUS_FILE_DETECTED("F502", "检测到恶意文件"),
    VIRUS_DETECTED("F503", "检测到病毒"),
    SENSITIVE_INFO_DETECTED("F504", "文件包含敏感信息"),
    FILE_ACCESS_DENIED("F505", "文件访问被拒绝"),

    // 业务逻辑错误 F601-F699
    FILE_IN_USE("F601", "文件正在使用中"),
    FILE_PROCESSING("F602", "文件正在处理中"),
    DUPLICATE_FILE("F603", "文件重复"),
    INVALID_OPERATION("F604", "无效的操作"),
    BUSINESS_CONSTRAINT_VIOLATION("F605", "违反业务约束"),

    // 系统错误 F701-F799
    SYSTEM_ERROR("F701", "系统内部错误"),
    CONFIG_ERROR("F702", "配置错误"),
    RESOURCE_EXHAUSTED("F703", "系统资源不足"),
    SERVICE_UNAVAILABLE("F704", "服务不可用"),
    RATE_LIMIT_EXCEEDED("F705", "请求频率超出限制");

    private final String code;
    private final String message;

    /**
     * 根据错误码获取枚举
     */
    public static FileErrorCode fromCode(String code) {
        for (FileErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("未知的文件错误码: " + code);
    }
}