package com.admin.framework.excel.service;

import com.admin.common.core.domain.PageResult;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.framework.excel.domain.ImportExportTask.TaskStatus;
import com.admin.framework.excel.domain.ImportExportTask.TaskType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 导入导出任务管理服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportExportTaskService {

    /**
     * 创建任务
     * 
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param businessType 业务类型
     * @param fileName 文件名
     * @return 任务ID
     */
    Long createTask(String taskName, TaskType taskType, String businessType, String fileName);

    /**
     * 获取任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    ImportExportTask getTask(Long taskId);

    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 任务状态
     */
    void updateTaskStatus(Long taskId, TaskStatus status);

    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param processed 已处理数量
     * @param total 总数量
     */
    void updateTaskProgress(Long taskId, int processed, int total);

    /**
     * 更新任务统计信息
     * 
     * @param taskId 任务ID
     * @param totalCount 总数量
     * @param successCount 成功数量
     * @param failCount 失败数量
     */
    void updateTaskStatistics(Long taskId, int totalCount, int successCount, int failCount);

    /**
     * 标记任务完成
     * 
     * @param taskId 任务ID
     * @param success 是否成功
     * @param errorMessage 错误信息（失败时）
     */
    void completeTask(Long taskId, boolean success, String errorMessage);

    /**
     * 设置任务文件路径
     * 
     * @param taskId 任务ID
     * @param filePath 文件路径
     */
    void setTaskFilePath(Long taskId, String filePath);

    /**
     * 分页查询任务列表
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param taskType 任务类型（可选）
     * @param businessType 业务类型（可选）
     * @param status 任务状态（可选）
     * @return 任务分页结果
     */
    PageResult<ImportExportTask> getTaskPage(int pageNum, int pageSize, TaskType taskType, 
                                           String businessType, TaskStatus status);

    /**
     * 获取用户的进行中任务
     * 
     * @param userId 用户ID
     * @param taskType 任务类型
     * @return 进行中的任务列表
     */
    List<ImportExportTask> getUserProcessingTasks(Long userId, TaskType taskType);

    /**
     * 检查用户是否可以创建新任务（并发控制）
     * 
     * @param userId 用户ID
     * @param taskType 任务类型
     * @return 是否可以创建
     */
    boolean canCreateTask(Long userId, TaskType taskType);

    /**
     * 清理过期任务
     * 
     * @param days 保留天数
     * @return 清理任务数量
     */
    int cleanExpiredTasks(int days);

    /**
     * 异步执行导入任务
     * 
     * @param taskId 任务ID
     * @param processor 任务处理器
     * @return 异步结果
     */
    CompletableFuture<Void> executeImportTaskAsync(Long taskId, ImportTaskProcessor processor);

    /**
     * 异步执行导出任务
     * 
     * @param taskId 任务ID
     * @param processor 任务处理器
     * @return 异步结果
     */
    CompletableFuture<Void> executeExportTaskAsync(Long taskId, ExportTaskProcessor processor);

    /**
     * 导入任务处理器接口
     */
    @FunctionalInterface
    interface ImportTaskProcessor {
        void process(Long taskId) throws Exception;
    }

    /**
     * 导出任务处理器接口
     */
    @FunctionalInterface
    interface ExportTaskProcessor {
        void process(Long taskId) throws Exception;
    }
}