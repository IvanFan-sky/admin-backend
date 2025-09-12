package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 导入导出任务数据对象
 * 
 * 对应数据库表 sys_import_export_task
 * 用于管理文件导入导出任务的基本信息和执行状态
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_import_export_task")
public class ImportExportTaskDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务类型
     * 1-导入，2-导出
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 数据类型
     * user-用户数据，role-角色数据，operation_log-操作日志
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 文件格式
     * xlsx, xls, csv
     */
    @TableField("file_format")
    private String fileFormat;

    /**
     * 任务状态
     * 0-待处理，1-处理中，2-已完成，3-失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 总记录数
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 成功记录数
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 失败记录数
     */
    @TableField("fail_count")
    private Integer failCount;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件ID（关联文件管理系统）
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 结果文件路径
     */
    @TableField("result_file_path")
    private String resultFilePath;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 处理进度
     * 百分比，0.00-100.00
     */
    @TableField("progress")
    private BigDecimal progress;

    /**
     * 导出条件
     * JSON格式存储查询条件
     */
    @TableField("export_conditions")
    private String exportConditions;

    /**
     * 导出字段
     * JSON格式存储选中的字段
     */
    @TableField("selected_fields")
    private String selectedFields;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 删除标识
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}