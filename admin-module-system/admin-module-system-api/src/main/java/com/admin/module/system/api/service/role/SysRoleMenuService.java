package com.admin.module.system.api.service.role;

import com.admin.module.system.api.dto.role.SysRoleMenuDTO;
import com.admin.module.system.api.vo.menu.SysMenuVO;

import java.util.List;
import java.util.Set;

/**
 * 角色菜单关联管理服务接口
 * 
 * 定义角色菜单关联相关的业务操作规范
 * 包括权限分配、查询、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysRoleMenuService {

    /**
     * 分配角色菜单权限
     * 
     * 1. 校验角色存在性
     * 2. 校验菜单存在性和有效性
     * 3. 删除角色原有菜单权限关联
     * 4. 批量插入新的菜单权限关联
     * 5. 记录操作日志
     *
     * @param roleMenuDTO 角色菜单权限分配参数
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当角色不存在或菜单无效时抛出
     * @author admin
     * @since 1.0
     */
    void assignRoleMenus(SysRoleMenuDTO roleMenuDTO);

    /**
     * 获取角色的菜单权限列表
     *
     * @param roleId 角色ID
     * @return 角色关联的菜单列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getRoleMenus(Long roleId);

    /**
     * 获取角色的菜单权限ID列表
     *
     * @param roleId 角色ID
     * @return 角色关联的菜单ID列表
     * @author admin
     * @since 1.0
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 移除角色菜单权限
     * 
     * 删除指定角色的指定菜单权限关联
     *
     * @param roleId 角色ID
     * @param menuId 菜单ID
     * @throws IllegalArgumentException 当参数为空时抛出
     * @throws RuntimeException 当角色或菜单不存在时抛出
     * @author admin
     * @since 1.0
     */
    void removeRoleMenu(Long roleId, Long menuId);

    /**
     * 移除角色的所有菜单权限
     * 
     * 删除指定角色的所有菜单权限关联
     *
     * @param roleId 角色ID
     * @throws IllegalArgumentException 当角色ID为空时抛出
     * @author admin
     * @since 1.0
     */
    void removeAllRoleMenus(Long roleId);

    /**
     * 批量移除角色菜单权限关联
     * 
     * 批量删除指定角色列表的所有菜单权限关联
     *
     * @param roleIds 角色ID列表
     * @return 实际移除关联的角色数量
     * @author admin
     * @since 1.0
     */
    int removeRoleMenusBatch(Set<Long> roleIds);

    /**
     * 获取拥有指定菜单权限的角色ID列表
     *
     * @param menuId 菜单ID
     * @return 拥有该菜单权限的角色ID列表
     * @author admin
     * @since 1.0
     */
    List<Long> getRoleIdsByMenuId(Long menuId);

    /**
     * 检查角色是否拥有指定菜单权限
     *
     * @param roleId 角色ID
     * @param menuId 菜单ID
     * @return true-拥有该菜单权限，false-不拥有该菜单权限
     * @author admin
     * @since 1.0
     */
    boolean hasRoleMenu(Long roleId, Long menuId);

    /**
     * 检查角色是否拥有指定菜单权限中的任意一个
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return true-拥有其中任意菜单权限，false-不拥有任何菜单权限
     * @author admin
     * @since 1.0
     */
    boolean hasAnyRoleMenu(Long roleId, Set<Long> menuIds);

    /**
     * 检查角色是否拥有指定的所有菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return true-拥有所有菜单权限，false-不拥有所有菜单权限
     * @author admin
     * @since 1.0
     */
    boolean hasAllRoleMenus(Long roleId, Set<Long> menuIds);

    /**
     * 根据菜单ID列表批量移除相关的角色菜单权限
     * 
     * 当菜单被删除时调用，清理相关的角色权限关联
     *
     * @param menuIds 菜单ID列表
     * @return 实际移除的关联数量
     * @author admin
     * @since 1.0
     */
    int removeRoleMenusByMenuIds(Set<Long> menuIds);

    /**
     * 检查菜单是否被任何角色使用
     *
     * @param menuId 菜单ID
     * @return true-被使用，false-未被使用
     * @author admin
     * @since 1.0
     */
    boolean isMenuUsedByAnyRole(Long menuId);

    /**
     * 获取角色菜单权限树形结构
     * 
     * 获取角色的菜单权限并构建成树形结构
     *
     * @param roleId 角色ID
     * @return 角色菜单权限树形结构列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getRoleMenuTree(Long roleId);
}