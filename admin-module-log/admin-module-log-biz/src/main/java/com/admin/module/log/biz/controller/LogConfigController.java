package com.admin.module.log.biz.controller;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.R;
import com.admin.module.log.biz.config.LogProperties;
import com.admin.module.log.biz.service.LogCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志配置控制器
 * 
 * 提供日志配置管理、维护功能和动态日志级别管理
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Tag(name = "日志配置管理")
@RestController
@RequestMapping("/system/log-config")
@RequiredArgsConstructor
@Validated
public class LogConfigController {

    private final LogProperties logProperties;
    private final LogCleanupService logCleanupService;
    private final LoggingSystem loggingSystem;

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
            return R.error("清理天数必须在1-365之间");
        }
        
        try {
            int cleanedCount = logCleanupService.manualCleanupFileLogs(days);
            return R.ok("清理完成，共清理了 " + cleanedCount + " 个日志文件");
        } catch (Exception e) {
            return R.error("清理失败：" + e.getMessage());
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
    
    // ==================== 动态日志级别管理 ====================
    
    @GetMapping("/levels")
    @Operation(summary = "获取当前日志级别配置")
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<Map<String, Object>> getLogLevels() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取根日志级别
        LogLevel rootLevel = loggingSystem.getLoggerConfiguration("ROOT").getEffectiveLevel();
        result.put("rootLevel", rootLevel != null ? rootLevel.name() : "INFO");
        
        // 获取常用包的日志级别
        Map<String, String> packageLevels = new HashMap<>();
        String[] commonPackages = {
            "com.admin",
            "org.springframework",
            "org.mybatis",
            "com.baomidou.mybatisplus",
            "org.apache.ibatis",
            "druid.sql"
        };
        
        for (String packageName : commonPackages) {
            try {
                var config = loggingSystem.getLoggerConfiguration(packageName);
                if (config != null && config.getEffectiveLevel() != null) {
                    packageLevels.put(packageName, config.getEffectiveLevel().name());
                }
            } catch (Exception e) {
                // 忽略获取失败的包
            }
        }
        
        result.put("packageLevels", packageLevels);
        result.put("availableLevels", new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"});
        
        return R.ok(result);
    }
    
    @PostMapping("/levels/{loggerName}")
    @Operation(summary = "设置指定Logger的日志级别")
    @OperationLog(title = "日志管理", description = "设置日志级别", businessType = OperationLog.BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<String> setLogLevel(
            @Parameter(description = "Logger名称") @PathVariable String loggerName,
            @Parameter(description = "日志级别") @RequestParam String level) {
        
        try {
            LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
            loggingSystem.setLogLevel(loggerName, logLevel);
            
            log.info("日志级别已更新: {} -> {}", loggerName, level);
            return R.ok(String.format("成功设置 %s 的日志级别为 %s", loggerName, level));
            
        } catch (IllegalArgumentException e) {
            return R.error("无效的日志级别: " + level);
        } catch (Exception e) {
            log.error("设置日志级别失败", e);
            return R.error("设置日志级别失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/levels/{loggerName}")
    @Operation(summary = "重置指定Logger的日志级别")
    @OperationLog(title = "日志管理", description = "重置日志级别", businessType = OperationLog.BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<String> resetLogLevel(@Parameter(description = "Logger名称") @PathVariable String loggerName) {
        try {
            loggingSystem.setLogLevel(loggerName, null);
            log.info("日志级别已重置: {}", loggerName);
            return R.ok(String.format("成功重置 %s 的日志级别", loggerName));
        } catch (Exception e) {
            log.error("重置日志级别失败", e);
            return R.error("重置日志级别失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取日志统计信息")
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<Map<String, Object>> getLogStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 基本统计信息
            stats.put("systemStartTime", System.getProperty("java.vm.start.time", "未知"));
            stats.put("currentTime", System.currentTimeMillis());
            
            // 如果是文件存储，获取文件统计
            if ("file".equals(logProperties.getStorageType().getCode())) {
                LogCleanupService.LogFileInfo fileInfo = logCleanupService.getLogFileInfo();
                stats.put("totalLogFiles", fileInfo.getTotalFiles());
                stats.put("totalLogSize", fileInfo.getTotalSize());
                stats.put("logDirectory", logProperties.getFile().getBasePath());
            }
            
            // 配置信息
            stats.put("asyncEnabled", logProperties.isAsyncEnabled());
            stats.put("storageType", logProperties.getStorageType().getDescription());
            
        } catch (Exception e) {
            log.error("获取日志统计信息失败", e);
            stats.put("error", "获取统计信息失败: " + e.getMessage());
        }
        
        return R.ok(stats);
    }
    
    @PostMapping("/test-output")
    @Operation(summary = "测试日志输出")
    @OperationLog(title = "日志管理", description = "测试日志输出", businessType = OperationLog.BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermission('system:log:config')")
    public R<String> testLogOutput(@RequestParam(defaultValue = "INFO") String level) {
        try {
            String message = "测试日志输出 - " + System.currentTimeMillis();
            
            switch (level.toUpperCase()) {
                case "TRACE":
                    log.trace(message);
                    break;
                case "DEBUG":
                    log.debug(message);
                    break;
                case "INFO":
                    log.info(message);
                    break;
                case "WARN":
                    log.warn(message);
                    break;
                case "ERROR":
                    log.error(message);
                    break;
                default:
                    return R.error("无效的日志级别: " + level);
            }
            
            return R.ok(String.format("成功输出 %s 级别的测试日志", level));
            
        } catch (Exception e) {
            log.error("测试日志输出失败", e);
            return R.error("测试日志输出失败: " + e.getMessage());
        }
    }
}