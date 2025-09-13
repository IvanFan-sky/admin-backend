package com.admin.common.result.minio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * MinIO对象信息封装类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectInfo {

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * ETag值
     */
    private String etag;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 最后修改时间
     */
    private ZonedDateTime lastModified;

    /**
     * 是否为目录
     */
    private Boolean isDir;

    /**
     * 用户元数据
     */
    private Map<String, String> userMetadata;

    /**
     * 构造对象信息
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param size 文件大小
     * @param etag ETag值
     * @param contentType 内容类型
     * @param lastModified 最后修改时间
     * @param isDir 是否为目录
     * @return 对象信息
     */
    public static ObjectInfo of(String bucketName, String objectName, Long size, String etag, 
                              String contentType, ZonedDateTime lastModified, Boolean isDir) {
        return new ObjectInfo(bucketName, objectName, size, etag, contentType, lastModified, isDir, null);
    }

    /**
     * 构造对象信息
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param size 文件大小
     * @param etag ETag值
     * @param contentType 内容类型
     * @param lastModified 最后修改时间
     * @param isDir 是否为目录
     * @param userMetadata 用户元数据
     * @return 对象信息
     */
    public static ObjectInfo of(String bucketName, String objectName, Long size, String etag, String contentType, 
                              ZonedDateTime lastModified, Boolean isDir, Map<String, String> userMetadata) {
        return new ObjectInfo(bucketName, objectName, size, etag, contentType, lastModified, isDir, userMetadata);
    }
}
