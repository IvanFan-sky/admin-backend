package com.admin.module.system.biz.convert.dict;

import com.admin.module.system.api.dto.dict.SysDictDataCreateDTO;
import com.admin.module.system.api.dto.dict.SysDictDataUpdateDTO;
import com.admin.module.system.api.vo.dict.SysDictDataVO;
import com.admin.module.system.biz.dal.dataobject.SysDictDataDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字典数据对象转换器
 * 
 * 负责字典数据相关对象之间的转换
 * 包括DTO、VO、DO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface SysDictDataConvert {

    SysDictDataConvert INSTANCE = Mappers.getMapper(SysDictDataConvert.class);

    /**
     * 创建DTO转换为DO
     * 
     * @param createDTO 创建DTO
     * @return 数据对象
     */
    SysDictDataDO convert(SysDictDataCreateDTO createDTO);

    /**
     * 更新DTO转换为DO
     * 
     * @param updateDTO 更新DTO
     * @return 数据对象
     */
    SysDictDataDO convert(SysDictDataUpdateDTO updateDTO);

    /**
     * DO转换为VO
     * 
     * @param dictDataDO 数据对象
     * @return 视图对象
     */
    SysDictDataVO convert(SysDictDataDO dictDataDO);

    /**
     * DO列表转换为VO列表
     * 
     * @param dictDataDOList DO列表
     * @return VO列表
     */
    List<SysDictDataVO> convertList(List<SysDictDataDO> dictDataDOList);
}