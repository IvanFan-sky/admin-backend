package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.module.infra.api.dto.ChunkUploadDTO;
import com.admin.module.infra.api.dto.ChunkUploadInitDTO;
import com.admin.module.infra.api.service.ChunkUploadService;
import com.admin.module.infra.api.vo.ChunkUploadInitVO;
import com.admin.module.infra.api.vo.ChunkUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 分片上传控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "分片上传管理", description = "大文件分片上传、断点续传相关接口")
@Slf4j
@RestController
@RequestMapping("/infra/chunk-upload")
@RequiredArgsConstructor
public class ChunkUploadController {

    private final ChunkUploadService chunkUploadService;

    @Operation(summary = "初始化分片上传", description = "开始分片上传前的初始化，检查文件是否存在（秒传）、创建上传会话")
    @PostMapping("/init")
    public R<ChunkUploadInitVO> initChunkUpload(@Valid @RequestBody ChunkUploadInitDTO initDTO) {
        log.info("初始化分片上传请求: fileName={}, fileSize={}", initDTO.getFileName(), initDTO.getFileSize());
        
        ChunkUploadInitVO result = chunkUploadService.initChunkUpload(initDTO);
        
        log.info("初始化分片上传响应: uploadId={}, needUpload={}", 
                result.getUploadId(), result.getNeedUpload());
        
        return R.ok(result);
    }

    @Operation(summary = "上传文件分片", description = "上传单个文件分片，支持断点续传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<ChunkUploadVO> uploadChunk(@Valid @ModelAttribute ChunkUploadDTO chunkDTO) {
        log.info("上传分片请求: uploadId={}, chunkNumber={}/{}", 
                chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), chunkDTO.getTotalChunks());
        
        ChunkUploadVO result = chunkUploadService.uploadChunk(chunkDTO);
        
        log.info("上传分片响应: uploadId={}, progress={}%, completed={}", 
                result.getUploadId(), result.getProgress(), result.getCompleted());
        
        return R.ok(result);
    }

    @Operation(summary = "完成分片上传", description = "手动触发分片合并，完成文件上传")
    @PostMapping("/complete/{uploadId}")
    public R<ChunkUploadVO> completeChunkUpload(
            @Parameter(description = "上传会话ID", required = true)
            @PathVariable String uploadId) {
        
        log.info("完成分片上传请求: uploadId={}", uploadId);
        
        ChunkUploadVO result = chunkUploadService.completeChunkUpload(uploadId);
        
        log.info("完成分片上传响应: uploadId={}, fileId={}", uploadId, result.getFileId());
        
        return R.ok(result);
    }

    @Operation(summary = "取消分片上传", description = "取消分片上传，清理已上传的分片和相关资源")
    @DeleteMapping("/cancel/{uploadId}")
    public R<Void> cancelChunkUpload(
            @Parameter(description = "上传会话ID", required = true)
            @PathVariable String uploadId) {
        
        log.info("取消分片上传请求: uploadId={}", uploadId);
        
        chunkUploadService.cancelChunkUpload(uploadId);
        
        log.info("取消分片上传成功: uploadId={}", uploadId);
        
        return R.ok();
    }

    @Operation(summary = "获取上传进度", description = "查询分片上传的当前进度")
    @GetMapping("/progress/{uploadId}")
    public R<ChunkUploadVO> getUploadProgress(
            @Parameter(description = "上传会话ID", required = true)
            @PathVariable String uploadId) {
        
        log.debug("获取上传进度请求: uploadId={}", uploadId);
        
        ChunkUploadVO result = chunkUploadService.getUploadProgress(uploadId);
        
        log.debug("获取上传进度响应: uploadId={}, progress={}%", uploadId, result.getProgress());
        
        return R.ok(result);
    }

    @Operation(summary = "检查分片是否存在", description = "检查指定分片是否已上传，用于断点续传")
    @GetMapping("/check/{uploadId}/{chunkNumber}")
    public R<Boolean> checkChunkExists(
            @Parameter(description = "上传会话ID", required = true)
            @PathVariable String uploadId,
            @Parameter(description = "分片序号", required = true)
            @PathVariable Integer chunkNumber) {
        
        log.debug("检查分片存在请求: uploadId={}, chunkNumber={}", uploadId, chunkNumber);
        
        boolean exists = chunkUploadService.isChunkExists(uploadId, chunkNumber);
        
        log.debug("检查分片存在响应: uploadId={}, chunkNumber={}, exists={}", 
                uploadId, chunkNumber, exists);
        
        return R.ok(exists);
    }
}
