package com.admin.module.infra.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件分片 DO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@TableName("sys_file_chunk")
@Data
public class FileChunkDO {

    /**
     * 分片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上传会话ID
     */
    private String uploadId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 分片序号（从1开始）
     */
    private Integer chunkNumber;

    /**
     * 分片大小（字节）
     */
    private Long chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 文件完整哈希值
     */
    private String fileHash;

    /**
     * 分片哈希值
     */
    private String chunkHash;

    /**
     * 分片存储键
     */
    private String chunkKey;

    /**
     * 上传状态：1-上传中，2-上传完成，3-上传失败
     */
    private Integer uploadStatus;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务关联ID
     */
    private String businessId;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}