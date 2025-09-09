package com.admin.common.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("records")
    private List<T> records;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("current")
    private Long current;

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