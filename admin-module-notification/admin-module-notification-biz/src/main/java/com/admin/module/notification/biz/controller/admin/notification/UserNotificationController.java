package com.admin.module.notification.biz.controller.admin.notification;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.notification.api.dto.notification.UserNotificationQueryDTO;
import com.admin.module.notification.api.service.notification.UserNotificationService;
import com.admin.module.notification.api.vo.notification.UserNotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户通知管理控制器
 * 
 * 提供用户通知管理相关的REST API接口
 * 包括用户通知的查询、标记已读/未读、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 用户通知管理")
@RestController
@RequestMapping("/admin/notification/user-notification")
@RequiredArgsConstructor
@Validated
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @Operation(
        summary = "获得用户通知分页列表", 
        description = "根据查询条件分页获取用户通知列表，支持按用户、通知、状态等条件筛选"
    )
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:query')")
    public R<PageResult<UserNotificationVO>> getUserNotificationPage(@Valid UserNotificationQueryDTO queryDTO) {
        PageResult<UserNotificationVO> pageResult = userNotificationService.getUserNotificationPage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(
        summary = "获得用户通知列表", 
        description = "根据查询条件获取用户通知列表，不分页返回所有匹配的数据"
    )
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:query')")
    public R<List<UserNotificationVO>> getUserNotificationList(@Valid UserNotificationQueryDTO queryDTO) {
        List<UserNotificationVO> list = userNotificationService.getUserNotificationList(queryDTO);
        return R.ok(list);
    }

    @Operation(
        summary = "获得用户未读通知列表", 
        description = "获取指定用户的未读通知列表"
    )
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1024")
    @GetMapping("/unread/{userId}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:query')")
    public R<List<UserNotificationVO>> getUnreadNotifications(@PathVariable("userId") Long userId) {
        List<UserNotificationVO> list = userNotificationService.getUnreadNotifications(userId);
        return R.ok(list);
    }

    @Operation(
        summary = "获得用户通知详情", 
        description = "根据用户通知ID获取详细信息"
    )
    @Parameter(name = "id", description = "用户通知ID", required = true, example = "1024")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:query')")
    public R<UserNotificationVO> getUserNotification(@PathVariable("id") Long id) {
        UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
        return R.ok(userNotification);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/markRead/{id}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:update')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "标记通知为已读")
    public R<Boolean> markAsRead(@PathVariable("id") Long id) {
        userNotificationService.markAsRead(id);
        return R.ok(true);
    }

    @Operation(summary = "标记通知为未读")
    @PutMapping("/markUnread/{id}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:update')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "标记通知为未读")
    public R<Boolean> markAsUnread(@PathVariable("id") Long id) {
        userNotificationService.markAsUnread(id);
        return R.ok(true);
    }

    @Operation(summary = "批量标记通知为已读")
    @PutMapping("/batchMarkRead")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:update')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量标记通知为已读")
    public R<Boolean> batchMarkAsRead(@RequestBody @NotEmpty(message = "通知ID不能为空") Long[] ids) {
        userNotificationService.batchMarkAsRead(ids);
        return R.ok(true);
    }

    @Operation(summary = "批量标记通知为未读")
    @PutMapping("/batchMarkUnread")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:update')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量标记通知为未读")
    public R<Boolean> batchMarkAsUnread(@RequestBody @NotEmpty(message = "通知ID不能为空") Long[] ids) {
        userNotificationService.batchMarkAsUnread(ids);
        return R.ok(true);
    }

    @Operation(summary = "标记用户所有通知为已读")
    @PutMapping("/markAllRead/{userId}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:update')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.UPDATE, description = "标记用户所有通知为已读")
    public R<Boolean> markAllAsRead(@PathVariable("userId") Long userId) {
        userNotificationService.markAllAsRead(userId);
        return R.ok(true);
    }

    @Operation(summary = "删除用户通知")
    @Parameter(name = "id", description = "用户通知ID", required = true, example = "1024")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:delete')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.DELETE, description = "删除用户通知")
    public R<Boolean> deleteUserNotification(@PathVariable("id") Long id) {
        userNotificationService.deleteUserNotification(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除用户通知")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:delete')")
    @OperationLog(title = "用户通知管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除用户通知")
    public R<Boolean> deleteUserNotifications(@RequestBody @NotEmpty(message = "删除通知不能为空") Long[] ids) {
        userNotificationService.deleteUserNotifications(ids);
        return R.ok(true);
    }

    @Operation(summary = "获取用户未读通知数量")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1024")
    @GetMapping("/unreadCount/{userId}")
    @PreAuthorize("@ss.hasPermission('notification:user-notification:query')")
    public R<Long> getUnreadCount(@PathVariable("userId") Long userId) {
        Long count = userNotificationService.getUnreadCount(userId);
        return R.ok(count);
    }
}