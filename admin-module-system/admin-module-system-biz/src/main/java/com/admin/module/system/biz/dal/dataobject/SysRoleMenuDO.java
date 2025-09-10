package com.admin.module.system.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色菜单关联数据对象
 * 
 * 对应数据库sys_role_menu表，用于存储角色与菜单的关联关系
 * 严格按照数据库表结构定义字段属性
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenuDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 字段：id bigint NOT NULL AUTO_INCREMENT
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     * 字段：role_id bigint NOT NULL
     * 关联sys_role表的主键
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 菜单ID
     * 字段：menu_id bigint NOT NULL
     * 关联sys_menu表的主键
     */
    @TableField("menu_id")
    private Long menuId;

    /**
     * 创建者
     * 字段：create_by varchar(50) DEFAULT NULL
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     * 字段：create_time datetime DEFAULT CURRENT_TIMESTAMP
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 备注信息
     * 用于存储额外的说明信息
     */
    @TableField("remark")
    private String remark;

}