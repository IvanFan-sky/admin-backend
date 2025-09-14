package com.admin.module.notification.biz.dal.mapper;

import com.admin.module.notification.biz.dal.dataobject.UserNotificationDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户通知数据访问层接口
 * 
 * 提供用户通知相关的数据库操作方法
 * 包括基础CRUD操作和业务查询方法
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotificationDO> {

    /**
     * 根据用户ID查询通知列表
     *
     * @param userId 用户ID
     * @param readStatus 阅读状态（可选）
     * @return 用户通知列表
     */
    List<UserNotificationDO> selectByUserId(@Param("userId") Long userId,
                                           @Param("readStatus") Integer readStatus);

    /**
     * 根据通知ID查询用户列表
     *
     * @param notificationId 通知ID
     * @param readStatus 阅读状态（可选）
     * @return 用户通知列表
     */
    List<UserNotificationDO> selectByNotificationId(@Param("notificationId") Long notificationId,
                                                   @Param("readStatus") Integer readStatus);

    /**
     * 查询用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 查询通知的阅读统计
     *
     * @param notificationId 通知ID
     * @return 阅读统计信息
     */
    Object selectReadStatsByNotificationId(@Param("notificationId") Long notificationId);

    /**
     * 批量插入用户通知记录
     *
     * @param userNotifications 用户通知列表
     * @return 插入的记录数
     */
    int batchInsert(@Param("list") List<UserNotificationDO> userNotifications);

    /**
     * 批量标记为已读
     *
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @param readTime 阅读时间
     * @return 更新的记录数
     */
    int batchMarkAsRead(@Param("userId") Long userId,
                       @Param("notificationIds") List<Long> notificationIds,
                       @Param("readTime") LocalDateTime readTime);

    /**
     * 批量标记为未读
     *
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 更新的记录数
     */
    int batchMarkAsUnread(@Param("userId") Long userId,
                         @Param("notificationIds") List<Long> notificationIds);

    /**
     * 批量删除用户通知
     *
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 删除的记录数
     */
    int batchDeleteByUserAndNotifications(@Param("userId") Long userId,
                                         @Param("notificationIds") List<Long> notificationIds);

    /**
     * 根据用户ID和通知ID查询记录
     *
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @return 用户通知记录
     */
    UserNotificationDO selectByUserIdAndNotificationId(@Param("userId") Long userId,
                                                       @Param("notificationId") Long notificationId);

    /**
     * 查询用户在指定时间范围内的通知
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param readStatus 阅读状态（可选）
     * @return 用户通知列表
     */
    List<UserNotificationDO> selectByUserIdAndTimeRange(@Param("userId") Long userId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       @Param("readStatus") Integer readStatus);

    /**
     * 统计用户通知的各种状态数量
     *
     * @param userId 用户ID
     * @return 状态统计结果
     */
    Object countStatusByUserId(@Param("userId") Long userId);

    /**
     * 清理指定时间之前的已读通知
     *
     * @param beforeTime 时间界限
     * @return 清理的记录数
     */
    int cleanupReadNotificationsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据通知ID删除所有相关的用户通知记录
     *
     * @param notificationId 通知ID
     * @return 删除的记录数
     */
    int deleteByNotificationId(@Param("notificationId") Long notificationId);

}