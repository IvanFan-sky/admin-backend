package com.admin.module.infra.biz.service;

import com.admin.module.infra.api.enums.TaskStatusEnum;
import com.admin.module.infra.api.service.ImportErrorDetailService;
import com.admin.module.infra.api.service.ImportExportTaskService;
import com.admin.module.infra.api.vo.ImportExportTaskVO;
import com.admin.module.infra.biz.config.ImportExportConfig;
import com.admin.module.infra.biz.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入导出定时任务服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportScheduleService {

    private final ImportExportTaskService importExportTaskService;
    private final ImportErrorDetailService importErrorDetailService;
    private final ImportExportMonitorService monitorService;
    private final ImportExportCacheService cacheService;
    private final ImportExportConfig importExportConfig;
    private final ImportExportFileService importExportFileService;

    /**
     * 清理过期的导出文件
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredExportFiles() {
        log.info("开始清理过期的导出文件");
        
        try {
            // 使用新的文件服务清理过期导入导出文件
            int cleanedCount = importExportFileService.cleanupExpiredImportExportFiles();
            
            log.info("清理过期导入导出文件完成，清理数量: {}", cleanedCount);
            
        } catch (Exception e) {
            log.error("清理过期导入导出文件失败", e);
        }
    }

    /**
     * 清理过期的错误详情
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredErrorDetails() {
        log.info("开始清理过期的错误详情");
        
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(importExportConfig.getErrorDetailRetentionDays());
            
            // 获取过期的任务
            // TODO: 实现根据创建时间查询过期任务的方法
            // List<ImportExportTaskVO> expiredTasks = importExportTaskService.getExpiredTasks(expireTime);
            
            // 模拟清理逻辑
            int cleanedCount = 0;
            /*
            for (ImportExportTaskVO task : expiredTasks) {
                if (task.getStatus().equals(TaskStatusEnum.COMPLETED.getCode()) || 
                    task.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                    
                    // 删除错误详情
                    importErrorDetailService.deleteErrorDetailsByTaskId(task.getId());
                    
                    // 删除任务记录（可选，根据业务需求）
                    // importExportTaskService.deleteTask(task.getId());
                    
                    cleanedCount++;
                }
            }
            */
            
            log.info("清理过期错误详情完成，清理任务数: {}", cleanedCount);
            
        } catch (Exception e) {
            log.error("清理过期错误详情失败", e);
        }
    }

    /**
     * 清理过期的临时文件
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void cleanupTempFiles() {
        log.debug("开始清理过期的临时文件");
        
        try {
            String tempPath = importExportConfig.getTempPath();
            
            // 清理超过2小时的临时文件
            FileUtils.cleanupExpiredFiles(tempPath, 0); // 0表示清理当天之前的文件
            
            log.debug("清理临时文件完成，目录: {}", tempPath);
            
        } catch (Exception e) {
            log.error("清理临时文件失败", e);
        }
    }

    /**
     * 检查超时的任务
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void checkTimeoutTasks() {
        log.debug("开始检查超时的任务");
        
        try {
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(importExportConfig.getTaskTimeoutMinutes());
            
            // TODO: 实现查询超时任务的方法
            // List<ImportExportTaskVO> timeoutTasks = importExportTaskService.getTimeoutTasks(timeoutTime);
            
            int timeoutCount = 0;
            /*
            for (ImportExportTaskVO task : timeoutTasks) {
                if (TaskStatusEnum.PROCESSING.getCode().equals(task.getStatus())) {
                    // 将超时任务标记为失败
                    importExportTaskService.cancelTask(task.getId());
                    
                    // 记录超时错误
                    monitorService.recordError(task.getId(), task.getDataType(), 
                                             "TIMEOUT", "任务执行超时");
                    
                    timeoutCount++;
                    log.warn("任务执行超时，任务ID: {}, 任务名称: {}", task.getId(), task.getTaskName());
                }
            }
            */
            
            if (timeoutCount > 0) {
                log.info("检查超时任务完成，处理超时任务数: {}", timeoutCount);
            }
            
        } catch (Exception e) {
            log.error("检查超时任务失败", e);
        }
    }

    /**
     * 清理性能监控数据
     * 每天凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanupPerformanceData() {
        log.info("开始清理性能监控数据");
        
        try {
            monitorService.cleanupExpiredData();
            log.info("清理性能监控数据完成");
            
        } catch (Exception e) {
            log.error("清理性能监控数据失败", e);
        }
    }

    /**
     * 系统健康检查
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void systemHealthCheck() {
        log.debug("开始系统健康检查");
        
        try {
            // 检查系统负载
            var loadInfo = monitorService.getSystemLoadInfo();
            
            // 内存使用率检查
            if (loadInfo.getMemoryUsagePercent() > 90) {
                log.warn("系统内存使用率过高: {}%", loadInfo.getMemoryUsagePercent());
                
                // 可以在这里添加告警通知逻辑
                // alertService.sendAlert("MEMORY_HIGH", "系统内存使用率过高: " + loadInfo.getMemoryUsagePercent() + "%");
            }
            
            // 并发任务数检查
            long totalTasks = loadInfo.getCurrentImportTasks() + loadInfo.getCurrentExportTasks();
            if (totalTasks > importExportConfig.getMaxConcurrentImportTasks() + importExportConfig.getMaxConcurrentExportTasks()) {
                log.warn("并发任务数过多: {}", totalTasks);
            }
            
            // 线程数检查
            if (loadInfo.getActiveThreads() > 100) {
                log.warn("系统活动线程数过多: {}", loadInfo.getActiveThreads());
            }
            
            log.debug("系统健康检查完成，内存使用: {}%, 活动任务: {}, 活动线程: {}", 
                     loadInfo.getMemoryUsagePercent(), totalTasks, loadInfo.getActiveThreads());
            
        } catch (Exception e) {
            log.error("系统健康检查失败", e);
        }
    }

    /**
     * 缓存维护
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void cacheMaintenance() {
        log.debug("开始缓存维护");
        
        try {
            // 这里可以添加缓存清理、预热等逻辑
            
            // 清理无效的用户任务限制缓存
            // cacheService.cleanupInvalidUserTaskLimits();
            
            log.debug("缓存维护完成");
            
        } catch (Exception e) {
            log.error("缓存维护失败", e);
        }
    }

    /**
     * 生成性能报告
     * 每天上午8点执行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void generatePerformanceReport() {
        log.info("开始生成性能报告");
        
        try {
            // 生成导入任务性能报告
            var importStats = monitorService.getPerformanceStats("import");
            log.info("导入任务性能统计 - 启动: {}, 完成: {}, 失败: {}, 成功率: {}%, 平均执行时间: {}ms", 
                    importStats.getTaskStartedCount(), importStats.getTaskCompletedCount(), 
                    importStats.getTaskFailedCount(), importStats.getSuccessRate(), 
                    importStats.getAverageExecutionTime());
            
            // 生成导出任务性能报告
            var exportStats = monitorService.getPerformanceStats("export");
            log.info("导出任务性能统计 - 启动: {}, 完成: {}, 失败: {}, 成功率: {}%, 平均执行时间: {}ms", 
                    exportStats.getTaskStartedCount(), exportStats.getTaskCompletedCount(), 
                    exportStats.getTaskFailedCount(), exportStats.getSuccessRate(), 
                    exportStats.getAverageExecutionTime());
            
            // 系统负载报告
            var loadInfo = monitorService.getSystemLoadInfo();
            log.info("系统负载统计 - 当前导入任务: {}, 当前导出任务: {}, 内存使用率: {}%, 活动线程: {}", 
                    loadInfo.getCurrentImportTasks(), loadInfo.getCurrentExportTasks(),
                    loadInfo.getMemoryUsagePercent(), loadInfo.getActiveThreads());
            
            log.info("性能报告生成完成");
            
        } catch (Exception e) {
            log.error("生成性能报告失败", e);
        }
    }
}