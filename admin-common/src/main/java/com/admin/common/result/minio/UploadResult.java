package com.admin.common.result.minio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MinIO文件上传结果封装类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {

    /**
     * 上传是否成功
     */
    private boolean success;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 构造成功结果
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param url 访问URL
     * @param size 文件大小
     * @param contentType 文件类型
     * @return 上传结果
     */
    public static UploadResult success(String bucketName, String objectName, String url, 
                                     Long size, String contentType) {
        return new UploadResult(true, "上传成功", bucketName, objectName, url, size, contentType);
    }

    /**
     * 构造成功结果
     * 
     * @param message 成功消息
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param url 访问URL
     * @param size 文件大小
     * @param contentType 文件类型
     * @return 上传结果
     */
    public static UploadResult success(String message, String bucketName, String objectName, 
                                     String url, Long size, String contentType) {
        return new UploadResult(true, message, bucketName, objectName, url, size, contentType);
    }

    /**
     * 构造失败结果
     * 
     * @param message 失败消息
     * @return 上传结果
     */
    public static UploadResult failure(String message) {
        return new UploadResult(false, message, null, null, null, null, null);
    }
}
