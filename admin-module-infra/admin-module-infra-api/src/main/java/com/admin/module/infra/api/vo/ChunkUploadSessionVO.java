package com.admin.module.infra.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分片上传会话信息
 * <p>
 * 该类用于记录分片上传过程中的会话信息，包含文件的基本属性、分片配置参数、
 * 业务关联信息以及上传用户信息等。主要用于支持大文件的分片上传和断点续传功能。
 * 
 * <h3>主要功能：</h3>
 * <ul>
 *   <li>记录文件基本信息（文件名、大小、MD5等）</li>
 *   <li>存储分片配置（分片大小、总分片数）</li>
 *   <li>关联业务信息（业务类型、业务ID）</li>
 *   <li>追踪上传用户信息</li>
 * </ul>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>大文件分片上传时创建上传会话</li>
 *   <li>断点续传时恢复上传状态</li>
 *   <li>上传进度跟踪和状态管理</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * ChunkUploadSession session = new ChunkUploadSession();
 * session.setFileName("example.pdf");
 * session.setFileSize(10485760L); // 10MB
 * session.setChunkSize(1048576L);  // 1MB per chunk
 * session.setTotalChunks(10);
 * session.setBusinessType("document");
 * session.setBusinessId("DOC_001");
 * }</pre>
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 * @see ChunkInfoVO
 * @see ChunkUploadCacheService
 */
@Data
@Schema(description = "分片上传会话信息")
public class ChunkUploadSessionVO {
    
    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "1001")
    private Long fileId;
    
    /**
     * 文件名称
     */
    @Schema(description = "文件名称", example = "example.pdf")
    private String fileName;
    
    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "10485760")
    private Long fileSize;
    
    /**
     * 文件MD5值，用于文件完整性校验
     */
    @Schema(description = "文件MD5值，用于文件完整性校验", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String fileMd5;
    
    /**
     * 文件MIME类型
     */
    @Schema(description = "文件MIME类型", example = "application/pdf")
    private String contentType;
    
    /**
     * 分片大小（字节）
     */
    @Schema(description = "分片大小（字节）", example = "1048576")
    private Long chunkSize;
    
    /**
     * 总分片数量
     */
    @Schema(description = "总分片数量", example = "10")
    private Integer totalChunks;
    
    /**
     * 业务类型，用于区分不同业务场景的文件上传
     */
    @Schema(description = "业务类型，用于区分不同业务场景的文件上传", example = "document")
    private String businessType;
    
    /**
     * 业务ID，关联具体的业务对象
     */
    @Schema(description = "业务ID，关联具体的业务对象", example = "DOC_001")
    private String businessId;
    
    /**
     * 文件标签，用于文件分类和检索
     */
    @Schema(description = "文件标签，用于文件分类和检索", example = "重要文档,合同")
    private String tags;
    
    /**
     * 备注信息
     */
    @Schema(description = "备注信息", example = "客户合同文件")
    private String remark;
    
    /**
     * 创建时间戳
     */
    @Schema(description = "创建时间戳", example = "1704067200000")
    private Long createTime;
    
    /**
     * 上传用户ID
     */
    @Schema(description = "上传用户ID", example = "user123")
    private String uploadUserId;
    
    /**
     * 上传用户名称
     */
    @Schema(description = "上传用户名称", example = "张三")
    private String uploadUserName;
}