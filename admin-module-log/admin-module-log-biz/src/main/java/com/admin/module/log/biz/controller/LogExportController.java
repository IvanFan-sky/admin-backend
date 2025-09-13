package com.admin.module.log.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.result.CommonResult;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.service.LogExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

/**
 * 日志导出控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "日志导出管理")
@RestController
@RequestMapping("/admin-api/log/export")
@RequiredArgsConstructor
@Slf4j
@Validated
public class LogExportController {

    private final LogExportService logExportService;

    @PostMapping("/operation-logs")
    @Operation(summary = "异步导出操作日志")
    @PreAuthorize("@ss.hasPermission('log:operation:export')")
    public CommonResult<Long> exportOperationLogs(
            @Parameter(description = "查询条件") @Valid @RequestBody OperationLogQueryDTO queryCondition) {
        
        try {
            CompletableFuture<Long> future = logExportService.exportOperationLogsAsync(queryCondition);
            Long taskId = future.get(); // 获取任务ID
            
            return CommonResult.success(taskId, "操作日志导出任务已创建，请通过任务ID查询进度");
        } catch (Exception e) {
            log.error("创建操作日志导出任务失败", e);
            return CommonResult.error("创建导出任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/login-logs")
    @Operation(summary = "异步导出登录日志")
    @PreAuthorize("@ss.hasPermission('log:login:export')")
    public CommonResult<Long> exportLoginLogs(
            @Parameter(description = "查询条件") @Valid @RequestBody LoginLogQueryDTO queryCondition) {
        
        try {
            CompletableFuture<Long> future = logExportService.exportLoginLogsAsync(queryCondition);
            Long taskId = future.get(); // 获取任务ID
            
            return CommonResult.success(taskId, "登录日志导出任务已创建，请通过任务ID查询进度");
        } catch (Exception e) {
            log.error("创建登录日志导出任务失败", e);
            return CommonResult.error("创建导出任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "查询任务详情")
    @PreAuthorize("@ss.hasPermission('log:operation:export') or @ss.hasPermission('log:login:export')")
    public CommonResult<ImportExportTask> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        ImportExportTask task = logExportService.getTaskDetail(taskId);
        if (task == null) {
            return CommonResult.error("任务不存在");
        }
        
        return CommonResult.success(task);
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询用户的导出任务列表")
    @PreAuthorize("@ss.hasPermission('log:operation:export') or @ss.hasPermission('log:login:export')")
    public CommonResult<PageResult<ImportExportTask>> getUserTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        
        PageResult<ImportExportTask> result = logExportService.getUserTasks(pageNum, pageSize);
        return CommonResult.success(result);
    }

    @GetMapping("/task/{taskId}/download-export-file")
    @Operation(summary = "下载导出文件")
    @PreAuthorize("@ss.hasPermission('log:operation:export') or @ss.hasPermission('log:login:export')")
    public void downloadExportFile(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId,
            HttpServletResponse response) {
        
        logExportService.downloadExportFile(taskId, response);
    }

    @DeleteMapping("/task/{taskId}/cancel")
    @Operation(summary = "取消任务")
    @PreAuthorize("@ss.hasPermission('log:operation:export') or @ss.hasPermission('log:login:export')")
    public CommonResult<Boolean> cancelTask(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        boolean success = logExportService.cancelTask(taskId);
        return success ? CommonResult.success(true, "任务已取消") 
                       : CommonResult.error("任务取消失败");
    }
}