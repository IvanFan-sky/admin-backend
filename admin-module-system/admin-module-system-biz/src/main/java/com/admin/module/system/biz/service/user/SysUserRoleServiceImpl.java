package com.admin.module.system.biz.service.user;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.system.api.dto.user.SysUserRoleDTO;
import com.admin.module.system.api.service.role.SysRoleService;
import com.admin.module.system.api.service.user.SysUserRoleService;
import com.admin.module.system.api.service.user.SysUserService;
import com.admin.module.system.api.vo.role.SysRoleVO;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import com.admin.module.system.biz.dal.dataobject.SysUserRoleDO;
import com.admin.module.system.biz.dal.mapper.SysRoleMapper;
import com.admin.module.system.biz.dal.mapper.SysUserRoleMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用户角色关联管理服务实现类
 * 
 * 提供用户角色关联的完整业务逻辑
 * 支持角色分配、查询、批量操作等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserService userService;
    private final SysRoleService roleService;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.USER_ROLE_CACHE, allEntries = true),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void assignUserRoles(SysUserRoleDTO userRoleDTO) {
        log.debug("开始分配用户角色，参数: {}", userRoleDTO);

        // 校验用户存在性
        validateUserExists(userRoleDTO.getUserId());

        // 校验角色存在性和有效性
        validateRolesExistAndEnabled(userRoleDTO.getRoleIds());

        // 删除用户原有角色关联
        userRoleMapper.deleteUserRoleByUserId(userRoleDTO.getUserId());

        // 批量插入新的角色关联
        if (!CollectionUtils.isEmpty(userRoleDTO.getRoleIds())) {
            List<SysUserRoleDO> userRoleList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (Long roleId : userRoleDTO.getRoleIds()) {
                SysUserRoleDO userRoleDO = new SysUserRoleDO();
                userRoleDO.setUserId(userRoleDTO.getUserId());
                userRoleDO.setRoleId(roleId);
                userRoleDO.setCreateTime(now);
                userRoleDO.setRemark(userRoleDTO.getRemark());
                userRoleList.add(userRoleDO);
            }
            
            userRoleMapper.batchUserRole(userRoleList);
        }

        log.info("用户角色分配成功，用户ID: {}, 角色数量: {}", 
                userRoleDTO.getUserId(), userRoleDTO.getRoleIds().size());
    }

    @Override
    public List<SysRoleVO> getUserRoles(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        return roleService.getRolesByUserId(userId);
    }

    @Override
    @Cacheable(value = CacheConstants.USER_ROLE_CACHE, key = "'user_role_ids:' + #userId", unless = "#result == null || #result.isEmpty()")
    public List<Long> getUserRoleIds(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.USER_ROLE_CACHE, key = "'user_role_ids:' + #userId"),
        @CacheEvict(value = CacheConstants.USER_ROLE_CACHE, key = "'role_user_ids:' + #roleId"),
        @CacheEvict(value = CacheConstants.USER_PERMISSION_CACHE, allEntries = true)
    })
    public void removeUserRole(Long userId, Long roleId) {
        log.debug("开始移除用户角色，用户ID: {}, 角色ID: {}", userId, roleId);

        // 校验用户和角色存在性
        validateUserExists(userId);
        validateRoleExists(roleId);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDO::getUserId, userId)
                .eq(SysUserRoleDO::getRoleId, roleId);
        
        int deleteCount = userRoleMapper.delete(wrapper);
        
        if (deleteCount == 0) {
            log.warn("用户角色关联不存在，用户ID: {}, 角色ID: {}", userId, roleId);
        }

        log.info("用户角色移除成功，用户ID: {}, 角色ID: {}", userId, roleId);
    }

    @Override
    @Transactional
    public void removeAllUserRoles(Long userId) {
        if (userId == null) {
            return;
        }

        log.debug("开始移除用户所有角色，用户ID: {}", userId);

        userRoleMapper.deleteUserRoleByUserId(userId);

        log.info("用户所有角色移除成功，用户ID: {}", userId);
    }

    @Override
    @Transactional
    public int removeUserRolesBatch(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        log.debug("开始批量移除用户角色关联，用户ID列表: {}", userIds);

        int removeCount = 0;
        for (Long userId : userIds) {
            try {
                removeAllUserRoles(userId);
                removeCount++;
            } catch (Exception e) {
                log.warn("移除用户角色失败，用户ID: {}, 原因: {}", userId, e.getMessage());
            }
        }

        log.info("批量移除用户角色完成，移除数量: {}", removeCount);
        return removeCount;
    }

    @Override
    @Cacheable(value = CacheConstants.USER_ROLE_CACHE, key = "'role_user_ids:' + #roleId", unless = "#result == null || #result.isEmpty()")
    public List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    public boolean hasUserRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return false;
        }

        LambdaQueryWrapper<SysUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDO::getUserId, userId)
                .eq(SysUserRoleDO::getRoleId, roleId);
        
        return userRoleMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasAnyUserRole(Long userId, Set<Long> roleIds) {
        if (userId == null || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        List<Long> userRoleIds = getUserRoleIds(userId);
        return userRoleIds.stream().anyMatch(roleIds::contains);
    }

    @Override
    public boolean hasAllUserRoles(Long userId, Set<Long> roleIds) {
        if (userId == null || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        List<Long> userRoleIds = getUserRoleIds(userId);
        return userRoleIds.containsAll(roleIds);
    }

    /**
     * 校验用户是否存在
     */
    private void validateUserExists(Long userId) {
        if (userService.getUser(userId) == null) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * 校验角色是否存在
     */
    private void validateRoleExists(Long roleId) {
        if (roleService.getRole(roleId) == null) {
            throw new ServiceException(ErrorCode.ROLE_NOT_FOUND);
        }
    }

    /**
     * 校验角色存在性和有效性
     */
    private void validateRolesExistAndEnabled(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        for (Long roleId : roleIds) {
            SysRoleDO role = roleMapper.selectById(roleId);
            if (role == null) {
                throw new ServiceException("角色不存在，角色ID: " + roleId);
            }
            if (role.getStatus() == null || role.getStatus() != 1) {
                throw new ServiceException("角色已被禁用，无法分配，角色ID: " + roleId);
            }
        }
    }
}