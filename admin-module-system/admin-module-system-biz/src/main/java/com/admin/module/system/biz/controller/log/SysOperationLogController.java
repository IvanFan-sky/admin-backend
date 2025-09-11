package com.admin.module.system.biz.controller.log;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.log.SysOperationLogQueryDTO;
import com.admin.module.system.api.service.log.SysOperationLogService;
import com.admin.module.system.api.vo.log.SysOperationLogVO;
import com.admin.module.system.api.vo.log.OperationLogStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 系统操作日志管理控制器
 * 
 * 提供操作日志相关的RESTful API接口
 * 包括日志查询、删除、导出、统计等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 操作日志管理")
@RestController
@RequestMapping("/system/operation-log")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysOperationLogController {

    private final SysOperationLogService operationLogService;

    /**
     * 获取操作日志分页列表
     * 
     * @param queryDTO 查询条件
     * @return 操作日志分页结果
     */
    @Operation(summary = "获取操作日志分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:operationlog:query')")
    public R<PageResult<SysOperationLogVO>> getOperationLogPage(@Valid SysOperationLogQueryDTO queryDTO) {
        PageResult<SysOperationLogVO> pageResult = operationLogService.getOperationLogPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取操作日志详情
     * 
     * @param id 日志ID
     * @return 操作日志详情
     */
    @Operation(summary = "获取操作日志详情")
    @Parameter(name = "id", description = "操作日志编号", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:operationlog:query')")
    public R<SysOperationLogVO> getOperationLog(@PathVariable @NotNull @Positive Long id) {
        SysOperationLogVO operationLog = operationLogService.getOperationLog(id);
        return R.ok(operationLog);
    }

    /**
     * 批量删除操作日志
     * 
     * @param ids 日志ID列表
     * @return 删除结果
     */
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('system:operationlog:delete')")
    @OperationLog(title = "操作日志管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除操作日志")
    public R<Integer> deleteOperationLogsBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = operationLogService.deleteOperationLogsBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 根据时间范围删除操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除结果
     */
    @Operation(summary = "根据时间范围删除操作日志")
    @Parameter(name = "startTime", description = "开始时间", required = true)
    @Parameter(name = "endTime", description = "结束时间", required = true)
    @DeleteMapping("/time-range")
    @PreAuthorize("@ss.hasPermission('system:operationlog:delete')")
    @OperationLog(title = "操作日志管理", businessType = OperationLog.BusinessType.DELETE, description = "按时间范围删除操作日志")
    public R<Integer> deleteOperationLogsByTimeRange(@RequestParam @NotNull LocalDateTime startTime,
                                                    @RequestParam @NotNull LocalDateTime endTime) {
        int deleteCount = operationLogService.deleteOperationLogsByTimeRange(startTime, endTime);
        return R.ok(deleteCount);
    }

    /**
     * 清空操作日志
     * 
     * @return 删除结果
     */
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    @PreAuthorize("@ss.hasPermission('system:operationlog:delete')")
    @OperationLog(title = "操作日志管理", businessType = OperationLog.BusinessType.CLEAN, description = "清空操作日志")
    public R<Integer> clearOperationLogs() {
        int deleteCount = operationLogService.clearOperationLogs();
        return R.ok(deleteCount);
    }

    /**
     * 清理过期操作日志
     * 
     * @param retentionDays 保留天数
     * @return 清理结果
     */
    @Operation(summary = "清理过期操作日志")
    @Parameter(name = "retentionDays", description = "保留天数", example = "30")
    @DeleteMapping("/clean")
    @PreAuthorize("@ss.hasPermission('system:operationlog:delete')")
    @OperationLog(title = "操作日志管理", businessType = OperationLog.BusinessType.CLEAN, description = "清理过期操作日志")
    public R<Integer> cleanExpiredOperationLogs(@RequestParam(defaultValue = "30") @Positive Integer retentionDays) {
        int cleanCount = operationLogService.cleanExpiredOperationLogs(retentionDays);
        return R.ok(cleanCount);
    }

    /**
     * 导出操作日志
     * 
     * @param queryDTO 查询条件
     * @return 操作日志列表
     */
    @Operation(summary = "导出操作日志")
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:operationlog:export')")
    @OperationLog(title = "操作日志管理", businessType = OperationLog.BusinessType.EXPORT, description = "导出操作日志")
    public R<List<SysOperationLogVO>> exportOperationLogs(@Valid @RequestBody SysOperationLogQueryDTO queryDTO) {
        List<SysOperationLogVO> operationLogs = operationLogService.exportOperationLogs(queryDTO);
        return R.ok(operationLogs);
    }

    /**
     * 获取操作日志统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @Operation(summary = "获取操作日志统计信息")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermission('system:operationlog:query')")
    public R<OperationLogStatisticsVO> getOperationLogStatistics(
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        OperationLogStatisticsVO statistics =
                operationLogService.getOperationLogStatistics(startTime, endTime);
        return R.ok(statistics);
    }

    /**
     * 获取用户操作日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户操作日志列表
     */
    @Operation(summary = "获取用户操作日志")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermission('system:operationlog:query')")
    public R<List<SysOperationLogVO>> getUserOperationLogs(@PathVariable @NotNull @Positive Long userId,
                                                          @RequestParam(required = false) LocalDateTime startTime,
                                                          @RequestParam(required = false) LocalDateTime endTime) {
        List<SysOperationLogVO> operationLogs = operationLogService.getUserOperationLogs(userId, startTime, endTime);
        return R.ok(operationLogs);
    }
}