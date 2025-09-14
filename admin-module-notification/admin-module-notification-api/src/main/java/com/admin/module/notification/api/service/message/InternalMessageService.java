package com.admin.module.notification.api.service.message;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.message.InternalMessageCreateDTO;
import com.admin.module.notification.api.dto.message.InternalMessageQueryDTO;
import com.admin.module.notification.api.dto.message.InternalMessageUpdateDTO;
import com.admin.module.notification.api.vo.message.InternalMessageVO;
import com.admin.module.notification.api.vo.message.InternalMessageDetailVO;

import java.util.List;

/**
 * 站内信服务接口
 * 
 * 定义站内信相关的业务操作规范
 * 包括站内信的增删改查、发送管理、状态控制等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface InternalMessageService {

    /**
     * 分页查询站内信列表
     *
     * @param queryDTO 分页查询条件，支持标题、类型、状态等筛选
     * @return 分页结果，包含站内信基本信息
     */
    PageResult<InternalMessageVO> getInternalMessagePage(InternalMessageQueryDTO queryDTO);

    /**
     * 查询站内信列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 站内信列表
     */
    List<InternalMessageVO> getInternalMessageList(InternalMessageQueryDTO queryDTO);

    /**
     * 根据站内信ID查询详细信息
     *
     * @param id 站内信ID
     * @return 站内信详细信息
     * @throws com.admin.common.exception.ServiceException 当站内信不存在时抛出
     */
    InternalMessageDetailVO getInternalMessage(Long id);

    /**
     * 创建站内信
     *
     * @param createDTO 站内信创建信息
     * @return 新创建的站内信ID
     * @throws com.admin.common.exception.ServiceException 当创建失败时抛出
     */
    Long createInternalMessage(InternalMessageCreateDTO createDTO);

    /**
     * 更新站内信信息
     *
     * @param updateDTO 站内信更新信息
     * @throws com.admin.common.exception.ServiceException 当站内信不存在或更新失败时抛出
     */
    void updateInternalMessage(InternalMessageUpdateDTO updateDTO);

    /**
     * 删除站内信
     *
     * @param id 站内信ID
     * @throws com.admin.common.exception.ServiceException 当站内信不存在时抛出
     */
    void deleteInternalMessage(Long id);

    /**
     * 批量删除站内信
     *
     * @param ids 站内信ID数组
     * @throws com.admin.common.exception.ServiceException 当部分站内信不存在时抛出
     */
    void deleteInternalMessages(Long[] ids);

    /**
     * 发送站内信
     *
     * @param id 站内信ID
     * @throws com.admin.common.exception.ServiceException 当站内信不存在或发送失败时抛出
     */
    void sendInternalMessage(Long id);

    /**
     * 撤回站内信
     *
     * @param id 站内信ID
     * @throws com.admin.common.exception.ServiceException 当站内信不存在或撤回失败时抛出
     */
    void revokeInternalMessage(Long id);

    /**
     * 更新站内信状态
     *
     * @param id 站内信ID
     * @param status 新状态（0-草稿，1-已发送，2-已撤回）
     * @throws com.admin.common.exception.ServiceException 当站内信不存在时抛出
     */
    void updateInternalMessageStatus(Long id, Integer status);

    /**
     * 获取草稿箱站内信列表
     *
     * @param senderId 发送者ID
     * @return 草稿箱站内信列表
     */
    List<InternalMessageVO> getDraftList(Long senderId);

    /**
     * 获取已发送站内信列表
     *
     * @param senderId 发送者ID
     * @return 已发送站内信列表
     */
    List<InternalMessageVO> getSentList(Long senderId);

    /**
     * 获取站内信统计信息
     *
     * @param userId 用户ID
     * @return 统计信息，包含草稿数、已发送数等
     */
    Object getInternalMessageStatistics(Long userId);

    /**
     * 批量标记站内信为已读
     *
     * @param messageIds 站内信ID列表
     * @param userId 用户ID
     */
    void batchMarkAsRead(List<Long> messageIds, Long userId);

    /**
     * 批量删除站内信
     *
     * @param ids 站内信ID列表
     */
    void deleteInternalMessageBatch(List<Long> ids);

    /**
     * 批量发送站内信
     *
     * @param ids 站内信ID列表
     */
    void sendInternalMessageBatch(List<Long> ids);

    /**
     * 批量更新站内信状态
     *
     * @param ids 站内信ID列表
     * @param status 新状态
     * @param updater 更新者
     */
    void updateInternalMessageStatusBatch(List<Long> ids, Integer status, String updater);

    /**
     * 按类型获取站内信列表
     *
     * @param type 消息类型
     * @param status 状态
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<InternalMessageVO> getListByType(Integer type, Integer status, Integer limit);

    /**
     * 按优先级获取站内信列表
     *
     * @param priority 优先级
     * @param status 状态
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<InternalMessageVO> getListByPriority(Integer priority, Integer status, Integer limit);

    /**
     * 获取站内信统计信息
     *
     * @param senderId 发送者ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    Object getStatistics(Long senderId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);

    /**
     * 获取状态统计
     *
     * @param senderId 发送者ID
     * @return 状态统计
     */
    Object getStatusCount(Long senderId);

    /**
     * 获取类型统计
     *
     * @param senderId 发送者ID
     * @return 类型统计
     */
    Object getTypeCount(Long senderId);

    /**
     * 获取优先级统计
     *
     * @param senderId 发送者ID
     * @return 优先级统计
     */
    Object getPriorityCount(Long senderId);

    /**
     * 处理定时发送的站内信
     */
    void processScheduledMessages();

    /**
     * 处理过期的站内信
     */
    void processExpiredMessages();
}