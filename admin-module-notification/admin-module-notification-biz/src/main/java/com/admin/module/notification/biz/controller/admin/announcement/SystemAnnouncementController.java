package com.admin.module.notification.biz.controller.admin.announcement;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.common.annotation.OperationLog;
import com.admin.framework.excel.util.ExcelUtils;
import com.alibaba.excel.EasyExcel;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementCreateDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementPageDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementUpdateDTO;
import com.admin.module.notification.api.service.announcement.SystemAnnouncementService;
import com.admin.module.notification.api.vo.announcement.SystemAnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


/**
 * 系统公告管理 Controller
 *
 * @author admin
 * @since 2025-01-14
 */
@Tag(name = "管理后台 - 系统公告管理")
@RestController
@RequestMapping("/notification/system-announcement")
@RequiredArgsConstructor
@Validated
public class SystemAnnouncementController {

    private final SystemAnnouncementService systemAnnouncementService;

    @GetMapping("/page")
    @Operation(summary = "获得系统公告分页列表")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<PageResult<SystemAnnouncementVO>> getSystemAnnouncementPage(@Valid SystemAnnouncementPageDTO pageDTO) {
        PageResult<SystemAnnouncementVO> pageResult = systemAnnouncementService.getSystemAnnouncementPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得系统公告列表")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<List<SystemAnnouncementVO>> getSystemAnnouncementList() {
        List<SystemAnnouncementVO> list = systemAnnouncementService.getSystemAnnouncementList();
        return R.ok(list);
    }

    @GetMapping("/effective")
    @Operation(summary = "获得有效的系统公告列表")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<List<SystemAnnouncementVO>> getEffectiveAnnouncements() {
        List<SystemAnnouncementVO> list = systemAnnouncementService.getEffectiveAnnouncements();
        return R.ok(list);
    }

    @GetMapping("/top")
    @Operation(summary = "获得置顶系统公告列表")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<List<SystemAnnouncementVO>> getTopAnnouncements() {
        List<SystemAnnouncementVO> list = systemAnnouncementService.getTopSystemAnnouncements();
        return R.ok(list);
    }

    @GetMapping("/popup")
    @Operation(summary = "获得弹窗系统公告列表")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<List<SystemAnnouncementVO>> getPopupAnnouncements() {
        List<SystemAnnouncementVO> list = systemAnnouncementService.getPopupAnnouncements();
        return R.ok(list);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "根据类型获得系统公告列表")
    @Parameter(name = "type", description = "公告类型", required = true)
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<List<SystemAnnouncementVO>> getAnnouncementsByType(@PathVariable("type") Integer type) {
        List<SystemAnnouncementVO> list = systemAnnouncementService.getAnnouncementsByType(type);
        return R.ok(list);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统公告详情")
    @Parameter(name = "id", description = "公告编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<SystemAnnouncementVO> getSystemAnnouncement(@RequestParam("id") Long id) {
        SystemAnnouncementVO systemAnnouncement = systemAnnouncementService.getSystemAnnouncement(id);
        return R.ok(systemAnnouncement);
    }

    @PostMapping("/create")
    @Operation(summary = "创建系统公告")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:create')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.INSERT, description = "创建系统公告")
    public R<Long> createSystemAnnouncement(@Valid @RequestBody SystemAnnouncementCreateDTO createDTO) {
        Long systemAnnouncementId = systemAnnouncementService.createSystemAnnouncement(createDTO);
        return R.ok(systemAnnouncementId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统公告")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:update')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新系统公告")
    public R<Boolean> updateSystemAnnouncement(@Valid @RequestBody SystemAnnouncementUpdateDTO updateDTO) {
        systemAnnouncementService.updateSystemAnnouncement(updateDTO);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统公告")
    @Parameter(name = "id", description = "公告编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:delete')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.DELETE, description = "删除系统公告")
    public R<Boolean> deleteSystemAnnouncement(@RequestParam("id") Long id) {
        systemAnnouncementService.deleteSystemAnnouncement(id);
        return R.ok(true);
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除系统公告")
    @Parameter(name = "ids", description = "公告编号列表", required = true, example = "[1024, 1025]")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:delete')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除系统公告")
    public R<Boolean> batchDeleteSystemAnnouncement(@RequestParam("ids") List<Long> ids) {
        systemAnnouncementService.deleteSystemAnnouncements(ids.toArray(new Long[0]));
        return R.ok(true);
    }

    @PutMapping("/publish")
    @Operation(summary = "发布系统公告")
    @Parameter(name = "id", description = "公告编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:publish')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.UPDATE, description = "发布系统公告")
    public R<Boolean> publishSystemAnnouncement(@RequestParam("id") Long id) {
        systemAnnouncementService.publishSystemAnnouncement(id);
        return R.ok(true);
    }

    @PutMapping("/withdraw")
    @Operation(summary = "撤回系统公告")
    @Parameter(name = "id", description = "公告编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:withdraw')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.UPDATE, description = "撤回系统公告")
    public R<Boolean> withdrawSystemAnnouncement(@RequestParam("id") Long id) {
        systemAnnouncementService.revokeSystemAnnouncement(id);
        return R.ok(true);
    }

    @PutMapping("/batch-update-status")
    @Operation(summary = "批量更新系统公告状态")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:update')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量更新系统公告状态")
    public R<Boolean> batchUpdateStatus(@RequestParam("ids") List<Long> ids,
                                                   @RequestParam("status") Integer status) {
        systemAnnouncementService.batchUpdateStatus(ids, status);
        return R.ok(true);
    }

    @PutMapping("/batch-update-top-status")
    @Operation(summary = "批量更新系统公告置顶状态")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:update')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量更新系统公告置顶状态")
    public R<Boolean> batchUpdateTopStatus(@RequestParam("ids") List<Long> ids,
                                                      @RequestParam("isTop") Boolean isTop) {
        systemAnnouncementService.batchUpdateTopStatus(ids, isTop);
        return R.ok(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得系统公告统计信息")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:query')")
    public R<Object> getAnnouncementStatistics() {
        Object statistics = systemAnnouncementService.getSystemAnnouncementStatistics();
        return R.ok(statistics);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统公告 Excel")
    @PreAuthorize("@ss.hasPermission('notification:system-announcement:export')")
    @OperationLog(title = "系统公告管理", businessType = OperationLog.BusinessType.EXPORT, description = "导出系统公告 Excel")
    public void exportSystemAnnouncementExcel(@Valid SystemAnnouncementPageDTO pageDTO,
                                              HttpServletResponse response) throws IOException {
        // 设置一个很大的页面大小来实现不分页效果
        pageDTO.setPageSize(Integer.MAX_VALUE);
        PageResult<SystemAnnouncementVO> pageResult = systemAnnouncementService.getSystemAnnouncementPage(pageDTO);
        List<SystemAnnouncementVO> list = pageResult.getRecords();
        
        // 设置响应头
        ExcelUtils.setExcelResponseHeader(response, "系统公告");
        
        // 导出 Excel
        EasyExcel.write(response.getOutputStream(), SystemAnnouncementVO.class)
                .sheet("系统公告")
                .doWrite(list);
    }
}