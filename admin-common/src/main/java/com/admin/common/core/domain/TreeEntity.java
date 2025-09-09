package com.admin.common.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
}