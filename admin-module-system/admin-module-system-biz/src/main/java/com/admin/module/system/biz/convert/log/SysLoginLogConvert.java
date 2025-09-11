package com.admin.module.system.biz.convert.log;

import com.admin.module.system.api.dto.log.SysLoginLogCreateDTO;
import com.admin.module.system.api.vo.log.LoginLogInfoVO;
import com.admin.module.system.api.vo.log.SysLoginLogVO;
import com.admin.module.system.biz.dal.dataobject.SysLoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统登录日志转换器
 * 
 * 提供登录日志相关的对象转换功能
 * 包括DO、DTO、VO之间的转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface SysLoginLogConvert {

    SysLoginLogConvert INSTANCE = Mappers.getMapper(SysLoginLogConvert.class);

    /**
     * 创建DTO转换为DO
     */
    SysLoginLogDO convert(SysLoginLogCreateDTO createDTO);

    /**
     * LoginLogInfoVO转换为DO
     */
    SysLoginLogDO toEntity(LoginLogInfoVO logInfo);

    /**
     * DO转换为VO
     */
    SysLoginLogVO convert(SysLoginLogDO loginLogDO);

    /**
     * DO列表转换为VO列表
     */
    List<SysLoginLogVO> convertList(List<SysLoginLogDO> loginLogList);
}