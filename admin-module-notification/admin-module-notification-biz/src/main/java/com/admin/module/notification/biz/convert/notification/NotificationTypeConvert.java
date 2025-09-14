package com.admin.module.notification.biz.convert.notification;


import com.admin.module.notification.api.dto.type.NotificationTypeCreateDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeUpdateDTO;
import com.admin.module.notification.api.vo.type.NotificationTypeVO;
import com.admin.module.notification.biz.dal.dataobject.NotificationTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 通知类型对象转换器
 * 
 * 用于实现通知类型相关对象之间的转换
 * 包括DTO、DO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface NotificationTypeConvert {

    NotificationTypeConvert INSTANCE = Mappers.getMapper(NotificationTypeConvert.class);

    /**
     * 将创建DTO转换为DO
     *
     * @param createDTO 创建DTO
     * @return DO对象
     */
    NotificationTypeDO convert(NotificationTypeCreateDTO createDTO);

    /**
     * 将更新DTO转换为DO
     *
     * @param updateDTO 更新DTO
     * @return DO对象
     */
    NotificationTypeDO convert(NotificationTypeUpdateDTO updateDTO);

    /**
     * 将DO转换为VO
     *
     * @param notificationTypeDO DO对象
     * @return VO对象
     */
    NotificationTypeVO convert(NotificationTypeDO notificationTypeDO);

    /**
     * 将DO列表转换为VO列表
     *
     * @param notificationTypeList DO列表
     * @return VO列表
     */
    List<NotificationTypeVO> convertList(List<NotificationTypeDO> notificationTypeList);

    /**
     * Integer到Boolean的映射方法
     *
     * @param value Integer值
     * @return Boolean值
     */
    default Boolean map(Integer value) {
        if (value == null) {
            return null;
        }
        return value == 1;
    }

    /**
     * Boolean到Integer的映射方法
     *
     * @param value Boolean值
     * @return Integer值
     */
    default Integer map(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? 1 : 0;
    }
}