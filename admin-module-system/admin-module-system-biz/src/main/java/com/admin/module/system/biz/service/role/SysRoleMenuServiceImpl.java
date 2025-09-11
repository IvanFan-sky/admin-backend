package com.admin.module.system.biz.service.role;

import com.admin.common.exception.ServiceException;
import com.admin.module.system.api.dto.role.SysRoleMenuDTO;
import com.admin.module.system.api.service.menu.SysMenuService;
import com.admin.module.system.api.service.role.SysRoleMenuService;
import com.admin.module.system.api.service.role.SysRoleService;
import com.admin.module.system.api.vo.menu.SysMenuVO;
import com.admin.module.system.biz.dal.dataobject.SysMenuDO;
import com.admin.module.system.biz.dal.dataobject.SysRoleMenuDO;
import com.admin.module.system.biz.dal.mapper.SysMenuMapper;
import com.admin.module.system.biz.dal.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.admin.framework.redis.constants.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色菜单关联管理服务实现类
 * 
 * 提供角色菜单关联的完整业务逻辑
 * 支持权限分配、查询、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleService roleService;
    private final SysMenuService menuService;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_ROLE_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void assignRoleMenus(SysRoleMenuDTO roleMenuDTO) {
        log.debug("开始分配角色菜单权限，参数: {}", roleMenuDTO);

        // 校验角色存在性
        validateRoleExists(roleMenuDTO.getRoleId());

        // 校验菜单存在性和有效性
        validateMenusExistAndEnabled(roleMenuDTO.getMenuIds());

        // 删除角色原有菜单权限关联
        removeAllRoleMenus(roleMenuDTO.getRoleId());

        // 批量插入新的菜单权限关联
        if (!CollectionUtils.isEmpty(roleMenuDTO.getMenuIds())) {
            List<SysRoleMenuDO> roleMenuList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (Long menuId : roleMenuDTO.getMenuIds()) {
                SysRoleMenuDO roleMenuDO = new SysRoleMenuDO();
                roleMenuDO.setRoleId(roleMenuDTO.getRoleId());
                roleMenuDO.setMenuId(menuId);
                roleMenuDO.setCreateTime(now);
                roleMenuDO.setRemark(roleMenuDTO.getRemark());
                roleMenuList.add(roleMenuDO);
            }
            
            roleMenuMapper.insertBatch(roleMenuList);
        }

        log.info("角色菜单权限分配成功，角色ID: {}, 菜单数量: {}", 
                roleMenuDTO.getRoleId(), roleMenuDTO.getMenuIds().size());
    }

    @Override
    public List<SysMenuVO> getRoleMenus(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        
        return menuService.getMenusByRoleId(roleId);
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_ROLE_CACHE, key = "'menu_ids:' + #roleId", unless = "#result == null || #result.isEmpty()")
    public List<Long> getRoleMenuIds(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_ROLE_CACHE, key = "'menu_ids:' + #roleId"),
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, key = "'role_ids:' + #menuId"),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void removeRoleMenu(Long roleId, Long menuId) {
        log.debug("开始移除角色菜单权限，角色ID: {}, 菜单ID: {}", roleId, menuId);

        // 校验角色和菜单存在性
        validateRoleExists(roleId);
        validateMenuExists(menuId);

        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDO::getRoleId, roleId)
                .eq(SysRoleMenuDO::getMenuId, menuId);
        
        int deleteCount = roleMenuMapper.delete(wrapper);
        
        if (deleteCount == 0) {
            log.warn("角色菜单权限关联不存在，角色ID: {}, 菜单ID: {}", roleId, menuId);
        }

        log.info("角色菜单权限移除成功，角色ID: {}, 菜单ID: {}", roleId, menuId);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_ROLE_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.SYS_MENU_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void removeAllRoleMenus(Long roleId) {
        if (roleId == null) {
            return;
        }

        log.debug("开始移除角色所有菜单权限，角色ID: {}", roleId);

        roleMenuMapper.deleteByRoleId(roleId);

        log.info("角色所有菜单权限移除成功，角色ID: {}", roleId);
    }

    @Override
    @Transactional
    public int removeRoleMenusBatch(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return 0;
        }

        log.debug("开始批量移除角色菜单权限关联，角色ID列表: {}", roleIds);

        int removeCount = 0;
        for (Long roleId : roleIds) {
            try {
                removeAllRoleMenus(roleId);
                removeCount++;
            } catch (Exception e) {
                log.warn("移除角色菜单权限失败，角色ID: {}, 原因: {}", roleId, e.getMessage());
            }
        }

        log.info("批量移除角色菜单权限完成，移除数量: {}", removeCount);
        return removeCount;
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_MENU_CACHE, key = "'role_ids:' + #menuId", unless = "#result == null || #result.isEmpty()")
    public List<Long> getRoleIdsByMenuId(Long menuId) {
        if (menuId == null) {
            return new ArrayList<>();
        }
        
        return roleMenuMapper.selectRoleIdsByMenuId(menuId);
    }

    @Override
    public boolean hasRoleMenu(Long roleId, Long menuId) {
        if (roleId == null || menuId == null) {
            return false;
        }

        return roleMenuMapper.existsRoleMenuRelation(roleId, menuId);
    }

    @Override
    public boolean hasAnyRoleMenu(Long roleId, Set<Long> menuIds) {
        if (roleId == null || CollectionUtils.isEmpty(menuIds)) {
            return false;
        }

        List<Long> roleMenuIds = getRoleMenuIds(roleId);
        return roleMenuIds.stream().anyMatch(menuIds::contains);
    }

    @Override
    public boolean hasAllRoleMenus(Long roleId, Set<Long> menuIds) {
        if (roleId == null || CollectionUtils.isEmpty(menuIds)) {
            return false;
        }

        List<Long> roleMenuIds = getRoleMenuIds(roleId);
        return roleMenuIds.containsAll(menuIds);
    }

    @Override
    @Transactional
    public int removeRoleMenusByMenuIds(Set<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return 0;
        }

        log.debug("开始根据菜单ID列表批量移除角色菜单权限关联，菜单ID列表: {}", menuIds);

        roleMenuMapper.deleteByMenuIds(menuIds);

        log.info("根据菜单ID列表批量移除角色菜单权限关联完成，菜单数量: {}", menuIds.size());
        return menuIds.size();
    }

    @Override
    public boolean isMenuUsedByAnyRole(Long menuId) {
        if (menuId == null) {
            return false;
        }

        LambdaQueryWrapper<SysRoleMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDO::getMenuId, menuId);
        
        return roleMenuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<SysMenuVO> getRoleMenuTree(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }

        List<SysMenuVO> menuList = getRoleMenus(roleId);
        return buildMenuTree(menuList);
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
     * 校验角色是否存在
     */
    private void validateRoleExists(Long roleId) {
        if (roleService.getRole(roleId) == null) {
            throw new ServiceException("角色不存在");
        }
    }

    /**
     * 校验菜单是否存在
     */
    private void validateMenuExists(Long menuId) {
        if (menuService.getMenu(menuId) == null) {
            throw new ServiceException("菜单不存在");
        }
    }

    /**
     * 校验菜单存在性和有效性
     */
    private void validateMenusExistAndEnabled(Set<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }

        for (Long menuId : menuIds) {
            SysMenuDO menu = menuMapper.selectById(menuId);
            if (menu == null) {
                throw new ServiceException("菜单不存在，菜单ID: " + menuId);
            }
            if (menu.getStatus() == null || menu.getStatus() != 1) {
                throw new ServiceException("菜单已被禁用，无法分配权限，菜单ID: " + menuId);
            }
        }
    }
}