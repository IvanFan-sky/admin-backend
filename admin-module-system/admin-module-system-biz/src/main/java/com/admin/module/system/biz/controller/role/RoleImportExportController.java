package com.admin.module.system.biz.controller.role;

import com.admin.common.core.domain.PageResult;
import com.admin.common.result.CommonResult;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.module.system.api.dto.RolePageDTO;
import com.admin.module.system.api.service.RoleImportExportService;
import com.admin.module.system.api.service.RoleImportExportService.RoleImportValidationResult;
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
 * 角色导入导出控制器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "角色导入导出管理")
@RestController
@RequestMapping("/admin-api/system/role-import-export")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RoleImportExportController {

    private final RoleImportExportService roleImportExportService;

    @GetMapping("/import-template")
    @Operation(summary = "下载角色导入模板")
    @PreAuthorize("@ss.hasPermission('system:role:import')")
    public void downloadImportTemplate(HttpServletResponse response) {
        roleImportExportService.downloadImportTemplate(response);
    }

    @PostMapping("/validate-import")
    @Operation(summary = "验证导入文件")
    @PreAuthorize("@ss.hasPermission('system:role:import')")
    public CommonResult<RoleImportValidationResult> validateImportFile(
            @Parameter(description = "导入文件") @RequestParam("file") MultipartFile file) {
        
        // 文件基本验证
        if (file.isEmpty()) {
            return CommonResult.error("导入文件不能为空");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
            return CommonResult.error("文件大小不能超过10MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return CommonResult.error("只支持Excel文件格式(.xlsx/.xls)");
        }

        RoleImportValidationResult result = roleImportExportService.validateImportFile(file);
        return CommonResult.success(result);
    }

    @PostMapping("/import")
    @Operation(summary = "异步导入角色")
    @PreAuthorize("@ss.hasPermission('system:role:import')")
    public CommonResult<Long> importRoles(
            @Parameter(description = "导入文件") @RequestParam("file") MultipartFile file) {
        
        // 文件基本验证
        if (file.isEmpty()) {
            return CommonResult.error("导入文件不能为空");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
            return CommonResult.error("文件大小不能超过10MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return CommonResult.error("只支持Excel文件格式(.xlsx/.xls)");
        }

        try {
            CompletableFuture<Long> future = roleImportExportService.importRolesAsync(file);
            Long taskId = future.get(); // 获取任务ID
            
            return CommonResult.success(taskId, "导入任务已创建，请通过任务ID查询进度");
        } catch (Exception e) {
            log.error("创建角色导入任务失败", e);
            return CommonResult.error("创建导入任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/export")
    @Operation(summary = "异步导出角色")
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public CommonResult<Long> exportRoles(
            @Parameter(description = "查询条件") @Valid @RequestBody RolePageDTO queryCondition) {
        
        try {
            CompletableFuture<Long> future = roleImportExportService.exportRolesAsync(queryCondition);
            Long taskId = future.get(); // 获取任务ID
            
            return CommonResult.success(taskId, "导出任务已创建，请通过任务ID查询进度");
        } catch (Exception e) {
            log.error("创建角色导出任务失败", e);
            return CommonResult.error("创建导出任务失败: " + e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "查询任务详情")
    @PreAuthorize("@ss.hasPermission('system:role:import') or @ss.hasPermission('system:role:export')")
    public CommonResult<ImportExportTask> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        ImportExportTask task = roleImportExportService.getTaskDetail(taskId);
        if (task == null) {
            return CommonResult.error("任务不存在");
        }
        
        return CommonResult.success(task);
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询用户的导入导出任务列表")
    @PreAuthorize("@ss.hasPermission('system:role:import') or @ss.hasPermission('system:role:export')")
    public CommonResult<PageResult<ImportExportTask>> getUserTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        
        PageResult<ImportExportTask> result = roleImportExportService.getUserTasks(pageNum, pageSize);
        return CommonResult.success(result);
    }

    @GetMapping("/task/{taskId}/download-error-report")
    @Operation(summary = "下载导入错误报告")
    @PreAuthorize("@ss.hasPermission('system:role:import')")
    public void downloadErrorReport(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId,
            HttpServletResponse response) {
        
        roleImportExportService.downloadErrorReport(taskId, response);
    }

    @GetMapping("/task/{taskId}/download-export-file")
    @Operation(summary = "下载导出文件")
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public void downloadExportFile(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId,
            HttpServletResponse response) {
        
        roleImportExportService.downloadExportFile(taskId, response);
    }

    @DeleteMapping("/task/{taskId}/cancel")
    @Operation(summary = "取消任务")
    @PreAuthorize("@ss.hasPermission('system:role:import') or @ss.hasPermission('system:role:export')")
    public CommonResult<Boolean> cancelTask(
            @Parameter(description = "任务ID") @PathVariable @NotNull Long taskId) {
        
        boolean success = roleImportExportService.cancelTask(taskId);
        return success ? CommonResult.success(true, "任务已取消") 
                       : CommonResult.error("任务取消失败");
    }
}