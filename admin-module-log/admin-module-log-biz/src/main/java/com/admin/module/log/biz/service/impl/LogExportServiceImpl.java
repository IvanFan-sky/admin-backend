package com.admin.module.log.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.admin.common.exception.ServiceException;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.framework.excel.service.ExcelExportService;
import com.admin.framework.excel.service.ImportExportTaskService;
import com.admin.framework.security.utils.SecurityContextHolder;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.service.LogExportService;
import com.admin.module.log.api.vo.LoginLogExportVO;
import com.admin.module.log.api.vo.OperationLogExportVO;
import com.admin.module.log.biz.convert.LoginLogConvert;
import com.admin.module.log.biz.convert.OperationLogConvert;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 日志导出服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogExportServiceImpl implements LogExportService {

    private static final String OPERATION_LOG_BUSINESS_TYPE = "OPERATION_LOG";
    private static final String LOGIN_LOG_BUSINESS_TYPE = "LOGIN_LOG";
    private static final int BATCH_SIZE = 1000;

    private final ExcelExportService excelExportService;
    private final ImportExportTaskService taskService;
    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    @Async
    public CompletableFuture<Long> exportOperationLogsAsync(OperationLogQueryDTO queryCondition) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("用户[{}]开始异步导出操作日志", userId);

        // 检查并发限制
        if (!taskService.canCreateTask(userId, ImportExportTask.TaskType.EXPORT)) {
            throw new ServiceException("您有正在执行的导出任务，请稍后再试");
        }

        // 创建任务
        Long taskId = taskService.createTask("操作日志导出", ImportExportTask.TaskType.EXPORT, 
                                           OPERATION_LOG_BUSINESS_TYPE, "操作日志数据.xlsx");

        // 异步执行导出
        return taskService.executeExportTaskAsync(taskId, () -> processOperationLogExportTask(taskId, queryCondition));
    }

    @Override
    @Async
    public CompletableFuture<Long> exportLoginLogsAsync(LoginLogQueryDTO queryCondition) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("用户[{}]开始异步导出登录日志", userId);

        // 检查并发限制
        if (!taskService.canCreateTask(userId, ImportExportTask.TaskType.EXPORT)) {
            throw new ServiceException("您有正在执行的导出任务，请稍后再试");
        }

        // 创建任务
        Long taskId = taskService.createTask("登录日志导出", ImportExportTask.TaskType.EXPORT, 
                                           LOGIN_LOG_BUSINESS_TYPE, "登录日志数据.xlsx");

        // 异步执行导出
        return taskService.executeExportTaskAsync(taskId, () -> processLoginLogExportTask(taskId, queryCondition));
    }

    /**
     * 处理操作日志导出任务
     */
    private void processOperationLogExportTask(Long taskId, OperationLogQueryDTO queryCondition) {
        try {
            log.info("开始处理操作日志导出任务: {}", taskId);
            taskService.updateTaskStatus(taskId, ImportExportTask.TaskStatus.PROCESSING);

            // 查询总数
            long totalCount = operationLogMapper.selectCountByQuery(queryCondition);
            taskService.updateTaskStatistics(taskId, (int) totalCount, 0, 0);

            // 分批查询并导出
            List<OperationLogExportVO> allExportData = new ArrayList<>();
            int pageSize = BATCH_SIZE;
            long current = 1;
            int processedCount = 0;

            while (true) {
                Page<OperationLogDO> page = new Page<>(current, pageSize);
                IPage<OperationLogDO> pageResult = operationLogMapper.selectPageByQuery(page, queryCondition);
                
                if (CollectionUtil.isEmpty(pageResult.getRecords())) {
                    break;
                }

                // 转换为导出VO
                List<OperationLogExportVO> exportVOs = OperationLogConvert.INSTANCE.toExportVOList(pageResult.getRecords());
                
                // 设置额外字段
                for (OperationLogExportVO exportVO : exportVOs) {
                    exportVO.setBusinessTypeText(exportVO.getBusinessType());
                    exportVO.setStatusText(exportVO.getStatus());
                }
                
                allExportData.addAll(exportVOs);

                processedCount += pageResult.getRecords().size();
                taskService.updateTaskProgress(taskId, processedCount, (int) totalCount);

                current++;
                
                // 如果当前页记录数小于页大小，说明是最后一页
                if (pageResult.getRecords().size() < pageSize) {
                    break;
                }
            }

            // 生成Excel文件
            String filePath = generateExportFile(taskId, allExportData, OperationLogExportVO.class, "操作日志导出");
            taskService.setTaskFilePath(taskId, filePath);
            taskService.completeTask(taskId, true, "导出成功");

            log.info("操作日志导出任务{}完成，导出数据: {}条", taskId, allExportData.size());

        } catch (Exception e) {
            log.error("操作日志导出任务{}执行失败", taskId, e);
            taskService.completeTask(taskId, false, "导出失败: " + e.getMessage());
        }
    }

    /**
     * 处理登录日志导出任务
     */
    private void processLoginLogExportTask(Long taskId, LoginLogQueryDTO queryCondition) {
        try {
            log.info("开始处理登录日志导出任务: {}", taskId);
            taskService.updateTaskStatus(taskId, ImportExportTask.TaskStatus.PROCESSING);

            // 查询总数
            long totalCount = loginLogMapper.selectCountByQuery(queryCondition);
            taskService.updateTaskStatistics(taskId, (int) totalCount, 0, 0);

            // 分批查询并导出
            List<LoginLogExportVO> allExportData = new ArrayList<>();
            int pageSize = BATCH_SIZE;
            long current = 1;
            int processedCount = 0;

            while (true) {
                Page<LoginLogDO> page = new Page<>(current, pageSize);
                IPage<LoginLogDO> pageResult = loginLogMapper.selectPageByQuery(page, queryCondition);
                
                if (CollectionUtil.isEmpty(pageResult.getRecords())) {
                    break;
                }

                // 转换为导出VO
                List<LoginLogExportVO> exportVOs = LoginLogConvert.INSTANCE.toExportVOList(pageResult.getRecords());
                
                // 设置额外字段
                for (LoginLogExportVO exportVO : exportVOs) {
                    exportVO.setLoginTypeText(exportVO.getLoginType());
                    exportVO.setStatusText(exportVO.getStatus());
                }
                
                allExportData.addAll(exportVOs);

                processedCount += pageResult.getRecords().size();
                taskService.updateTaskProgress(taskId, processedCount, (int) totalCount);

                current++;
                
                // 如果当前页记录数小于页大小，说明是最后一页
                if (pageResult.getRecords().size() < pageSize) {
                    break;
                }
            }

            // 生成Excel文件
            String filePath = generateExportFile(taskId, allExportData, LoginLogExportVO.class, "登录日志导出");
            taskService.setTaskFilePath(taskId, filePath);
            taskService.completeTask(taskId, true, "导出成功");

            log.info("登录日志导出任务{}完成，导出数据: {}条", taskId, allExportData.size());

        } catch (Exception e) {
            log.error("登录日志导出任务{}执行失败", taskId, e);
            taskService.completeTask(taskId, false, "导出失败: " + e.getMessage());
        }
    }

    /**
     * 生成导出文件
     */
    private <T> String generateExportFile(Long taskId, List<T> exportData, Class<T> clazz, String sheetName) {
        try {
            return excelExportService.exportToFile(exportData, clazz, taskId + "_" + sheetName, sheetName);
        } catch (Exception e) {
            throw new ServiceException("生成导出文件失败: " + e.getMessage());
        }
    }

    @Override
    public ImportExportTask getTaskDetail(Long taskId) {
        return taskService.getTask(taskId);
    }

    @Override
    public void downloadExportFile(Long taskId, HttpServletResponse response) {
        ImportExportTask task = taskService.getTask(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        
        if (task.getStatus() != ImportExportTask.TaskStatus.COMPLETED || task.getFilePath() == null) {
            throw new ServiceException("文件尚未生成或任务未完成");
        }
        
        // 委托给通用的文件下载服务
        excelExportService.downloadFile(task.getFilePath(), task.getFileName(), response);
        log.info("下载任务{}的导出文件", taskId);
    }

    @Override
    public com.admin.common.core.domain.PageResult<ImportExportTask> getUserTasks(int pageNum, int pageSize) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        return taskService.getTaskPage(pageNum, pageSize, null, 
                OPERATION_LOG_BUSINESS_TYPE + "," + LOGIN_LOG_BUSINESS_TYPE, null);
    }

    @Override
    public boolean cancelTask(Long taskId) {
        return taskService.cancelTask(taskId);
    }
}