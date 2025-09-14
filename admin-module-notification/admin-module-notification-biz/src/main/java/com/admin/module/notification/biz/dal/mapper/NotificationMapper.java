package com.admin.module.notification.biz.dal.mapper;

import com.admin.module.notification.biz.dal.dataobject.NotificationDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知数据访问层接口
 * 
 * 提供通知相关的数据库操作方法
 * 包括基础CRUD操作和业务查询方法
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface NotificationMapper extends BaseMapper<NotificationDO> {

    /**
     * 根据条件查询通知列表
     *
     * @param typeId 通知类型ID
     * @param title 通知标题（模糊查询）
     * @param level 通知级别
     * @param publishStatus 发布状态
     * @param beginTime 创建开始时间
     * @param endTime 创建结束时间
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectNotificationList(@Param("typeId") Long typeId,
                                                @Param("title") String title,
                                                @Param("level") Integer level,
                                                @Param("publishStatus") Integer publishStatus,
                                                @Param("beginTime") LocalDateTime beginTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 根据发布状态查询通知列表
     *
     * @param publishStatus 发布状态
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectByPublishStatus(@Param("publishStatus") Integer publishStatus);

    /**
     * 根据通知类型查询通知列表
     *
     * @param typeId 通知类型ID
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectByTypeId(@Param("typeId") Long typeId);

    /**
     * 根据目标类型查询通知列表
     *
     * @param targetType 目标用户类型
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectByTargetType(@Param("targetType") Integer targetType);

    /**
     * 查询指定时间范围内的已发布通知
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectPublishedInTimeRange(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 查询需要推送给指定用户的通知
     *
     * @param userId 用户ID
     * @param roleIds 用户角色ID列表
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectNotificationsForUser(@Param("userId") Long userId,
                                                    @Param("roleIds") List<Long> roleIds);

    /**
     * 查询需要推送给指定角色的通知
     *
     * @param roleIds 角色ID列表
     * @return 符合条件的通知列表
     */
    List<NotificationDO> selectNotificationsForRoles(@Param("roleIds") List<Long> roleIds);


}