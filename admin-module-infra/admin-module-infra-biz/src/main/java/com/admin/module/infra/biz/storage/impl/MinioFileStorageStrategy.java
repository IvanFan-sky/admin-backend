package com.admin.module.infra.biz.storage.impl;

import com.admin.module.infra.biz.config.FileStorageConfig;
import com.admin.module.infra.biz.storage.FileStorageStrategy;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MinIO 文件存储策略实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@ConditionalOnProperty(prefix = "admin.file.storage", name = "default-type", havingValue = "minio", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorageStrategy implements FileStorageStrategy {

    private final FileStorageConfig fileStorageConfig;
    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        FileStorageConfig.MinioConfig config = fileStorageConfig.getMinio();
        
        this.minioClient = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKey(), config.getSecretKey())
                .region(config.getRegion())
                .build();
                
        // 设置超时时间
        this.minioClient.setTimeout(
                config.getConnectTimeout(),
                config.getWriteTimeout(),
                config.getReadTimeout()
        );

        log.info("MinIO 客户端初始化完成，endpoint: {}", config.getEndpoint());

        // 自动创建默认存储桶
        if (config.isAutoCreateBucket()) {
            ensureBucketExists(config.getDefaultBucket());
        }
    }

    @Override
    public String getStorageType() {
        return "minio";
    }

    @Override
    public String uploadFile(String bucketName, String objectKey, InputStream inputStream, 
                            String contentType, long fileSize) {
        try {
            ensureBucketExists(bucketName);

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(inputStream, fileSize, -1)
                    .contentType(contentType)
                    .build();

            ObjectWriteResponse response = minioClient.putObject(args);
            
            log.info("文件上传成功，bucket: {}, object: {}, etag: {}", 
                    bucketName, objectKey, response.etag());

            return generateFileUrl(bucketName, objectKey);
            
        } catch (Exception e) {
            log.error("MinIO 上传文件失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String initiateMultipartUpload(String bucketName, String objectKey, String contentType) {
        try {
            ensureBucketExists(bucketName);

            CreateMultipartUploadResponse response = minioClient.createMultipartUpload(
                    CreateMultipartUploadArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

            log.info("初始化分片上传，bucket: {}, object: {}, uploadId: {}", 
                    bucketName, objectKey, response.result().uploadId());

            return response.result().uploadId();
            
        } catch (Exception e) {
            log.error("初始化分片上传失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("初始化分片上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadPart(String bucketName, String objectKey, String uploadId, 
                            int partNumber, InputStream inputStream, long partSize) {
        try {
            UploadPartResponse response = minioClient.uploadPart(
                    UploadPartArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .uploadId(uploadId)
                            .partNumber(partNumber)
                            .stream(inputStream, partSize, -1)
                            .build()
            );

            log.debug("上传分片成功，bucket: {}, object: {}, partNumber: {}, etag: {}", 
                    bucketName, objectKey, partNumber, response.etag());

            return response.etag();
            
        } catch (Exception e) {
            log.error("上传分片失败，bucket: {}, object: {}, partNumber: {}", 
                    bucketName, objectKey, partNumber, e);
            throw new RuntimeException("上传分片失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String completeMultipartUpload(String bucketName, String objectKey, String uploadId, 
                                         List<PartETag> partETags) {
        try {
            // 转换为 MinIO 的 Part 对象
            io.minio.messages.Part[] parts = partETags.stream()
                    .map(partETag -> new io.minio.messages.Part(partETag.getPartNumber(), partETag.getEtag()))
                    .toArray(io.minio.messages.Part[]::new);

            ObjectWriteResponse response = minioClient.completeMultipartUpload(
                    CompleteMultipartUploadArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .uploadId(uploadId)
                            .parts(parts)
                            .build()
            );

            log.info("完成分片上传，bucket: {}, object: {}, etag: {}", 
                    bucketName, objectKey, response.etag());

            return generateFileUrl(bucketName, objectKey);
            
        } catch (Exception e) {
            log.error("完成分片上传失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("完成分片上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void abortMultipartUpload(String bucketName, String objectKey, String uploadId) {
        try {
            minioClient.abortMultipartUpload(
                    AbortMultipartUploadArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .uploadId(uploadId)
                            .build()
            );

            log.info("取消分片上传，bucket: {}, object: {}, uploadId: {}", 
                    bucketName, objectKey, uploadId);
                    
        } catch (Exception e) {
            log.error("取消分片上传失败，bucket: {}, object: {}, uploadId: {}", 
                    bucketName, objectKey, uploadId, e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectKey) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

            log.info("下载文件，bucket: {}, object: {}", bucketName, objectKey);
            return response;
            
        } catch (Exception e) {
            log.error("下载文件失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("下载文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String bucketName, String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

            log.info("删除文件成功，bucket: {}, object: {}", bucketName, objectKey);
            return true;
            
        } catch (Exception e) {
            log.error("删除文件失败，bucket: {}, object: {}", bucketName, objectKey, e);
            return false;
        }
    }

    @Override
    public boolean deleteFiles(String bucketName, List<String> objectKeys) {
        try {
            List<DeleteObject> objects = objectKeys.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build()
            );

            boolean hasError = false;
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                if (error != null) {
                    log.error("删除文件失败，bucket: {}, object: {}, error: {}", 
                            bucketName, error.objectName(), error.message());
                    hasError = true;
                }
            }

            log.info("批量删除文件完成，bucket: {}, 删除数量: {}, 是否有错误: {}", 
                    bucketName, objectKeys.size(), hasError);

            return !hasError;
            
        } catch (Exception e) {
            log.error("批量删除文件失败，bucket: {}", bucketName, e);
            return false;
        }
    }

    @Override
    public FileObjectInfo getFileInfo(String bucketName, String objectKey) {
        try {
            StatObjectResponse response = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

            LocalDateTime lastModified = response.lastModified() != null 
                    ? LocalDateTime.ofInstant(response.lastModified().toInstant(), ZoneOffset.systemDefault())
                    : LocalDateTime.now();

            return new FileObjectInfo(
                    objectKey,
                    response.size(),
                    response.contentType(),
                    response.etag(),
                    lastModified
            );
            
        } catch (Exception e) {
            log.error("获取文件信息失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("获取文件信息失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String bucketName, String objectKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String objectKey, int expirationSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expirationSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成预签名上传URL失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("生成预签名上传URL失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String generatePresignedDownloadUrl(String bucketName, String objectKey, int expirationSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expirationSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成预签名下载URL失败，bucket: {}, object: {}", bucketName, objectKey, e);
            throw new RuntimeException("生成预签名下载URL失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .region(fileStorageConfig.getMinio().getRegion())
                                .build()
                );
                log.info("创建存储桶成功，bucket: {}", bucketName);
            }

            return true;
            
        } catch (Exception e) {
            log.error("检查/创建存储桶失败，bucket: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 生成文件访问URL
     */
    private String generateFileUrl(String bucketName, String objectKey) {
        FileStorageConfig.MinioConfig config = fileStorageConfig.getMinio();
        String protocol = config.isSecure() ? "https" : "http";
        return String.format("%s://%s/%s/%s", 
                protocol, 
                config.getEndpoint().replace("http://", "").replace("https://", ""),
                bucketName, 
                objectKey);
    }
}