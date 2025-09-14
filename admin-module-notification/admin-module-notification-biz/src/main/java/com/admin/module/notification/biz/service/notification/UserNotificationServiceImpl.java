package com.admin.module.notification.biz.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.notification.api.dto.notification.UserNotificationQueryDTO;
import com.admin.module.notification.api.dto.notification.UserNotificationOperateDTO;
import com.admin.module.notification.api.service.notification.UserNotificationService;
import com.admin.module.notification.api.vo.notification.UserNotificationVO;
import com.admin.module.notification.biz.convert.notification.UserNotificationConvert;
import com.admin.module.notification.biz.dal.dataobject.UserNotificationDO;
import com.admin.module.notification.biz.dal.mapper.UserNotificationMapper;
import com.admin.module.notification.biz.websocket.service.WebSocketPushService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户通知管理服务实现类
 * 
 * 提供用户通知的查询、标记已读、批量操作等核心功能
 * 支持用户通知状态管理、统计分析等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationMapper userNotificationMapper;
    private final WebSocketPushService webSocketPushService;

    @Override
    public PageResult<UserNotificationVO> getUserNotificationPage(UserNotificationQueryDTO queryDTO) {
        Page<UserNotificationDO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<UserNotificationDO> wrapper = new LambdaQueryWrapper<UserNotificationDO>()
                .eq(queryDTO.getUserId() != null, UserNotificationDO::getUserId, queryDTO.getUserId())
                .eq(queryDTO.getReadStatus() != null, UserNotificationDO::getReadStatus, queryDTO.getReadStatus())
                .orderByDesc(UserNotificationDO::getCreateTime);
        
        Page<UserNotificationDO> result = userNotificationMapper.selectPage(page, wrapper);
        List<UserNotificationVO> records = UserNotificationConvert.INSTANCE.convertList(result.getRecords());
        return new PageResult<>(records, result.getTotal());
    }

    @Override
    public List<UserNotificationVO> getUserNotificationList(UserNotificationQueryDTO queryDTO) {
        LambdaQueryWrapper<UserNotificationDO> wrapper = new LambdaQueryWrapper<UserNotificationDO>()
                .eq(queryDTO.getUserId() != null, UserNotificationDO::getUserId, queryDTO.getUserId())
                .eq(queryDTO.getReadStatus() != null, UserNotificationDO::getReadStatus, queryDTO.getReadStatus())
                .orderByDesc(UserNotificationDO::getCreateTime);
        
        List<UserNotificationDO> list = userNotificationMapper.selectList(wrapper);
        return UserNotificationConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<UserNotificationVO> getUnreadNotifications(Long userId) {
        List<UserNotificationDO> list = userNotificationMapper.selectByUserId(userId, 0); // 0表示未读
        return UserNotificationConvert.INSTANCE.convertList(list);
    }

    @Override
    public UserNotificationVO getUserNotification(Long id) {
        UserNotificationDO userNotification = userNotificationMapper.selectById(id);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        return UserNotificationConvert.INSTANCE.convert(userNotification);
    }

    @Override
    public UserNotificationVO getUserNotification(Long userId, Long notificationId) {
        UserNotificationDO userNotification = userNotificationMapper.selectByUserIdAndNotificationId(userId, notificationId);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        return UserNotificationConvert.INSTANCE.convert(userNotification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id) {
        UserNotificationDO userNotification = userNotificationMapper.selectById(id);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        
        if (userNotification.getReadStatus() == 1) {
            return; // 已经是已读状态，无需重复操作
        }
        
        userNotification.setReadStatus(1);
        userNotification.setReadTime(LocalDateTime.now());
        userNotificationMapper.updateById(userNotification);
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userNotification.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsUnread(Long id) {
        UserNotificationDO userNotification = userNotificationMapper.selectById(id);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        
        if (userNotification.getReadStatus() == 0) {
            return; // 已经是未读状态，无需重复操作
        }
        
        userNotification.setReadStatus(0);
        userNotification.setReadTime(null);
        userNotificationMapper.updateById(userNotification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsRead(Long[] ids) {
        for (Long id : ids) {
            markAsRead(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsUnread(Long[] ids) {
        for (Long id : ids) {
            markAsUnread(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        // 查询用户所有未读通知
        List<UserNotificationDO> unreadNotifications = userNotificationMapper.selectByUserId(userId, 0);
        
        if (!unreadNotifications.isEmpty()) {
            for (UserNotificationDO notification : unreadNotifications) {
                markAsRead(notification.getId());
            }
        }
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserNotification(Long id) {
        UserNotificationDO userNotification = userNotificationMapper.selectById(id);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        userNotificationMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserNotifications(Long[] ids) {
        for (Long id : ids) {
            deleteUserNotification(id);
        }
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return (long) userNotificationMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markNotificationAsRead(Long userId, Long notificationId) {
        UserNotificationDO userNotification = userNotificationMapper.selectByUserIdAndNotificationId(userId, notificationId);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        
        if (userNotification.getReadStatus() == 1) {
            return; // 已经是已读状态，无需重复操作
        }
        
        userNotification.setReadStatus(1);
        userNotification.setReadTime(LocalDateTime.now());
        userNotificationMapper.updateById(userNotification);
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markNotificationAsUnread(Long userId, Long notificationId) {
        UserNotificationDO userNotification = userNotificationMapper.selectByUserIdAndNotificationId(userId, notificationId);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        
        if (userNotification.getReadStatus() == 0) {
            return; // 已经是未读状态，无需重复操作
        }
        
        userNotification.setReadStatus(0);
        userNotification.setReadTime(null);
        userNotificationMapper.updateById(userNotification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllNotificationsAsRead(Long userId) {
        markAllAsRead(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserNotification(Long userId, Long notificationId) {
        UserNotificationDO userNotification = userNotificationMapper.selectByUserIdAndNotificationId(userId, notificationId);
        if (userNotification == null) {
            throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND);
        }
        userNotificationMapper.deleteById(userNotification.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperateUserNotifications(Long userId, UserNotificationOperateDTO operateDTO) {
        Long[] notificationIds = operateDTO.getNotificationIds();
        String operationType = operateDTO.getOperationType();
        
        switch (operationType) {
            case "READ":
                for (Long notificationId : notificationIds) {
                    markNotificationAsRead(userId, notificationId);
                }
                break;
            case "unread":
                for (Long notificationId : notificationIds) {
                    markNotificationAsUnread(userId, notificationId);
                }
                break;
            case "delete":
                for (Long notificationId : notificationIds) {
                    deleteUserNotification(userId, notificationId);
                }
                break;
            default:
                throw new ServiceException(ErrorCode.USER_NOTIFICATION_NOT_FOUND, "不支持的操作类型: " + operationType);
        }
    }

    @Override
    public Long getUserUnreadNotificationCount(Long userId) {
        return getUnreadCount(userId);
    }

    @Override
    public Object getUserNotificationStatistics(Long userId) {
        return userNotificationMapper.countStatusByUserId(userId);
    }

    /**
     * 推送用户未读数量更新
     *
     * @param userId 用户ID
     */
    private void pushUnreadCountUpdate(Long userId) {
        try {
            // 获取用户未读数量
            Long unreadCount = getUnreadCount(userId);
            
            // 推送未读数量更新
            webSocketPushService.pushUnreadCountUpdate(userId, unreadCount);
        } catch (Exception e) {
            // 推送失败不影响主业务逻辑
        }
    }

}