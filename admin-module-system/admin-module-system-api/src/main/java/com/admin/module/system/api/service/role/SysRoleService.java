package com.admin.module.system.api.service.role;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.role.*;
import com.admin.module.system.api.vo.role.SysRoleVO;

import java.util.List;
import java.util.Set;

/**
 * 系统角色管理服务接口
 * 
 * 定义角色相关的业务操作规范
 * 包括角色生命周期管理、权限分配等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysRoleService {

    /**
     * 创建角色
     * 
     * 1. 校验角色名称唯一性
     * 2. 校验角色编码唯一性
     * 3. 设置默认值并保存角色信息
     * 4. 记录操作日志
     *
     * @param createDTO 创建角色请求参数，包含角色名称、编码、描述等信息
     * @return 新创建的角色ID
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当角色名称或编码已存在时抛出
     * @author admin
     * @since 1.0
     */
    Long createRole(SysRoleCreateDTO createDTO);

    /**
     * 更新角色
     * 
     * 1. 校验角色存在性
     * 2. 校验角色名称唯一性（排除自身）
     * 3. 校验角色编码唯一性（排除自身）
     * 4. 使用乐观锁更新角色信息
     * 5. 记录操作日志
     *
     * @param updateDTO 更新角色请求参数，包含角色ID、版本号等信息
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当角色不存在、版本号不匹配或名称编码冲突时抛出
     * @author admin
     * @since 1.0
     */
    void updateRole(SysRoleUpdateDTO updateDTO);

    /**
     * 删除角色
     * 
     * 1. 校验角色存在性
     * 2. 校验角色是否被用户使用
     * 3. 逻辑删除角色信息
     * 4. 清除相关权限关联
     * 5. 记录操作日志
     *
     * @param id 角色ID
     * @throws IllegalArgumentException 当角色ID为空时抛出
     * @throws RuntimeException 当角色不存在或正在被使用时抛出
     * @author admin
     * @since 1.0
     */
    void deleteRole(Long id);

    /**
     * 批量删除角色
     * 
     * 批量执行角色删除操作，遇到无法删除的角色时跳过
     *
     * @param ids 角色ID列表
     * @return 实际删除的角色数量
     * @author admin
     * @since 1.0
     */
    int deleteRolesBatch(Set<Long> ids);

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情信息，不存在时返回null
     * @author admin
     * @since 1.0
     */
    SysRoleVO getRole(Long id);

    /**
     * 根据角色编码获取角色详情
     *
     * @param roleCode 角色编码
     * @return 角色详情信息，不存在时返回null
     * @author admin
     * @since 1.0
     */
    SysRoleVO getRoleByCode(String roleCode);

    /**
     * 获取角色分页列表
     *
     * @param queryDTO 查询条件，支持角色名称、编码、状态等筛选
     * @return 角色分页结果
     * @author admin
     * @since 1.0
     */
    PageResult<SysRoleVO> getRolePage(SysRoleQueryDTO queryDTO);

    /**
     * 获取所有启用状态的角色列表
     * 
     * 用于下拉选择等场景
     *
     * @return 启用状态的角色列表
     * @author admin
     * @since 1.0
     */
    List<SysRoleVO> getEnabledRoleList();

    /**
     * 根据用户ID获取角色列表
     * 
     * @param userId 用户ID
     * @return 用户关联的角色列表
     * @author admin
     * @since 1.0
     */
    List<SysRoleVO> getRolesByUserId(Long userId);

    /**
     * 分配角色菜单权限
     * 
     * 1. 校验角色存在性
     * 2. 校验菜单权限有效性
     * 3. 删除原有权限关联
     * 4. 批量插入新的权限关联
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
     * 获取角色的菜单权限ID列表
     *
     * @param roleId 角色ID
     * @return 角色关联的菜单ID列表
     * @author admin
     * @since 1.0
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 更新角色状态
     * 
     * @param id 角色ID
     * @param status 新的状态值 0-禁用，1-启用
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当角色不存在时抛出
     * @author admin
     * @since 1.0
     */
    void updateRoleStatus(Long id, Integer status);
}