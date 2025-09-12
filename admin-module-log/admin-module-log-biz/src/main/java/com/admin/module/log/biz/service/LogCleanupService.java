package com.admin.module.log.biz.service;

import com.admin.module.log.biz.config.LogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * 日志清理服务
 * 
 * 负责清理过期的文件日志和数据库日志
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogCleanupService {

    private final LogProperties logProperties;

    /**
     * 定时清理过期的文件日志
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredFileLogs() {
        if (!logProperties.getFile().isRotationEnabled()) {
            log.debug("文件日志轮转未启用，跳过清理任务");
            return;
        }

        try {
            int retentionDays = logProperties.getFile().getRetentionDays();
            LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
            
            log.info("开始清理{}天前的文件日志，截止日期: {}", retentionDays, cutoffDate);
            
            cleanupOperationLogFiles(cutoffDate);
            cleanupLoginLogFiles(cutoffDate);
            
            log.info("文件日志清理任务完成");
            
        } catch (Exception e) {
            log.error("清理文件日志时发生错误", e);
        }
    }

    /**
     * 清理操作日志文件
     */
    private void cleanupOperationLogFiles(LocalDate cutoffDate) {
        Path operationLogDir = Paths.get(logProperties.getFile().getBasePath(), "operation");
        cleanupLogFiles(operationLogDir, "operation_", cutoffDate);
    }

    /**
     * 清理登录日志文件
     */
    private void cleanupLoginLogFiles(LocalDate cutoffDate) {
        Path loginLogDir = Paths.get(logProperties.getFile().getBasePath(), "login");
        cleanupLogFiles(loginLogDir, "login_", cutoffDate);
    }

    /**
     * 清理指定目录下的日志文件
     */
    private void cleanupLogFiles(Path logDir, String filePrefix, LocalDate cutoffDate) {
        if (!Files.exists(logDir) || !Files.isDirectory(logDir)) {
            log.debug("日志目录不存在: {}", logDir);
            return;
        }

        try (Stream<Path> files = Files.list(logDir)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            files.filter(Files::isRegularFile)
                 .filter(path -> path.getFileName().toString().startsWith(filePrefix))
                 .forEach(path -> {
                     try {
                         String fileName = path.getFileName().toString();
                         // 提取日期部分，格式：prefix_yyyy-MM-dd.log
                         String dateStr = fileName.substring(filePrefix.length(), filePrefix.length() + 10);
                         LocalDate fileDate = LocalDate.parse(dateStr, formatter);
                         
                         if (fileDate.isBefore(cutoffDate)) {
                             Files.deleteIfExists(path);
                             log.info("已删除过期日志文件: {}", path);
                         }
                         
                     } catch (Exception e) {
                         log.warn("处理日志文件时发生错误: {}", path, e);
                     }
                 });
                 
        } catch (IOException e) {
            log.error("清理日志目录时发生错误: {}", logDir, e);
        }
    }

    /**
     * 手动清理文件日志
     * 
     * @param days 清理多少天前的日志
     * @return 清理的文件数量
     */
    public int manualCleanupFileLogs(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        log.info("手动清理{}天前的文件日志，截止日期: {}", days, cutoffDate);
        
        int cleanedCount = 0;
        
        try {
            cleanedCount += countAndCleanupLogFiles(
                Paths.get(logProperties.getFile().getBasePath(), "operation"), 
                "operation_", 
                cutoffDate
            );
            
            cleanedCount += countAndCleanupLogFiles(
                Paths.get(logProperties.getFile().getBasePath(), "login"), 
                "login_", 
                cutoffDate
            );
            
            log.info("手动清理文件日志完成，共清理{}个文件", cleanedCount);
            
        } catch (Exception e) {
            log.error("手动清理文件日志时发生错误", e);
            throw new RuntimeException("清理文件日志失败", e);
        }
        
        return cleanedCount;
    }

    /**
     * 统计并清理日志文件
     */
    private int countAndCleanupLogFiles(Path logDir, String filePrefix, LocalDate cutoffDate) {
        if (!Files.exists(logDir) || !Files.isDirectory(logDir)) {
            return 0;
        }

        int count = 0;
        
        try (Stream<Path> files = Files.list(logDir)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (Path path : files.filter(Files::isRegularFile)
                                  .filter(p -> p.getFileName().toString().startsWith(filePrefix))
                                  .toList()) {
                try {
                    String fileName = path.getFileName().toString();
                    String dateStr = fileName.substring(filePrefix.length(), filePrefix.length() + 10);
                    LocalDate fileDate = LocalDate.parse(dateStr, formatter);
                    
                    if (fileDate.isBefore(cutoffDate)) {
                        Files.deleteIfExists(path);
                        count++;
                        log.debug("已删除过期日志文件: {}", path);
                    }
                    
                } catch (Exception e) {
                    log.warn("处理日志文件时发生错误: {}", path, e);
                }
            }
            
        } catch (IOException e) {
            log.error("清理日志目录时发生错误: {}", logDir, e);
        }
        
        return count;
    }

    /**
     * 获取文件日志存储信息
     */
    public LogFileInfo getLogFileInfo() {
        LogFileInfo info = new LogFileInfo();
        
        try {
            // 操作日志文件信息
            Path operationDir = Paths.get(logProperties.getFile().getBasePath(), "operation");
            info.setOperationLogFiles(countLogFiles(operationDir));
            info.setOperationLogSize(calculateDirectorySize(operationDir));
            
            // 登录日志文件信息
            Path loginDir = Paths.get(logProperties.getFile().getBasePath(), "login");
            info.setLoginLogFiles(loginDir);
            info.setLoginLogSize(calculateDirectorySize(loginDir));
            
        } catch (Exception e) {
            log.error("获取日志文件信息时发生错误", e);
        }
        
        return info;
    }

    /**
     * 统计目录下的日志文件数量
     */
    private int countLogFiles(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return 0;
        }
        
        try (Stream<Path> files = Files.list(dir)) {
            return (int) files.filter(Files::isRegularFile)
                             .filter(path -> path.getFileName().toString().endsWith(".log"))
                             .count();
        }
    }

    /**
     * 计算目录大小
     */
    private long calculateDirectorySize(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return 0;
        }
        
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(Files::isRegularFile)
                       .mapToLong(path -> {
                           try {
                               return Files.size(path);
                           } catch (IOException e) {
                               return 0L;
                           }
                       })
                       .sum();
        }
    }

    /**
     * 日志文件信息
     */
    public static class LogFileInfo {
        private int operationLogFiles;
        private long operationLogSize;
        private int loginLogFiles;
        private long loginLogSize;

        // Getters and Setters
        public int getOperationLogFiles() { return operationLogFiles; }
        public void setOperationLogFiles(int operationLogFiles) { this.operationLogFiles = operationLogFiles; }
        
        public long getOperationLogSize() { return operationLogSize; }
        public void setOperationLogSize(long operationLogSize) { this.operationLogSize = operationLogSize; }
        
        public int getLoginLogFiles() { return loginLogFiles; }
        public void setLoginLogFiles(Path loginDir) throws IOException { 
            this.loginLogFiles = countLogFiles(loginDir); 
        }
        
        public long getLoginLogSize() { return loginLogSize; }
        public void setLoginLogSize(long loginLogSize) { this.loginLogSize = loginLogSize; }
        
        public int getTotalFiles() { return operationLogFiles + loginLogFiles; }
        public long getTotalSize() { return operationLogSize + loginLogSize; }
        
        private int countLogFiles(Path dir) throws IOException {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                return 0;
            }
            
            try (Stream<Path> files = Files.list(dir)) {
                return (int) files.filter(Files::isRegularFile)
                                 .filter(path -> path.getFileName().toString().endsWith(".log"))
                                 .count();
            }
        }
    }
}