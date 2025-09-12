package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息 DO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@TableName("sys_file_info")
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoDO extends BaseDO {

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件原始名称
     */
    private String fileName;

    /**
     * 文件存储键（包含路径）
     */
    private String fileKey;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件SHA256哈希值（用于去重）
     */
    private String fileHash;

    /**
     * 存储类型：MINIO, OSS
     */
    private String storageType;

    /**
     * 存储桶名称
     */
    private String storageBucket;

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 上传状态：1-上传中，2-上传完成，3-上传失败
     */
    private Integer uploadStatus;

    /**
     * 业务类型：avatar, document, import_template
     */
    private String businessType;

    /**
     * 业务关联ID
     */
    private String businessId;

    /**
     * 是否公开：0-私有，1-公开
     */
    private Integer isPublic;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    private String remark;
}