package com.admin.module.infra.biz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导入错误详情数据对象
 * 
 * 对应数据库表 sys_import_error_detail
 * 用于记录导入过程中具体的错误信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@TableName("sys_import_error_detail")
public class ImportErrorDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 行号
     */
    @TableField("row_number")
    private Integer rowNumber;

    /**
     * 字段名称
     */
    @TableField("field_name")
    private String fieldName;

    /**
     * 字段值
     */
    @TableField("field_value")
    private String fieldValue;

    /**
     * 错误类型
     * FORMAT_ERROR-格式错误，DUPLICATE_ERROR-重复错误，VALIDATION_ERROR-校验错误
     */
    @TableField("error_type")
    private String errorType;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}