package com.admin.module.log.api.enums;

import lombok.Getter;

/**
 * 业务类型枚举
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public enum BusinessTypeEnum {

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

    private final Integer code;
    private final String description;

    BusinessTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}