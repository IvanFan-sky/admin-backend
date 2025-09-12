package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.infra.api.dto.ImportExportTaskCreateDTO;
import com.admin.module.infra.api.dto.ImportExportTaskPageDTO;
import com.admin.module.infra.api.dto.ImportExportTaskUpdateDTO;
import com.admin.module.infra.api.service.ImportErrorDetailService;
import com.admin.module.infra.api.service.ImportExportTaskService;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.api.vo.ImportExportStatisticsVO;
import com.admin.module.infra.api.vo.ImportExportTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导入导出任务控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "导入导出任务管理")
@RestController
@RequestMapping("/admin-api/infra/import-export")
@RequiredArgsConstructor
@Validated
public class ImportExportTaskController {

    private final ImportExportTaskService importExportTaskService;
    private final ImportErrorDetailService importErrorDetailService;

    @PostMapping("/create")
    @Operation(summary = "创建导入导出任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:create')")
    public R<Long> createTask(@Valid @RequestBody ImportExportTaskCreateDTO createDTO) {
        Long taskId = importExportTaskService.createTask(createDTO);
        return R.ok(taskId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新导入导出任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:update')")
    public R<Boolean> updateTask(@Valid @RequestBody ImportExportTaskUpdateDTO updateDTO) {
        importExportTaskService.updateTask(updateDTO);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除导入导出任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:delete')")
    public R<Boolean> deleteTask(@Parameter(description = "任务ID", required = true) @RequestParam("id") Long id) {
        importExportTaskService.deleteTask(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取导入导出任务详情")
    @PreAuthorize("@ss.hasPermission('infra:import-export:query')")
    public R<ImportExportTaskVO> getTask(@Parameter(description = "任务ID", required = true) @RequestParam("id") Long id) {
        ImportExportTaskVO taskVO = importExportTaskService.getTask(id);
        return R.ok(taskVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获取导入导出任务分页列表")
    @PreAuthorize("@ss.hasPermission('infra:import-export:query')")
    public R<PageResult<ImportExportTaskVO>> getTaskPage(@Valid ImportExportTaskPageDTO pageDTO) {
        PageResult<ImportExportTaskVO> pageResult = importExportTaskService.getTaskPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/user-tasks")
    @Operation(summary = "获取当前用户的任务列表")
    public R<List<ImportExportTaskVO>> getUserTasks() {
        // 获取当前用户
        String currentUser = getCurrentUsername();
        List<ImportExportTaskVO> taskList = importExportTaskService.getUserTasks(currentUser);
        return R.ok(taskList);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取导入导出统计信息")
    @PreAuthorize("@ss.hasPermission('infra:import-export:query')")
    public R<ImportExportStatisticsVO> getStatistics() {
        ImportExportStatisticsVO statistics = importExportTaskService.getStatistics();
        return R.ok(statistics);
    }

    @PostMapping("/start-import")
    @Operation(summary = "启动导入任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:execute')")
    public R<Boolean> startImportTask(@Parameter(description = "任务ID", required = true) @RequestParam("taskId") Long taskId) {
        importExportTaskService.startImportTask(taskId);
        return R.ok(true);
    }

    @PostMapping("/start-export")
    @Operation(summary = "启动导出任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:execute')")
    public R<Boolean> startExportTask(@Parameter(description = "任务ID", required = true) @RequestParam("taskId") Long taskId) {
        importExportTaskService.startExportTask(taskId);
        return R.ok(true);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消任务")
    @PreAuthorize("@ss.hasPermission('infra:import-export:execute')")
    public R<Boolean> cancelTask(@Parameter(description = "任务ID", required = true) @RequestParam("taskId") Long taskId) {
        importExportTaskService.cancelTask(taskId);
        return R.ok(true);
    }

    @GetMapping("/error-details")
    @Operation(summary = "获取任务错误详情")
    @PreAuthorize("@ss.hasPermission('infra:import-export:query')")
    public R<List<ImportErrorDetailVO>> getErrorDetails(@Parameter(description = "任务ID", required = true) @RequestParam("taskId") Long taskId) {
        List<ImportErrorDetailVO> errorDetails = importErrorDetailService.getErrorDetailsByTaskId(taskId);
        return R.ok(errorDetails);
    }

    /**
     * 获取当前用户名
     * TODO: 这里需要根据实际的安全框架实现
     */
    private String getCurrentUsername() {
        // 暂时返回固定值，实际应该从SecurityContext获取
        return "admin";
    }
}