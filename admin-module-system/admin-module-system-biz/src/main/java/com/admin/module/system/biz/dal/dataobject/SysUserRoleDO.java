package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户角色关联数据对象
 * 
 * 对应数据库sys_user_role表
 * 用于存储用户与角色的多对多关联关系
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class SysUserRoleDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     * 主键，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     * 指向sys_user表的主键
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 角色ID
     * 指向sys_role表的主键
     */
    @TableField("role_id")
    private Long roleId;
}