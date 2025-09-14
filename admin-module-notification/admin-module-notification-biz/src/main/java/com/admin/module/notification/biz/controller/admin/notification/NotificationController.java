package com.admin.module.notification.biz.controller.admin.notification;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.notification.api.dto.notification.NotificationCreateDTO;
import com.admin.module.notification.api.dto.notification.NotificationQueryDTO;
import com.admin.module.notification.api.dto.notification.NotificationUpdateDTO;
import com.admin.module.notification.api.service.notification.NotificationService;
import com.admin.module.notification.api.vo.notification.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知管理控制器
 * 
 * 提供通知管理相关的REST API接口
 * 包括通知的增删改查、发布、撤回等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 通知管理")
@RestController
@RequestMapping("/admin/notification/notification")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "获得通知分页列表", 
        description = "根据查询条件分页获取通知列表，支持按标题、类型、状态等条件筛选"
    )
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('notification:notification:query')")
    public R<PageResult<NotificationVO>> getNotificationPage(@Valid NotificationQueryDTO queryDTO) {
        PageResult<NotificationVO> pageResult = notificationService.getNotificationPage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(
        summary = "获得通知列表", 
        description = "根据查询条件获取通知列表，不分页返回所有匹配的通知数据"
    )
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('notification:notification:query')")
    public R<List<NotificationVO>> getNotificationList(@Valid NotificationQueryDTO queryDTO) {
        List<NotificationVO> list = notificationService.getNotificationList(queryDTO);
        return R.ok(list);
    }

    @Operation(
        summary = "获得通知详情", 
        description = "根据通知ID获取通知的详细信息"
    )
    @Parameter(name = "id", description = "通知ID", required = true, example = "1024")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:notification:query')")
    public R<NotificationVO> getNotification(@PathVariable("id") Long id) {
        NotificationVO notification = notificationService.getNotification(id);
        return R.ok(notification);
    }

    @Operation(
        summary = "创建通知", 
        description = "创建新通知，需要提供通知基本信息和内容"
    )
    @PostMapping
    @PreAuthorize("@ss.hasPermission('notification:notification:create')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.INSERT, description = "创建通知")
    public R<Long> createNotification(@Valid @RequestBody NotificationCreateDTO createDTO) {
        Long notificationId = notificationService.createNotification(createDTO);
        return R.ok(notificationId);
    }

    @Operation(
        summary = "更新通知信息", 
        description = "更新通知的基本信息，支持修改标题、内容、类型等"
    )
    @PutMapping
    @PreAuthorize("@ss.hasPermission('notification:notification:update')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新通知")
    public R<Boolean> updateNotification(@Valid @RequestBody NotificationUpdateDTO updateDTO) {
        notificationService.updateNotification(updateDTO);
        return R.ok(true);
    }

    @Operation(
        summary = "删除通知", 
        description = "根据通知ID删除通知，执行逻辑删除"
    )
    @Parameter(name = "id", description = "通知ID", required = true, example = "1024")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:notification:delete')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.DELETE, description = "删除通知")
    public R<Boolean> deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除通知")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('notification:notification:delete')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除通知")
    public R<Boolean> deleteNotifications(@RequestBody @NotEmpty(message = "删除通知不能为空") Long[] ids) {
        notificationService.deleteNotifications(ids);
        return R.ok(true);
    }

    @Operation(summary = "发布通知")
    @PutMapping("/publish/{id}")
    @PreAuthorize("@ss.hasPermission('notification:notification:publish')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "发布通知")
    public R<Boolean> publishNotification(@PathVariable("id") Long id) {
        notificationService.publishNotification(id);
        return R.ok(true);
    }

    @Operation(summary = "撤回通知")
    @PutMapping("/revoke/{id}")
    @PreAuthorize("@ss.hasPermission('notification:notification:revoke')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "撤回通知")
    public R<Boolean> revokeNotification(@PathVariable("id") Long id) {
        notificationService.revokeNotification(id);
        return R.ok(true);
    }

    @Operation(summary = "修改通知状态")
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermission('notification:notification:update')")
    @OperationLog(title = "通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "修改通知状态")
    public R<Boolean> changeStatus(@RequestParam("id") @NotNull(message = "通知ID不能为空") Long id,
                                   @RequestParam("status") @NotNull(message = "状态不能为空") Integer status) {
        notificationService.updateNotificationStatus(id, status);
        return R.ok(true);
    }
}