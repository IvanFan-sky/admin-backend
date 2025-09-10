package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysRoleMenuDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联数据访问层
 * 
 * 提供角色菜单关联关系的数据库操作方法
 * 继承MyBatis-Plus的BaseMapper，获得基础CRUD操作
 * 复杂查询逻辑放在XML中，保持接口简洁
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenuDO> {

    /**
     * 根据角色ID查询菜单ID列表
     * 
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID查询角色ID列表
     * 
     * @param menuId 菜单ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据角色ID删除关联关系
     * 
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID删除关联关系
     * 
     * @param menuId 菜单ID
     * @return 删除的记录数
     */
    int deleteByMenuId(@Param("menuId") Long menuId);

    /**
     * 批量插入角色菜单关联关系
     * 
     * @param roleMenuList 角色菜单关联列表
     * @return 插入的记录数
     */
    int insertBatch(@Param("roleMenuList") List<SysRoleMenuDO> roleMenuList);
}