package com.admin.module.notification.api.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.type.NotificationTypeCreateDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeQueryDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeUpdateDTO;
import com.admin.module.notification.api.vo.type.NotificationTypeVO;

import java.util.List;

/**
 * 通知类型服务接口
 * 
 * 定义通知类型相关的业务操作规范
 * 包括通知类型的增删改查、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface NotificationTypeService {

    /**
     * 分页查询通知类型列表
     *
     * @param queryDTO 查询条件，支持名称、编码、状态等筛选
     * @return 分页结果，包含通知类型基本信息
     */
    PageResult<NotificationTypeVO> getNotificationTypePage(NotificationTypeQueryDTO queryDTO);

    /**
     * 查询通知类型列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 通知类型列表
     */
    List<NotificationTypeVO> getNotificationTypeList(NotificationTypeQueryDTO queryDTO);

    /**
     * 根据通知类型ID查询通知类型信息
     *
     * @param id 通知类型ID
     * @return 通知类型信息
     * @throws com.admin.common.exception.ServiceException 当通知类型不存在时抛出
     */
    NotificationTypeVO getNotificationType(Long id);

    /**
     * 创建通知类型
     *
     * @param createDTO 通知类型创建信息
     * @return 新创建的通知类型ID
     * @throws com.admin.common.exception.ServiceException 当创建失败时抛出
     */
    Long createNotificationType(NotificationTypeCreateDTO createDTO);

    /**
     * 更新通知类型信息
     *
     * @param updateDTO 通知类型更新信息
     * @throws com.admin.common.exception.ServiceException 当通知类型不存在或更新失败时抛出
     */
    void updateNotificationType(NotificationTypeUpdateDTO updateDTO);

    /**
     * 删除通知类型
     *
     * @param id 通知类型ID
     * @throws com.admin.common.exception.ServiceException 当通知类型不存在时抛出
     */
    void deleteNotificationType(Long id);

    /**
     * 批量删除通知类型
     *
     * @param ids 通知类型ID数组
     * @throws com.admin.common.exception.ServiceException 当部分通知类型不存在时抛出
     */
    void deleteNotificationTypes(Long[] ids);

    /**
     * 更新通知类型状态
     *
     * @param id 通知类型ID
     * @param status 新状态（0-禁用，1-启用）
     * @throws com.admin.common.exception.ServiceException 当通知类型不存在时抛出
     */
    void updateNotificationTypeStatus(Long id, Integer status);

    /**
     * 获取启用的通知类型列表
     *
     * @return 启用的通知类型列表
     */
    List<NotificationTypeVO> getEnabledTypes();

    /**
     * 根据编码查询通知类型
     *
     * @param code 通知类型编码
     * @return 通知类型信息
     */
    NotificationTypeVO getByCode(String code);

    /**
     * 校验通知类型编码是否唯一
     *
     * @param code 通知类型编码
     * @param id 通知类型ID（更新时传入，新增时传null）
     * @return true-唯一，false-不唯一
     */
    boolean checkCodeUnique(String code, Long id);

    /**
     * 校验通知类型名称是否唯一
     *
     * @param name 通知类型名称
     * @param id 通知类型ID（更新时传入，新增时传null）
     * @return true-唯一，false-不唯一
     */
    boolean checkNameUnique(String name, Long id);

    /**
     * 修改通知类型状态
     *
     * @param id 通知类型ID
     * @param status 新状态（0-禁用，1-启用）
     * @throws com.admin.common.exception.ServiceException 当通知类型不存在时抛出
     */
    void changeStatus(Long id, Integer status);

    /**
     * 批量修改通知类型状态
     *
     * @param ids 通知类型ID数组
     * @param status 新状态（0-禁用，1-启用）
     * @throws com.admin.common.exception.ServiceException 当部分通知类型不存在时抛出
     */
    void batchChangeStatus(Long[] ids, Integer status);
}