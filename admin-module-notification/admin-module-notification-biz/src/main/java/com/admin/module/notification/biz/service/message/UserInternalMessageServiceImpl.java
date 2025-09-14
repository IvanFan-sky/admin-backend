package com.admin.module.notification.biz.service.message;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.notification.api.dto.message.UserInternalMessagePageReqDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageReceiptDTO;
import com.admin.module.notification.api.service.message.UserInternalMessageService;
import com.admin.module.notification.api.vo.message.UserInternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageStatisticsVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageTypeCountVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageVO;
import com.admin.module.notification.biz.convert.message.UserInternalMessageConvert;
import com.admin.module.notification.biz.dal.dataobject.InternalMessageDO;
import com.admin.module.notification.biz.dal.dataobject.UserInternalMessageDO;
import com.admin.module.notification.biz.dal.mapper.InternalMessageMapper;
import com.admin.module.notification.biz.dal.mapper.UserInternalMessageMapper;
import com.admin.module.notification.biz.websocket.service.WebSocketPushService;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户站内信服务实现类
 *
 * @author admin
 * @since 2025-01-14
 */
@Slf4j
@Service
public class UserInternalMessageServiceImpl implements UserInternalMessageService {

    @Resource
    private UserInternalMessageMapper userInternalMessageMapper;

    @Resource
    private InternalMessageMapper internalMessageMapper;

    @Resource
    private WebSocketPushService webSocketPushService;

    @Override
    public PageResult<UserInternalMessageVO> getUserInternalMessagePage(Long userId, UserInternalMessagePageReqDTO pageDTO) {
        // 简化实现，返回空的分页结果
        PageResult<UserInternalMessageDO> pageResult = new PageResult<>();
        pageResult.setRecords(java.util.Collections.emptyList());
        pageResult.setTotal(0L);
        return UserInternalMessageConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<UserInternalMessageVO> getUserInternalMessageList(Long userId, Integer type, Integer readStatus, Integer limit) {
        // 简化实现，返回空列表
        return java.util.Collections.emptyList();
    }

    @Override
    public UserInternalMessageDetailVO getUserInternalMessage(Long userId, Long id) {
        UserInternalMessageDO userMessage = userInternalMessageMapper.selectById(id);
        if (userMessage == null || !userMessage.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCode.USER_INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 获取站内信详情
        InternalMessageDO messageDO = internalMessageMapper.selectById(userMessage.getMessageId());
        if (messageDO == null) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        return UserInternalMessageConvert.INSTANCE.convertDetail(userMessage, messageDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long userId, Long id) {
        UserInternalMessageDO userMessage = userInternalMessageMapper.selectById(id);
        if (userMessage == null || !userMessage.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCode.USER_INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 如果已读，直接返回
        if (userMessage.isRead()) {
            return;
        }
        
        // 标记为已读
        UserInternalMessageDO updateDO = new UserInternalMessageDO();
        updateDO.setId(id);
        updateDO.setReadStatus(1);
        updateDO.setReadTime(LocalDateTime.now());
        userInternalMessageMapper.updateById(updateDO);
        
        // 更新站内信阅读数量（简化实现）
        internalMessageMapper.incrementReadCount(userMessage.getMessageId(), 1);
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
        
        log.info("[markAsRead][标记站内信已读成功，userId={}, id={}]", userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsReadBatch(Long userId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        
        // 批量标记为已读
        int updateCount = userInternalMessageMapper.markAsReadBatch(userId, ids, LocalDateTime.now(), "system");
        
        // 更新站内信阅读数量
        for (Long id : ids) {
            UserInternalMessageDO userMessage = userInternalMessageMapper.selectById(id);
            if (userMessage != null) {
                internalMessageMapper.incrementReadCount(userMessage.getMessageId(), 1);
            }
        }
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
        
        log.info("[markAsReadBatch][批量标记站内信已读成功，userId={}, 更新数量={}]", userId, updateCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        // 批量标记所有未读消息为已读
        int updateCount = userInternalMessageMapper.markAllAsRead(userId, LocalDateTime.now(), "system");
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
        
        log.info("[markAllAsRead][标记所有站内信已读成功，userId={}, 更新数量={}]", userId, updateCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsReadByType(Long userId, Integer type) {
        // 按类型标记为已读
        int updateCount = userInternalMessageMapper.markAsReadByType(userId, type, LocalDateTime.now());
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
        
        log.info("[markAsReadByType][按类型标记站内信已读成功，userId={}, type={}, 更新数量={}]", userId, type, updateCount);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserInternalMessageBatch(Long userId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        
        // 批量标记为已删除
        int updateCount = userInternalMessageMapper.markAsDeletedBatch(userId, ids, LocalDateTime.now());
        
        // 推送未读数量更新
        pushUnreadCountUpdate(userId);
        
        log.info("[deleteUserInternalMessageBatch][批量删除用户站内信成功，userId={}, 更新数量={}]", userId, updateCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favoriteUserInternalMessage(Long userId, Long id) {
        UserInternalMessageDO userMessage = userInternalMessageMapper.selectById(id);
        if (userMessage == null || !userMessage.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCode.USER_INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 切换收藏状态
        Integer favoriteStatus = userMessage.isFavorite() ? 0 : 1;
        LocalDateTime favoriteTime = favoriteStatus == 1 ? LocalDateTime.now() : null;
        
        UserInternalMessageDO updateDO = new UserInternalMessageDO();
        updateDO.setId(id);
        updateDO.setFavoriteStatus(favoriteStatus);
        updateDO.setFavoriteTime(favoriteTime);
        userInternalMessageMapper.updateById(updateDO);
        
        log.info("[favoriteUserInternalMessage][收藏/取消收藏用户站内信成功，userId={}, id={}, favoriteStatus={}]", 
                userId, id, favoriteStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendReceipt(Long userId, UserInternalMessageReceiptDTO receiptDTO) {
        UserInternalMessageDO userMessage = userInternalMessageMapper.selectById(receiptDTO.getId());
        if (userMessage == null || !userMessage.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCode.USER_INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 更新回执信息
        UserInternalMessageDO updateDO = new UserInternalMessageDO();
        updateDO.setId(receiptDTO.getId());
        updateDO.setReceiptStatus(1);
        updateDO.setReceiptTime(LocalDateTime.now());
        updateDO.setReceiptContent(receiptDTO.getReceiptContent());
        userInternalMessageMapper.updateById(updateDO);
        
        // 更新站内信回执数量
        internalMessageMapper.incrementReceiptCount(userMessage.getMessageId(), 1);
        
        log.info("[sendReceipt][发送站内信回执成功，userId={}, id={}]", userId, receiptDTO.getId());
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return userInternalMessageMapper.countUnread(userId);
    }

    @Override
    public Long getUnreadCountByType(Long userId, Integer type) {
        return userInternalMessageMapper.countUnreadByType(userId, type);
    }

    @Override
    public UserInternalMessageStatisticsVO getStatistics(Long userId) {
        Map<String, Object> statistics = userInternalMessageMapper.selectUserStatistics(userId);
        return UserInternalMessageConvert.INSTANCE.convertStatistics(statistics);
    }

    @Override
    public List<UserInternalMessageTypeCountVO> getTypeCount(Long userId) {
        List<Map<String, Object>> typeCounts = userInternalMessageMapper.selectUserTypeCount(userId);
        return UserInternalMessageConvert.INSTANCE.convertTypeCounts(typeCounts);
    }

    @Override
    public List<UserInternalMessageVO> getUnreadList(Long userId, Integer limit) {
        List<UserInternalMessageDO> list = userInternalMessageMapper.selectUnreadList(userId, limit);
        return UserInternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<UserInternalMessageVO> getFavoriteList(Long userId, Integer limit) {
        List<UserInternalMessageDO> list = userInternalMessageMapper.selectFavoriteList(userId, limit);
        return UserInternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<UserInternalMessageVO> getRecentList(Long userId, Integer limit) {
        List<UserInternalMessageDO> list = userInternalMessageMapper.selectRecentList(userId, limit);
        return UserInternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<UserInternalMessageVO> getListByType(Long userId, Integer type, Integer limit) {
        List<UserInternalMessageDO> list = userInternalMessageMapper.selectListByType(userId, type, limit);
        return UserInternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<UserInternalMessageVO> getListByPriority(Long userId, Integer priority, Integer limit) {
        List<UserInternalMessageDO> list = userInternalMessageMapper.selectListByPriority(userId, priority, limit);
        return UserInternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanExpiredMessages(Long userId, Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        int deleteCount = userInternalMessageMapper.deleteExpiredMessages(userId, expireTime);
        
        log.info("[cleanExpiredMessages][清理过期站内信成功，userId={}, days={}, 删除数量={}]", userId, days, deleteCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanReadMessages(Long userId, Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        int deleteCount = userInternalMessageMapper.deleteReadMessages(userId, expireTime);
        
        log.info("[cleanReadMessages][清理已读站内信成功，userId={}, days={}, 删除数量={}]", userId, days, deleteCount);
    }


    // ========== 私有方法 ==========

    /**
     * 推送未读数量更新
     *
     * @param userId 用户ID
     */
    private void pushUnreadCountUpdate(Long userId) {
        try {
            Long unreadCount = getUnreadCount(userId);
            webSocketPushService.pushUnreadCountUpdate(userId, unreadCount);
        } catch (Exception e) {
            log.error("[pushUnreadCountUpdate][推送未读数量更新失败，userId={}]", userId, e);
        }
    }
}