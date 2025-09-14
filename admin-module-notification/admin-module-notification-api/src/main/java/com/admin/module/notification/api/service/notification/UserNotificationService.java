package com.admin.module.notification.api.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.notification.UserNotificationOperateDTO;
import com.admin.module.notification.api.dto.notification.UserNotificationQueryDTO;
import com.admin.module.notification.api.vo.notification.UserNotificationVO;

import java.util.List;

/**
 * 用户通知服务接口
 * 
 * 定义用户通知相关的业务操作规范
 * 包括用户通知查询、状态管理、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface UserNotificationService {

    /**
     * 分页查询用户通知列表
     *
     * @param queryDTO 查询条件，包含用户ID和分页参数
     * @return 分页结果，包含用户通知信息
     */
    PageResult<UserNotificationVO> getUserNotificationPage(UserNotificationQueryDTO queryDTO);

    /**
     * 查询用户通知列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 用户通知列表
     */
    List<UserNotificationVO> getUserNotificationList(UserNotificationQueryDTO queryDTO);

    /**
     * 查询用户未读通知列表
     *
     * @param userId 用户ID
     * @return 用户未读通知列表
     */
    List<UserNotificationVO> getUnreadNotifications(Long userId);

    /**
     * 根据用户通知ID查询详情
     *
     * @param id 用户通知ID
     * @return 用户通知详情
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    UserNotificationVO getUserNotification(Long id);

    /**
     * 根据用户通知ID查询详情
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @return 用户通知详情
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    UserNotificationVO getUserNotification(Long userId, Long notificationId);

    /**
     * 标记通知为已读
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void markNotificationAsRead(Long userId, Long notificationId);

    /**
     * 标记通知为未读
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void markNotificationAsUnread(Long userId, Long notificationId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllNotificationsAsRead(Long userId);

    /**
     * 删除用户通知
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void deleteUserNotification(Long userId, Long notificationId);

    /**
     * 批量操作用户通知
     *
     * @param userId 用户ID
     * @param operateDTO 操作信息
     * @throws com.admin.common.exception.ServiceException 当操作失败时抛出
     */
    void batchOperateUserNotifications(Long userId, UserNotificationOperateDTO operateDTO);

    /**
     * 获取用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Long getUserUnreadNotificationCount(Long userId);

    /**
     * 获取用户通知统计信息
     *
     * @param userId 用户ID
     * @return 统计信息，包含总数、已读数、未读数等
     */
    Object getUserNotificationStatistics(Long userId);

    /**
     * 标记通知为已读（通过用户通知ID）
     *
     * @param id 用户通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void markAsRead(Long id);

    /**
     * 标记通知为未读（通过用户通知ID）
     *
     * @param id 用户通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void markAsUnread(Long id);

    /**
     * 批量标记通知为已读
     *
     * @param ids 用户通知ID数组
     * @throws com.admin.common.exception.ServiceException 当操作失败时抛出
     */
    void batchMarkAsRead(Long[] ids);

    /**
     * 批量标记通知为未读
     *
     * @param ids 用户通知ID数组
     * @throws com.admin.common.exception.ServiceException 当操作失败时抛出
     */
    void batchMarkAsUnread(Long[] ids);

    /**
     * 标记用户所有通知为已读
     *
     * @param userId 用户ID
     * @throws com.admin.common.exception.ServiceException 当操作失败时抛出
     */
    void markAllAsRead(Long userId);

    /**
     * 删除用户通知（通过用户通知ID）
     *
     * @param id 用户通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void deleteUserNotification(Long id);

    /**
     * 批量删除用户通知
     *
     * @param ids 用户通知ID数组
     * @throws com.admin.common.exception.ServiceException 当操作失败时抛出
     */
    void deleteUserNotifications(Long[] ids);

    /**
     * 获取用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Long getUnreadCount(Long userId);

}