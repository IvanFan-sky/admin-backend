package com.admin.module.infra.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传状态枚举
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
@AllArgsConstructor
public enum FileUploadStatusEnum {

    /**
     * 上传中
     */
    UPLOADING(1, "上传中"),

    /**
     * 上传完成
     */
    COMPLETED(2, "上传完成"),

    /**
     * 上传失败
     */
    FAILED(3, "上传失败");

    private final Integer code;
    private final String name;

    public static FileUploadStatusEnum fromCode(Integer code) {
        for (FileUploadStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的上传状态: " + code);
    }
}