package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色数据对象
 * 
 * 对应数据库sys_role表，用于存储系统角色信息
 * 严格按照数据库表结构定义字段属性
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRoleDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * 字段：id bigint NOT NULL AUTO_INCREMENT
     * 主键，自增长
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     * 字段：role_name varchar(50) NOT NULL
     * 显示给用户的角色名称，如"管理员"、"普通用户"
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色编码
     * 字段：role_code varchar(50) NOT NULL
     * 系统内部使用的角色标识，如"ADMIN"、"USER"
     * 唯一约束，用于权限判断
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色描述
     * 字段：role_desc varchar(200) DEFAULT NULL
     * 详细说明该角色的职责和权限范围
     */
    @TableField("role_desc")
    private String roleDesc;

    /**
     * 显示顺序
     * 字段：sort_order int DEFAULT '0'
     * 用于角色列表的显示排序，数值越小越靠前
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 角色状态
     * 字段：status tinyint DEFAULT '1'
     * 0-禁用，1-启用
     * 禁用的角色不能分配给用户
     */
    @TableField("status")
    private Integer status;

    /**
     * 逻辑删除标识
     * 字段：deleted tinyint DEFAULT '0'
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 乐观锁版本号
     * 字段：version int DEFAULT '0'
     * 用于并发控制，防止数据冲突
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;
}