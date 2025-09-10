package com.admin.module.system.api.service.user;

import com.admin.module.system.api.dto.user.SysUserRoleDTO;
import com.admin.module.system.api.vo.role.SysRoleVO;

import java.util.List;
import java.util.Set;

/**
 * 用户角色关联管理服务接口
 * 
 * 定义用户角色关联相关的业务操作规范
 * 包括用户角色分配、查询、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysUserRoleService {

    /**
     * 分配用户角色
     * 
     * 1. 校验用户存在性
     * 2. 校验角色存在性和有效性
     * 3. 删除用户原有角色关联
     * 4. 批量插入新的角色关联
     * 5. 记录操作日志
     *
     * @param userRoleDTO 用户角色分配参数
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当用户不存在或角色无效时抛出
     * @author admin
     * @since 1.0
     */
    void assignUserRoles(SysUserRoleDTO userRoleDTO);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 用户关联的角色列表
     * @author admin
     * @since 1.0
     */
    List<SysRoleVO> getUserRoles(Long userId);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 用户关联的角色ID列表
     * @author admin
     * @since 1.0
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 移除用户角色
     * 
     * 删除指定用户的指定角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @throws IllegalArgumentException 当参数为空时抛出
     * @throws RuntimeException 当用户或角色不存在时抛出
     * @author admin
     * @since 1.0
     */
    void removeUserRole(Long userId, Long roleId);

    /**
     * 移除用户的所有角色
     * 
     * 删除指定用户的所有角色关联
     *
     * @param userId 用户ID
     * @throws IllegalArgumentException 当用户ID为空时抛出
     * @author admin
     * @since 1.0
     */
    void removeAllUserRoles(Long userId);

    /**
     * 批量移除用户角色关联
     * 
     * 批量删除指定用户列表的所有角色关联
     *
     * @param userIds 用户ID列表
     * @return 实际移除关联的用户数量
     * @author admin
     * @since 1.0
     */
    int removeUserRolesBatch(Set<Long> userIds);

    /**
     * 获取拥有指定角色的用户ID列表
     *
     * @param roleId 角色ID
     * @return 拥有该角色的用户ID列表
     * @author admin
     * @since 1.0
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return true-拥有该角色，false-不拥有该角色
     * @author admin
     * @since 1.0
     */
    boolean hasUserRole(Long userId, Long roleId);

    /**
     * 检查用户是否拥有指定角色中的任意一个
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return true-拥有其中任意角色，false-不拥有任何角色
     * @author admin
     * @since 1.0
     */
    boolean hasAnyUserRole(Long userId, Set<Long> roleIds);

    /**
     * 检查用户是否拥有指定的所有角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return true-拥有所有角色，false-不拥有所有角色
     * @author admin
     * @since 1.0
     */
    boolean hasAllUserRoles(Long userId, Set<Long> roleIds);
}