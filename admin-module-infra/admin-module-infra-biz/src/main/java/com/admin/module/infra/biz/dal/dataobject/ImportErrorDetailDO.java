package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入错误详情数据对象
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_import_error_detail")
public class ImportErrorDetailDO extends BaseEntity {

    /**
     * 错误详情ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 错误行号
     */
    private Integer rowNumber;

    /**
     * 错误列名
     */
    private String columnName;

    /**
     * 错误列值
     */
    private String columnValue;

    /**
     * 错误类型
     * VALIDATION-校验错误 DUPLICATE-重复数据 CONSTRAINT-约束错误 BUSINESS-业务错误
     */
    private String errorType;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 原始数据（JSON格式）
     */
    private String originalData;

    /**
     * 建议修复方案
     */
    private String suggestion;
}
