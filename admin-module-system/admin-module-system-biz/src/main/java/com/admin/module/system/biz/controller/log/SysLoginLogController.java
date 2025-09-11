package com.admin.module.system.biz.controller.log;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.log.SysLoginLogQueryDTO;
import com.admin.module.system.api.service.log.SysLoginLogService;
import com.admin.module.system.api.vo.log.SysLoginLogVO;
import com.admin.module.system.api.vo.log.LoginLogStatisticsVO;
import com.admin.module.system.api.vo.log.LoginTypeStatisticsVO;
import com.admin.module.system.api.vo.log.LoginLocationStatisticsVO;
import com.admin.module.system.api.vo.log.BrowserStatisticsVO;
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
 * 系统登录日志管理控制器
 * 
 * 提供登录日志相关的RESTful API接口
 * 包括日志查询、删除、导出、统计、在线用户管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 登录日志管理")
@RestController
@RequestMapping("/system/login-log")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    /**
     * 获取登录日志分页列表
     * 
     * @param queryDTO 查询条件
     * @return 登录日志分页结果
     */
    @Operation(summary = "获取登录日志分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<PageResult<SysLoginLogVO>> getLoginLogPage(@Valid SysLoginLogQueryDTO queryDTO) {
        PageResult<SysLoginLogVO> pageResult = loginLogService.getLoginLogPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取登录日志列表
     * 
     * @param queryDTO 查询条件
     * @return 登录日志列表
     */
    @Operation(summary = "获取登录日志列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<PageResult<SysLoginLogVO>> getLoginLogList(@Valid SysLoginLogQueryDTO queryDTO) {
        PageResult<SysLoginLogVO> pageResult = loginLogService.getLoginLogPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取登录日志详情
     * 
     * @param id 日志ID
     * @return 登录日志详情
     */
    @Operation(summary = "获取登录日志详情")
    @Parameter(name = "id", description = "登录日志编号", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<SysLoginLogVO> getLoginLog(@PathVariable @NotNull @Positive Long id) {
        SysLoginLogVO loginLog = loginLogService.getLoginLog(id);
        return R.ok(loginLog);
    }

    /**
     * 批量删除登录日志
     * 
     * @param ids 日志ID列表
     * @return 删除结果
     */
    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('system:loginlog:delete')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除登录日志")
    public R<Integer> deleteLoginLogsBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = loginLogService.deleteLoginLogsBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 根据时间范围删除登录日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除结果
     */
    @Operation(summary = "根据时间范围删除登录日志")
    @Parameter(name = "startTime", description = "开始时间", required = true)
    @Parameter(name = "endTime", description = "结束时间", required = true)
    @DeleteMapping("/time-range")
    @PreAuthorize("@ss.hasPermission('system:loginlog:delete')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.DELETE, description = "按时间范围删除登录日志")
    public R<Integer> deleteLoginLogsByTimeRange(@RequestParam @NotNull LocalDateTime startTime,
                                               @RequestParam @NotNull LocalDateTime endTime) {
        int deleteCount = loginLogService.deleteLoginLogsByTimeRange(startTime, endTime);
        return R.ok(deleteCount);
    }

    /**
     * 清空登录日志
     * 
     * @return 删除结果
     */
    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clear")
    @PreAuthorize("@ss.hasPermission('system:loginlog:delete')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.CLEAN, description = "清空登录日志")
    public R<Integer> clearLoginLogs() {
        int deleteCount = loginLogService.clearLoginLogs();
        return R.ok(deleteCount);
    }

    /**
     * 清理过期登录日志
     * 
     * @param retentionDays 保留天数
     * @return 清理结果
     */
    @Operation(summary = "清理过期登录日志")
    @Parameter(name = "retentionDays", description = "保留天数", example = "30")
    @DeleteMapping("/clean")
    @PreAuthorize("@ss.hasPermission('system:loginlog:delete')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.CLEAN, description = "清理过期登录日志")
    public R<Integer> cleanExpiredLoginLogs(@RequestParam(defaultValue = "30") @Positive Integer retentionDays) {
        int cleanCount = loginLogService.cleanExpiredLoginLogs(retentionDays);
        return R.ok(cleanCount);
    }

    /**
     * 导出登录日志
     * 
     * @param queryDTO 查询条件
     * @return 登录日志列表
     */
    @Operation(summary = "导出登录日志")
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:loginlog:export')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.EXPORT, description = "导出登录日志")
    public R<List<SysLoginLogVO>> exportLoginLogs(@Valid @RequestBody SysLoginLogQueryDTO queryDTO) {
        List<SysLoginLogVO> loginLogs = loginLogService.exportLoginLogs(queryDTO);
        return R.ok(loginLogs);
    }

    /**
     * 获取在线用户列表
     * 
     * @return 在线用户列表
     */
    @Operation(summary = "获取在线用户列表")
    @GetMapping("/online")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<List<SysLoginLogVO>> getOnlineUsers() {
        List<SysLoginLogVO> onlineUsers = loginLogService.getOnlineUsers();
        return R.ok(onlineUsers);
    }

    /**
     * 强制登出用户
     * 
     * @param tokenId 令牌ID
     * @return 操作结果
     */
    @Operation(summary = "强制登出用户")
    @Parameter(name = "tokenId", description = "令牌ID", required = true)
    @PostMapping("/logout")
    @PreAuthorize("@ss.hasPermission('system:loginlog:forceLogout')")
    @OperationLog(title = "登录日志管理", businessType = OperationLog.BusinessType.FORCE, description = "强制登出用户")
    public R<Boolean> forceLogoutUser(@RequestParam @NotNull String tokenId) {
        loginLogService.updateLogoutInfo(tokenId, "强制登出");
        return R.ok(true);
    }

    /**
     * 获取用户登录日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 用户登录日志列表
     */
    @Operation(summary = "获取用户登录日志")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @Parameter(name = "limit", description = "限制条数", example = "10")
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<List<SysLoginLogVO>> getUserLoginLogs(@PathVariable @NotNull @Positive Long userId,
                                                 @RequestParam(required = false) LocalDateTime startTime,
                                                 @RequestParam(required = false) LocalDateTime endTime,
                                                 @RequestParam(required = false) @Positive Integer limit) {
        List<SysLoginLogVO> loginLogs = loginLogService.getUserLoginLogs(userId, startTime, endTime, limit);
        return R.ok(loginLogs);
    }

    /**
     * 获取登录日志统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @Operation(summary = "获取登录日志统计信息")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<LoginLogStatisticsVO> getLoginLogStatistics(
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        LoginLogStatisticsVO statistics = 
                loginLogService.getLoginLogStatistics(startTime, endTime);
        return R.ok(statistics);
    }

    /**
     * 获取登录方式统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录方式统计列表
     */
    @Operation(summary = "获取登录方式统计")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @GetMapping("/statistics/login-type")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<List<LoginTypeStatisticsVO>> getLoginTypeStatistics(
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        List<LoginTypeStatisticsVO> statistics =
                loginLogService.getLoginTypeStatistics(startTime, endTime);
        return R.ok(statistics);
    }

    /**
     * 获取登录地点统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 登录地点统计列表
     */
    @Operation(summary = "获取登录地点统计")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @Parameter(name = "limit", description = "限制条数", example = "10")
    @GetMapping("/statistics/location")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<List<LoginLocationStatisticsVO>> getLocationStatistics(
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        List<LoginLocationStatisticsVO> statistics =
                loginLogService.getLocationStatistics(startTime, endTime, limit);
        return R.ok(statistics);
    }

    /**
     * 获取浏览器统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 浏览器统计列表
     */
    @Operation(summary = "获取浏览器统计")
    @Parameter(name = "startTime", description = "开始时间")
    @Parameter(name = "endTime", description = "结束时间")
    @Parameter(name = "limit", description = "限制条数", example = "10")
    @GetMapping("/statistics/browser")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<List<BrowserStatisticsVO>> getBrowserStatistics(
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        List<BrowserStatisticsVO> statistics =
                loginLogService.getBrowserStatistics(startTime, endTime, limit);
        return R.ok(statistics);
    }
}