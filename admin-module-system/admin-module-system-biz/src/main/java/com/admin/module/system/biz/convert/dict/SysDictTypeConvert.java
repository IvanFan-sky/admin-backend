package com.admin.module.system.biz.convert.dict;

import com.admin.module.system.api.dto.dict.SysDictTypeCreateDTO;
import com.admin.module.system.api.dto.dict.SysDictTypeUpdateDTO;
import com.admin.module.system.api.vo.dict.SysDictTypeVO;
import com.admin.module.system.biz.dal.dataobject.SysDictTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字典类型对象转换器
 * 
 * 负责字典类型相关对象之间的转换
 * 包括DTO、VO、DO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysDictTypeConvert {

    SysDictTypeConvert INSTANCE = Mappers.getMapper(SysDictTypeConvert.class);

    /**
     * 创建DTO转换为DO
     * 
     * @param createDTO 创建DTO
     * @return 数据对象
     */
    SysDictTypeDO convert(SysDictTypeCreateDTO createDTO);

    /**
     * 更新DTO转换为DO
     * 
     * @param updateDTO 更新DTO
     * @return 数据对象
     */
    SysDictTypeDO convert(SysDictTypeUpdateDTO updateDTO);

    /**
     * DO转换为VO
     * 
     * @param dictTypeDO 数据对象
     * @return 视图对象
     */
    SysDictTypeVO convert(SysDictTypeDO dictTypeDO);

    /**
     * DO列表转换为VO列表
     * 
     * @param dictTypeDOList DO列表
     * @return VO列表
     */
    List<SysDictTypeVO> convertList(List<SysDictTypeDO> dictTypeDOList);
}