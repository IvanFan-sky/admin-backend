package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.biz.service.FileManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 文件管理控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/admin-api/infra/file-management")
@RequiredArgsConstructor
@Validated
@Slf4j
public class FileManagementController {

    private final FileManagementService fileManagementService;

    @GetMapping("/statistics/storage")
    @Operation(summary = "获取存储统计信息")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> getStorageStatistics() {
        Map<String, Object> statistics = fileManagementService.getStorageStatistics();
        return R.ok(statistics);
    }

    @GetMapping("/statistics/business")
    @Operation(summary = "获取业务文件统计")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> getBusinessStatistics() {
        Map<String, Object> statistics = fileManagementService.getBusinessStatistics();
        return R.ok(statistics);
    }

    @GetMapping("/statistics/storage-type")
    @Operation(summary = "获取存储类型分布")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> getStorageTypeDistribution() {
        Map<String, Object> distribution = fileManagementService.getStorageTypeDistribution();
        return R.ok(distribution);
    }

    @GetMapping("/statistics/upload-trend")
    @Operation(summary = "获取文件上传趋势")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> getUploadTrend() {
        Map<String, Object> trend = fileManagementService.getUploadTrend();
        return R.ok(trend);
    }

    @GetMapping("/large-files")
    @Operation(summary = "获取大文件列表")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<PageResult<FileInfoVO>> getLargeFiles(
            @Parameter(description = "最小文件大小（字节）") @RequestParam(defaultValue = "104857600") Long minSize,
            @Valid FilePageDTO pageDTO) {
        
        PageResult<FileInfoVO> result = fileManagementService.getLargeFiles(minSize, pageDTO);
        return R.ok(result);
    }

    @GetMapping("/duplicate-files")
    @Operation(summary = "获取重复文件列表")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<PageResult<FileInfoVO>> getDuplicateFiles(@Valid FilePageDTO pageDTO) {
        PageResult<FileInfoVO> result = fileManagementService.getDuplicateFiles(pageDTO);
        return R.ok(result);
    }

    @GetMapping("/orphan-files")
    @Operation(summary = "获取孤儿文件列表")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<PageResult<FileInfoVO>> getOrphanFiles(@Valid FilePageDTO pageDTO) {
        PageResult<FileInfoVO> result = fileManagementService.getOrphanFiles(pageDTO);
        return R.ok(result);
    }

    @PostMapping("/cleanup/storage")
    @Operation(summary = "执行存储空间清理")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> performStorageCleanup() {
        Map<String, Object> result = fileManagementService.performStorageCleanup();
        return R.ok(result);
    }

    @PostMapping("/repair/orphan-files")
    @Operation(summary = "修复孤儿文件")
    @PreAuthorize("@ss.hasPermission('infra:file:management')")
    public R<Map<String, Object>> repairOrphanFiles() {
        Map<String, Object> result = fileManagementService.repairOrphanFiles();
        return R.ok(result);
    }
}