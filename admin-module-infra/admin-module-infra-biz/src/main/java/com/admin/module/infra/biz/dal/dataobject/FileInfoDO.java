package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息数据对象
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_file_info")
public class FileInfoDO extends BaseEntity {

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件原始名称
     */
    private String originalFileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件MD5哈希值
     */
    private String fileHash;

    /**
     * 存储类型（MINIO, OSS, LOCAL等）
     */
    private String storageType;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 上传状态
     * 0-上传中 1-上传完成 2-上传失败 3-已删除
     */
    private Integer uploadStatus;

    /**
     * 是否为分片上传
     */
    private Boolean isChunked;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 上传会话ID（分片上传使用）
     */
    private String uploadId;

    /**
     * 访问URL
     */
    private String accessUrl;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    private String uploadUserName;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 最后下载时间
     */
    private java.time.LocalDateTime lastDownloadTime;

    /**
     * 文件标签
     */
    private String tags;

    @TableLogic
    private Integer deleted;


}
