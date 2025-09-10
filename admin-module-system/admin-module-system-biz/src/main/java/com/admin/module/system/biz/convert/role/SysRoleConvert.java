package com.admin.module.system.biz.convert.role;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.role.SysRoleCreateDTO;
import com.admin.module.system.api.dto.role.SysRoleUpdateDTO;
import com.admin.module.system.api.vo.role.SysRoleVO;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统角色转换器
 * 
 * 使用MapStruct进行对象转换
 * 负责DO、DTO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    /**
     * 角色DO转换为VO
     * 
     * @param roleDO 角色数据对象
     * @return 角色响应VO
     */
    SysRoleVO convertToVO(SysRoleDO roleDO);

    /**
     * 角色DO列表转换为VO列表
     * 
     * @param roleDOList 角色数据对象列表
     * @return 角色响应VO列表
     */
    List<SysRoleVO> convertToVOList(List<SysRoleDO> roleDOList);

    /**
     * 角色DO分页结果转换为VO分页结果
     * 
     * @param pageResult 角色DO分页结果
     * @return 角色VO分页结果
     */
    default PageResult<SysRoleVO> convertToVOPage(PageResult<SysRoleDO> pageResult) {
        if (pageResult == null) {
            return null;
        }
        List<SysRoleVO> list = convertToVOList(pageResult.getRecords());
        return PageResult.of(list, pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    /**
     * 创建角色DTO转换为DO
     * 忽略id、创建时间、更新时间、版本号、删除标识等系统字段
     * 
     * @param createDTO 创建角色请求DTO
     * @return 角色数据对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysRoleDO convertToDO(SysRoleCreateDTO createDTO);

    /**
     * 更新角色DTO转换为DO
     * 忽略创建时间、更新时间、删除标识等系统维护字段
     * 
     * @param updateDTO 更新角色请求DTO
     * @return 角色数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysRoleDO convertToDO(SysRoleUpdateDTO updateDTO);

    /**
     * 使用更新DTO的数据更新现有DO对象
     * 只更新非系统字段，保护系统维护的字段不被覆盖
     * 
     * @param updateDTO 更新角色请求DTO
     * @param targetDO 目标角色数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateDO(SysRoleUpdateDTO updateDTO, @MappingTarget SysRoleDO targetDO);
}