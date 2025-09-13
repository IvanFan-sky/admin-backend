package com.admin.module.log.api.service;

import com.admin.common.core.domain.PageResult;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

/**
 * 日志导出服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogExportService {

    /**
     * 异步导出操作日志
     * 
     * @param queryCondition 查询条件
     * @return 任务ID
     */
    CompletableFuture<Long> exportOperationLogsAsync(OperationLogQueryDTO queryCondition);

    /**
     * 异步导出登录日志
     * 
     * @param queryCondition 查询条件
     * @return 任务ID
     */
    CompletableFuture<Long> exportLoginLogsAsync(LoginLogQueryDTO queryCondition);

    /**
     * 获取任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    ImportExportTask getTaskDetail(Long taskId);

    /**
     * 下载导出文件
     * 
     * @param taskId 任务ID
     * @param response HTTP响应
     */
    void downloadExportFile(Long taskId, HttpServletResponse response);

    /**
     * 获取用户的导出任务列表
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 任务列表
     */
    PageResult<ImportExportTask> getUserTasks(int pageNum, int pageSize);

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean cancelTask(Long taskId);
}