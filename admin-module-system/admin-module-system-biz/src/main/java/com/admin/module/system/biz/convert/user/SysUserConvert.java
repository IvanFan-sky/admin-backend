package com.admin.module.system.biz.convert.user;

import com.admin.module.system.api.dto.user.SysUserCreateDTO;
import com.admin.module.system.api.dto.user.SysUserUpdateDTO;
import com.admin.module.system.api.vo.user.SysUserVO;
import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统用户对象转换器
 * 
 * 使用MapStruct进行对象转换
 * 负责DTO、DO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * 创建DTO转换为数据对象
     *
     * @param dto 用户创建DTO
     * @return 用户数据对象
     */
    SysUserDO convert(SysUserCreateDTO dto);

    /**
     * 更新DTO转换为数据对象
     *
     * @param dto 用户更新DTO
     * @return 用户数据对象
     */
    SysUserDO convert(SysUserUpdateDTO dto);

    /**
     * 数据对象转换为展示VO
     *
     * @param dataObject 用户数据对象
     * @return 用户展示VO
     */
    SysUserVO convert(SysUserDO dataObject);

    /**
     * 数据对象列表转换为展示VO列表
     *
     * @param list 用户数据对象列表
     * @return 用户展示VO列表
     */
    List<SysUserVO> convertList(List<SysUserDO> list);
}