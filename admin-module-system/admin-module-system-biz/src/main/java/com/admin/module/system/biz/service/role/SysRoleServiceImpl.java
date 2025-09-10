package com.admin.module.system.biz.service.role;

import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.PageUtils;
import com.admin.module.system.api.dto.role.*;
import com.admin.module.system.api.service.role.SysRoleService;
import com.admin.module.system.api.vo.role.SysRoleVO;
import com.admin.module.system.biz.convert.role.SysRoleConvert;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import com.admin.module.system.biz.dal.dataobject.SysRoleMenuDO;
import com.admin.module.system.biz.dal.mapper.SysRoleMapper;
import com.admin.module.system.biz.dal.mapper.SysRoleMenuMapper;
import com.admin.module.system.biz.dal.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 系统角色管理服务实现类
 * 
 * 提供角色的增删改查、权限管理等核心功能
 * 支持角色状态管理、权限分配、批量操作等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleConvert roleConvert;

    @Override
    @Transactional
    public Long createRole(SysRoleCreateDTO createDTO) {
        log.debug("开始创建角色，参数: {}", createDTO);

        // 校验角色名称唯一性
        validateRoleNameUnique(null, createDTO.getRoleName());

        // 校验角色编码唯一性
        validateRoleCodeUnique(null, createDTO.getRoleCode());

        // 转换并保存
        SysRoleDO roleDO = roleConvert.convertToDO(createDTO);
        roleMapper.insert(roleDO);

        log.info("角色创建成功，角色ID: {}, 角色名称: {}, 角色编码: {}", 
                roleDO.getId(), roleDO.getRoleName(), roleDO.getRoleCode());
        
        return roleDO.getId();
    }

    @Override
    @Transactional
    public void updateRole(SysRoleUpdateDTO updateDTO) {
        log.debug("开始更新角色，参数: {}", updateDTO);

        // 校验角色存在性
        SysRoleDO existingRole = validateRoleExists(updateDTO.getId());

        // 校验角色名称唯一性（排除自身）
        validateRoleNameUnique(updateDTO.getId(), updateDTO.getRoleName());

        // 校验角色编码唯一性（排除自身）
        validateRoleCodeUnique(updateDTO.getId(), updateDTO.getRoleCode());

        // 更新角色信息
        roleConvert.updateDO(updateDTO, existingRole);
        int updateCount = roleMapper.updateById(existingRole);
        
        if (updateCount == 0) {
            throw new ServiceException("角色更新失败，数据可能已被其他人修改，请刷新后重试");
        }

        log.info("角色更新成功，角色ID: {}, 角色名称: {}", updateDTO.getId(), updateDTO.getRoleName());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        log.debug("开始删除角色，角色ID: {}", id);

        // 校验角色存在性
        validateRoleExists(id);

        // 校验角色是否被用户使用
        validateRoleNotUsed(id);

        // 删除角色
        roleMapper.deleteById(id);

        // 删除角色菜单关联关系
        roleMenuMapper.deleteByRoleId(id);

        log.info("角色删除成功，角色ID: {}", id);
    }

    @Override
    @Transactional
    public int deleteRolesBatch(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        log.debug("开始批量删除角色，角色ID列表: {}", ids);

        int deleteCount = 0;
        for (Long id : ids) {
            try {
                deleteRole(id);
                deleteCount++;
            } catch (Exception e) {
                log.warn("删除角色失败，角色ID: {}, 原因: {}", id, e.getMessage());
            }
        }

        log.info("批量删除角色完成，删除数量: {}", deleteCount);
        return deleteCount;
    }

    @Override
    public SysRoleVO getRole(Long id) {
        if (id == null) {
            return null;
        }
        
        SysRoleDO roleDO = roleMapper.selectById(id);
        return roleConvert.convertToVO(roleDO);
    }

    @Override
    public SysRoleVO getRoleByCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return null;
        }
        
        SysRoleDO roleDO = roleMapper.selectByRoleCode(roleCode);
        return roleConvert.convertToVO(roleDO);
    }

    @Override
    public PageResult<SysRoleVO> getRolePage(SysRoleQueryDTO queryDTO) {
        Page<SysRoleDO> page = PageUtils.buildPage(queryDTO, "sort_order ASC, create_time DESC");
        
        LambdaQueryWrapper<SysRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()), SysRoleDO::getRoleName, queryDTO.getRoleName())
                .like(StringUtils.hasText(queryDTO.getRoleCode()), SysRoleDO::getRoleCode, queryDTO.getRoleCode())
                .eq(queryDTO.getStatus() != null, SysRoleDO::getStatus, queryDTO.getStatus())
                .orderByAsc(SysRoleDO::getSortOrder)
                .orderByDesc(SysRoleDO::getCreateTime);
                
        Page<SysRoleDO> pageResult = roleMapper.selectPage(page, wrapper);
        
        List<SysRoleVO> voList = roleConvert.convertToVOList(pageResult.getRecords());
        return PageUtils.buildPageResult(pageResult, voList);
    }

    @Override
    public List<SysRoleVO> getEnabledRoleList() {
        LambdaQueryWrapper<SysRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDO::getStatus, 1)
                .orderByAsc(SysRoleDO::getSortOrder)
                .orderByDesc(SysRoleDO::getCreateTime);
                
        List<SysRoleDO> roleDOList = roleMapper.selectList(wrapper);
        return roleConvert.convertToVOList(roleDOList);
    }

    @Override
    public List<SysRoleVO> getRolesByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<SysRoleDO> roleDOList = roleMapper.selectRolesByUserId(userId);
        return roleConvert.convertToVOList(roleDOList);
    }

    @Override
    @Transactional
    public void assignRoleMenus(SysRoleMenuDTO roleMenuDTO) {
        log.debug("开始分配角色菜单权限，参数: {}", roleMenuDTO);

        // 校验角色存在性
        validateRoleExists(roleMenuDTO.getRoleId());

        // 删除原有权限关联
        roleMenuMapper.deleteByRoleId(roleMenuDTO.getRoleId());

        // 批量插入新的权限关联
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
    public List<Long> getRoleMenuIds(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void updateRoleStatus(Long id, Integer status) {
        log.debug("开始更新角色状态，角色ID: {}, 状态: {}", id, status);

        // 校验角色存在性
        SysRoleDO roleDO = validateRoleExists(id);

        // 更新状态
        roleDO.setStatus(status);
        int updateCount = roleMapper.updateById(roleDO);
        
        if (updateCount == 0) {
            throw new ServiceException("角色状态更新失败");
        }

        log.info("角色状态更新成功，角色ID: {}, 新状态: {}", id, status);
    }

    /**
     * 校验角色是否存在
     */
    private SysRoleDO validateRoleExists(Long id) {
        SysRoleDO roleDO = roleMapper.selectById(id);
        if (roleDO == null) {
            throw new ServiceException("角色不存在");
        }
        return roleDO;
    }

    /**
     * 校验角色名称唯一性
     */
    private void validateRoleNameUnique(Long excludeId, String roleName) {
        SysRoleDO role = roleMapper.selectByRoleName(roleName);
        if (role != null && !role.getId().equals(excludeId)) {
            throw new ServiceException("角色名称已存在");
        }
    }

    /**
     * 校验角色编码唯一性
     */
    private void validateRoleCodeUnique(Long excludeId, String roleCode) {
        SysRoleDO role = roleMapper.selectByRoleCode(roleCode);
        if (role != null && !role.getId().equals(excludeId)) {
            throw new ServiceException("角色编码已存在");
        }
    }

    /**
     * 校验角色是否被使用
     */
    private void validateRoleNotUsed(Long roleId) {
        LambdaQueryWrapper<com.admin.module.system.biz.dal.dataobject.SysUserRoleDO> wrapper = 
            new LambdaQueryWrapper<>();
        wrapper.eq(com.admin.module.system.biz.dal.dataobject.SysUserRoleDO::getRoleId, roleId);
        
        Long count = userRoleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ServiceException("角色正在被使用，无法删除");
        }
    }
}