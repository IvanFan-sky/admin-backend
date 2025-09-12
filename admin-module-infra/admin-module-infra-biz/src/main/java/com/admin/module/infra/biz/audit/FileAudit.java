package com.admin.module.infra.biz.audit;

import java.lang.annotation.*;

/**
 * 文件操作审计注解
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileAudit {

    /**
     * 操作类型
     */
    FileOperationType operation();

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否记录请求参数
     */
    boolean logParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean logResult() default false;

    /**
     * 是否异步记录（默认异步）
     */
    boolean async() default true;

    /**
     * 文件操作类型枚举
     */
    enum FileOperationType {
        UPLOAD("上传"),
        DOWNLOAD("下载"),
        DELETE("删除"),
        VIEW("查看"),
        UPDATE("更新"),
        SHARE("分享"),
        COPY("复制"),
        MOVE("移动"),
        SCAN("扫描"),
        COMPRESS("压缩"),
        DECOMPRESS("解压");

        private final String description;

        FileOperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}