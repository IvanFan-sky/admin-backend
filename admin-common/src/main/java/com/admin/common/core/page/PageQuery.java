package com.admin.common.core.page;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分页查询请求基础类
 * 
 * 用于接收前端分页查询参数
 * 提供统一的分页参数封装和排序功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /**
     * 默认每页条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大每页条数限制
     */
    public static final int MAX_PAGE_SIZE = 500;

    /**
     * 页码
     * 从1开始的页码数，默认为第1页
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNum = DEFAULT_PAGE_NUM;

    /**
     * 每页显示条数
     * 每页显示的记录数，默认10条，最大500条
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = MAX_PAGE_SIZE, message = "每页条数最大值为 500")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     * 支持驼峰命名，会自动转换为数据库字段格式
     */
    private String orderByColumn;

    /**
     * 排序方向
     * asc-升序 desc-降序
     */
    private String isAsc;

    public String getOrderBy() {
        if (orderByColumn == null || orderByColumn.trim().isEmpty()) {
            return "";
        }
        String orderBy = toUnderScoreCase(orderByColumn);
        if ("asc".equalsIgnoreCase(isAsc)) {
            return orderBy + " ASC";
        } else if ("desc".equalsIgnoreCase(isAsc)) {
            return orderBy + " DESC";
        }
        return orderBy + " DESC";
    }

    private String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean preCharIsUpperCase = true;
        boolean curreCharIsUpperCase;
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (curreCharIsUpperCase && (i > 0 && !preCharIsUpperCase || nexteCharIsUpperCase && !preCharIsUpperCase)) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(c));

            preCharIsUpperCase = curreCharIsUpperCase;
        }

        return sb.toString();
    }
}