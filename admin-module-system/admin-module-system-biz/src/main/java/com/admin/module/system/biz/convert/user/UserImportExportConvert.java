package com.admin.module.system.biz.convert.user;

import com.admin.module.system.api.dto.imports.UserImportDTO;
import com.admin.module.system.api.dto.user.SysUserCreateDTO;
import com.admin.module.system.api.vo.imports.UserExportVO;
import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户导入导出转换器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface UserImportExportConvert {

    UserImportExportConvert INSTANCE = Mappers.getMapper(UserImportExportConvert.class);

    /**
     * 导入DTO转换为创建DTO
     */
    @Mapping(target = "password", constant = "123456") // 默认密码
    @Mapping(target = "gender", expression = "java(importDTO.getGenderValue())")
    @Mapping(target = "phone", source = "mobile")
    @Mapping(target = "status", expression = "java(importDTO.getStatusValue())")
    @Mapping(target = "roleIds", ignore = true) // 需要根据角色名称查询ID
    @Mapping(target = "avatar", ignore = true) // 头像字段
    SysUserCreateDTO toCreateDTO(UserImportDTO importDTO);

    /**
     * 用户DO转换为导出VO
     */
    @Mapping(target = "genderText", ignore = true) // 在service中设置
    @Mapping(target = "statusText", ignore = true) // 在service中设置// 部门信息需要在service中设置
    @Mapping(target = "roleNames", ignore = true) // 需要在service中设置
    UserExportVO toExportVO(SysUserDO userDO);

    /**
     * 用户DO列表转换为导出VO列表
     */
    List<UserExportVO> toExportVOList(List<SysUserDO> userDOList);

    /**
     * 导入DTO列表转换为创建DTO列表
     */
    List<SysUserCreateDTO> toCreateDTOList(List<UserImportDTO> importDTOList);
}