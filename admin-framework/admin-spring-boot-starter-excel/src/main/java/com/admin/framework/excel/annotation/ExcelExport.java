package com.admin.framework.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel导出注解
 * 
 * 用于标记需要导出到Excel的方法
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelExport {

    /**
     * 文件名称（不包含扩展名）
     */
    String fileName() default "export";

    /**
     * 工作表名称
     */
    String sheetName() default "数据";

    /**
     * 数据类型Class
     */
    Class<?> dataClass();

    /**
     * 是否包含表头
     */
    boolean includeHeader() default true;

    /**
     * 是否自动调整列宽
     */
    boolean autoColumnWidth() default true;

    /**
     * 最大导出行数
     */
    int maxRows() default 100000;

    /**
     * 导出描述
     */
    String description() default "";
}
