package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件分片信息
 * <p>
 * 该类用于记录单个文件分片的详细信息，包括分片序号、ETag标识和上传时间戳。
 * 主要用于分片上传过程中跟踪每个分片的状态，支持分片完整性校验和上传进度管理。
 * 
 * <h3>主要功能：</h3>
 * <ul>
 *   <li>标识分片序号，用于分片排序和合并</li>
 *   <li>存储ETag值，用于分片完整性校验</li>
 *   <li>记录上传时间，用于上传进度跟踪</li>
 * </ul>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>分片上传成功后记录分片信息</li>
 *   <li>断点续传时检查已上传分片</li>
 *   <li>分片合并前进行完整性校验</li>
 *   <li>上传进度统计和展示</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * ChunkInfo chunkInfo = new ChunkInfo();
 * chunkInfo.setChunkNumber(1);
 * chunkInfo.setEtag("d41d8cd98f00b204e9800998ecf8427e");
 * chunkInfo.setUploadTime(System.currentTimeMillis());
 * }</pre>
 * 
 * <h3>注意事项：</h3>
 * <ul>
 *   <li>分片序号从1开始，按顺序递增</li>
 *   <li>ETag值由存储服务提供，用于唯一标识分片</li>
 *   <li>上传时间使用毫秒级时间戳</li>
 * </ul>
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 * @see ChunkUploadSessionVO
 */
@Data
@Schema(description = "文件分片信息")
public class ChunkInfoVO {
    
    /**
     * 分片序号
     */
    @Schema(description = "分片序号，从1开始", example = "1", minimum = "1")
    private Integer chunkNumber;
    
    /**
     * 分片ETag，用于标识分片的唯一性
     */
    @Schema(description = "分片ETag，用于标识分片的唯一性", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String etag;
    
    /**
     * 上传时间戳
     */
    @Schema(description = "上传时间戳（毫秒）", example = "1704067200000")
    private Long uploadTime;
}