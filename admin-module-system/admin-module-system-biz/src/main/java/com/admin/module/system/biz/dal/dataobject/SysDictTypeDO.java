package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典类型表数据对象
 * 
 * 对应数据库表 sys_dict_type
 * 用于管理系统中的字典分类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictTypeDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     * 字典类型的显示名称
     */
    @TableField("dict_name")
    private String dictName;

    /**
     * 字典类型
     * 字典类型的唯一标识符
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 删除标识
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}