package com.admin.framework.minio.service;

import com.admin.common.result.minio.ObjectInfo;
import com.admin.common.result.minio.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * MinIO服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface MinioService {

    /**
     * 创建存储桶
     * 
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    boolean createBucket(String bucketName);

    /**
     * 检查存储桶是否存在
     * 
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);

    /**
     * 删除存储桶
     * 
     * @param bucketName 存储桶名称
     * @return 是否删除成功
     */
    boolean deleteBucket(String bucketName);

    /**
     * 上传文件
     * 
     * @param file 文件
     * @return 上传结果
     */
    UploadResult uploadFile(MultipartFile file);

    /**
     * 上传文件到指定存储桶
     * 
     * @param bucketName 存储桶名称
     * @param file 文件
     * @return 上传结果
     */
    UploadResult uploadFile(String bucketName, MultipartFile file);

    /**
     * 上传文件流
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 输入流
     * @param size 文件大小
     * @param contentType 内容类型
     * @return 上传结果
     */
    UploadResult uploadFile(String bucketName, String objectName, InputStream inputStream, 
                           long size, String contentType);

    /**
     * 下载文件
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件输入流
     */
    InputStream downloadFile(String bucketName, String objectName);

    /**
     * 下载文件（使用默认存储桶）
     * 
     * @param objectName 对象名称
     * @return 文件输入流
     */
    InputStream downloadFile(String objectName);

    /**
     * 获取预签名下载URL
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedDownloadUrl(String bucketName, String objectName, int expiry);

    /**
     * 获取预签名下载URL（使用默认存储桶）
     * 
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedDownloadUrl(String objectName, int expiry);

    /**
     * 获取预签名上传URL
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedUploadUrl(String bucketName, String objectName, int expiry);

    /**
     * 删除文件
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    boolean deleteFile(String bucketName, String objectName);

    /**
     * 删除文件（使用默认存储桶）
     * 
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    boolean deleteFile(String objectName);

    /**
     * 批量删除文件
     * 
     * @param bucketName 存储桶名称
     * @param objectNames 对象名称列表
     * @return 删除结果
     */
    List<String> deleteFiles(String bucketName, List<String> objectNames);

    /**
     * 获取文件信息
     * 
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件信息
     */
    ObjectInfo getObjectInfo(String bucketName, String objectName);

    /**
     * 列出存储桶中的对象
     * 
     * @param bucketName 存储桶名称
     * @param prefix 前缀
     * @param maxKeys 最大返回数量
     * @return 对象列表
     */
    List<ObjectInfo> listObjects(String bucketName, String prefix, int maxKeys);

    /**
     * 复制文件
     * 
     * @param sourceBucket 源存储桶
     * @param sourceObject 源对象名称
     * @param targetBucket 目标存储桶
     * @param targetObject 目标对象名称
     * @return 是否复制成功
     */
    boolean copyObject(String sourceBucket, String sourceObject, String targetBucket, String targetObject);
}
