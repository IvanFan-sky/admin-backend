package com.admin.common.utils;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.page.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 分页工具类
 * 
 * 提供MyBatis-Plus分页查询的工具方法
 * 包括分页对象构建、排序处理、结果转换等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class PageUtils {

    /**
     * 构建MyBatis-Plus分页对象
     * 
     * 根据查询条件构建分页和排序配置
     * 支持动态排序字段和排序方向
     *
     * @param <T> 数据类型
     * @param pageQuery 分页查询条件
     * @return MyBatis-Plus分页对象
     */
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

    /**
     * 构建MyBatis-Plus分页对象（带默认排序）
     * 
     * 当用户未指定排序时，使用默认排序规则
     * 提供更灵活的分页排序控制
     *
     * @param <T> 数据类型
     * @param pageQuery 分页查询条件
     * @param defaultOrderBy 默认排序字段和方向
     * @return MyBatis-Plus分页对象
     */
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

    /**
     * 构建分页结果对象
     * 
     * 将MyBatis-Plus的分页结果转换为统一的分页响应格式
     *
     * @param <T> 数据类型
     * @param page MyBatis-Plus分页结果
     * @return 统一分页结果对象
     */
    public static <T> PageResult<T> buildPageResult(IPage<T> page) {
        return PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    /**
     * 构建分页结果对象（自定义数据列表）
     * 
     * 适用于需要对查询结果进行转换处理的场景
     * 保持分页信息不变，替换数据内容
     *
     * @param <T> 原始数据类型
     * @param <R> 转换后数据类型
     * @param page MyBatis-Plus分页结果
     * @param list 转换后的数据列表
     * @return 统一分页结果对象
     */
    public static <T, R> PageResult<R> buildPageResult(IPage<T> page, List<R> list) {
        return PageResult.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }
}