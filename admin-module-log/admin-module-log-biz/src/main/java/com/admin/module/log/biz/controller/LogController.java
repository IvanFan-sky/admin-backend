package com.admin.module.log.biz.controller;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.service.LogExportService;
import com.admin.module.log.api.service.LogQueryService;
import com.admin.module.log.api.vo.LoginLogVO;
import com.admin.module.log.api.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志管理控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "日志管理")
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
@Validated
public class LogController {

    private final LogQueryService logQueryService;
    private final LogExportService logExportService;

    @GetMapping("/operation/page")
    @Operation(summary = "分页查询操作日志")
    @PreAuthorize("@ss.hasPermission('system:operlog:query')")
    public R<PageResult<OperationLogVO>> getOperationLogPage(@Valid OperationLogQueryDTO queryDTO) {
        PageResult<OperationLogVO> pageResult = logQueryService.getOperationLogPage(queryDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/operation/{id}")
    @Operation(summary = "获取操作日志详情")
    @PreAuthorize("@ss.hasPermission('system:operlog:query')")
    public R<OperationLogVO> getOperationLog(@Parameter(description = "日志ID") @PathVariable Long id) {
        OperationLogVO operationLog = logQueryService.getOperationLogById(id);
        return R.ok(operationLog);
    }

    @DeleteMapping("/operation")
    @Operation(summary = "删除操作日志")
    @OperationLog(title = "操作日志", description = "删除操作日志", businessType = OperationLog.BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:operlog:remove')")
    public R<Void> deleteOperationLogs(@Parameter(description = "日志ID数组") @RequestBody Long[] ids) {
        logQueryService.deleteOperationLogs(ids);
        return R.ok();
    }

    @DeleteMapping("/operation/clear")
    @Operation(summary = "清空操作日志")
    @OperationLog(title = "操作日志", description = "清空操作日志", businessType = OperationLog.BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermission('system:operlog:remove')")
    public R<Void> clearOperationLogs() {
        logQueryService.clearOperationLogs();
        return R.ok();
    }

    @GetMapping("/login/page")
    @Operation(summary = "分页查询登录日志")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<PageResult<LoginLogVO>> getLoginLogPage(@Valid LoginLogQueryDTO queryDTO) {
        PageResult<LoginLogVO> pageResult = logQueryService.getLoginLogPage(queryDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/login/{id}")
    @Operation(summary = "获取登录日志详情")
    @PreAuthorize("@ss.hasPermission('system:loginlog:query')")
    public R<LoginLogVO> getLoginLog(@Parameter(description = "日志ID") @PathVariable Long id) {
        LoginLogVO loginLog = logQueryService.getLoginLogById(id);
        return R.ok(loginLog);
    }

    @DeleteMapping("/login")
    @Operation(summary = "删除登录日志")
    @OperationLog(title = "登录日志", description = "删除登录日志", businessType = OperationLog.BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:loginlog:remove')")
    public R<Void> deleteLoginLogs(@Parameter(description = "日志ID数组") @RequestBody Long[] ids) {
        logQueryService.deleteLoginLogs(ids);
        return R.ok();
    }

    @DeleteMapping("/login/clear")
    @Operation(summary = "清空登录日志")
    @OperationLog(title = "登录日志", description = "清空登录日志", businessType = OperationLog.BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermission('system:loginlog:remove')")
    public R<Void> clearLoginLogs() {
        logQueryService.clearLoginLogs();
        return R.ok();
    }

    @GetMapping("/operation/export")
    @Operation(summary = "导出操作日志")
    @OperationLog(title = "操作日志", description = "导出操作日志", businessType = OperationLog.BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:operlog:export')")
    public ResponseEntity<byte[]> exportOperationLogs(@Valid OperationLogQueryDTO queryDTO) {
        byte[] data = logExportService.exportOperationLogs(queryDTO);
        String fileName = "operation_log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(data);
    }

    @GetMapping("/login/export")
    @Operation(summary = "导出登录日志")
    @OperationLog(title = "登录日志", description = "导出登录日志", businessType = OperationLog.BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:loginlog:export')")
    public ResponseEntity<byte[]> exportLoginLogs(@Valid LoginLogQueryDTO queryDTO) {
        byte[] data = logExportService.exportLoginLogs(queryDTO);
        String fileName = "login_log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(data);
    }
}