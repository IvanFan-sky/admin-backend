package com.admin.module.system.biz.service.user;

import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.common.utils.PageUtils;
import com.admin.module.system.api.dto.user.SysUserCreateDTO;
import com.admin.module.system.api.dto.user.SysUserQueryDTO;
import com.admin.module.system.api.dto.user.SysUserResetPwdDTO;
import com.admin.module.system.api.dto.user.SysUserUpdateDTO;
import com.admin.module.system.api.service.user.SysUserService;
import com.admin.module.system.api.vo.user.SysUserVO;
import com.admin.module.system.biz.convert.user.SysUserConvert;
import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import com.admin.module.system.biz.dal.dataobject.SysUserRoleDO;
import com.admin.framework.redis.constants.CacheConstants;
import com.admin.module.system.biz.dal.mapper.SysUserMapper;
import com.admin.module.system.biz.dal.mapper.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统用户管理服务实现类
 * 
 * 提供用户的增删改查、权限管理等核心功能
 * 支持用户状态管理、密码加密、角色分配等业务逻辑
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUserVO> getUserPage(SysUserQueryDTO queryDTO) {
        Page<SysUserDO> page = PageUtils.buildPage(queryDTO, "create_time DESC");
        
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUserDO::getUsername, queryDTO.getUsername())
               .like(StringUtils.hasText(queryDTO.getNickname()), SysUserDO::getNickname, queryDTO.getNickname())
               .like(StringUtils.hasText(queryDTO.getPhone()), SysUserDO::getPhone, queryDTO.getPhone())
               .eq(queryDTO.getStatus() != null, SysUserDO::getStatus, queryDTO.getStatus())
               .ge(StringUtils.hasText(queryDTO.getBeginTime()), SysUserDO::getCreateTime, queryDTO.getBeginTime())
               .le(StringUtils.hasText(queryDTO.getEndTime()), SysUserDO::getCreateTime, queryDTO.getEndTime())
               .orderByDesc(SysUserDO::getCreateTime);

        Page<SysUserDO> result = userMapper.selectPage(page, wrapper);
        return PageUtils.buildPageResult(result, SysUserConvert.INSTANCE.convertList(result.getRecords()));
    }

    @Override
    public List<SysUserVO> getUserList(SysUserQueryDTO queryDTO) {
        List<SysUserDO> list = userMapper.selectUserList(
            queryDTO.getUsername(),
            queryDTO.getNickname(),
            queryDTO.getPhone(),
            queryDTO.getStatus(),
            queryDTO.getBeginTime(),
            queryDTO.getEndTime()
        );
        return SysUserConvert.INSTANCE.convertList(list);
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_USER_CACHE, key = "#id", unless = "#result == null")
    public SysUserVO getUser(Long id) {
        SysUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return SysUserConvert.INSTANCE.convert(user);
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_USER_CACHE, key = "'username:' + #username", unless = "#result == null")
    public SysUserVO getUserByUsername(String username) {
        SysUserDO user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }
        return SysUserConvert.INSTANCE.convert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_USER_CACHE, allEntries = true)
    public Long createUser(SysUserCreateDTO createDTO) {
        // 1. 数据唯一性校验
        validateUserForCreateOrUpdate(null, createDTO.getUsername(), createDTO.getPhone(), createDTO.getEmail());
        
        // 2. 转换DTO为数据对象
        SysUserDO user = SysUserConvert.INSTANCE.convert(createDTO);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);
        
        // 3. 插入用户基本信息
        userMapper.insert(user);
        
        // 4. 分配用户角色
        insertUserRole(user.getId(), createDTO.getRoleIds());
        
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_USER_CACHE, key = "#updateDTO.id"),
        @CacheEvict(value = CacheConstants.SYS_USER_CACHE, allEntries = true)
    })
    public void updateUser(SysUserUpdateDTO updateDTO) {
        SysUserDO existUser = userMapper.selectById(updateDTO.getId());
        if (existUser == null) {
            throw new ServiceException("用户不存在");
        }
        
        validateUserForCreateOrUpdate(updateDTO.getId(), null, updateDTO.getPhone(), updateDTO.getEmail());
        
        SysUserDO user = SysUserConvert.INSTANCE.convert(updateDTO);
        userMapper.updateById(user);
        
        userRoleMapper.deleteUserRoleByUserId(updateDTO.getId());
        insertUserRole(updateDTO.getId(), updateDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.SYS_USER_CACHE, key = "#id"),
        @CacheEvict(value = CacheConstants.SYS_USER_CACHE, allEntries = true)
    })
    public void deleteUser(Long id) {
        if (SysUserDO.isAdmin(id)) {
            throw new ServiceException("不允许删除超级管理员用户");
        }
        
        userMapper.deleteById(id);
        userRoleMapper.deleteUserRoleByUserId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_USER_CACHE, allEntries = true)
    public void deleteUsers(Long[] ids) {
        for (Long id : ids) {
            if (SysUserDO.isAdmin(id)) {
                throw new ServiceException("不允许删除超级管理员用户");
            }
        }
        
        userMapper.deleteBatchIds(Arrays.asList(ids));
        for (Long id : ids) {
            userRoleMapper.deleteUserRoleByUserId(id);
        }
    }

    @Override
    @CacheEvict(value = CacheConstants.SYS_USER_CACHE, key = "#resetPwdDTO.id")
    public void resetUserPwd(SysUserResetPwdDTO resetPwdDTO) {
        SysUserDO user = userMapper.selectById(resetPwdDTO.getId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(resetPwdDTO.getPassword()));
        user.setVersion(resetPwdDTO.getVersion());
        userMapper.updateById(user);
    }

    @Override
    @CacheEvict(value = CacheConstants.SYS_USER_CACHE, key = "#id")
    public void updateUserStatus(Long id, Integer status) {
        if (SysUserDO.isAdmin(id)) {
            throw new ServiceException("不允许修改超级管理员用户状态");
        }
        
        SysUserDO user = new SysUserDO();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public boolean checkUsernameUnique(String username, Long id) {
        return userMapper.checkUsernameUnique(username, id) == 0;
    }

    @Override
    public boolean checkPhoneUnique(String phone, Long id) {
        if (!StringUtils.hasText(phone)) {
            return true;
        }
        return userMapper.checkPhoneUnique(phone, id) == 0;
    }

    @Override
    public boolean checkEmailUnique(String email, Long id) {
        if (!StringUtils.hasText(email)) {
            return true;
        }
        return userMapper.checkEmailUnique(email, id) == 0;
    }

    @Override
    @Cacheable(value = CacheConstants.USER_ROLE_CACHE, key = "'role_user_ids:' + #roleId", unless = "#result == null || #result.isEmpty()")
    public List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    /**
     * 校验用户数据唯一性
     * 
     * 在创建或更新用户时，校验用户名、手机号、邮箱的唯一性
     * 避免重复数据导致的业务异常
     *
     * @param id 用户ID，用于排除自身（创建时为null）
     * @param username 用户名
     * @param phone 手机号
     * @param email 邮箱
     * @throws ServiceException 当发现重复数据时抛出异常
     */
    private void validateUserForCreateOrUpdate(Long id, String username, String phone, String email) {
        if (StringUtils.hasText(username) && !checkUsernameUnique(username, id)) {
            throw new ServiceException("用户账号已存在");
        }
        if (!checkPhoneUnique(phone, id)) {
            throw new ServiceException("手机号码已存在");
        }
        if (!checkEmailUnique(email, id)) {
            throw new ServiceException("邮箱已存在");
        }
    }

    /**
     * 批量插入用户角色关联关系
     * 
     * 为指定用户分配角色列表
     * 支持一个用户拥有多个角色的业务场景
     *
     * @param userId 用户ID
     * @param roleIds 角色ID数组
     */
    private void insertUserRole(Long userId, Long[] roleIds) {
        if (roleIds != null && roleIds.length > 0) {
            List<SysUserRoleDO> userRoleList = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRoleDO userRole = new SysUserRoleDO();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            userRoleMapper.batchUserRole(userRoleList);
        }
    }
}