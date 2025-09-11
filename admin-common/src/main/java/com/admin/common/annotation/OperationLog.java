package com.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解
 * 
 * 用于标记需要记录操作日志的方法
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 功能描述
     */
    String description() default "";

    /**
     * 业务类型
     * 0=其他,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作类别
     * 0=其他 1=后台用户 2=手机端用户
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否记录请求参数
     */
    boolean recordRequestParam() default true;

    /**
     * 是否记录响应参数
     */
    boolean recordResponseResult() default true;

    /**
     * 是否记录异常
     */
    boolean recordException() default true;

    /**
     * 业务类型枚举
     */
    enum BusinessType {
        /**
         * 其它
         */
        OTHER(0, "其它"),

        /**
         * 新增
         */
        INSERT(1, "新增"),

        /**
         * 修改
         */
        UPDATE(2, "修改"),

        /**
         * 删除
         */
        DELETE(3, "删除"),

        /**
         * 授权
         */
        GRANT(4, "授权"),

        /**
         * 导出
         */
        EXPORT(5, "导出"),

        /**
         * 导入
         */
        IMPORT(6, "导入"),

        /**
         * 强退
         */
        FORCE(7, "强退"),

        /**
         * 生成代码
         */
        GENCODE(8, "生成代码"),

        /**
         * 清空数据
         */
        CLEAN(9, "清空数据");

        private final int code;
        private final String description;

        BusinessType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 操作人类别枚举
     */
    enum OperatorType {
        /**
         * 其它
         */
        OTHER(0, "其它"),

        /**
         * 后台用户
         */
        MANAGE(1, "后台用户"),

        /**
         * 手机端用户
         */
        MOBILE(2, "手机端用户");

        private final int code;
        private final String description;

        OperatorType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}