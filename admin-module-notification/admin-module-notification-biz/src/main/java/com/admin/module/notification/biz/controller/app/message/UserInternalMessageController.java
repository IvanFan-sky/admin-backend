package com.admin.module.notification.biz.controller.app.message;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.framework.security.utils.SecurityAuthUtils;
import com.admin.module.notification.api.dto.message.UserInternalMessagePageReqDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageReceiptDTO;
import com.admin.module.notification.api.service.message.UserInternalMessageService;
import com.admin.module.notification.api.vo.message.UserInternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageStatisticsVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageTypeCountVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户站内信 Controller
 *
 * @author admin
 * @since 2025-01-14
 */
@Tag(name = "用户 APP - 站内信")
@RestController
@RequestMapping("/notification/user-internal-message")
@Validated
public class UserInternalMessageController {

    @Resource
    private UserInternalMessageService userInternalMessageService;

    @GetMapping("/page")
    @Operation(summary = "获得用户站内信分页")
    @PreAuthorize("isAuthenticated()")
    public R<PageResult<UserInternalMessageVO>> getUserInternalMessagePage(@Valid UserInternalMessagePageReqDTO pageReqDTO) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        PageResult<UserInternalMessageVO> pageResult = userInternalMessageService.getUserInternalMessagePage(userId, pageReqDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getUserInternalMessageList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "readStatus", required = false) Integer readStatus,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getUserInternalMessageList(userId, type, readStatus, limit);
        return R.ok(list);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户站内信详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("isAuthenticated()")
    public R<UserInternalMessageDetailVO> getUserInternalMessage(@RequestParam("id") Long id) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        UserInternalMessageDetailVO userInternalMessage = userInternalMessageService.getUserInternalMessage(userId, id);
        return R.ok(userInternalMessage);
    }

    @PostMapping("/mark-read")
    @Operation(summary = "标记站内信为已读")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> markAsRead(@RequestParam("id") Long id) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.markAsRead(userId, id);
        return R.ok(true);
    }

    @PostMapping("/mark-read-batch")
    @Operation(summary = "批量标记站内信为已读")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> markAsReadBatch(@RequestParam("ids") List<Long> ids) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.markAsReadBatch(userId, ids);
        return R.ok(true);
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "标记所有站内信为已读")
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> markAllAsRead() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.markAllAsRead(userId);
        return R.ok(true);
    }

    @PostMapping("/mark-read-by-type")
    @Operation(summary = "按类型标记站内信为已读")
    @Parameter(name = "type", description = "类型", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> markAsReadByType(@RequestParam("type") Integer type) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.markAsReadByType(userId, type);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户站内信")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> deleteUserInternalMessage(@RequestParam("id") Long id) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.deleteUserInternalMessageBatch(userId, List.of(id));
        return R.ok(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除用户站内信")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> deleteUserInternalMessageBatch(@RequestParam("ids") List<Long> ids) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.deleteUserInternalMessageBatch(userId, ids);
        return R.ok(true);
    }

    @PostMapping("/favorite")
    @Operation(summary = "收藏/取消收藏站内信")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> favoriteUserInternalMessage(@RequestParam("id") Long id) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.favoriteUserInternalMessage(userId, id);
        return R.ok(true);
    }

    @PostMapping("/send-receipt")
    @Operation(summary = "发送站内信回执")
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> sendReceipt(@Valid @RequestBody UserInternalMessageReceiptDTO receiptDTO) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.sendReceipt(userId, receiptDTO);
        return R.ok(true);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获得未读站内信数量")
    @PreAuthorize("isAuthenticated()")
    public R<Long> getUnreadCount() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        Long unreadCount = userInternalMessageService.getUnreadCount(userId);
        return R.ok(unreadCount);
    }

    @GetMapping("/unread-count-by-type")
    @Operation(summary = "按类型获得未读站内信数量")
    @Parameter(name = "type", description = "类型", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Long> getUnreadCountByType(@RequestParam("type") Integer type) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        Long unreadCount = userInternalMessageService.getUnreadCountByType(userId, type);
        return R.ok(unreadCount);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得用户站内信统计信息")
    @PreAuthorize("isAuthenticated()")
    public R<UserInternalMessageStatisticsVO> getStatistics() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        UserInternalMessageStatisticsVO statistics = userInternalMessageService.getStatistics(userId);
        return R.ok(statistics);
    }

    @GetMapping("/type-count")
    @Operation(summary = "获得用户站内信类型统计")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageTypeCountVO>> getTypeCount() {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageTypeCountVO> typeCounts = userInternalMessageService.getTypeCount(userId);
        return R.ok(typeCounts);
    }

    @GetMapping("/unread-list")
    @Operation(summary = "获得未读站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getUnreadList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getUnreadList(userId, limit);
        return R.ok(list);
    }

    @GetMapping("/favorite-list")
    @Operation(summary = "获得收藏站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getFavoriteList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getFavoriteList(userId, limit);
        return R.ok(list);
    }

    @GetMapping("/recent-list")
    @Operation(summary = "获得最近站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getRecentList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getRecentList(userId, limit);
        return R.ok(list);
    }

    @GetMapping("/list-by-type")
    @Operation(summary = "按类型获得用户站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getListByType(
            @RequestParam("type") Integer type,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getListByType(userId, type, limit);
        return R.ok(list);
    }

    @GetMapping("/list-by-priority")
    @Operation(summary = "按优先级获得用户站内信列表")
    @PreAuthorize("isAuthenticated()")
    public R<List<UserInternalMessageVO>> getListByPriority(
            @RequestParam("priority") Integer priority,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        List<UserInternalMessageVO> list = userInternalMessageService.getListByPriority(userId, priority, limit);
        return R.ok(list);
    }

    @PostMapping("/clean-expired")
    @Operation(summary = "清理过期站内信")
    @Parameter(name = "days", description = "过期天数", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> cleanExpiredMessages(@RequestParam("days") Integer days) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.cleanExpiredMessages(userId, days);
        return R.ok(true);
    }

    @PostMapping("/clean-read")
    @Operation(summary = "清理已读站内信")
    @Parameter(name = "days", description = "已读天数", required = true)
    @PreAuthorize("isAuthenticated()")
    public R<Boolean> cleanReadMessages(@RequestParam("days") Integer days) {
        Long userId = SecurityAuthUtils.getCurrentUserId();
        userInternalMessageService.cleanReadMessages(userId, days);
        return R.ok(true);
    }
}