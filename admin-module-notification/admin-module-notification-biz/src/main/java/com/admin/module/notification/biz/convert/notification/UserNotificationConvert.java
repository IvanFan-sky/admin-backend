package com.admin.module.notification.biz.convert.notification;

import com.admin.module.notification.api.dto.notification.UserNotificationOperateDTO;
import com.admin.module.notification.api.vo.notification.UserNotificationVO;
import com.admin.module.notification.biz.dal.dataobject.UserNotificationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户通知对象转换器
 * 
 * 使用MapStruct进行对象转换
 * 负责DTO、DO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface UserNotificationConvert {

    UserNotificationConvert INSTANCE = Mappers.getMapper(UserNotificationConvert.class);

    /**
     * 操作DTO转换为数据对象
     *
     * @param dto 用户通知操作DTO
     * @return 用户通知数据对象
     */
    UserNotificationDO convert(UserNotificationOperateDTO dto);

    /**
     * 数据对象转换为展示VO
     *
     * @param dataObject 用户通知数据对象
     * @return 用户通知展示VO
     */
    UserNotificationVO convert(UserNotificationDO dataObject);

    /**
     * 数据对象列表转换为展示VO列表
     *
     * @param list 用户通知数据对象列表
     * @return 用户通知展示VO列表
     */
    List<UserNotificationVO> convertList(List<UserNotificationDO> list);

}