package com.admin.module.system.api.service.user;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.user.SysUserCreateDTO;
import com.admin.module.system.api.dto.user.SysUserQueryDTO;
import com.admin.module.system.api.dto.user.SysUserResetPwdDTO;
import com.admin.module.system.api.dto.user.SysUserUpdateDTO;
import com.admin.module.system.api.vo.user.SysUserVO;

import java.util.List;

/**
 * 系统用户管理服务接口
 * 
 * 定义用户相关的业务操作规范
 * 包括用户生命周期管理、权限控制、数据校验等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysUserService {

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件，支持用户名、昵称、手机号、状态等筛选
     * @return 分页结果，包含用户基本信息
     */
    PageResult<SysUserVO> getUserPage(SysUserQueryDTO queryDTO);

    /**
     * 查询用户列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    List<SysUserVO> getUserList(SysUserQueryDTO queryDTO);

    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     * @throws com.admin.common.exception.ServiceException 当用户不存在时抛出
     */
    SysUserVO getUser(Long id);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，不存在时返回null
     */
    SysUserVO getUserByUsername(String username);

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 拥有该角色的用户ID列表
     */
//    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 创建用户
     * 
     * 1. 校验用户名、手机号、邮箱唯一性
     * 2. 加密用户密码
     * 3. 分配用户角色
     * 4. 设置默认状态
     *
     * @param createDTO 创建用户请求参数，包含用户名、密码、邮箱等信息
     * @return 新创建的用户ID
     * @throws com.admin.common.exception.ServiceException 当用户名已存在或参数校验失败时抛出
     */
    Long createUser(SysUserCreateDTO createDTO);

    /**
     * 更新用户信息
     * 
     * 1. 校验用户是否存在
     * 2. 校验手机号、邮箱唯一性
     * 3. 更新用户基本信息
     * 4. 重新分配角色关系
     *
     * @param updateDTO 更新用户请求参数
     * @throws com.admin.common.exception.ServiceException 当用户不存在或校验失败时抛出
     */
    void updateUser(SysUserUpdateDTO updateDTO);

    /**
     * 删除用户
     * 
     * 执行逻辑删除，同时清理用户角色关联关系
     * 超级管理员账户不允许删除
     *
     * @param id 用户ID
     * @throws com.admin.common.exception.ServiceException 当尝试删除超级管理员时抛出
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID数组
     * @throws com.admin.common.exception.ServiceException 当包含超级管理员ID时抛出
     */
    void deleteUsers(Long[] ids);

    /**
     * 重置用户密码
     *
     * @param resetPwdDTO 重置密码请求参数，包含用户ID和新密码
     * @throws com.admin.common.exception.ServiceException 当用户不存在时抛出
     */
    void resetUserPwd(SysUserResetPwdDTO resetPwdDTO);

    /**
     * 更新用户状态
     * 
     * 超级管理员状态不允许修改
     *
     * @param id 用户ID
     * @param status 用户状态 1-正常 0-禁用
     * @throws com.admin.common.exception.ServiceException 当尝试修改超级管理员状态时抛出
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 校验用户名是否唯一
     *
     * @param username 用户名
     * @param id 用户ID，用于排除自身（可为空）
     * @return true-唯一 false-重复
     */
    boolean checkUsernameUnique(String username, Long id);

    /**
     * 校验手机号是否唯一
     *
     * @param phone 手机号
     * @param id 用户ID，用于排除自身（可为空）
     * @return true-唯一 false-重复
     */
    boolean checkPhoneUnique(String phone, Long id);

    /**
     * 校验邮箱是否唯一
     *
     * @param email 邮箱
     * @param id 用户ID，用于排除自身（可为空）
     * @return true-唯一 false-重复
     */
    boolean checkEmailUnique(String email, Long id);
}