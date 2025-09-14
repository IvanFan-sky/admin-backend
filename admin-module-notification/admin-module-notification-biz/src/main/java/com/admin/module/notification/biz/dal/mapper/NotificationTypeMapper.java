package com.admin.module.notification.biz.dal.mapper;

import com.admin.module.notification.api.dto.type.NotificationTypeQueryDTO;
import com.admin.module.notification.biz.dal.dataobject.NotificationTypeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知类型 Mapper 接口
 * 
 * 提供通知类型相关的数据库操作方法
 * 包括基础的CRUD操作和业务查询方法
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface NotificationTypeMapper extends BaseMapper<NotificationTypeDO> {

    /**
     * 分页查询通知类型列表
     *
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<NotificationTypeDO> selectNotificationTypePage(Page<NotificationTypeDO> page, @Param("query") NotificationTypeQueryDTO queryDTO);

    /**
     * 查询通知类型列表
     *
     * @param queryDTO 查询条件
     * @return 通知类型列表
     */
    List<NotificationTypeDO> selectNotificationTypeList(@Param("query") NotificationTypeQueryDTO queryDTO);

    /**
     * 根据状态查询通知类型列表
     *
     * @param status 状态：0-禁用，1-启用
     * @return 通知类型列表
     */
    List<NotificationTypeDO> selectByStatus(@Param("status") Integer status);

    /**
     * 根据编码查询通知类型
     *
     * @param code 类型编码
     * @return 通知类型
     */
    NotificationTypeDO selectByCode(@Param("code") String code);

    /**
     * 检查类型编码是否唯一
     *
     * @param code 类型编码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否唯一
     */
    boolean checkCodeUnique(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * 检查类型名称是否唯一
     *
     * @param name 类型名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否唯一
     */
    boolean checkNameUnique(@Param("name") String name, @Param("excludeId") Long excludeId);


    /**
     * 获取最大排序值
     *
     * @return 最大排序值
     */
    Integer selectMaxSort();

    /**
     * 批量更新状态
     *
     * @param ids 通知类型ID列表
     * @param status 状态
     * @param updater 更新者
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("updater") String updater);
}