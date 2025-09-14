package com.admin.module.notification.api.service.message;

import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.message.UserInternalMessagePageReqDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageReceiptDTO;
import com.admin.module.notification.api.vo.message.UserInternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageStatisticsVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageTypeCountVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageVO;

import java.util.List;

/**
 * 用户站内信服务接口
 * 
 * 定义用户站内信相关的业务操作规范
 * 包括用户站内信查询、状态管理、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface UserInternalMessageService {

    /**
     * 分页查询用户站内信列表
     *
     * @param userId 用户ID
     * @param pageReqDTO 分页查询条件
     * @return 分页结果，包含用户站内信信息
     */
    PageResult<UserInternalMessageVO> getUserInternalMessagePage(Long userId, UserInternalMessagePageReqDTO pageReqDTO);

    /**
     * 获取用户站内信列表
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param readStatus 读取状态
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<UserInternalMessageVO> getUserInternalMessageList(Long userId, Integer type, Integer readStatus, Integer limit);

    /**
     * 获取用户站内信详情
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @return 站内信详情
     */
    UserInternalMessageDetailVO getUserInternalMessage(Long userId, Long messageId);

    /**
     * 标记站内信为已读
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     */
    void markAsRead(Long userId, Long messageId);

    /**
     * 批量标记站内信为已读
     *
     * @param userId 用户ID
     * @param ids 站内信ID列表
     */
    void markAsReadBatch(Long userId, List<Long> ids);

    /**
     * 标记所有站内信为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 按类型标记站内信为已读
     *
     * @param userId 用户ID
     * @param type 消息类型
     */
    void markAsReadByType(Long userId, Integer type);

    /**
     * 批量删除用户站内信
     *
     * @param userId 用户ID
     * @param ids 站内信ID列表
     */
    void deleteUserInternalMessageBatch(Long userId, List<Long> ids);

    /**
     * 收藏用户站内信
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     */
    void favoriteUserInternalMessage(Long userId, Long messageId);

    /**
     * 发送站内信回执
     *
     * @param userId 用户ID
     * @param receiptDTO 回执信息
     */
    void sendReceipt(Long userId, UserInternalMessageReceiptDTO receiptDTO);

    /**
     * 获取用户未读站内信数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 按类型获取用户未读站内信数量
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @return 未读数量
     */
    Long getUnreadCountByType(Long userId, Integer type);

    /**
     * 获取用户站内信统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    UserInternalMessageStatisticsVO getStatistics(Long userId);

    /**
     * 获取用户站内信类型统计
     *
     * @param userId 用户ID
     * @return 类型统计列表
     */
    List<UserInternalMessageTypeCountVO> getTypeCount(Long userId);

    /**
     * 获取用户未读站内信列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 未读站内信列表
     */
    List<UserInternalMessageVO> getUnreadList(Long userId, Integer limit);

    /**
     * 获取用户收藏的站内信列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 收藏站内信列表
     */
    List<UserInternalMessageVO> getFavoriteList(Long userId, Integer limit);

    /**
     * 获取用户最近的站内信列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近站内信列表
     */
    List<UserInternalMessageVO> getRecentList(Long userId, Integer limit);

    /**
     * 按类型获取用户站内信列表
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<UserInternalMessageVO> getListByType(Long userId, Integer type, Integer limit);

    /**
     * 按优先级获取用户站内信列表
     *
     * @param userId 用户ID
     * @param priority 优先级
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<UserInternalMessageVO> getListByPriority(Long userId, Integer priority, Integer limit);

    /**
     * 清理过期的站内信
     *
     * @param userId 用户ID
     * @param days 过期天数
     */
    void cleanExpiredMessages(Long userId, Integer days);

    /**
     * 清理已读的站内信
     *
     * @param userId 用户ID
     * @param days 保留天数
     */
    void cleanReadMessages(Long userId, Integer days);
}