package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色数据访问层
 * 
 * 提供角色相关的数据库操作方法
 * 继承MyBatis-Plus的BaseMapper，获得基础CRUD操作
 * 复杂查询逻辑放在XML中，保持接口简洁
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleDO> {

    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息，不存在时返回null
     */
    SysRoleDO selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据角色名称查询角色
     * 
     * @param roleName 角色名称
     * @return 角色信息，不存在时返回null
     */
    SysRoleDO selectByRoleName(@Param("roleName") String roleName);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 用户关联的角色列表
     */
    List<SysRoleDO> selectRolesByUserId(@Param("userId") Long userId);
}