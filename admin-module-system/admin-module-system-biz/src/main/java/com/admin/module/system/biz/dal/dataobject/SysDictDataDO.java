package com.admin.module.system.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典数据表数据对象
 * 
 * 对应数据库表 sys_dict_data
 * 用于管理具体的字典数据项
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictDataDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典排序
     * 数值越小越靠前显示
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 字典标签
     * 显示给用户看的名称
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     * 实际存储的值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典类型
     * 关联字典类型表的dict_type字段
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 样式属性
     * 前端显示时使用的CSS类名
     */
    @TableField("css_class")
    private String cssClass;

    /**
     * 表格回显样式
     * 在表格中显示时使用的样式
     */
    @TableField("list_class")
    private String listClass;

    /**
     * 是否默认
     * 0-否，1-是
     */
    @TableField("is_default")
    private Integer isDefault;

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