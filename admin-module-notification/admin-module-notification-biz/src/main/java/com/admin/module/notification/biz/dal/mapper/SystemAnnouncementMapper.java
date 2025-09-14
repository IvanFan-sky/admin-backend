package com.admin.module.notification.biz.dal.mapper;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementPageDTO;
import com.admin.module.notification.biz.dal.dataobject.SystemAnnouncementDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告 Mapper 接口
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper
public interface SystemAnnouncementMapper extends BaseMapper<SystemAnnouncementDO> {

    /**
     * 分页查询系统公告
     *
     * @param pageDTO 分页查询条件
     * @return 分页结果
     */
    PageResult<SystemAnnouncementDO> selectSystemAnnouncementPage(SystemAnnouncementPageDTO pageDTO);

    /**
     * 查询有效的系统公告列表
     *
     * @param status 发布状态
     * @param currentTime 当前时间
     * @return 公告列表
     */
    List<SystemAnnouncementDO> selectEffectiveAnnouncements(@Param("status") Integer status, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询置顶公告列表
     *
     * @param status 发布状态
     * @param isTop 是否置顶
     * @param currentTime 当前时间
     * @return 置顶公告列表
     */
    List<SystemAnnouncementDO> selectTopAnnouncements(@Param("status") Integer status, @Param("isTop") Boolean isTop, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询弹窗公告列表
     *
     * @param status 发布状态
     * @param isPopup 是否弹窗
     * @param currentTime 当前时间
     * @return 弹窗公告列表
     */
    List<SystemAnnouncementDO> selectPopupAnnouncements(@Param("status") Integer status, @Param("isPopup") Boolean isPopup, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 根据类型查询公告列表
     *
     * @param type 公告类型
     * @param status 发布状态
     * @param currentTime 当前时间
     * @return 公告列表
     */
    List<SystemAnnouncementDO> selectAnnouncementsByType(@Param("type") Integer type, @Param("status") Integer status, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计各状态公告数量
     *
     * @param status 发布状态
     * @return 数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 统计各类型公告数量
     *
     * @param type 公告类型
     * @param status 发布状态
     * @return 数量
     */
    Long countByTypeAndStatus(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 批量更新公告状态
     *
     * @param ids 公告ID列表
     * @param status 状态
     * @param updater 更新人
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("updater") String updater);

    /**
     * 批量更新公告置顶状态
     *
     * @param ids 公告ID列表
     * @param isTop 是否置顶
     * @param updater 更新人
     * @return 更新数量
     */
    int batchUpdateTopStatus(@Param("ids") List<Long> ids, @Param("isTop") Boolean isTop, @Param("updater") String updater);

    /**
     * 增加阅读次数
     *
     * @param id 公告ID
     * @return 更新数量
     */
    int incrementReadCount(@Param("id") Long id);

    /**
     * 查询即将过期的公告
     *
     * @param beforeTime 过期时间前多久
     * @param status 发布状态
     * @return 即将过期的公告列表
     */
    List<SystemAnnouncementDO> selectExpiringAnnouncements(@Param("beforeTime") LocalDateTime beforeTime, @Param("status") Integer status);

    /**
     * 查询已过期的公告
     *
     * @param currentTime 当前时间
     * @param status 发布状态
     * @return 已过期的公告列表
     */
    List<SystemAnnouncementDO> selectExpiredAnnouncements(@Param("currentTime") LocalDateTime currentTime, @Param("status") Integer status);

}