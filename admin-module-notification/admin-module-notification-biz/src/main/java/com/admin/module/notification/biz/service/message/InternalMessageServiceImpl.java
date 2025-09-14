package com.admin.module.notification.biz.service.message;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;

import com.admin.module.notification.api.dto.message.InternalMessageCreateDTO;
import com.admin.module.notification.api.dto.message.InternalMessageExportReqVO;
import com.admin.module.notification.api.dto.message.InternalMessageQueryDTO;
import com.admin.module.notification.api.dto.message.InternalMessageUpdateDTO;
import com.admin.module.notification.api.service.message.InternalMessageService;
import com.admin.module.notification.api.vo.message.InternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.InternalMessageExportVO;
import com.admin.module.notification.api.vo.message.InternalMessageVO;
// TODO: 修复AdminUserApi导入
// import com.admin.module.system.api.user.AdminUserApi;
import com.admin.module.notification.biz.convert.message.InternalMessageConvert;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 站内信服务实现类
 *
 * @author admin
 * @since 2025-01-14
 */
@Slf4j
@Service
public class InternalMessageServiceImpl implements InternalMessageService {

    @Resource
    private InternalMessageMapper internalMessageMapper;

    @Resource
    private UserInternalMessageMapper userInternalMessageMapper;

    // TODO: 修复AdminUserApi注入
    // @Resource
    // private AdminUserApi adminUserApi;

    @Resource
    private WebSocketPushService webSocketPushService;

    @Override
    public PageResult<InternalMessageVO> getInternalMessagePage(InternalMessageQueryDTO queryDTO) {
        PageResult<InternalMessageDO> pageResult = internalMessageMapper.selectPage(queryDTO);
        return InternalMessageConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<InternalMessageVO> getInternalMessageList(InternalMessageQueryDTO queryDTO) {
        List<InternalMessageDO> list = internalMessageMapper.selectList(
            queryDTO.getTitle(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getSenderId());
        return InternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public InternalMessageDetailVO getInternalMessage(Long id) {
        InternalMessageDO messageDO = internalMessageMapper.selectById(id);
        if (messageDO == null) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_FOUND);
        }
        return InternalMessageConvert.INSTANCE.convertDetail(messageDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInternalMessage(InternalMessageCreateDTO createDTO) {
        // 转换为DO对象
        InternalMessageDO messageDO = InternalMessageConvert.INSTANCE.convert(createDTO);
        
        // 设置默认值
        messageDO.setStatus(0); // 草稿状态
        messageDO.setSuccessCount(0);
        messageDO.setFailureCount(0);
        messageDO.setReadCount(0);
        messageDO.setReceiptCount(0);
        
        // TODO: 设置发送人信息
        // if (messageDO.getSenderId() != null) {
        //     AdminUserRespDTO senderUser = adminUserApi.getUser(messageDO.getSenderId());
        //     if (senderUser != null) {
        //         messageDO.setSenderName(senderUser.getNickname());
        //     }
        // }
        
        // TODO: 设置接收人信息（单发时）
        // if (messageDO.isSingleSend() && messageDO.getReceiverId() != null) {
        //     AdminUserRespDTO receiverUser = adminUserApi.getUser(messageDO.getReceiverId());
        //     if (receiverUser != null) {
        //         messageDO.setReceiverName(receiverUser.getNickname());
        //     }
        // }
        
        // 插入数据库
        internalMessageMapper.insert(messageDO);
        
        log.info("[createInternalMessage][创建站内信成功，ID={}]", messageDO.getId());
        return messageDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInternalMessage(InternalMessageUpdateDTO updateDTO) {
        // 校验存在
        validateInternalMessageExists(updateDTO.getId());
        
        // 转换为DO对象
        InternalMessageDO messageDO = InternalMessageConvert.INSTANCE.convert(updateDTO);
        
        // 更新数据库
        internalMessageMapper.updateById(messageDO);
        
        log.info("[updateInternalMessage][更新站内信成功，ID={}]", updateDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInternalMessage(Long id) {
        // 校验存在
        validateInternalMessageExists(id);
        
        // 删除站内信
        internalMessageMapper.deleteById(id);
        
        log.info("[deleteInternalMessage][删除站内信成功，ID={}]", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInternalMessageBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        
        // 校验存在
        ids.forEach(this::validateInternalMessageExists);
        
        // 批量删除
        internalMessageMapper.deleteBatchIds(ids);
        
        log.info("[deleteInternalMessageBatch][批量删除站内信成功，IDs={}]", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendInternalMessage(Long id) {
        // 获取站内信
        InternalMessageDO messageDO = internalMessageMapper.selectById(id);
        if (messageDO == null) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 校验状态
        if (!messageDO.isDraft()) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_DRAFT);
        }
        
        // 校验定时发送
        if (messageDO.isScheduled()) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_SCHEDULED);
        }
        
        // 发送站内信
        doSendInternalMessage(messageDO);
        
        log.info("[sendInternalMessage][发送站内信成功，ID={}]", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendInternalMessageBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        
        for (Long id : ids) {
            try {
                sendInternalMessage(id);
            } catch (Exception e) {
                log.error("[sendInternalMessageBatch][批量发送站内信失败，ID={}]", id, e);
            }
        }
        
        log.info("[sendInternalMessageBatch][批量发送站内信完成，IDs={}]", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeInternalMessage(Long id) {
        // 获取站内信
        InternalMessageDO messageDO = internalMessageMapper.selectById(id);
        if (messageDO == null) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_FOUND);
        }
        
        // 校验状态
        if (!messageDO.isSent()) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_SENT);
        }
        
        // 更新状态为已撤回
        InternalMessageDO updateDO = new InternalMessageDO();
        updateDO.setId(id);
        updateDO.setStatus(3); // 已撤回
        internalMessageMapper.updateById(updateDO);
        
        // 推送撤回消息
        pushWithdrawMessage(messageDO);
        
        log.info("[revokeInternalMessage][撤回站内信成功，ID={}]", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInternalMessages(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        
        for (Long id : ids) {
            deleteInternalMessage(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInternalMessageStatus(Long id, Integer status) {
        // 校验存在
        validateInternalMessageExists(id);
        
        // 更新状态
        InternalMessageDO updateDO = new InternalMessageDO();
        updateDO.setId(id);
        updateDO.setStatus(status);
        internalMessageMapper.updateById(updateDO);
        
        log.info("[updateInternalMessageStatus][更新站内信状态成功，ID={}, status={}]", id, status);
    }

    @Override
    public Object getInternalMessageStatistics(Long senderId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 简单的统计实现，返回基础统计信息
        statistics.put("totalCount", 0);
        statistics.put("draftCount", 0);
        statistics.put("sentCount", 0);
        statistics.put("failedCount", 0);
        statistics.put("revokedCount", 0);
        
        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInternalMessageStatusBatch(List<Long> ids, Integer status, String updater) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        
        // 批量更新状态
        int updateCount = internalMessageMapper.updateStatusBatch(ids, status, updater);
        
        log.info("[updateInternalMessageStatusBatch][批量更新站内信状态成功，更新数量={}]", updateCount);
    }

    @Override
    public List<InternalMessageVO> getDraftList(Long senderId) {
        List<InternalMessageDO> list = internalMessageMapper.selectDraftList(senderId);
        return InternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<InternalMessageVO> getSentList(Long senderId) {
        List<InternalMessageDO> list = internalMessageMapper.selectSentList(senderId);
        return InternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<InternalMessageVO> getListByType(Integer type, Integer status, Integer limit) {
        List<InternalMessageDO> list = internalMessageMapper.selectListByType(type, status, limit);
        return InternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<InternalMessageVO> getListByPriority(Integer priority, Integer status, Integer limit) {
        List<InternalMessageDO> list = internalMessageMapper.selectListByPriority(priority, status, limit);
        return InternalMessageConvert.INSTANCE.convertList(list);
    }

    @Override
    public Object getStatistics(Long senderId, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", 0);
        statistics.put("successCount", 0);
        statistics.put("failureCount", 0);
        return statistics;
    }

    @Override
    public Object getStatusCount(Long senderId) {
        List<Map<String, Object>> statusCounts = new ArrayList<>();
        return statusCounts;
    }

    @Override
    public Object getTypeCount(Long senderId) {
        List<Map<String, Object>> typeCounts = new ArrayList<>();
        return typeCounts;
    }

    @Override
    public Object getPriorityCount(Long senderId) {
        List<Map<String, Object>> priorityCounts = new ArrayList<>();
        return priorityCounts;
    }

    public List<InternalMessageExportVO> getInternalMessageListForExport(InternalMessageExportReqVO exportReqVO) {
        List<InternalMessageDO> list = internalMessageMapper.selectList(exportReqVO.getTitle(), 
                exportReqVO.getType(), exportReqVO.getStatus(), exportReqVO.getSenderId());
        return InternalMessageConvert.INSTANCE.convertExportList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processScheduledMessages() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<InternalMessageDO> scheduledMessages = internalMessageMapper.selectScheduledList(currentTime);
        
        for (InternalMessageDO messageDO : scheduledMessages) {
            try {
                doSendInternalMessage(messageDO);
                log.info("[processScheduledMessages][定时发送站内信成功，ID={}]", messageDO.getId());
            } catch (Exception e) {
                log.error("[processScheduledMessages][定时发送站内信失败，ID={}]", messageDO.getId(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processExpiredMessages() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<InternalMessageDO> expiredMessages = internalMessageMapper.selectExpiredList(currentTime);
        
        for (InternalMessageDO messageDO : expiredMessages) {
            try {
                // 更新状态为已过期（可以自定义状态码）
                InternalMessageDO updateDO = new InternalMessageDO();
                updateDO.setId(messageDO.getId());
                updateDO.setStatus(4); // 已过期
                internalMessageMapper.updateById(updateDO);
                
                log.info("[processExpiredMessages][处理过期站内信成功，ID={}]", messageDO.getId());
            } catch (Exception e) {
                log.error("[processExpiredMessages][处理过期站内信失败，ID={}]", messageDO.getId(), e);
            }
        }
    }

    // ========== 私有方法 ==========

    /**
     * 校验站内信是否存在
     *
     * @param id 站内信ID
     */
    private void validateInternalMessageExists(Long id) {
        if (internalMessageMapper.selectById(id) == null) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NOT_FOUND);
        }
    }

    /**
     * 执行发送站内信
     *
     * @param messageDO 站内信DO
     */
    private void doSendInternalMessage(InternalMessageDO messageDO) {
        List<Long> receiverIds = getReceiverIds(messageDO);
        if (CollectionUtils.isEmpty(receiverIds)) {
            throw new ServiceException(ErrorCode.INTERNAL_MESSAGE_NO_RECEIVERS);
        }
        
        // 创建用户站内信关联记录
        List<UserInternalMessageDO> userMessages = createUserInternalMessages(messageDO.getId(), receiverIds);
        
        // 批量插入用户站内信关联记录
        int successCount = 0;
        int failureCount = 0;
        
        try {
            userInternalMessageMapper.insertBatch(userMessages);
            successCount = userMessages.size();
        } catch (Exception e) {
            log.error("[doSendInternalMessage][批量插入用户站内信关联记录失败]", e);
            failureCount = userMessages.size();
        }
        
        // 更新发送统计信息
        internalMessageMapper.updateSendStatistics(messageDO.getId(), successCount, failureCount, LocalDateTime.now());
        
        // 推送站内信消息
        if (successCount > 0) {
            pushInternalMessage(messageDO, receiverIds);
        }
    }

    /**
     * 获取接收人ID列表
     *
     * @param messageDO 站内信DO
     * @return 接收人ID列表
     */
    private List<Long> getReceiverIds(InternalMessageDO messageDO) {
        List<Long> receiverIds = new ArrayList<>();
        
        if (messageDO.isSingleSend()) {
            // 单发
            if (messageDO.getReceiverId() != null) {
                receiverIds.add(messageDO.getReceiverId());
            }
        } else if (messageDO.isGroupSend()) {
            // 群发
            if (StringUtils.hasText(messageDO.getReceiverIds())) {
                try {
                    // 解析JSON格式的接收人ID列表
                    String[] ids = messageDO.getReceiverIds().split(",");
                    for (String id : ids) {
                        receiverIds.add(Long.parseLong(id.trim()));
                    }
                } catch (Exception e) {
                    log.error("[getReceiverIds][解析接收人ID列表失败，receiverIds={}]", messageDO.getReceiverIds(), e);
                }
            }
        } else if (messageDO.isBroadcast()) {
            // 广播 - 获取所有用户ID
            // TODO: 根据接收人类型获取用户ID列表
            // 这里需要根据具体业务逻辑实现
            receiverIds = getAllUserIds(messageDO.getReceiverType());
        }
        
        return receiverIds;
    }

    /**
     * 获取所有用户ID（广播时使用）
     *
     * @param receiverType 接收人类型
     * @return 用户ID列表
     */
    private List<Long> getAllUserIds(Integer receiverType) {
        // TODO: 根据接收人类型获取用户ID列表
        // 1-指定用户 2-指定角色 3-指定部门 4-全体用户
        // List<AdminUserRespDTO> users = adminUserApi.getUserList();
        // return users.stream().map(AdminUserRespDTO::getId).collect(Collectors.toList());
        return new ArrayList<>(); // 临时返回空列表
    }

    /**
     * 创建用户站内信关联记录
     *
     * @param messageId 站内信ID
     * @param receiverIds 接收人ID列表
     * @return 用户站内信关联记录列表
     */
    private List<UserInternalMessageDO> createUserInternalMessages(Long messageId, List<Long> receiverIds) {
        LocalDateTime now = LocalDateTime.now();
        
        return receiverIds.stream().map(receiverId -> {
            UserInternalMessageDO userMessage = new UserInternalMessageDO();
            userMessage.setUserId(receiverId);
            userMessage.setMessageId(messageId);
            userMessage.setReceiveStatus(1); // 已接收
            userMessage.setReadStatus(0); // 未读
            userMessage.setReceiptStatus(0); // 未回执
            userMessage.setFavoriteStatus(0); // 未收藏
            userMessage.setDeleteStatus(0); // 未删除
            userMessage.setReceiveTime(now);
            return userMessage;
        }).collect(Collectors.toList());
    }

    /**
     * 推送站内信消息
     *
     * @param messageDO 站内信DO
     * @param receiverIds 接收人ID列表
     */
    private void pushInternalMessage(InternalMessageDO messageDO, List<Long> receiverIds) {
        try {
            if (messageDO.isBroadcast()) {
                // 广播消息
                webSocketPushService.broadcastInternalMessage(messageDO.getTitle(), messageDO.getContent());
            } else {
                // 推送给指定用户
                webSocketPushService.pushInternalMessageToUsers(receiverIds, messageDO.getTitle(), messageDO.getContent());
            }
            
            // 推送未读数量更新
            for (Long receiverId : receiverIds) {
                Long unreadCount = userInternalMessageMapper.countUnread(receiverId);
                webSocketPushService.pushUnreadCountUpdate(receiverId, unreadCount);
            }
        } catch (Exception e) {
            log.error("[pushInternalMessage][推送站内信消息失败，messageId={}]", messageDO.getId(), e);
        }
    }

    /**
     * 推送撤回消息
     *
     * @param messageDO 站内信DO
     */
    private void pushWithdrawMessage(InternalMessageDO messageDO) {
        try {
            // 获取接收人ID列表
            List<UserInternalMessageDO> userMessages = userInternalMessageMapper.selectByMessageId(messageDO.getId(), 1);
            List<Long> receiverIds = userMessages.stream()
                    .map(UserInternalMessageDO::getUserId)
                    .collect(Collectors.toList());
            
            if (!CollectionUtils.isEmpty(receiverIds)) {
                // 推送撤回消息
                webSocketPushService.pushInternalMessageWithdraw(receiverIds, messageDO.getId());
                
                // 推送未读数量更新
                for (Long receiverId : receiverIds) {
                    Long unreadCount = userInternalMessageMapper.countUnread(receiverId);
                    webSocketPushService.pushUnreadCountUpdate(receiverId, unreadCount);
                }
            }
        } catch (Exception e) {
            log.error("[pushWithdrawMessage][推送撤回消息失败，messageId={}]", messageDO.getId(), e);
        }
    }


    @Override
    public void batchMarkAsRead(List<Long> messageIds, Long userId) {
        if (CollectionUtils.isEmpty(messageIds) || userId == null) {
            return;
        }
        
        LocalDateTime readTime = LocalDateTime.now();
        String updater = String.valueOf(userId); // TODO: 获取当前用户名
        
        userInternalMessageMapper.markAsReadBatch(userId, messageIds, readTime, updater);
        
        // 更新站内信的已读数量
        for (Long messageId : messageIds) {
            internalMessageMapper.incrementReadCount(messageId, 1);
        }
    }
}