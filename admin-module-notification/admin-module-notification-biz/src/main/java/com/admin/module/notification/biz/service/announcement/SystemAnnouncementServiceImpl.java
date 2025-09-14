package com.admin.module.notification.biz.service.announcement;

import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementCreateDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementPageDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementUpdateDTO;
import com.admin.common.enums.ErrorCode;
import com.admin.module.notification.api.service.announcement.SystemAnnouncementService;
import com.admin.module.notification.api.vo.announcement.SystemAnnouncementVO;
import com.admin.module.notification.biz.convert.announcement.SystemAnnouncementConvert;
import com.admin.module.notification.biz.dal.dataobject.SystemAnnouncementDO;
import com.admin.module.notification.biz.dal.mapper.SystemAnnouncementMapper;
import com.admin.module.notification.biz.websocket.service.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统公告服务实现类
 *
 * @author admin
 * @since 2025-01-14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    private final SystemAnnouncementMapper systemAnnouncementMapper;
    private final WebSocketPushService webSocketPushService;

    @Override
    public PageResult<SystemAnnouncementVO> getSystemAnnouncementPage(SystemAnnouncementPageDTO pageDTO) {
        PageResult<SystemAnnouncementDO> pageResult = systemAnnouncementMapper.selectSystemAnnouncementPage(pageDTO);
        return SystemAnnouncementConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<SystemAnnouncementVO> getSystemAnnouncementList() {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectList(null);
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<SystemAnnouncementVO> getEffectiveAnnouncements() {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectEffectiveAnnouncements(1, LocalDateTime.now());
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<SystemAnnouncementVO> getTopSystemAnnouncements() {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectTopAnnouncements(1, true, LocalDateTime.now());
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<SystemAnnouncementVO> getPopupAnnouncements() {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectPopupAnnouncements(1, true, LocalDateTime.now());
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<SystemAnnouncementVO> getAnnouncementsByType(Integer type) {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectAnnouncementsByType(type, 1, LocalDateTime.now());
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public SystemAnnouncementVO getSystemAnnouncement(Long id) {
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 增加阅读次数
        systemAnnouncementMapper.incrementReadCount(id);
        
        return SystemAnnouncementConvert.INSTANCE.convert(systemAnnouncement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSystemAnnouncement(SystemAnnouncementCreateDTO createDTO) {
        // 转换为DO对象
        SystemAnnouncementDO systemAnnouncement = SystemAnnouncementConvert.INSTANCE.convert(createDTO);
        
        // 设置初始状态为草稿
        systemAnnouncement.setStatus(0);
        systemAnnouncement.setReadCount(0);
        
        // 保存到数据库
        systemAnnouncementMapper.insert(systemAnnouncement);
        
        log.info("系统公告创建成功：id={}, title={}", systemAnnouncement.getId(), systemAnnouncement.getTitle());
        return systemAnnouncement.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystemAnnouncement(SystemAnnouncementUpdateDTO updateDTO) {
        // 校验公告是否存在
        SystemAnnouncementDO existingAnnouncement = systemAnnouncementMapper.selectById(updateDTO.getId());
        if (existingAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 已发布的公告不允许修改核心内容
        if (existingAnnouncement.isPublished()) {
            // 只允许修改部分字段
            SystemAnnouncementDO updateAnnouncement = new SystemAnnouncementDO();
            updateAnnouncement.setId(updateDTO.getId());
            updateAnnouncement.setIsTop(updateDTO.getIsTop());
            updateAnnouncement.setIsPopup(updateDTO.getIsPopup());
            updateAnnouncement.setExpireTime(updateDTO.getExpireTime());
            systemAnnouncementMapper.updateById(updateAnnouncement);
        } else {
            // 草稿状态允许修改所有字段
            SystemAnnouncementDO systemAnnouncement = SystemAnnouncementConvert.INSTANCE.convert(updateDTO);
            systemAnnouncementMapper.updateById(systemAnnouncement);
        }
        
        log.info("系统公告更新成功：id={}, title={}", updateDTO.getId(), updateDTO.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystemAnnouncement(Long id) {
        // 校验公告是否存在
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 已发布的公告不允许删除，只能撤回
        if (systemAnnouncement.isPublished()) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_PUBLISHED_CANNOT_DELETE);
        }
        
        // 删除公告
        systemAnnouncementMapper.deleteById(id);
        
        log.info("系统公告删除成功：id={}, title={}", id, systemAnnouncement.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystemAnnouncements(Long[] ids) {
        for (Long id : ids) {
            deleteSystemAnnouncement(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishSystemAnnouncement(Long id) {
        // 校验公告是否存在
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 只有草稿状态的公告才能发布
        if (!systemAnnouncement.isDraft()) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_DRAFT_CANNOT_PUBLISH);
        }
        
        // 更新发布状态和发布时间
        SystemAnnouncementDO updateAnnouncement = new SystemAnnouncementDO();
        updateAnnouncement.setId(id);
        updateAnnouncement.setStatus(1);
        updateAnnouncement.setPublishTime(LocalDateTime.now());
        systemAnnouncementMapper.updateById(updateAnnouncement);
        
        // 推送WebSocket实时通知
        pushAnnouncementToUsers(systemAnnouncement);
        
        log.info("系统公告发布成功：id={}, title={}", id, systemAnnouncement.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeSystemAnnouncement(Long id) {
        // 校验公告是否存在
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 只有已发布的公告才能撤回
        if (!systemAnnouncement.isPublished()) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_PUBLISHED_CANNOT_WITHDRAW);
        }
        
        // 更新撤回状态
        SystemAnnouncementDO updateAnnouncement = new SystemAnnouncementDO();
        updateAnnouncement.setId(id);
        updateAnnouncement.setStatus(2);
        systemAnnouncementMapper.updateById(updateAnnouncement);
        
        // 推送撤回通知
        pushAnnouncementWithdraw(systemAnnouncement);
        
        log.info("系统公告撤回成功：id={}, title={}", id, systemAnnouncement.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystemAnnouncementStatus(Long id, Integer status) {
        // 校验公告是否存在
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 更新状态
        SystemAnnouncementDO updateAnnouncement = new SystemAnnouncementDO();
        updateAnnouncement.setId(id);
        updateAnnouncement.setStatus(status);
        systemAnnouncementMapper.updateById(updateAnnouncement);
        
        log.info("系统公告状态更新成功：id={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystemAnnouncementTop(Long id, Boolean isTop) {
        // 校验公告是否存在
        SystemAnnouncementDO systemAnnouncement = systemAnnouncementMapper.selectById(id);
        if (systemAnnouncement == null) {
            throw new ServiceException(ErrorCode.SYSTEM_ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 更新置顶状态
        SystemAnnouncementDO updateAnnouncement = new SystemAnnouncementDO();
        updateAnnouncement.setId(id);
        updateAnnouncement.setIsTop(isTop);
        systemAnnouncementMapper.updateById(updateAnnouncement);
        
        log.info("系统公告置顶状态更新成功：id={}, isTop={}", id, isTop);
    }

    @Override
    public List<SystemAnnouncementVO> getValidSystemAnnouncements(Integer limit) {
        List<SystemAnnouncementDO> list = systemAnnouncementMapper.selectEffectiveAnnouncements(1, LocalDateTime.now());
        
        // 如果有限制数量，则截取
        if (limit != null && limit > 0 && list.size() > limit) {
            list = list.subList(0, limit);
        }
        
        return SystemAnnouncementConvert.INSTANCE.convertList(list);
    }

    @Override
    public Object getSystemAnnouncementStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("draft", systemAnnouncementMapper.countByStatus(0));
        statistics.put("published", systemAnnouncementMapper.countByStatus(1));
        statistics.put("withdrawn", systemAnnouncementMapper.countByStatus(2));
        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        // 批量更新状态
        int updatedCount = systemAnnouncementMapper.batchUpdateStatus(ids, status, "system");
        log.info("批量更新系统公告状态完成：ids={}, status={}, updatedCount={}", ids, status, updatedCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateTopStatus(List<Long> ids, Boolean isTop) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        // 批量更新置顶状态
        int updatedCount = systemAnnouncementMapper.batchUpdateTopStatus(ids, isTop, "system");
        log.info("批量更新系统公告置顶状态完成：ids={}, isTop={}, updatedCount={}", ids, isTop, updatedCount);
    }

    /**
     * 推送公告给用户
     *
     * @param announcement 公告信息
     */
    private void pushAnnouncementToUsers(SystemAnnouncementDO announcement) {
        try {
            // 构建推送数据
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("announcementId", announcement.getId());
            pushData.put("type", announcement.getType());
            pushData.put("priority", announcement.getPriority());
            pushData.put("isTop", announcement.getIsTop());
            pushData.put("isPopup", announcement.getIsPopup());
            pushData.put("publishTime", announcement.getPublishTime());
            pushData.put("effectiveTime", announcement.getEffectiveTime());
            pushData.put("expireTime", announcement.getExpireTime());

            // 广播系统公告给所有在线用户
            int successCount = webSocketPushService.broadcastSystemAnnouncement(
                    announcement.getTitle(), 
                    announcement.getContent(), 
                    pushData
            );
            
            log.info("系统公告WebSocket推送完成：announcementId={}, successCount={}", announcement.getId(), successCount);
        } catch (Exception e) {
            log.error("推送系统公告WebSocket消息失败：announcementId={}", announcement.getId(), e);
        }
    }

    /**
     * 推送公告撤回消息
     *
     * @param announcement 公告信息
     */
    private void pushAnnouncementWithdraw(SystemAnnouncementDO announcement) {
        try {
            // 构建撤回消息数据
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("announcementId", announcement.getId());
            pushData.put("action", "withdraw");
            pushData.put("withdrawTime", LocalDateTime.now());

            // 广播撤回消息
            int successCount = webSocketPushService.broadcastCustomMessage(
                    "系统公告撤回",
                    "系统公告《" + announcement.getTitle() + "》已被撤回",
                    pushData
            );
            
            log.info("系统公告撤回WebSocket推送完成：announcementId={}, successCount={}", announcement.getId(), successCount);
        } catch (Exception e) {
            log.error("推送系统公告撤回WebSocket消息失败：announcementId={}", announcement.getId(), e);
        }
    }
}