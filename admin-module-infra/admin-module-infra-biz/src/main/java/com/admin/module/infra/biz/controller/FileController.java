package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.infra.api.dto.FileChunkUploadDTO;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.vo.FileChunkUploadVO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.audit.FileAudit;
import com.admin.module.infra.biz.ratelimit.FileRateLimiter;
import com.admin.module.infra.biz.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件管理控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/admin-api/infra/file")
@RequiredArgsConstructor
@Validated
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    @FileRateLimiter(limitType = FileRateLimiter.LimitType.USER, timeWindow = 60, maxRequests = 20,
                    message = "文件上传过于频繁，每分钟最多上传20个文件")
    @FileAudit(operation = FileAudit.FileOperationType.UPLOAD, description = "用户上传文件")
    public R<FileUploadVO> uploadFile(@Valid FileUploadDTO uploadDTO) {
        FileUploadVO result = fileService.uploadFile(uploadDTO);
        return R.ok(result);
    }

    @PostMapping("/chunk/initiate")
    @Operation(summary = "初始化分片上传")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<String> initiateChunkUpload(
            @Parameter(description = "文件名") @RequestParam String fileName,
            @Parameter(description = "文件大小") @RequestParam Long fileSize,
            @Parameter(description = "文件类型") @RequestParam String contentType,
            @Parameter(description = "业务类型") @RequestParam(required = false) String businessType,
            @Parameter(description = "业务关联ID") @RequestParam(required = false) String businessId) {
        
        String uploadId = fileService.initiateChunkUpload(fileName, fileSize, contentType, businessType, businessId);
        return R.ok(uploadId);
    }

    @PostMapping(value = "/chunk/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "分片上传")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<FileChunkUploadVO> uploadChunk(@Valid FileChunkUploadDTO chunkUploadDTO) {
        FileChunkUploadVO result = fileService.uploadChunk(chunkUploadDTO);
        return R.ok(result);
    }

    @PostMapping("/chunk/complete")
    @Operation(summary = "完成分片上传")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<FileInfoVO> completeChunkUpload(@Parameter(description = "上传会话ID") @RequestParam String uploadId) {
        FileInfoVO result = fileService.completeChunkUpload(uploadId);
        return R.ok(result);
    }

    @PostMapping("/chunk/abort")
    @Operation(summary = "取消分片上传")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<Boolean> abortChunkUpload(@Parameter(description = "上传会话ID") @RequestParam String uploadId) {
        fileService.abortChunkUpload(uploadId);
        return R.ok(true);
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "下载文件")
    @PreAuthorize("@ss.hasPermission('infra:file:download')")
    public void downloadFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "是否内联显示") @RequestParam(defaultValue = "false") Boolean inline,
            @Parameter(description = "下载文件名") @RequestParam(required = false) String downloadName,
            HttpServletResponse response) {
        
        fileService.downloadFile(fileId, inline, downloadName, response);
    }

    @GetMapping("/view/{fileId}")
    @Operation(summary = "预览文件（内联显示）")
    public void viewFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            HttpServletResponse response) {
        
        fileService.downloadFile(fileId, true, null, response);
    }

    @DeleteMapping("/delete/{fileId}")
    @Operation(summary = "删除文件")
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public R<Boolean> deleteFile(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        boolean result = fileService.deleteFile(fileId);
        return R.ok(result);
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除文件")
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public R<Integer> deleteFiles(@Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds) {
        int deletedCount = fileService.deleteFiles(fileIds);
        return R.ok(deletedCount);
    }

    @GetMapping("/info/{fileId}")
    @Operation(summary = "获取文件信息")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<FileInfoVO> getFileInfo(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        FileInfoVO result = fileService.getFileInfo(fileId);
        return R.ok(result);
    }

    @GetMapping("/list/business")
    @Operation(summary = "根据业务获取文件列表")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<List<FileInfoVO>> getFilesByBusiness(
            @Parameter(description = "业务类型") @RequestParam String businessType,
            @Parameter(description = "业务关联ID") @RequestParam String businessId) {
        
        List<FileInfoVO> result = fileService.getFilesByBusiness(businessType, businessId);
        return R.ok(result);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询文件")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<PageResult<FileInfoVO>> getFilePage(@Valid FilePageDTO pageDTO) {
        PageResult<FileInfoVO> result = fileService.getFilePage(pageDTO);
        return R.ok(result);
    }

    @GetMapping("/presigned-upload-url")
    @Operation(summary = "生成预签名上传URL")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<String> generatePresignedUploadUrl(
            @Parameter(description = "文件名") @RequestParam String fileName,
            @Parameter(description = "文件类型") @RequestParam String contentType,
            @Parameter(description = "过期时间（秒）") @RequestParam(defaultValue = "3600") int expirationSeconds) {
        
        String url = fileService.generatePresignedUploadUrl(fileName, contentType, expirationSeconds);
        return R.ok(url);
    }

    @GetMapping("/presigned-download-url/{fileId}")
    @Operation(summary = "生成预签名下载URL")
    @PreAuthorize("@ss.hasPermission('infra:file:download')")
    public R<String> generatePresignedDownloadUrl(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "过期时间（秒）") @RequestParam(defaultValue = "3600") int expirationSeconds) {
        
        String url = fileService.generatePresignedDownloadUrl(fileId, expirationSeconds);
        return R.ok(url);
    }

    @PostMapping("/cleanup/expired")
    @Operation(summary = "清理过期文件")
    @PreAuthorize("@ss.hasPermission('infra:file:manage')")
    public R<Integer> cleanupExpiredFiles() {
        int cleanedCount = fileService.cleanupExpiredFiles();
        return R.ok(cleanedCount);
    }

    @PostMapping("/cleanup/temp")
    @Operation(summary = "清理临时文件")
    @PreAuthorize("@ss.hasPermission('infra:file:manage')")
    public R<Integer> cleanupTempFiles() {
        int cleanedCount = fileService.cleanupTempFiles();
        return R.ok(cleanedCount);
    }
}