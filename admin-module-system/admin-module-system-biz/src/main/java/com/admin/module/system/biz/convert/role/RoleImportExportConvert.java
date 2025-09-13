package com.admin.module.system.biz.convert.role;

import com.admin.module.system.api.dto.RoleCreateDTO;
import com.admin.module.system.api.dto.RoleImportDTO;
import com.admin.module.system.api.vo.RoleExportVO;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 角色导入导出转换器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface RoleImportExportConvert {

    RoleImportExportConvert INSTANCE = Mappers.getMapper(RoleImportExportConvert.class);

    /**
     * 导入DTO转换为创建DTO
     */
    @Mapping(target = "status", expression = "java(importDTO.getStatusValue())")
    @Mapping(target = "menuIds", ignore = true) // 需要根据权限字符串解析
    RoleCreateDTO toCreateDTO(RoleImportDTO importDTO);

    /**
     * 角色DO转换为导出VO
     */
    @Mapping(target = "statusText", ignore = true) // 在service中设置
    @Mapping(target = "permissions", ignore = true) // 需要在service中设置
    @Mapping(target = "userCount", ignore = true) // 需要在service中设置
    RoleExportVO toExportVO(SysRoleDO roleDO);

    /**
     * 角色DO列表转换为导出VO列表
     */
    List<RoleExportVO> toExportVOList(List<SysRoleDO> roleDOList);

    /**
     * 导入DTO列表转换为创建DTO列表
     */
    List<RoleCreateDTO> toCreateDTOList(List<RoleImportDTO> importDTOList);
}