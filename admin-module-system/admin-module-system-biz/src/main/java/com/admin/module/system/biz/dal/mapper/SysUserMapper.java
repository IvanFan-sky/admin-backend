package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysUserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户数据访问层接口
 * 
 * 提供用户相关的数据库操作方法
 * 包括基础CRUD操作和业务查询方法
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，不存在时返回null
     */
    SysUserDO selectUserByUsername(@Param("username") String username);

    /**
     * 根据条件查询用户列表
     *
     * @param username 用户名（模糊查询）
     * @param nickname 昵称（模糊查询）
     * @param phone 手机号（模糊查询）
     * @param status 用户状态
     * @param beginTime 创建开始时间
     * @param endTime 创建结束时间
     * @return 符合条件的用户列表
     */
    List<SysUserDO> selectUserList(@Param("username") String username, 
                                   @Param("nickname") String nickname,
                                   @Param("phone") String phone, 
                                   @Param("status") Integer status,
                                   @Param("beginTime") String beginTime, 
                                   @Param("endTime") String endTime);

    /**
     * 校验用户名唯一性
     *
     * @param username 用户名
     * @param id 用户ID（排除自身）
     * @return 重复数量，0表示唯一
     */
    int checkUsernameUnique(@Param("username") String username, @Param("id") Long id);

    /**
     * 校验手机号唯一性
     *
     * @param phone 手机号
     * @param id 用户ID（排除自身）
     * @return 重复数量，0表示唯一
     */
    int checkPhoneUnique(@Param("phone") String phone, @Param("id") Long id);

    /**
     * 校验邮箱唯一性
     *
     * @param email 邮箱
     * @param id 用户ID（排除自身）
     * @return 重复数量，0表示唯一
     */
    int checkEmailUnique(@Param("email") String email, @Param("id") Long id);

    /**
     * 根据用户名查询用户（用于导入验证）
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUserDO selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户（用于导入验证）
     *
     * @param email 邮箱
     * @return 用户信息
     */
    SysUserDO selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户（用于导入验证）
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    SysUserDO selectByMobile(@Param("mobile") String mobile);
}