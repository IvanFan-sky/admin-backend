package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysUserRoleDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户角色关联数据访问层接口
 * 
 * 提供用户与角色关联关系的数据库操作方法
 * 支持用户角色的分配、删除和查询
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleDO> {

    /**
     * 根据用户ID删除用户角色关联关系
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联关系
     *
     * @param userRoleList 用户角色关联列表
     * @return 影响行数
     */
    int batchUserRole(@Param("userRoleList") List<SysUserRoleDO> userRoleList);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}