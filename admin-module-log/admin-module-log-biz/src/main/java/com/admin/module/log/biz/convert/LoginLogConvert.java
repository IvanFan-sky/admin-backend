package com.admin.module.log.biz.convert;

import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.vo.LoginLogVO;
import com.admin.module.log.api.vo.LoginLogExportVO;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 登录日志转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface LoginLogConvert {

    LoginLogConvert INSTANCE = Mappers.getMapper(LoginLogConvert.class);

    /**
     * DTO转DO
     */
    LoginLogDO convert(LoginLogCreateDTO dto);

    /**
     * DO转VO
     */
    LoginLogVO convert(LoginLogDO dataObject);

    /**
     * DO列表转VO列表
     */
    List<LoginLogVO> convertList(List<LoginLogDO> list);

    /**
     * 转换为导出VO列表
     */
    List<LoginLogExportVO> toExportVOList(List<LoginLogDO> doList);

    /**
     * 转换为导出VO
     */
    LoginLogExportVO toExportVO(LoginLogDO loginLogDO);
}