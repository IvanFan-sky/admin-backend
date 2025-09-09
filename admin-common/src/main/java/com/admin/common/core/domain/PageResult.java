package com.admin.common.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果封装类
 * 
 * 用于封装分页查询的结果数据
 * 包含数据列表、总数、页码等分页信息
 *
 * @param <T> 数据项的类型
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     * 当前页的具体数据记录
     */
    @JsonProperty("records")
    private List<T> records;

    /**
     * 总记录数
     * 符合查询条件的记录总数
     */
    @JsonProperty("total")
    private Long total;

    /**
     * 每页显示条数
     * 分页查询的页面大小
     */
    @JsonProperty("size")
    private Long size;

    /**
     * 当前页码
     * 从1开始的页码数
     */
    @JsonProperty("current")
    private Long current;

    /**
     * 总页数
     * 根据总记录数和每页条数计算得出
     */
    @JsonProperty("pages")
    private Long pages;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Long size, Long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = (total + size - 1) / size;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long size, Long current) {
        return new PageResult<>(records, total, size, current);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
}