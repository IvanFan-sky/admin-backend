package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件分片信息数据对象
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_file_chunk")
public class FileChunkDO extends BaseEntity {

    /**
     * 分片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联文件ID
     */
    private Long fileId;

    /**
     * 上传会话ID
     */
    private String uploadId;

    /**
     * 分片序号（从1开始）
     */
    private Integer chunkNumber;

    /**
     * 分片大小（字节）
     */
    private Long chunkSize;

    /**
     * 分片MD5哈希值
     */
    private String chunkHash;

    /**
     * 分片ETag（MinIO返回的标识）
     */
    private String etag;

    /**
     * 上传状态
     * 0-未上传 1-上传完成 2-上传失败
     */
    private Integer uploadStatus;

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 上传开始时间
     */
    private java.time.LocalDateTime uploadStartTime;

    /**
     * 上传完成时间
     */
    private java.time.LocalDateTime uploadEndTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 删除标识
     * 0-未删除，1-已删除
     */
    @com.baomidou.mybatisplus.annotation.TableField("deleted")
    @com.baomidou.mybatisplus.annotation.TableLogic
    private Integer deleted;
}
