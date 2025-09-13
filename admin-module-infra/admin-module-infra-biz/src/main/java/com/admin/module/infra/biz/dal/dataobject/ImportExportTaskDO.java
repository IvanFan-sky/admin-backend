package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 导入导出任务数据对象
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("infra_import_export_task")
public class ImportExportTaskDO extends BaseEntity {

    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     * IMPORT-导入 EXPORT-导出
     */
    private String taskType;

    /**
     * 业务类型
     * USER-用户 ROLE-角色 LOG-日志等
     */
    private String businessType;

    /**
     * 任务状态
     * PENDING-待处理 PROCESSING-处理中 COMPLETED-已完成 FAILED-失败 CANCELLED-已取消
     */
    private String status;

    /**
     * 进度百分比（0-100）
     */
    private Integer progressPercent;

    /**
     * 当前操作描述
     */
    private String currentOperation;

    /**
     * 源文件ID（导入时使用）
     */
    private Long sourceFileId;

    /**
     * 结果文件ID（导出时使用）
     */
    private Long resultFileId;

    /**
     * 错误文件ID（导入失败时的错误详情文件）
     */
    private Long errorFileId;

    /**
     * 处理总数
     */
    private Integer totalCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failureCount;

    /**
     * 跳过数量
     */
    private Integer skipCount;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 结果摘要
     */
    private String resultSummary;

    /**
     * 任务参数（JSON格式）
     */
    private String taskParams;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行用户ID
     */
    private Long executeUserId;

    /**
     * 执行用户名
     */
    private String executeUserName;

    /**
     * 是否允许部分失败
     */
    private Boolean allowPartialFailure;

    /**
     * 优先级（1-5，数字越大优先级越高）
     */
    private Integer priority;
}
