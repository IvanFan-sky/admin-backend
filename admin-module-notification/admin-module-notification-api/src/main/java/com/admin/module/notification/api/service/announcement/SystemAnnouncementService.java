package com.admin.module.notification.api.service.announcement;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementCreateDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementPageDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementUpdateDTO;
import com.admin.module.notification.api.vo.announcement.SystemAnnouncementVO;

import java.util.List;

/**
 * 系统公告服务接口
 * 
 * 定义系统公告相关的业务操作规范
 * 包括公告的增删改查、发布管理、状态控制等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SystemAnnouncementService {

    /**
     * 分页查询系统公告列表
     *
     * @param pageDTO 分页查询条件，支持标题、类型、状态等筛选
     * @return 分页结果，包含系统公告基本信息
     */
    PageResult<SystemAnnouncementVO> getSystemAnnouncementPage(SystemAnnouncementPageDTO pageDTO);

    /**
     * 查询系统公告列表（不分页）
     *
     * @return 系统公告列表
     */
    List<SystemAnnouncementVO> getSystemAnnouncementList();

    /**
     * 根据公告ID查询公告信息
     *
     * @param id 公告ID
     * @return 公告信息
     * @throws com.admin.common.exception.ServiceException 当公告不存在时抛出
     */
    SystemAnnouncementVO getSystemAnnouncement(Long id);

    /**
     * 创建系统公告
     *
     * @param createDTO 公告创建信息
     * @return 新创建的公告ID
     * @throws com.admin.common.exception.ServiceException 当创建失败时抛出
     */
    Long createSystemAnnouncement(SystemAnnouncementCreateDTO createDTO);

    /**
     * 更新系统公告信息
     *
     * @param updateDTO 公告更新信息
     * @throws com.admin.common.exception.ServiceException 当公告不存在或更新失败时抛出
     */
    void updateSystemAnnouncement(SystemAnnouncementUpdateDTO updateDTO);

    /**
     * 删除系统公告
     *
     * @param id 公告ID
     * @throws com.admin.common.exception.ServiceException 当公告不存在时抛出
     */
    void deleteSystemAnnouncement(Long id);

    /**
     * 批量删除系统公告
     *
     * @param ids 公告ID数组
     * @throws com.admin.common.exception.ServiceException 当部分公告不存在时抛出
     */
    void deleteSystemAnnouncements(Long[] ids);

    /**
     * 发布系统公告
     *
     * @param id 公告ID
     * @throws com.admin.common.exception.ServiceException 当公告不存在或发布失败时抛出
     */
    void publishSystemAnnouncement(Long id);

    /**
     * 撤回系统公告
     *
     * @param id 公告ID
     * @throws com.admin.common.exception.ServiceException 当公告不存在或撤回失败时抛出
     */
    void revokeSystemAnnouncement(Long id);

    /**
     * 更新公告状态
     *
     * @param id 公告ID
     * @param status 新状态（0-草稿，1-已发布，2-已撤回）
     * @throws com.admin.common.exception.ServiceException 当公告不存在时抛出
     */
    void updateSystemAnnouncementStatus(Long id, Integer status);

    /**
     * 置顶/取消置顶公告
     *
     * @param id 公告ID
     * @param isTop 是否置顶（true-置顶，false-取消置顶）
     * @throws com.admin.common.exception.ServiceException 当公告不存在时抛出
     */
    void updateSystemAnnouncementTop(Long id, Boolean isTop);

    /**
     * 获取有效的系统公告列表（已发布且在有效期内）
     *
     * @param limit 限制数量，为null时不限制
     * @return 有效的系统公告列表
     */
    List<SystemAnnouncementVO> getValidSystemAnnouncements(Integer limit);

    /**
     * 获取置顶的系统公告列表
     *
     * @return 置顶的系统公告列表
     */
    List<SystemAnnouncementVO> getTopSystemAnnouncements();

    /**
     * 获取系统公告统计信息
     *
     * @return 统计信息，包含总数、已发布数、草稿数等
     */
    Object getSystemAnnouncementStatistics();

    /**
     * 获取有效的系统公告列表
     *
     * @return 有效的系统公告列表
     */
    List<SystemAnnouncementVO> getEffectiveAnnouncements();

    /**
     * 获取弹窗系统公告列表
     *
     * @return 弹窗系统公告列表
     */
    List<SystemAnnouncementVO> getPopupAnnouncements();

    /**
     * 根据类型获取系统公告列表
     *
     * @param type 公告类型
     * @return 系统公告列表
     */
    List<SystemAnnouncementVO> getAnnouncementsByType(Integer type);

    /**
     * 批量更新系统公告状态
     *
     * @param ids 公告ID列表
     * @param status 新状态
     */
    void batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 批量更新系统公告置顶状态
     *
     * @param ids 公告ID列表
     * @param isTop 是否置顶
     */
    void batchUpdateTopStatus(List<Long> ids, Boolean isTop);
}