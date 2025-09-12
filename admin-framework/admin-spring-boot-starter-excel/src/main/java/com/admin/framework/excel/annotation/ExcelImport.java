package com.admin.framework.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel导入注解
 * 
 * 用于标记需要从Excel导入数据的方法
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelImport {

    /**
     * 数据类型Class
     */
    Class<?> dataClass();

    /**
     * 是否包含表头
     */
    boolean hasHeader() default true;

    /**
     * 跳过的行数（从0开始）
     */
    int skipRows() default 0;

    /**
     * 最大导入行数
     */
    int maxRows() default 10000;

    /**
     * 批处理大小
     */
    int batchSize() default 1000;

    /**
     * 是否异步处理
     */
    boolean async() default false;

    /**
     * 异步处理阈值
     */
    int asyncThreshold() default 5000;

    /**
     * 是否跳过空行
     */
    boolean skipEmptyRows() default true;

    /**
     * 导入描述
     */
    String description() default "";
}
