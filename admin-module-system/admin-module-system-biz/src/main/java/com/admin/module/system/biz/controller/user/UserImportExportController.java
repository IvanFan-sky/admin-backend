package com.admin.module.system.biz.controller.user;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.module.system.api.service.imports.UserImportExportService;
import com.admin.module.system.api.dto.user.SysUserQueryDTO;
import com.admin.module.system.api.vo.imports.UserImportValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

/**
 * 用户导入导出控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "用户导入导出管理")
@RestController
@RequestMapping("/admin-api/system/user-import-export")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserImportExportController {

    private final UserImportExportService userImportExportService;

    @GetMapping("/import-template")
    @Operation(summary = "下载用户导入模板")
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public void downloadImportTemplate(HttpServletResponse response) {
        userImportExportService.downloadImportTemplate(response);
    }

    @PostMapping("/validate-import")
    @Operation(summary = "验证导入文件")
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public R<UserImportValidationResult> validateImportFile(
            @Parameter(description = "导入文件") @RequestParam("file") MultipartFile file) {
        
        // 文件基本验证
        if (file.isEmpty()) {
            return R.error("导入文件不能为空");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
            return R.error("文件大小不能超过10MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return R.error("只支持Excel文件格式(.xlsx/.xls)");
        }

        UserImportValidationResult result = userImportExportService.validateImportFile(file);
        return R.ok(result);
    }

    @PostMapping("/import")
    @Operation(summary = "异步导入用户")
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public R<Long> importUsers(
            @Parameter(description = "导入文件") @RequestParam("file") MultipartFile file) {
        
        // 文件基本验证
        if (file.isEmpty()) {
            return R.error("导入文件不能为空");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
            return R.error("文件大小不能超过10MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return R.error("只支持Excel文件格式(.xlsx/.xls)");
        }

        try {
            CompletableFuture<Long> future = userImportExportService.importUsersAsync(file);
            Long taskId = future.get(); // 获取任务ID
            
            return R.ok("导入任务已创建，请通过任务ID查询进度", taskId);
        } catch (Exception e) {
            log.error("创建用户导入任务失败", e);
            return R.error("创建导入任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/export")
    @Operation(summary = "异步导出用户")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    public R<Long> exportUsers(
            @Parameter(description = "查询条件") @Valid @RequestBody SysUserQueryDTO queryCondition) {
        
        try {
            CompletableFuture<Long> future = userImportExportService.exportUsersAsync(queryCondition);
            Long taskId = future.get(); // 获取任务ID
            
            return R.ok("导出任务已创建，请通过任务ID查询进度", taskId);
        } catch (Exception e) {
            log.error("创建用户导出任务失败", e);
            return R.error("创建导出任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "查询任务详情")
    @PreAuthorize("@ss.hasPermission('system:user:import') or @ss.hasPermission('system:user:export')")
    public R<ImportExportTask> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        ImportExportTask task = userImportExportService.getTaskDetail(taskId);
        if (task == null) {
            return R.error("任务不存在");
        }
        
        return R.ok(task);
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询用户的导入导出任务列表")
    @PreAuthorize("@ss.hasPermission('system:user:import') or @ss.hasPermission('system:user:export')")
    public R<PageResult<ImportExportTask>> getUserTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        
        PageResult<ImportExportTask> result = userImportExportService.getUserTasks(pageNum, pageSize);
        return R.ok(result);
    }

    @GetMapping("/task/{taskId}/download-error-report")
    @Operation(summary = "下载导入错误报告")
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public void downloadErrorReport(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId,
            HttpServletResponse response) {
        
        userImportExportService.downloadErrorReport(taskId, response);
    }

    @GetMapping("/task/{taskId}/download-export-file")
    @Operation(summary = "下载导出文件")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    public void downloadExportFile(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId,
            HttpServletResponse response) {
        
        userImportExportService.downloadExportFile(taskId, response);
    }

    @DeleteMapping("/task/{taskId}/cancel")
    @Operation(summary = "取消任务")
    @PreAuthorize("@ss.hasPermission('system:user:import') or @ss.hasPermission('system:user:export')")
    public R<Boolean> cancelTask(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        boolean success = userImportExportService.cancelTask(taskId);
        return success ? R.ok("任务已取消", true) 
                       : R.error("任务取消失败");
    }

    @GetMapping("/progress/{taskId}")
    @Operation(summary = "获取任务进度（轮询接口）")
    @PreAuthorize("@ss.hasPermission('system:user:import') or @ss.hasPermission('system:user:export')")
    public R<TaskProgress> getTaskProgress(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        ImportExportTask task = userImportExportService.getTaskDetail(taskId);
        if (task == null) {
            return R.error("任务不存在");
        }

        TaskProgress progress = new TaskProgress();
        progress.setTaskId(taskId);
        progress.setStatus(task.getStatus().name());
        progress.setProgress(task.getProgress());
        progress.setTotalCount(task.getTotalCount());
        progress.setSuccessCount(task.getSuccessCount());
        progress.setFailCount(task.getFailCount());
        progress.setErrorMessage(task.getErrorMessage());
        progress.setCompleted(task.getStatus() == ImportExportTask.TaskStatus.SUCCESS || 
                             task.getStatus() == ImportExportTask.TaskStatus.FAILED);
        
        return R.ok(progress);
    }

    /**
     * 任务进度响应对象
     */
    public static class TaskProgress {
        private Long taskId;
        private String status;
        private Integer progress;
        private Integer totalCount;
        private Integer successCount;
        private Integer failCount;
        private String errorMessage;
        private Boolean completed;

        // Getters and Setters
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
        
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        
        public Integer getSuccessCount() { return successCount; }
        public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
        
        public Integer getFailCount() { return failCount; }
        public void setFailCount(Integer failCount) { this.failCount = failCount; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }
}