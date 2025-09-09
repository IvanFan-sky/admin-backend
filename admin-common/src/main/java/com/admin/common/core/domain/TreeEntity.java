package com.admin.common.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long parentId;

    private String ancestors;

    private List<T> children = new ArrayList<>();

    public List<T> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}