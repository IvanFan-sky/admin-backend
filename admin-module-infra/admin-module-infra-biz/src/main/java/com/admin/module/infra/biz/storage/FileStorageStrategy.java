package com.admin.module.infra.biz.storage;

import java.io.InputStream;
import java.util.List;

/**
 * 文件存储策略接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileStorageStrategy {

    /**
     * 获取存储类型
     *
     * @return 存储类型
     */
    String getStorageType();

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键（文件路径）
     * @param inputStream 文件流
     * @param contentType 文件类型
     * @param fileSize 文件大小
     * @return 文件访问URL
     */
    String uploadFile(String bucketName, String objectKey, InputStream inputStream, 
                     String contentType, long fileSize);

    /**
     * 分片上传初始化
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param contentType 文件类型
     * @return 上传ID
     */
    String initiateMultipartUpload(String bucketName, String objectKey, String contentType);

    /**
     * 上传分片
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param uploadId 上传ID
     * @param partNumber 分片号
     * @param inputStream 分片流
     * @param partSize 分片大小
     * @return 分片ETag
     */
    String uploadPart(String bucketName, String objectKey, String uploadId, 
                     int partNumber, InputStream inputStream, long partSize);

    /**
     * 完成分片上传
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param uploadId 上传ID
     * @param partETags 分片ETag列表
     * @return 文件访问URL
     */
    String completeMultipartUpload(String bucketName, String objectKey, String uploadId, 
                                  List<PartETag> partETags);

    /**
     * 取消分片上传
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param uploadId 上传ID
     */
    void abortMultipartUpload(String bucketName, String objectKey, String uploadId);

    /**
     * 下载文件
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 文件流
     */
    InputStream downloadFile(String bucketName, String objectKey);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 是否删除成功
     */
    boolean deleteFile(String bucketName, String objectKey);

    /**
     * 批量删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectKeys 对象键列表
     * @return 删除结果
     */
    boolean deleteFiles(String bucketName, List<String> objectKeys);

    /**
     * 获取文件信息
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 文件信息
     */
    FileObjectInfo getFileInfo(String bucketName, String objectKey);

    /**
     * 检查文件是否存在
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 是否存在
     */
    boolean fileExists(String bucketName, String objectKey);

    /**
     * 生成预签名上传URL
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param expirationSeconds 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUploadUrl(String bucketName, String objectKey, int expirationSeconds);

    /**
     * 生成预签名下载URL
     *
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param expirationSeconds 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedDownloadUrl(String bucketName, String objectKey, int expirationSeconds);

    /**
     * 检查并创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    boolean ensureBucketExists(String bucketName);

    /**
     * 分片ETag信息
     */
    class PartETag {
        private int partNumber;
        private String etag;

        public PartETag(int partNumber, String etag) {
            this.partNumber = partNumber;
            this.etag = etag;
        }

        public int getPartNumber() {
            return partNumber;
        }

        public String getEtag() {
            return etag;
        }
    }

    /**
     * 文件对象信息
     */
    class FileObjectInfo {
        private String objectKey;
        private long size;
        private String contentType;
        private String etag;
        private java.time.LocalDateTime lastModified;

        public FileObjectInfo(String objectKey, long size, String contentType, 
                             String etag, java.time.LocalDateTime lastModified) {
            this.objectKey = objectKey;
            this.size = size;
            this.contentType = contentType;
            this.etag = etag;
            this.lastModified = lastModified;
        }

        // Getters
        public String getObjectKey() { return objectKey; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
        public String getEtag() { return etag; }
        public java.time.LocalDateTime getLastModified() { return lastModified; }
    }
}