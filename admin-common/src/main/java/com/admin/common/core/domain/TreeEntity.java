package com.admin.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构实体基类
 * 
 * 用于支持树形数据结构的实体类
 * 提供父子关系管理和祖先路径记录功能
 * 适用于菜单、部门、分类等树形数据场景
 *
 * @param <T> 子节点的数据类型
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID
     * 指向父级节点的主键，根节点的父ID通常为0或null
     */
    private Long parentId;

    /**
     * 祖先节点路径
     * 存储从根节点到父节点的完整路径，用逗号分隔
     * 例如：0,1,2 表示根节点->1号节点->2号节点
     */
    private String ancestors;

    /**
     * 子节点列表
     * 存储当前节点的所有直接子节点
     */
    private List<T> children = new ArrayList<>();

    public List<T> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }


    /**
     * 创建人
     * 记录数据创建者的用户名或用户ID
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     * 记录数据的创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人
     * 记录数据最后修改者的用户名或用户ID
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     * 记录数据的最后修改时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     * 用于存储额外的说明信息
     */
    @TableField("remark")
    private String remark;
}