package com.admin.module.log.biz.controller;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.R;
import com.admin.module.log.biz.config.LogProperties;
import com.admin.module.log.biz.service.LogCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 日志管理控制器
 * 
 * 提供日志配置管理和维护功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "日志管理")
@RestController
@RequestMapping("/admin-api/log/management")
@RequiredArgsConstructor
@Validated
public class LogManagementController {

    private final LogProperties logProperties;
    private final LogCleanupService logCleanupService;

    @GetMapping("/config")
    @Operation(summary = "获取日志配置")
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<LogProperties> getLogConfig() {
        return R.ok(logProperties);
    }

    @GetMapping("/file-info")
    @Operation(summary = "获取文件日志存储信息")
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<LogCleanupService.LogFileInfo> getLogFileInfo() {
        LogCleanupService.LogFileInfo fileInfo = logCleanupService.getLogFileInfo();
        return R.ok(fileInfo);
    }

    @PostMapping("/cleanup")
    @Operation(summary = "手动清理过期日志")
    @OperationLog(title = "日志管理", description = "清理过期日志文件", businessType = OperationLog.BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermission('system:log:cleanup')")
    public R<String> cleanupLogs(
            @Parameter(description = "清理多少天前的日志") @RequestParam(defaultValue = "30") Integer days) {
        
        if (days <= 0 || days > 365) {
            return R.fail("清理天数必须在1-365之间");
        }
        
        try {
            int cleanedCount = logCleanupService.manualCleanupFileLogs(days);
            return R.ok("清理完成，共清理了 " + cleanedCount + " 个日志文件");
        } catch (Exception e) {
            return R.fail("清理失败：" + e.getMessage());
        }
    }

    @PostMapping("/test-log")
    @Operation(summary = "测试日志记录")
    @OperationLog(title = "日志管理", description = "测试日志记录功能", businessType = OperationLog.BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermission('system:log:test')")
    public R<String> testLogRecord() {
        return R.ok("日志记录测试成功，请查看操作日志");
    }

    @GetMapping("/health")
    @Operation(summary = "获取日志系统健康状态")
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<LogSystemHealth> getLogSystemHealth() {
        LogSystemHealth health = new LogSystemHealth();
        
        try {
            // 检查存储配置
            health.setStorageType(logProperties.getStorageType().getDescription());
            health.setAsyncEnabled(logProperties.isAsyncEnabled());
            
            // 检查文件存储状态
            if ("file".equals(logProperties.getStorageType().getCode())) {
                LogCleanupService.LogFileInfo fileInfo = logCleanupService.getLogFileInfo();
                health.setFileStorageAvailable(true);
                health.setTotalFiles(fileInfo.getTotalFiles());
                health.setTotalSize(fileInfo.getTotalSize());
            }
            
            health.setStatus("healthy");
            health.setMessage("日志系统运行正常");
            
        } catch (Exception e) {
            health.setStatus("error");
            health.setMessage("日志系统异常：" + e.getMessage());
        }
        
        return R.ok(health);
    }

    /**
     * 日志系统健康状态
     */
    public static class LogSystemHealth {
        private String status;
        private String message;
        private String storageType;
        private boolean asyncEnabled;
        private boolean fileStorageAvailable;
        private int totalFiles;
        private long totalSize;

        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getStorageType() { return storageType; }
        public void setStorageType(String storageType) { this.storageType = storageType; }
        
        public boolean isAsyncEnabled() { return asyncEnabled; }
        public void setAsyncEnabled(boolean asyncEnabled) { this.asyncEnabled = asyncEnabled; }
        
        public boolean isFileStorageAvailable() { return fileStorageAvailable; }
        public void setFileStorageAvailable(boolean fileStorageAvailable) { this.fileStorageAvailable = fileStorageAvailable; }
        
        public int getTotalFiles() { return totalFiles; }
        public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
        
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
    }
}