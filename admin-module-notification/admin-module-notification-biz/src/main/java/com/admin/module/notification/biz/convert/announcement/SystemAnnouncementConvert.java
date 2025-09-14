package com.admin.module.notification.biz.convert.announcement;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementCreateDTO;
import com.admin.module.notification.api.dto.announcement.SystemAnnouncementUpdateDTO;
import com.admin.module.notification.api.vo.announcement.SystemAnnouncementVO;
import com.admin.module.notification.biz.dal.dataobject.SystemAnnouncementDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统公告对象转换器
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper(componentModel = "spring")
public interface SystemAnnouncementConvert {

    SystemAnnouncementConvert INSTANCE = Mappers.getMapper(SystemAnnouncementConvert.class);

    /**
     * 创建DTO转换为DO
     *
     * @param createDTO 创建DTO
     * @return DO对象
     */
    SystemAnnouncementDO convert(SystemAnnouncementCreateDTO createDTO);

    /**
     * 更新DTO转换为DO
     *
     * @param updateDTO 更新DTO
     * @return DO对象
     */
    SystemAnnouncementDO convert(SystemAnnouncementUpdateDTO updateDTO);

    /**
     * DO转换为VO
     *
     * @param systemAnnouncementDO DO对象
     * @return VO对象
     */
    SystemAnnouncementVO convert(SystemAnnouncementDO systemAnnouncementDO);

    /**
     * DO列表转换为VO列表
     *
     * @param list DO列表
     * @return VO列表
     */
    List<SystemAnnouncementVO> convertList(List<SystemAnnouncementDO> list);

    /**
     * DO分页结果转换为VO分页结果
     *
     * @param page DO分页结果
     * @return VO分页结果
     */
    PageResult<SystemAnnouncementVO> convertPage(PageResult<SystemAnnouncementDO> page);
}