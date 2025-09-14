package com.admin.module.notification.biz.convert.notification;

import com.admin.module.notification.api.dto.notification.NotificationCreateDTO;
import com.admin.module.notification.api.dto.notification.NotificationUpdateDTO;
import com.admin.module.notification.api.vo.notification.NotificationVO;
import com.admin.module.notification.biz.dal.dataobject.NotificationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 通知对象转换器
 * 
 * 使用MapStruct进行对象转换
 * 负责DTO、DO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface NotificationConvert {

    NotificationConvert INSTANCE = Mappers.getMapper(NotificationConvert.class);

    /**
     * 创建DTO转换为数据对象
     *
     * @param dto 通知创建DTO
     * @return 通知数据对象
     */
    NotificationDO convert(NotificationCreateDTO dto);

    /**
     * 更新DTO转换为数据对象
     *
     * @param dto 通知更新DTO
     * @return 通知数据对象
     */
    NotificationDO convert(NotificationUpdateDTO dto);

    /**
     * 数据对象转换为展示VO
     *
     * @param dataObject 通知数据对象
     * @return 通知展示VO
     */
    NotificationVO convert(NotificationDO dataObject);

    /**
     * 数据对象列表转换为展示VO列表
     *
     * @param list 通知数据对象列表
     * @return 通知展示VO列表
     */
    List<NotificationVO> convertList(List<NotificationDO> list);

}