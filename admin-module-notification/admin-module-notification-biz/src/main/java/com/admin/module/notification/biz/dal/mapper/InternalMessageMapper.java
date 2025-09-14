package com.admin.module.notification.biz.dal.mapper;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.message.InternalMessageQueryDTO;
import com.admin.module.notification.biz.dal.dataobject.InternalMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 站内信 Mapper
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper
public interface InternalMessageMapper extends BaseMapper<InternalMessageDO> {

    /**
     * 分页查询站内信列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<InternalMessageDO> selectPage(@Param("queryDTO") InternalMessageQueryDTO queryDTO);

    /**
     * 查询站内信列表
     *
     * @param title 标题
     * @param type 类型
     * @param status 状态
     * @param senderId 发送人ID
     * @return 站内信列表
     */
    List<InternalMessageDO> selectList(@Param("title") String title, @Param("type") Integer type, @Param("status") Integer status, @Param("senderId") Long senderId);

    /**
     * 查询草稿站内信列表
     *
     * @param senderId 发送人ID
     * @return 草稿站内信列表
     */
    List<InternalMessageDO> selectDraftList(@Param("senderId") Long senderId);

    /**
     * 查询已发送站内信列表
     *
     * @param senderId 发送人ID
     * @return 已发送站内信列表
     */
    List<InternalMessageDO> selectSentList(@Param("senderId") Long senderId);

    /**
     * 查询待发送的定时站内信列表
     *
     * @param currentTime 当前时间
     * @return 待发送的定时站内信列表
     */
    List<InternalMessageDO> selectScheduledList(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询已过期的站内信列表
     *
     * @param currentTime 当前时间
     * @return 已过期的站内信列表
     */
    List<InternalMessageDO> selectExpiredList(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询即将过期的站内信列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 即将过期的站内信列表
     */
    List<InternalMessageDO> selectSoonExpiredList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按类型查询站内信列表
     *
     * @param type 消息类型
     * @param status 状态
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<InternalMessageDO> selectListByType(@Param("type") Integer type, @Param("status") Integer status, @Param("limit") Integer limit);

    /**
     * 按优先级查询站内信列表
     *
     * @param priority 优先级
     * @param status 状态
     * @param limit 限制数量
     * @return 站内信列表
     */
    List<InternalMessageDO> selectListByPriority(@Param("priority") Integer priority, @Param("status") Integer status, @Param("limit") Integer limit);

    /**
     * 批量更新站内信状态
     *
     * @param ids 站内信ID列表
     * @param status 新状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("updater") String updater);

    /**
     * 更新发送统计信息
     *
     * @param id 站内信ID
     * @param successCount 成功数量
     * @param failureCount 失败数量
     * @param sendTime 发送时间
     * @return 更新数量
     */
    int updateSendStatistics(@Param("id") Long id,
                           @Param("successCount") Integer successCount,
                           @Param("failureCount") Integer failureCount,
                           @Param("sendTime") LocalDateTime sendTime);

    /**
     * 增加阅读数量
     *
     * @param id 站内信ID
     * @param increment 增加数量
     * @return 更新数量
     */
    int incrementReadCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 增加回执数量
     *
     * @param id 站内信ID
     * @param increment 增加数量
     * @return 更新数量
     */
    int incrementReceiptCount(@Param("id") Long id, @Param("increment") Integer increment);

}