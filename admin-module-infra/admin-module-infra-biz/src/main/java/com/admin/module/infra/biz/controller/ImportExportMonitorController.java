package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.module.infra.biz.service.ImportExportMonitorService;
import com.admin.module.infra.biz.service.ImportExportScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 导入导出监控控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "导入导出监控")
@RestController
@RequestMapping("/admin-api/infra/import-export-monitor")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ImportExportMonitorController {

    private final ImportExportMonitorService monitorService;
    private final ImportExportScheduleService scheduleService;

    @GetMapping("/performance/{taskType}")
    @Operation(summary = "获取性能统计信息")
    @PreAuthorize("@ss.hasPermission('infra:monitor:query')")
    public R<ImportExportMonitorService.PerformanceStats> getPerformanceStats(
            @Parameter(description = "任务类型", required = true) @PathVariable String taskType) {
        
        ImportExportMonitorService.PerformanceStats stats = monitorService.getPerformanceStats(taskType);
        return R.ok(stats);
    }

    @GetMapping("/system-load")
    @Operation(summary = "获取系统负载信息")
    @PreAuthorize("@ss.hasPermission('infra:monitor:query')")
    public R<ImportExportMonitorService.SystemLoadInfo> getSystemLoadInfo() {
        ImportExportMonitorService.SystemLoadInfo loadInfo = monitorService.getSystemLoadInfo();
        return R.ok(loadInfo);
    }

    @PostMapping("/cleanup/export-files")
    @Operation(summary = "手动清理过期导出文件")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> cleanupExportFiles() {
        scheduleService.cleanupExpiredExportFiles();
        return R.ok(true);
    }

    @PostMapping("/cleanup/error-details")
    @Operation(summary = "手动清理过期错误详情")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> cleanupErrorDetails() {
        scheduleService.cleanupExpiredErrorDetails();
        return R.ok(true);
    }

    @PostMapping("/cleanup/temp-files")
    @Operation(summary = "手动清理临时文件")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> cleanupTempFiles() {
        scheduleService.cleanupTempFiles();
        return R.ok(true);
    }

    @PostMapping("/cleanup/performance-data")
    @Operation(summary = "手动清理性能数据")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> cleanupPerformanceData() {
        scheduleService.cleanupPerformanceData();
        return R.ok(true);
    }

    @PostMapping("/health-check")
    @Operation(summary = "手动执行健康检查")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> healthCheck() {
        scheduleService.systemHealthCheck();
        return R.ok(true);
    }

    @PostMapping("/generate-report")
    @Operation(summary = "手动生成性能报告")
    @PreAuthorize("@ss.hasPermission('infra:monitor:manage')")
    public R<Boolean> generatePerformanceReport() {
        scheduleService.generatePerformanceReport();
        return R.ok(true);
    }

    @GetMapping("/health")
    @Operation(summary = "系统健康状态检查（无需权限）")
    public R<String> healthStatus() {
        try {
            ImportExportMonitorService.SystemLoadInfo loadInfo = monitorService.getSystemLoadInfo();
            
            // 简单的健康状态判断
            if (loadInfo.getMemoryUsagePercent() < 90) {
                return R.ok("HEALTHY");
            } else {
                return R.ok("WARNING");
            }
        } catch (Exception e) {
            log.error("健康状态检查失败", e);
            return R.ok("ERROR");
        }
    }
}