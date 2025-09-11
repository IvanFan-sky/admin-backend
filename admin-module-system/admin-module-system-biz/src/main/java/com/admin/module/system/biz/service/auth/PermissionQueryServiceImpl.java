package com.admin.module.system.biz.service.auth;

import com.admin.module.system.api.service.auth.PermissionQueryService;
import com.admin.module.system.biz.dal.dataobject.SysMenuDO;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import com.admin.module.system.biz.dal.mapper.SysMenuMapper;
import com.admin.module.system.biz.dal.mapper.SysRoleMapper;
import com.admin.module.system.biz.dal.mapper.SysUserMapper;
import com.admin.module.system.biz.dal.mapper.SysUserRoleMapper;
import com.admin.framework.redis.constants.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限查询服务实现类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;

    @Override
    @Cacheable(value = CacheConstants.USER_ROLE_CACHE, key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<String> getUserRoles(Long userId) {
        try {
            // 查询用户关联的角色ID列表
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            
            if (CollectionUtils.isEmpty(roleIds)) {
                return new ArrayList<>();
            }
            
            // 查询角色信息
            List<SysRoleDO> roles = roleMapper.selectBatchIds(roleIds);
            
            // 返回角色编码列表，过滤禁用的角色
            return roles.stream()
                    // 1表示启用
                    .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                    .map(SysRoleDO::getRoleCode)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取用户角色失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(value = CacheConstants.USER_PERMISSION_CACHE, key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<String> getUserPermissions(Long userId) {
        try {
            // 直接通过用户ID查询菜单权限（已考虑角色关联）
            List<SysMenuDO> menus = menuMapper.selectMenusByUserId(userId);
            
            if (CollectionUtils.isEmpty(menus)) {
                return new ArrayList<>();
            }
            
            // 提取权限标识，过滤空值和禁用的菜单
            return menus.stream()
                    // 1表示启用
                    .filter(menu -> menu.getStatus() != null && menu.getStatus() == 1)
                    .map(SysMenuDO::getPermission)
                    .filter(permission -> permission != null && !permission.trim().isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取用户权限失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isAdmin(Long userId) {
        try {
            SysUserDO user = userMapper.selectById(userId);
            return user != null && user.isAdmin();
        } catch (Exception e) {
            log.error("检查用户管理员状态失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        try {
            // 如果是管理员，直接返回true
            if (isAdmin(userId)) {
                return true;
            }
            
            // 获取用户权限列表
            List<String> permissions = getUserPermissions(userId);
            return permissions.contains(permission);
            
        } catch (Exception e) {
            log.error("检查用户权限失败，用户ID: {}, 权限: {}, 错误: {}", userId, permission, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        try {
            // 如果是管理员，直接返回true
            if (isAdmin(userId)) {
                return true;
            }
            
            // 获取用户角色列表
            List<String> roles = getUserRoles(userId);
            return roles.contains(roleCode);
            
        } catch (Exception e) {
            log.error("检查用户角色失败，用户ID: {}, 角色: {}, 错误: {}", userId, roleCode, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UserPermissionInfo getUserPermissionInfo(Long userId) {
        try {
            List<String> roles = getUserRoles(userId);
            List<String> permissions = getUserPermissions(userId);
            boolean admin = isAdmin(userId);
            
            return new UserPermissionInfo(userId, admin, roles, permissions);
                    
        } catch (Exception e) {
            log.error("获取用户权限信息失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return new UserPermissionInfo(userId, false, new ArrayList<>(), new ArrayList<>());
        }
    }
}