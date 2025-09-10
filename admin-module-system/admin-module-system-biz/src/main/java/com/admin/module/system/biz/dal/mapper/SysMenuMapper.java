package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysMenuDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统菜单数据访问层
 * 
 * 提供菜单相关的数据库操作接口
 * 继承MyBatis-Plus的BaseMapper获得基础CRUD能力
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuDO> {

    /**
     * 根据菜单名称查询菜单
     * 
     * @param menuName 菜单名称
     * @return 菜单信息
     */
    SysMenuDO selectByMenuName(@Param("menuName") String menuName);

    /**
     * 根据权限标识查询菜单
     * 
     * @param permission 权限标识
     * @return 菜单信息
     */
    SysMenuDO selectByPermission(@Param("permission") String permission);

    /**
     * 根据父菜单ID查询子菜单数量
     * 
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    Long selectChildrenCountByParentId(@Param("parentId") Long parentId);

    /**
     * 根据角色ID查询菜单列表
     * 
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenuDO> selectMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuDO> selectMenusByUserId(@Param("userId") Long userId);
}