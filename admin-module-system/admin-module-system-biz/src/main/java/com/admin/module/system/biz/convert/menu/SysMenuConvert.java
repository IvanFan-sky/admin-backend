package com.admin.module.system.biz.convert.menu;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.menu.SysMenuCreateDTO;
import com.admin.module.system.api.dto.menu.SysMenuUpdateDTO;
import com.admin.module.system.api.vo.menu.SysMenuVO;
import com.admin.module.system.biz.dal.dataobject.SysMenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统菜单转换器
 * 
 * 使用MapStruct进行对象转换
 * 负责DO、DTO、VO之间的相互转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    /**
     * 菜单DO转换为VO
     * 
     * @param menuDO 菜单数据对象
     * @return 菜单响应VO
     */
    SysMenuVO convertToVO(SysMenuDO menuDO);

    /**
     * 菜单DO列表转换为VO列表
     * 
     * @param menuDOList 菜单数据对象列表
     * @return 菜单响应VO列表
     */
    List<SysMenuVO> convertToVOList(List<SysMenuDO> menuDOList);

    /**
     * 菜单DO分页结果转换为VO分页结果
     * 
     * @param pageResult 菜单DO分页结果
     * @return 菜单VO分页结果
     */
    default PageResult<SysMenuVO> convertToVOPage(PageResult<SysMenuDO> pageResult) {
        if (pageResult == null) {
            return null;
        }
        List<SysMenuVO> list = convertToVOList(pageResult.getRecords());
        return PageResult.of(list, pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    /**
     * 创建菜单DTO转换为DO
     * 忽略id、创建时间、更新时间、版本号、删除标识等系统字段
     * 
     * @param createDTO 创建菜单请求DTO
     * @return 菜单数据对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "ancestors", ignore = true)
    @Mapping(target = "children", ignore = true)
    SysMenuDO convertToDO(SysMenuCreateDTO createDTO);

    /**
     * 更新菜单DTO转换为DO
     * 忽略创建时间、更新时间、删除标识等系统维护字段
     * 
     * @param updateDTO 更新菜单请求DTO
     * @return 菜单数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "ancestors", ignore = true)
    @Mapping(target = "children", ignore = true)
    SysMenuDO convertToDO(SysMenuUpdateDTO updateDTO);

    /**
     * 使用更新DTO的数据更新现有DO对象
     * 只更新非系统字段，保护系统维护的字段不被覆盖
     * 
     * @param updateDTO 更新菜单请求DTO
     * @param targetDO 目标菜单数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "ancestors", ignore = true)
    @Mapping(target = "children", ignore = true)
    void updateDO(SysMenuUpdateDTO updateDTO, @MappingTarget SysMenuDO targetDO);
}