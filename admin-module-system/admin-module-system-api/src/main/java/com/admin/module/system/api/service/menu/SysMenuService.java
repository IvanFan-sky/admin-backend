package com.admin.module.system.api.service.menu;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.menu.SysMenuCreateDTO;
import com.admin.module.system.api.dto.menu.SysMenuQueryDTO;
import com.admin.module.system.api.dto.menu.SysMenuUpdateDTO;
import com.admin.module.system.api.vo.menu.SysMenuVO;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单管理服务接口
 * 
 * 定义菜单相关的业务操作规范
 * 包括菜单的CRUD操作、树形结构构建、权限查询等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysMenuService {

    /**
     * 创建菜单
     * 
     * 1. 校验菜单名称唯一性（同级菜单下）
     * 2. 校验父菜单存在性
     * 3. 校验权限标识唯一性（如果有）
     * 4. 构建祖先路径
     * 5. 保存菜单信息
     *
     * @param createDTO 创建菜单请求参数，包含菜单名称、类型、权限等信息
     * @return 新创建的菜单ID
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当菜单名称已存在或父菜单不存在时抛出
     * @author admin
     * @since 1.0
     */
    Long createMenu(SysMenuCreateDTO createDTO);

    /**
     * 更新菜单
     * 
     * 1. 校验菜单存在性
     * 2. 校验菜单名称唯一性（同级菜单下，排除自身）
     * 3. 校验权限标识唯一性（排除自身）
     * 4. 校验父菜单变更的合法性（不能将菜单移动到自己的子菜单下）
     * 5. 更新祖先路径（如果父菜单发生变更）
     * 6. 使用乐观锁更新菜单信息
     *
     * @param updateDTO 更新菜单请求参数，包含菜单ID、版本号等信息
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当菜单不存在、版本号不匹配或名称权限冲突时抛出
     * @author admin
     * @since 1.0
     */
    void updateMenu(SysMenuUpdateDTO updateDTO);

    /**
     * 删除菜单
     * 
     * 1. 校验菜单存在性
     * 2. 校验是否有子菜单（有子菜单时不允许删除）
     * 3. 校验是否被角色使用（被角色引用时不允许删除）
     * 4. 逻辑删除菜单信息
     *
     * @param id 菜单ID
     * @throws IllegalArgumentException 当菜单ID为空时抛出
     * @throws RuntimeException 当菜单不存在、存在子菜单或正在被使用时抛出
     * @author admin
     * @since 1.0
     */
    void deleteMenu(Long id);

    /**
     * 批量删除菜单
     * 
     * 批量执行菜单删除操作，遇到无法删除的菜单时跳过
     *
     * @param ids 菜单ID列表
     * @return 实际删除的菜单数量
     * @author admin
     * @since 1.0
     */
    int deleteMenusBatch(Set<Long> ids);

    /**
     * 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情信息，不存在时返回null
     * @author admin
     * @since 1.0
     */
    SysMenuVO getMenu(Long id);

    /**
     * 获取菜单分页列表
     *
     * @param queryDTO 查询条件，支持菜单名称、类型、状态等筛选
     * @return 菜单分页结果
     * @author admin
     * @since 1.0
     */
    PageResult<SysMenuVO> getMenuPage(SysMenuQueryDTO queryDTO);

    /**
     * 获取菜单树形列表
     * 
     * 获取所有菜单并构建成树形结构
     * 可通过查询条件筛选特定的菜单
     *
     * @param queryDTO 查询条件，为null时查询所有菜单
     * @return 菜单树形结构列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO);

    /**
     * 获取启用状态的菜单树形列表
     * 
     * 用于前端菜单展示等场景
     *
     * @return 启用状态的菜单树形结构列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getEnabledMenuTree();

    /**
     * 根据用户ID获取菜单树形列表
     * 
     * 获取用户有权限访问的菜单，并构建成树形结构
     *
     * @param userId 用户ID
     * @return 用户权限内的菜单树形结构列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID获取菜单列表
     * 
     * @param roleId 角色ID
     * @return 角色关联的菜单列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getMenusByRoleId(Long roleId);

    /**
     * 获取父菜单下拉选项列表
     * 
     * 获取可作为父菜单的选项列表，排除按钮类型的菜单
     * 用于创建/编辑菜单时选择父菜单
     *
     * @param excludeId 需要排除的菜单ID（编辑时排除自身及其子菜单）
     * @return 父菜单选项列表
     * @author admin
     * @since 1.0
     */
    List<SysMenuVO> getParentMenuOptions(Long excludeId);

    /**
     * 更新菜单状态
     * 
     * @param id 菜单ID
     * @param status 新的状态值 0-禁用，1-启用
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当菜单不存在时抛出
     * @author admin
     * @since 1.0
     */
    void updateMenuStatus(Long id, Integer status);

    /**
     * 刷新菜单缓存
     * 
     * 清除相关的缓存数据，重新加载菜单权限信息
     *
     * @author admin
     * @since 1.0
     */
    void refreshMenuCache();
}