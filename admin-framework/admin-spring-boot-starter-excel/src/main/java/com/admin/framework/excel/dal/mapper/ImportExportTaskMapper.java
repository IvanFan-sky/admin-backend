package com.admin.framework.excel.dal.mapper;

import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.framework.excel.domain.ImportExportTask.TaskStatus;
import com.admin.framework.excel.domain.ImportExportTask.TaskType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入导出任务数据访问层 - Framework模块
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
@Component("frameworkImportExportTaskMapper")
public interface ImportExportTaskMapper extends BaseMapper<ImportExportTask> {

    /**
     * 根据用户和任务类型查询进行中的任务
     * 
     * @param userId 用户ID
     * @param taskType 任务类型
     * @return 进行中的任务列表
     */
    default List<ImportExportTask> selectProcessingTasksByUser(Long userId, TaskType taskType) {
        LambdaQueryWrapper<ImportExportTask> wrapper = new LambdaQueryWrapper<ImportExportTask>()
                .eq(ImportExportTask::getCreateBy, userId)
                .eq(taskType != null, ImportExportTask::getTaskType, taskType)
                .in(ImportExportTask::getStatus, TaskStatus.PENDING, TaskStatus.PROCESSING)
                .orderByDesc(ImportExportTask::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 根据用户查询任务分页
     * 
     * @param userId 用户ID
     * @param taskType 任务类型（可选）
     * @param businessType 业务类型（可选）
     * @param status 任务状态（可选）
     * @return 任务列表
     */
    default List<ImportExportTask> selectTasksByUser(Long userId, TaskType taskType, 
                                                    String businessType, TaskStatus status) {
        LambdaQueryWrapper<ImportExportTask> wrapper = new LambdaQueryWrapper<ImportExportTask>()
                .eq(ImportExportTask::getCreateBy, userId)
                .eq(taskType != null, ImportExportTask::getTaskType, taskType)
                .eq(businessType != null, ImportExportTask::getBusinessType, businessType)
                .eq(status != null, ImportExportTask::getStatus, status)
                .orderByDesc(ImportExportTask::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 删除过期任务
     * 
     * @param beforeDate 截止日期
     * @return 删除数量
     */
    default int deleteExpiredTasks(LocalDateTime beforeDate) {
        LambdaQueryWrapper<ImportExportTask> wrapper = new LambdaQueryWrapper<ImportExportTask>()
                .lt(ImportExportTask::getCreateTime, beforeDate)
                .in(ImportExportTask::getStatus, TaskStatus.SUCCESS, TaskStatus.FAILED);
        return delete(wrapper);
    }

    /**
     * 统计用户的任务数量
     * 
     * @param userId 用户ID
     * @param taskType 任务类型
     * @param status 任务状态
     * @return 任务数量
     */
    default long countUserTasks(Long userId, TaskType taskType, TaskStatus status) {
        LambdaQueryWrapper<ImportExportTask> wrapper = new LambdaQueryWrapper<ImportExportTask>()
                .eq(ImportExportTask::getCreateBy, userId)
                .eq(taskType != null, ImportExportTask::getTaskType, taskType)
                .eq(status != null, ImportExportTask::getStatus, status);
        return selectCount(wrapper);
    }

    /**
     * 根据业务类型统计任务
     * 
     * @param businessType 业务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计信息
     */
    List<TaskStatistics> selectTaskStatistics(@Param("businessType") String businessType,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 任务统计信息
     */
    class TaskStatistics {
        private String taskType;
        private String status;
        private Long count;
        private Double avgDuration; // 平均执行时长（分钟）

        // Getters and Setters
        public String getTaskType() { return taskType; }
        public void setTaskType(String taskType) { this.taskType = taskType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        
        public Double getAvgDuration() { return avgDuration; }
        public void setAvgDuration(Double avgDuration) { this.avgDuration = avgDuration; }
    }
}