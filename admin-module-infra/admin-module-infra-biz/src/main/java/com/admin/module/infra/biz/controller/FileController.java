package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.service.FileService;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "文件管理", description = "文件上传、下载、管理等功能")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;

    @Operation(summary = "文件上传", description = "支持单文件上传，自动进行文件类型检测和安全校验")
    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermission('infra:file:upload')")
    public R<FileUploadVO> uploadFile(@Validated FileUploadDTO uploadDTO) {
        FileUploadVO result = fileService.uploadFile(uploadDTO);
        return R.ok(result);
    }

    @Operation(summary = "文件下载", description = "支持断点续传和Range请求")
    @GetMapping("/{fileId}/download")
    @PreAuthorize("@ss.hasPermission('infra:file:download')")
    public void downloadFile(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            @Parameter(description = "是否内联显示") @RequestParam(required = false) Boolean inline,
            @Parameter(description = "下载文件名") @RequestParam(required = false) String downloadName,
            HttpServletRequest request,
            HttpServletResponse response) {
        fileService.downloadFile(fileId, request, response, inline, downloadName);
    }

    @Operation(summary = "获取文件信息", description = "根据文件ID获取详细信息")
    @GetMapping("/{fileId}")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<FileInfoVO> getFileInfo(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {
        FileInfoVO fileInfo = fileService.getFileInfo(fileId);
        return R.ok(fileInfo);
    }

    @Operation(summary = "文件列表查询", description = "支持分页和多条件查询")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('infra:file:list')")
    public R<PageResult<FileInfoVO>> getFileList(@Validated FilePageDTO pageDTO) {
        PageResult<FileInfoVO> result = fileService.getFileList(pageDTO);
        return R.ok(result);
    }

    @Operation(summary = "删除文件", description = "删除指定文件，同时删除存储和数据库记录")
    @DeleteMapping("/{fileId}")
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public R<Boolean> deleteFile(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {
        Boolean result = fileService.deleteFile(fileId);
        return R.ok(result);
    }

    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public R<Integer> batchDeleteFiles(
            @Parameter(description = "文件ID列表", required = true) @RequestBody List<Long> fileIds) {
        Integer okCount = fileService.batchDeleteFiles(fileIds);
        return R.ok(okCount);
    }

    @Operation(summary = "获取文件访问URL", description = "生成预签名URL，用于直接访问文件")
    @GetMapping("/{fileId}/access-url")
    @PreAuthorize("@ss.hasPermission('infra:file:access')")
    public R<String> getFileAccessUrl(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            @Parameter(description = "过期时间（秒），默认3600秒") @RequestParam(required = false, defaultValue = "3600") Integer expireSeconds) {
        String accessUrl = fileService.getFileAccessUrl(fileId, expireSeconds);
        return R.ok(accessUrl);
    }

    @Operation(summary = "根据哈希值查找文件", description = "用于文件去重功能")
    @GetMapping("/find-by-hash")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<FileInfoVO> findFileByHash(
            @Parameter(description = "文件MD5哈希值", required = true) @RequestParam String fileHash) {
        FileInfoVO fileInfo = fileService.findFileByHash(fileHash);
        return R.ok(fileInfo);
    }
}
