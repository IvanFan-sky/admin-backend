package com.admin.common.utils;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.page.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class PageUtils {

    public static <T> Page<T> buildPage(PageQuery pageQuery) {
        Integer pageNum = pageQuery.getPageNum();
        Integer pageSize = pageQuery.getPageSize();
        if (pageNum != null && pageSize != null) {
            String orderBy = pageQuery.getOrderBy();
            Page<T> page = new Page<>(pageNum, pageSize);
            if (orderBy != null && !orderBy.isEmpty()) {
                if (orderBy.toUpperCase().contains("ASC")) {
                    String column = orderBy.replace(" ASC", "").trim();
                    page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.asc(column));
                } else if (orderBy.toUpperCase().contains("DESC")) {
                    String column = orderBy.replace(" DESC", "").trim();
                    page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.desc(column));
                } else {
                    page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.desc(orderBy));
                }
            }
            return page;
        }
        return new Page<>();
    }

    public static <T> Page<T> buildPage(PageQuery pageQuery, String defaultOrderBy) {
        Page<T> page = buildPage(pageQuery);
        if (page.orders().isEmpty() && defaultOrderBy != null && !defaultOrderBy.isEmpty()) {
            if (defaultOrderBy.toUpperCase().contains("ASC")) {
                String column = defaultOrderBy.replace(" ASC", "").trim();
                page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.asc(column));
            } else if (defaultOrderBy.toUpperCase().contains("DESC")) {
                String column = defaultOrderBy.replace(" DESC", "").trim();
                page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.desc(column));
            } else {
                page.addOrder(com.baomidou.mybatisplus.core.conditions.query.OrderItem.desc(defaultOrderBy));
            }
        }
        return page;
    }

    public static <T> PageResult<T> buildPageResult(IPage<T> page) {
        return PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    public static <T, R> PageResult<R> buildPageResult(IPage<T> page, List<R> list) {
        return PageResult.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }
}