package com.admin.module.infra.api.enums;

import lombok.Getter;

/**
 * 文件上传状态枚举
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum FileUploadStatusEnum {

    /**
     * 上传中
     */
    UPLOADING(0, "上传中"),

    /**
     * 上传完成
     */
    COMPLETED(1, "上传完成"),

    /**
     * 上传失败
     */
    FAILED(2, "上传失败"),

    /**
     * 已删除
     */
    DELETED(3, "已删除");

    private final Integer code;
    private final String message;

    FileUploadStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据代码获取枚举
     * 
     * @param code 状态代码
     * @return 枚举值
     */
    public static FileUploadStatusEnum getByCode(Integer code) {
        for (FileUploadStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否为最终状态
     * 
     * @return true-最终状态 false-中间状态
     */
    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED || this == DELETED;
    }
}
