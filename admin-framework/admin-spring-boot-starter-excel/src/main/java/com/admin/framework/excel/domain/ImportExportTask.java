package com.admin.framework.excel.domain;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 导入导出任务实体
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportExportTask extends BaseEntity {

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private TaskType taskType;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 任务状态
     */
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * 总数据量
     */
    private Integer totalCount = 0;

    /**
     * 成功数量
     */
    private Integer successCount = 0;

    /**
     * 失败数量
     */
    private Integer failCount = 0;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 进度百分比
     */
    private Integer progress = 0;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        IMPORT("导入"),
        EXPORT("导出");

        private final String description;

        TaskType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING("待处理"),
        PROCESSING("处理中"),
        SUCCESS("成功"),
        FAILED("失败");

        private final String description;

        TaskStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 更新进度
     */
    public void updateProgress(int processed, int total) {
        if (total > 0) {
            this.progress = (int) ((processed * 100.0) / total);
        }
    }

    /**
     * 标记任务开始
     */
    public void markStarted() {
        this.status = TaskStatus.PROCESSING;
        this.startTime = LocalDateTime.now();
        this.progress = 0;
    }

    /**
     * 标记任务成功
     */
    public void markSuccess() {
        this.status = TaskStatus.SUCCESS;
        this.endTime = LocalDateTime.now();
        this.progress = 100;
    }

    /**
     * 标记任务失败
     */
    public void markFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
}