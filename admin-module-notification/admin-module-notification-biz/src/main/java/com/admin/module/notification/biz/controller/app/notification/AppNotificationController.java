package com.admin.module.notification.biz.controller.app.notification;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.admin.framework.security.utils.SecurityAuthUtils;

import java.util.List;

/**
 * 用户端通知控制器
 * 
 * 提供用户端通知相关的REST API接口
 * 包括用户查看自己的通知、标记已读/未读等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "用户端 - 通知管理")
@RestController
@RequestMapping("/app/notification")
@RequiredArgsConstructor
@Validated
public class AppNotificationController {

    private final UserNotificationService userNotificationService;

    @Operation(
        summary = "获得我的通知分页列表", 
        description = "分页获取当前用户的通知列表，支持按状态等条件筛选"
    )
    @GetMapping("/my/page")
    public R<PageResult<UserNotificationVO>> getMyNotificationPage(@Valid UserNotificationQueryDTO queryDTO) {
        // 设置当前用户ID
        queryDTO.setUserId(SecurityAuthUtils.getCurrentUserId());
        PageResult<UserNotificationVO> pageResult = userNotificationService.getUserNotificationPage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(
        summary = "获得我的通知列表", 
        description = "获取当前用户的通知列表，不分页返回所有匹配的数据"
    )
    @GetMapping("/my/list")
    public R<List<UserNotificationVO>> getMyNotificationList(@Valid UserNotificationQueryDTO queryDTO) {
        // 设置当前用户ID
        queryDTO.setUserId(SecurityAuthUtils.getCurrentUserId());
        List<UserNotificationVO> list = userNotificationService.getUserNotificationList(queryDTO);
        return R.ok(list);
    }

    @Operation(
        summary = "获得我的未读通知列表", 
        description = "获取当前用户的未读通知列表"
    )
    @GetMapping("/my/unread")
    public R<List<UserNotificationVO>> getMyUnreadNotifications() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserNotificationVO> list = userNotificationService.getUnreadNotifications(userId);
        return R.ok(list);
    }

    @Operation(
        summary = "获得通知详情", 
        description = "根据通知ID获取详细信息，只能查看自己的通知"
    )
    @Parameter(name = "id", description = "用户通知ID", required = true, example = "1024")
    @GetMapping("/{id}")
    public R<UserNotificationVO> getNotification(@PathVariable("id") Long id) {
        UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
        // 验证是否为当前用户的通知
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        if (!currentUserId.equals(userNotification.getUserId())) {
            return R.error("无权访问该通知");
        }
        return R.ok(userNotification);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/markRead/{id}")
    public R<Boolean> markAsRead(@PathVariable("id") Long id) {
        // 验证通知所有权
        UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        if (!currentUserId.equals(userNotification.getUserId())) {
            return R.error("无权操作该通知");
        }
        
        userNotificationService.markAsRead(id);
        return R.ok(true);
    }

    @Operation(summary = "标记通知为未读")
    @PutMapping("/markUnread/{id}")
    public R<Boolean> markAsUnread(@PathVariable("id") Long id) {
        // 验证通知所有权
        UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        if (!currentUserId.equals(userNotification.getUserId())) {
            return R.error("无权操作该通知");
        }
        
        userNotificationService.markAsUnread(id);
        return R.ok(true);
    }

    @Operation(summary = "批量标记通知为已读")
    @PutMapping("/batchMarkRead")
    public R<Boolean> batchMarkAsRead(@RequestBody @NotEmpty(message = "通知ID不能为空") Long[] ids) {
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        // 验证每个通知的所有权
        for (Long id : ids) {
            UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
            if (!currentUserId.equals(userNotification.getUserId())) {
                return R.error("无权操作通知ID: " + id);
            }
        }
        
        userNotificationService.batchMarkAsRead(ids);
        return R.ok(true);
    }

    @Operation(summary = "标记我的所有通知为已读")
    @PutMapping("/markAllRead")
    public R<Boolean> markAllAsRead() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userNotificationService.markAllAsRead(userId);
        return R.ok(true);
    }

    @Operation(summary = "删除通知")
    @Parameter(name = "id", description = "用户通知ID", required = true, example = "1024")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteNotification(@PathVariable("id") Long id) {
        // 验证通知所有权
        UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        if (!currentUserId.equals(userNotification.getUserId())) {
            return R.error("无权删除该通知");
        }
        
        userNotificationService.deleteUserNotification(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除通知")
    @DeleteMapping("/batch")
    public R<Boolean> deleteNotifications(@RequestBody @NotEmpty(message = "删除通知不能为空") Long[] ids) {
        Long currentUserId = SecurityAuthUtils.getCurrentUserId();
        // 验证每个通知的所有权
        for (Long id : ids) {
            UserNotificationVO userNotification = userNotificationService.getUserNotification(id);
            if (!currentUserId.equals(userNotification.getUserId())) {
                return R.error("无权删除通知ID: " + id);
            }
        }
        
        userNotificationService.deleteUserNotifications(ids);
        return R.ok(true);
    }

    @Operation(summary = "获取我的未读通知数量")
    @GetMapping("/my/unreadCount")
    public R<Long> getMyUnreadCount() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        Long count = userNotificationService.getUnreadCount(userId);
        return R.ok(count);
    }
}