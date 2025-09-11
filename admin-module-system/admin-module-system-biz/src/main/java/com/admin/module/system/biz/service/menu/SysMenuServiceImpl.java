package com.admin.module.system.biz.service.menu;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.PageUtils;
import com.admin.module.system.api.dto.menu.SysMenuCreateDTO;
import com.admin.module.system.api.dto.menu.SysMenuQueryDTO;
import com.admin.module.system.api.dto.menu.SysMenuUpdateDTO;
import com.admin.module.system.api.service.menu.SysMenuService;
import com.admin.module.system.api.vo.menu.SysMenuVO;
import com.admin.module.system.biz.convert.menu.SysMenuConvert;
import com.admin.module.system.biz.dal.dataobject.SysMenuDO;
import com.admin.module.system.biz.dal.mapper.SysMenuMapper;
import com.admin.module.system.biz.dal.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.admin.framework.redis.constants.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单管理服务实现类
 * 
 * 提供菜单的增删改查、树形结构构建、权限管理等核心功能
 * 支持菜单状态管理、层级关系维护、权限验证等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuConvert menuConvert;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public Long createMenu(SysMenuCreateDTO createDTO) {
        log.debug("开始创建菜单，参数: {}", createDTO);

        // 校验父菜单存在性
        if (createDTO.getParentId() != null && !createDTO.getParentId().equals(0L)) {
            validateMenuExists(createDTO.getParentId());
        }

        // 校验菜单名称唯一性（同级菜单下）
        validateMenuNameUnique(null, createDTO.getParentId(), createDTO.getMenuName());

        // 校验权限标识唯一性
        if (StringUtils.hasText(createDTO.getPermission())) {
            validatePermissionUnique(null, createDTO.getPermission());
        }

        // 转换并保存
        SysMenuDO menuDO = menuConvert.convertToDO(createDTO);
        
        // 构建祖先路径
        menuDO.setAncestors(buildAncestors(createDTO.getParentId()));
        
        menuMapper.insert(menuDO);

        log.info("菜单创建成功，菜单ID: {}, 菜单名称: {}", menuDO.getId(), menuDO.getMenuName());
        
        return menuDO.getId();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void updateMenu(SysMenuUpdateDTO updateDTO) {
        log.debug("开始更新菜单，参数: {}", updateDTO);

        // 校验菜单存在性
        SysMenuDO existingMenu = validateMenuExists(updateDTO.getId());

        // 校验父菜单存在性
        if (updateDTO.getParentId() != null && !updateDTO.getParentId().equals(0L)) {
            validateMenuExists(updateDTO.getParentId());
        }

        // 校验不能将菜单移动到自己的子菜单下
        if (!updateDTO.getParentId().equals(existingMenu.getParentId())) {
            validateParentMenuChange(updateDTO.getId(), updateDTO.getParentId());
        }

        // 校验菜单名称唯一性（同级菜单下，排除自身）
        validateMenuNameUnique(updateDTO.getId(), updateDTO.getParentId(), updateDTO.getMenuName());

        // 校验权限标识唯一性（排除自身）
        if (StringUtils.hasText(updateDTO.getPermission())) {
            validatePermissionUnique(updateDTO.getId(), updateDTO.getPermission());
        }

        // 更新菜单信息
        menuConvert.updateDO(updateDTO, existingMenu);
        
        // 如果父菜单发生变更，需要更新祖先路径
        if (!updateDTO.getParentId().equals(existingMenu.getParentId())) {
            existingMenu.setAncestors(buildAncestors(updateDTO.getParentId()));
        }
        
        int updateCount = menuMapper.updateById(existingMenu);
        
        if (updateCount == 0) {
            throw new ServiceException(ErrorCode.DATA_VERSION_CONFLICT);
        }

        log.info("菜单更新成功，菜单ID: {}, 菜单名称: {}", updateDTO.getId(), updateDTO.getMenuName());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.SYS_ROLE_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void deleteMenu(Long id) {
        log.debug("开始删除菜单，菜单ID: {}", id);

        // 校验菜单存在性
        validateMenuExists(id);

        // 校验是否有子菜单
        validateNoChildrenMenus(id);

        // 校验是否被角色使用
        validateMenuNotUsedByRole(id);

        // 删除菜单
        menuMapper.deleteById(id);

        log.info("菜单删除成功，菜单ID: {}", id);
    }

    @Override
    @Transactional
    public int deleteMenusBatch(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        log.debug("开始批量删除菜单，菜单ID列表: {}", ids);

        int deleteCount = 0;
        for (Long id : ids) {
            try {
                deleteMenu(id);
                deleteCount++;
            } catch (Exception e) {
                log.warn("删除菜单失败，菜单ID: {}, 原因: {}", id, e.getMessage());
            }
        }

        log.info("批量删除菜单完成，删除数量: {}", deleteCount);
        return deleteCount;
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_MENU_CACHE, key = "#id", unless = "#result == null")
    public SysMenuVO getMenu(Long id) {
        if (id == null) {
            return null;
        }
        
        SysMenuDO menuDO = menuMapper.selectById(id);
        return menuConvert.convertToVO(menuDO);
    }

    @Override
    public PageResult<SysMenuVO> getMenuPage(SysMenuQueryDTO queryDTO) {
        Page<SysMenuDO> page = PageUtils.buildPage(queryDTO, "sort_order ASC, create_time DESC");
        
        LambdaQueryWrapper<SysMenuDO> wrapper = buildQueryWrapper(queryDTO);
                
        Page<SysMenuDO> pageResult = menuMapper.selectPage(page, wrapper);
        
        List<SysMenuVO> voList = menuConvert.convertToVOList(pageResult.getRecords());
        return PageUtils.buildPageResult(pageResult, voList);
    }

    @Override
    public List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO) {
        LambdaQueryWrapper<SysMenuDO> wrapper = buildQueryWrapper(queryDTO);
        wrapper.orderByAsc(SysMenuDO::getSortOrder)
                .orderByDesc(SysMenuDO::getCreateTime);
                
        List<SysMenuDO> menuDOList = menuMapper.selectList(wrapper);
        List<SysMenuVO> menuVOList = menuConvert.convertToVOList(menuDOList);
        
        return buildMenuTree(menuVOList);
    }

    @Override
    public List<SysMenuVO> getEnabledMenuTree() {
        LambdaQueryWrapper<SysMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuDO::getStatus, 1)
                .eq(SysMenuDO::getVisible, 1)
                .orderByAsc(SysMenuDO::getSortOrder)
                .orderByDesc(SysMenuDO::getCreateTime);
                
        List<SysMenuDO> menuDOList = menuMapper.selectList(wrapper);
        List<SysMenuVO> menuVOList = menuConvert.convertToVOList(menuDOList);
        
        return buildMenuTree(menuVOList);
    }

    @Override
    public List<SysMenuVO> getMenuTreeByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<SysMenuDO> menuDOList = menuMapper.selectMenusByUserId(userId);
        List<SysMenuVO> menuVOList = menuConvert.convertToVOList(menuDOList);
        
        return buildMenuTree(menuVOList);
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_MENU_CACHE, key = "'role:' + #roleId", unless = "#result == null || #result.isEmpty()")
    public List<SysMenuVO> getMenusByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        
        List<SysMenuDO> menuDOList = menuMapper.selectMenusByRoleId(roleId);
        return menuConvert.convertToVOList(menuDOList);
    }

    @Override
    public List<SysMenuVO> getParentMenuOptions(Long excludeId) {
        LambdaQueryWrapper<SysMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuDO::getStatus, 1)
                .ne(SysMenuDO::getMenuType, 3) // 排除按钮类型
                .orderByAsc(SysMenuDO::getSortOrder)
                .orderByDesc(SysMenuDO::getCreateTime);
                
        List<SysMenuDO> menuDOList = menuMapper.selectList(wrapper);
        List<SysMenuVO> menuVOList = menuConvert.convertToVOList(menuDOList);
        
        // 如果是编辑操作，需要排除自身及其子菜单
        if (excludeId != null) {
            menuVOList = filterExcludeMenus(menuVOList, excludeId);
        }
        
        return buildMenuTree(menuVOList);
    }

    @Override
    @Transactional
    public void updateMenuStatus(Long id, Integer status) {
        log.debug("开始更新菜单状态，菜单ID: {}, 状态: {}", id, status);

        // 校验菜单存在性
        SysMenuDO menuDO = validateMenuExists(id);

        // 更新状态
        menuDO.setStatus(status);
        int updateCount = menuMapper.updateById(menuDO);
        
        if (updateCount == 0) {
            throw new ServiceException(ErrorCode.DATA_UPDATE_FAILED);
        }

        log.info("菜单状态更新成功，菜单ID: {}, 新状态: {}", id, status);
    }

    @Override
    public void refreshMenuCache() {
        log.info("刷新菜单缓存");
        // TODO: 实现菜单缓存刷新逻辑
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysMenuDO> buildQueryWrapper(SysMenuQueryDTO queryDTO) {
        LambdaQueryWrapper<SysMenuDO> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO != null) {
            wrapper.like(StringUtils.hasText(queryDTO.getMenuName()), SysMenuDO::getMenuName, queryDTO.getMenuName())
                    .eq(queryDTO.getMenuType() != null, SysMenuDO::getMenuType, queryDTO.getMenuType())
                    .eq(queryDTO.getVisible() != null, SysMenuDO::getVisible, queryDTO.getVisible())
                    .eq(queryDTO.getStatus() != null, SysMenuDO::getStatus, queryDTO.getStatus())
                    .like(StringUtils.hasText(queryDTO.getPermission()), SysMenuDO::getPermission, queryDTO.getPermission())
                    .eq(queryDTO.getParentId() != null, SysMenuDO::getParentId, queryDTO.getParentId());
        }
        
        return wrapper;
    }

    /**
     * 构建菜单树形结构
     */
    private List<SysMenuVO> buildMenuTree(List<SysMenuVO> menuList) {
        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }

        Map<Long, SysMenuVO> menuMap = menuList.stream()
                .collect(Collectors.toMap(SysMenuVO::getId, menu -> menu));

        List<SysMenuVO> rootMenus = new ArrayList<>();
        
        for (SysMenuVO menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId().equals(0L)) {
                rootMenus.add(menu);
            } else {
                SysMenuVO parentMenu = menuMap.get(menu.getParentId());
                if (parentMenu != null) {
                    parentMenu.getChildren().add(menu);
                }
            }
        }
        
        return rootMenus;
    }

    /**
     * 构建祖先路径
     */
    private String buildAncestors(Long parentId) {
        if (parentId == null || parentId.equals(0L)) {
            return "0";
        }
        
        SysMenuDO parentMenu = menuMapper.selectById(parentId);
        if (parentMenu == null) {
            return "0";
        }
        
        return parentMenu.getAncestors() + "," + parentId;
    }

    /**
     * 过滤排除的菜单（编辑时排除自身及其子菜单）
     */
    private List<SysMenuVO> filterExcludeMenus(List<SysMenuVO> menuList, Long excludeId) {
        // 获取需要排除的菜单ID集合（包括自身和所有子菜单）
        Set<Long> excludeIds = new HashSet<>();
        collectExcludeMenuIds(menuList, excludeId, excludeIds);
        
        return menuList.stream()
                .filter(menu -> !excludeIds.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 递归收集需要排除的菜单ID
     */
    private void collectExcludeMenuIds(List<SysMenuVO> menuList, Long parentId, Set<Long> excludeIds) {
        excludeIds.add(parentId);
        
        for (SysMenuVO menu : menuList) {
            if (parentId.equals(menu.getParentId())) {
                collectExcludeMenuIds(menuList, menu.getId(), excludeIds);
            }
        }
    }

    /**
     * 校验菜单是否存在
     */
    private SysMenuDO validateMenuExists(Long id) {
        SysMenuDO menuDO = menuMapper.selectById(id);
        if (menuDO == null) {
            throw new ServiceException(ErrorCode.MENU_NOT_FOUND);
        }
        return menuDO;
    }

    /**
     * 校验菜单名称唯一性（同级菜单下）
     */
    private void validateMenuNameUnique(Long excludeId, Long parentId, String menuName) {
        LambdaQueryWrapper<SysMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuDO::getParentId, parentId)
                .eq(SysMenuDO::getMenuName, menuName);
                
        if (excludeId != null) {
            wrapper.ne(SysMenuDO::getId, excludeId);
        }
        
        SysMenuDO menu = menuMapper.selectOne(wrapper);
        if (menu != null) {
            throw new ServiceException(ErrorCode.MENU_NAME_ALREADY_EXISTS);
        }
    }

    /**
     * 校验权限标识唯一性
     */
    private void validatePermissionUnique(Long excludeId, String permission) {
        SysMenuDO menu = menuMapper.selectByPermission(permission);
        if (menu != null && !menu.getId().equals(excludeId)) {
            throw new ServiceException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }
    }

    /**
     * 校验父菜单变更的合法性
     */
    private void validateParentMenuChange(Long menuId, Long newParentId) {
        // 不能将菜单移动到自己的子菜单下
        String ancestors = buildAncestors(newParentId);
        if (ancestors.contains(menuId.toString())) {
            throw new ServiceException(ErrorCode.CANNOT_SELECT_CHILD_AS_PARENT);
        }
    }

    /**
     * 校验是否有子菜单
     */
    private void validateNoChildrenMenus(Long menuId) {
        Long childrenCount = menuMapper.selectChildrenCountByParentId(menuId);
        if (childrenCount > 0) {
            throw new ServiceException(ErrorCode.MENU_HAS_CHILDREN);
        }
    }

    /**
     * 校验菜单是否被角色使用
     */
    private void validateMenuNotUsedByRole(Long menuId) {
        LambdaQueryWrapper<com.admin.module.system.biz.dal.dataobject.SysRoleMenuDO> wrapper = 
            new LambdaQueryWrapper<>();
        wrapper.eq(com.admin.module.system.biz.dal.dataobject.SysRoleMenuDO::getMenuId, menuId);
        
        Long count = roleMenuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ServiceException(ErrorCode.MENU_ASSIGNED_TO_ROLE);
        }
    }
}