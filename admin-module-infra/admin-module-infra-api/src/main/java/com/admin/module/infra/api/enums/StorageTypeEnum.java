package com.admin.module.infra.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储类型枚举
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum StorageTypeEnum {

    /**
     * MinIO 对象存储
     */
    MINIO("minio", "MinIO对象存储"),

    /**
     * 阿里云 OSS
     */
    OSS("oss", "阿里云OSS"),

    /**
     * 腾讯云 COS
     */
    COS("cos", "腾讯云COS"),

    /**
     * AWS S3
     */
    S3("s3", "AWS S3");

    private final String code;
    private final String name;

    public static StorageTypeEnum fromCode(String code) {
        for (StorageTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的存储类型: " + code);
    }
}