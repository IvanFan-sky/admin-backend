package com.admin.module.log.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.module.log.api.service.LogStatisticsService;
import com.admin.module.log.api.vo.LogStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 日志统计控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "日志统计")
@RestController
@RequestMapping("/admin-api/log/statistics")
@RequiredArgsConstructor
@Validated
public class LogStatisticsController {

    private final LogStatisticsService logStatisticsService;

    @GetMapping("/overview")
    @Operation(summary = "获取日志统计概览")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<LogStatisticsVO> getLogStatistics() {
        LogStatisticsVO statistics = logStatisticsService.getLogStatistics();
        return R.ok(statistics);
    }

    @GetMapping("/operation/business-type")
    @Operation(summary = "获取操作日志按业务类型统计")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<Map<String, Long>> getOperationLogByBusinessType(
            @Parameter(description = "统计天数，默认30天") @RequestParam(defaultValue = "30") Integer days) {
        Map<String, Long> statistics = logStatisticsService.getOperationLogByBusinessType(days);
        return R.ok(statistics);
    }

    @GetMapping("/operation/trend")
    @Operation(summary = "获取操作日志趋势统计")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<List<LogStatisticsVO.DailyStats>> getOperationLogTrend(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<LogStatisticsVO.DailyStats> trend = logStatisticsService.getOperationLogTrend(startDate, endDate);
        return R.ok(trend);
    }

    @GetMapping("/login/trend")
    @Operation(summary = "获取登录日志趋势统计")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<List<LogStatisticsVO.DailyStats>> getLoginLogTrend(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<LogStatisticsVO.DailyStats> trend = logStatisticsService.getLoginLogTrend(startDate, endDate);
        return R.ok(trend);
    }

    @GetMapping("/system/performance")
    @Operation(summary = "获取系统性能统计")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<LogStatisticsVO.SystemOverview> getSystemPerformanceStats(
            @Parameter(description = "统计天数，默认7天") @RequestParam(defaultValue = "7") Integer days) {
        LogStatisticsVO.SystemOverview performance = logStatisticsService.getSystemPerformanceStats(days);
        return R.ok(performance);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表盘数据")
    @PreAuthorize("@ss.hasPermission('system:log:statistics')")
    public R<Map<String, Object>> getDashboardData() {
        // 获取关键指标数据
        LogStatisticsVO statistics = logStatisticsService.getLogStatistics();
        
        // 构建仪表盘数据
        Map<String, Object> dashboardData = Map.of(
            "todayOperations", statistics.getOperationLog().getTodayCount(),
            "todayLogins", statistics.getLoginLog().getTodayCount(),
            "errorRate", statistics.getSystemOverview().getErrorRate(),
            "avgResponseTime", statistics.getSystemOverview().getAvgResponseTime(),
            "activeUsers", statistics.getSystemOverview().getActiveUserCount(),
            "operationTrend", statistics.getOperationLog().getDailyTrend(),
            "loginTrend", statistics.getLoginLog().getDailyTrend(),
            "topModules", statistics.getSystemOverview().getTopModules()
        );
        
        return R.ok(dashboardData);
    }
}