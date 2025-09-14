package com.admin.module.notification.api.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.notification.NotificationCreateDTO;
import com.admin.module.notification.api.dto.notification.NotificationQueryDTO;
import com.admin.module.notification.api.dto.notification.NotificationUpdateDTO;
import com.admin.module.notification.api.vo.notification.NotificationVO;

import java.util.List;

/**
 * 通知管理服务接口
 * 
 * 定义通知相关的业务操作规范
 * 包括通知生命周期管理、推送控制、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface NotificationService {

    /**
     * 分页查询通知列表
     *
     * @param queryDTO 查询条件，支持标题、类型、级别、状态等筛选
     * @return 分页结果，包含通知基本信息
     */
    PageResult<NotificationVO> getNotificationPage(NotificationQueryDTO queryDTO);

    /**
     * 查询通知列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 通知列表
     */
    List<NotificationVO> getNotificationList(NotificationQueryDTO queryDTO);

    /**
     * 根据通知ID查询通知信息
     *
     * @param id 通知ID
     * @return 通知信息
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    NotificationVO getNotification(Long id);

    /**
     * 创建通知
     *
     * @param createDTO 通知创建信息
     * @return 新创建的通知ID
     * @throws com.admin.common.exception.ServiceException 当创建失败时抛出
     */
    Long createNotification(NotificationCreateDTO createDTO);

    /**
     * 更新通知信息
     *
     * @param updateDTO 通知更新信息
     * @throws com.admin.common.exception.ServiceException 当通知不存在或更新失败时抛出
     */
    void updateNotification(NotificationUpdateDTO updateDTO);

    /**
     * 删除通知
     *
     * @param id 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void deleteNotification(Long id);

    /**
     * 批量删除通知
     *
     * @param ids 通知ID数组
     * @throws com.admin.common.exception.ServiceException 当部分通知不存在时抛出
     */
    void deleteNotifications(Long[] ids);

    /**
     * 发布通知（立即推送）
     *
     * @param id 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在或发布失败时抛出
     */
    void publishNotification(Long id);

    /**
     * 撤回通知
     *
     * @param id 通知ID
     * @throws com.admin.common.exception.ServiceException 当通知不存在或撤回失败时抛出
     */
    void revokeNotification(Long id);

    /**
     * 更新通知状态
     *
     * @param id 通知ID
     * @param status 新状态（0-草稿，1-已发布）
     * @throws com.admin.common.exception.ServiceException 当通知不存在时抛出
     */
    void updateNotificationStatus(Long id, Integer status);

    /**
     * 获取通知统计信息
     *
     * @return 统计信息，包含总数、已读数、未读数等
     */
    Object getNotificationStatistics();

}