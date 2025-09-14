package com.admin.module.notification.biz.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.PageUtils;
import com.admin.module.notification.api.dto.notification.NotificationCreateDTO;
import com.admin.module.notification.api.dto.notification.NotificationQueryDTO;
import com.admin.module.notification.api.dto.notification.NotificationUpdateDTO;
import com.admin.module.notification.api.service.notification.NotificationService;
import com.admin.module.notification.api.vo.notification.NotificationVO;
import com.admin.module.notification.biz.convert.notification.NotificationConvert;
import com.admin.module.notification.biz.dal.dataobject.NotificationDO;
import com.admin.module.notification.biz.dal.mapper.NotificationMapper;
import com.admin.module.notification.biz.websocket.service.WebSocketPushService;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知管理服务实现类
 * 
 * 提供通知的增删改查、发布撤回等核心功能
 * 支持通知状态管理、目标用户设置、批量操作等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final WebSocketPushService webSocketPushService;

    @Override
    public PageResult<NotificationVO> getNotificationPage(NotificationQueryDTO queryDTO) {
        Page<NotificationDO> page = PageUtils.buildPage(queryDTO);
        
        LambdaQueryWrapper<NotificationDO> wrapper = new LambdaQueryWrapper<NotificationDO>()
                .eq(queryDTO.getTypeId() != null, NotificationDO::getTypeId, queryDTO.getTypeId())
                .like(StringUtils.hasText(queryDTO.getTitle()), NotificationDO::getTitle, queryDTO.getTitle())
                .eq(queryDTO.getLevel() != null, NotificationDO::getLevel, queryDTO.getLevel())
                .eq(queryDTO.getStatus() != null, NotificationDO::getPublishStatus, queryDTO.getStatus())
                .orderByDesc(NotificationDO::getCreateTime);
        
        Page<NotificationDO> result = notificationMapper.selectPage(page, wrapper);
        return PageUtils.buildPageResult(result, NotificationConvert.INSTANCE.convertList(result.getRecords()));
    }

    @Override
    public List<NotificationVO> getNotificationList(NotificationQueryDTO queryDTO) {
        LambdaQueryWrapper<NotificationDO> wrapper = new LambdaQueryWrapper<NotificationDO>()
                .eq(queryDTO.getTypeId() != null, NotificationDO::getTypeId, queryDTO.getTypeId())
                .like(StringUtils.hasText(queryDTO.getTitle()), NotificationDO::getTitle, queryDTO.getTitle())
                .eq(queryDTO.getLevel() != null, NotificationDO::getLevel, queryDTO.getLevel())
                .eq(queryDTO.getStatus() != null, NotificationDO::getPublishStatus, queryDTO.getStatus())
                .orderByDesc(NotificationDO::getCreateTime);
        
        List<NotificationDO> list = notificationMapper.selectList(wrapper);
        return NotificationConvert.INSTANCE.convertList(list);
    }

    @Override
    public NotificationVO getNotification(Long id) {
        NotificationDO notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        return NotificationConvert.INSTANCE.convert(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotification(NotificationCreateDTO createDTO) {
        // 转换为数据对象
        NotificationDO notification = NotificationConvert.INSTANCE.convert(createDTO);
        notification.setPublishStatus(0); // 默认为草稿状态
        
        // 保存到数据库
        notificationMapper.insert(notification);
        return notification.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotification(NotificationUpdateDTO updateDTO) {
        // 检查通知是否存在
        NotificationDO existingNotification = notificationMapper.selectById(updateDTO.getId());
        if (existingNotification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        
        // 检查是否可以修改（已发布的通知不能修改基本信息）
        if (existingNotification.isPublished()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_ALREADY_PUBLISHED);
        }
        
        // 转换并更新
        NotificationDO notification = NotificationConvert.INSTANCE.convert(updateDTO);
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long id) {
        NotificationDO notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        
        // 检查是否可以删除（已发布的通知不能删除）
        if (notification.isPublished()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_ALREADY_PUBLISHED);
        }
        
        notificationMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotifications(Long[] ids) {
        for (Long id : ids) {
            deleteNotification(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotification(Long id) {
        NotificationDO notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        
        // 检查是否可以发布
        if (notification.isPublished()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_ALREADY_PUBLISHED);
        }
        
        if (notification.isRevoked()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_ALREADY_WITHDRAWN);
        }
        
        // 更新发布状态
        notification.setPublishStatus(1);
        notification.setPublishTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
        
        // 推送WebSocket实时通知
        pushNotificationToUsers(notification);
        
        log.info("通知发布成功：id={}, title={}", notification.getId(), notification.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeNotification(Long id) {
        revokeNotification(id, null);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void revokeNotification(Long id, String reason) {
        NotificationDO notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        
        // 检查是否可以撤回
        if (!notification.isPublished()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_PUBLISH_FAILED);
        }
        
        // 更新撤回状态
        notification.setPublishStatus(2);
        notification.setRevokeTime(LocalDateTime.now());
        notification.setRevokeReason(reason);
        notificationMapper.updateById(notification);
        
        // 推送撤回通知
        pushNotificationWithdraw(notification);
        
        log.info("通知撤回成功：id={}, title={}", id, notification.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationStatus(Long id, Integer status) {
        NotificationDO notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        
        notification.setPublishStatus(status);
        notificationMapper.updateById(notification);
    }

    @Override
    public Object getNotificationStatistics() {
        // 简化实现，返回基本统计信息
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalCount", notificationMapper.selectCount(null));
        statistics.put("publishedCount", notificationMapper.selectCount(
            new LambdaQueryWrapper<NotificationDO>().eq(NotificationDO::getPublishStatus, 1)
        ));
        statistics.put("draftCount", notificationMapper.selectCount(
            new LambdaQueryWrapper<NotificationDO>().eq(NotificationDO::getPublishStatus, 0)
        ));
        return statistics;
    }


    /**
     * 推送通知给目标用户
     *
     * @param notification 通知信息
     */
    private void pushNotificationToUsers(NotificationDO notification) {
        try {
            // 构建推送数据
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("notificationId", notification.getId());
            pushData.put("level", notification.getLevel());
            pushData.put("publishTime", notification.getPublishTime());

            // 推送给所有在线用户（简化处理）
            int successCount = webSocketPushService.broadcastSystemAnnouncement(
                    notification.getTitle(), 
                    notification.getContent(), 
                    pushData
            );
            log.info("通知WebSocket推送完成：notificationId={}, successCount={}", notification.getId(), successCount);
        } catch (Exception e) {
            log.error("推送通知WebSocket消息失败：notificationId={}", notification.getId(), e);
        }
    }

    /**
     * 推送通知撤回消息
     *
     * @param notification 通知信息
     */
    private void pushNotificationWithdraw(NotificationDO notification) {
        try {
            // 构建撤回消息数据
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("notificationId", notification.getId());
            pushData.put("action", "withdraw");
            pushData.put("withdrawTime", LocalDateTime.now());

            // 推送撤回消息
            int successCount = webSocketPushService.broadcastSystemAnnouncement(
                    "通知撤回",
                    "通知《" + notification.getTitle() + "》已被撤回",
                    pushData
            );
            
            log.info("通知撤回WebSocket推送完成：notificationId={}, successCount={}", 
                    notification.getId(), successCount);
        } catch (Exception e) {
            log.error("推送通知撤回WebSocket消息失败：notificationId={}", notification.getId(), e);
        }
    }

}