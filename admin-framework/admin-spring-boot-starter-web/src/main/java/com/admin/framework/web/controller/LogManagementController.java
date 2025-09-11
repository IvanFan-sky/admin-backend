package com.admin.framework.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志管理控制器
 * 
 * 提供动态调整日志级别、查看日志配置等管理功能
 * 仅管理员可以访问这些功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
@Tag(name = "日志管理", description = "日志级别动态调整和配置管理")
public class LogManagementController {

    private final LoggingSystem loggingSystem;

    @Operation(summary = "获取当前日志级别配置")
    @GetMapping("/levels")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLogLevels() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取常用包的日志级别
        String[] packages = {
            "ROOT",
            "com.admin",
            "org.springframework",
            "org.springframework.web",
            "org.springframework.security",
            "com.baomidou.mybatisplus",
            "org.apache.ibatis",
            "org.redisson",
            "BUSINESS",
            "PERFORMANCE",
            "SECURITY",
            "API"
        };
        
        Map<String, String> levels = new HashMap<>();
        for (String packageName : packages) {
            LogLevel level = loggingSystem.getLoggerConfiguration(packageName).getEffectiveLevel();
            levels.put(packageName, level != null ? level.name() : "INHERIT");
        }
        
        result.put("levels", levels);
        result.put("availableLevels", new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"});
        result.put("timestamp", System.currentTimeMillis());
        
        log.info("查询日志级别配置");
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "动态调整日志级别")
    @PostMapping("/levels/{loggerName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> setLogLevel(
            @Parameter(description = "日志记录器名称") @PathVariable String loggerName,
            @Parameter(description = "日志级别") @RequestParam String level) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证日志级别
            LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
            
            // 设置日志级别
            loggingSystem.setLogLevel(loggerName, logLevel);
            
            // 验证设置是否成功
            LogLevel currentLevel = loggingSystem.getLoggerConfiguration(loggerName).getEffectiveLevel();
            
            result.put("success", true);
            result.put("message", String.format("日志级别设置成功: %s -> %s", loggerName, currentLevel));
            result.put("loggerName", loggerName);
            result.put("previousLevel", "unknown");
            result.put("currentLevel", currentLevel.name());
            result.put("timestamp", System.currentTimeMillis());
            
            log.warn("动态调整日志级别: {} -> {}", loggerName, currentLevel);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的日志级别: " + level);
            result.put("availableLevels", new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"});
            
            log.error("设置日志级别失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置日志级别失败: " + e.getMessage());
            
            log.error("设置日志级别异常", e);
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @Operation(summary = "重置日志级别为默认配置")
    @PostMapping("/levels/{loggerName}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetLogLevel(
            @Parameter(description = "日志记录器名称") @PathVariable String loggerName) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 重置为null，使其继承父级配置
            loggingSystem.setLogLevel(loggerName, null);
            
            LogLevel currentLevel = loggingSystem.getLoggerConfiguration(loggerName).getEffectiveLevel();
            
            result.put("success", true);
            result.put("message", String.format("日志级别已重置: %s -> %s (继承)", loggerName, currentLevel));
            result.put("loggerName", loggerName);
            result.put("currentLevel", currentLevel != null ? currentLevel.name() : "INHERIT");
            result.put("timestamp", System.currentTimeMillis());
            
            log.warn("重置日志级别: {} -> {} (继承)", loggerName, currentLevel);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置日志级别失败: " + e.getMessage());
            
            log.error("重置日志级别异常", e);
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @Operation(summary = "获取日志统计信息")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLogStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 这里可以集成实际的日志统计功能
        // 例如：错误日志数量、警告日志数量、性能日志统计等
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalLoggers", getAllLoggerCount());
        statistics.put("activeLoggers", getActiveLoggerCount());
        statistics.put("errorCount", "暂不支持");
        statistics.put("warnCount", "暂不支持");
        statistics.put("performanceStats", "暂不支持");
        
        result.put("statistics", statistics);
        result.put("timestamp", System.currentTimeMillis());
        result.put("note", "统计功能需要集成日志收集系统");
        
        log.info("查询日志统计信息");
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "测试日志输出")
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testLog(
            @Parameter(description = "日志级别") @RequestParam(defaultValue = "INFO") String level,
            @Parameter(description = "测试消息") @RequestParam(defaultValue = "测试日志消息") String message) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据级别输出测试日志
            switch (level.toUpperCase()) {
                case "TRACE":
                    log.trace("测试TRACE日志: {}", message);
                    break;
                case "DEBUG":
                    log.debug("测试DEBUG日志: {}", message);
                    break;
                case "INFO":
                    log.info("测试INFO日志: {}", message);
                    break;
                case "WARN":
                    log.warn("测试WARN日志: {}", message);
                    break;
                case "ERROR":
                    log.error("测试ERROR日志: {}", message);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的日志级别: " + level);
            }
            
            result.put("success", true);
            result.put("message", String.format("测试日志已输出 [%s]: %s", level, message));
            result.put("level", level);
            result.put("testMessage", message);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("availableLevels", new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR"});
            
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取所有日志记录器数量（简化实现）
     */
    private int getAllLoggerCount() {
        // 这里可以通过反射或其他方式获取实际的日志记录器数量
        return 50; // 示例数值
    }

    /**
     * 获取活跃日志记录器数量（简化实现）
     */
    private int getActiveLoggerCount() {
        // 这里可以统计实际活跃的日志记录器数量
        return 25; // 示例数值
    }
}
