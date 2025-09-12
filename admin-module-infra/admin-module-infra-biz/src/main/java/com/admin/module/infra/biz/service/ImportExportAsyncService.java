package com.admin.module.infra.biz.service;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.ExportConfigDTO;
import com.admin.module.infra.api.dto.ImportExportTaskUpdateDTO;
import com.admin.module.infra.api.enums.TaskStatusEnum;
import com.admin.module.infra.api.enums.TaskTypeEnum;
import com.admin.module.infra.api.service.ImportErrorDetailService;
import com.admin.module.infra.api.service.ImportExportTaskService;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.api.vo.ImportExportTaskVO;
import com.admin.module.infra.biz.config.ImportExportConfig;
import com.admin.module.infra.biz.monitor.PerformanceMonitor;
import com.admin.module.infra.biz.processor.ExportDataProcessor;
import com.admin.module.infra.biz.processor.ExportProcessorFactory;
import com.admin.module.infra.biz.processor.ImportDataProcessor;
import com.admin.module.infra.biz.processor.ImportProcessorFactory;
import com.admin.module.infra.biz.util.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入导出异步任务服务
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportAsyncService {

    private final ImportExportTaskService importExportTaskService;
    private final ImportErrorDetailService importErrorDetailService;
    private final ImportProcessorFactory importProcessorFactory;
    private final ExportProcessorFactory exportProcessorFactory;
    private final ImportExportConfig importExportConfig;
    private final ObjectMapper objectMapper;
    private final PerformanceMonitor performanceMonitor;

    @Async("importExportTaskExecutor")
    public void executeImportTask(Long taskId) {
        log.info("开始执行增强导入任务，任务ID: {}", taskId);

        ImportExportTaskVO taskVO = null;
        PerformanceMonitor.ProcessingContext perfContext = performanceMonitor.startProcessing("IMPORT", taskId.toString());
        
        try {
            // 获取任务详情
            taskVO = importExportTaskService.getTask(taskId);
            if (taskVO == null) {
                throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "任务不存在");
            }

            // 检查任务状态
            if (!TaskStatusEnum.PENDING.getCode().equals(taskVO.getStatus()) &&
                !TaskStatusEnum.PROCESSING.getCode().equals(taskVO.getStatus())) {
                log.warn("任务状态不正确，无法执行，任务ID: {}, 状态: {}", taskId, taskVO.getStatus());
                return;
            }

            // 更新任务状态为处理中
            updateTaskStatus(taskId, TaskStatusEnum.PROCESSING, 0, "开始处理增强导入任务");

            // 检查文件是否存在
            if (!FileUtils.fileExists(taskVO.getFilePath())) {
                throw new ServiceException(ErrorCode.FILE_NOT_FOUND, "导入文件不存在");
            }

            // 获取增强处理器（支持并行、事务、流式处理）
            ImportDataProcessor processor = importProcessorFactory.getProcessor(taskVO.getDataType());

            // 执行增强导入处理
            try (InputStream inputStream = new FileInputStream(taskVO.getFilePath())) {
                ImportDataProcessor.ImportProcessResult result = processor.processImport(taskId, inputStream,
                    (processedCount, totalCount, currentOperation) -> {
                        // 进度回调
                        int progressPercent = totalCount > 0 ? (processedCount * 100 / totalCount) : 0;
                        updateTaskProgress(taskId, progressPercent, currentOperation);
                        
                        // 每处理1000条记录记录一次性能数据
                        if (processedCount % 1000 == 0) {
                            log.debug("增强导入进度 - 任务ID: {}, 已处理: {}, 总数: {}, 当前操作: {}", 
                                     taskId, processedCount, totalCount, currentOperation);
                        }
                    });

                // 保存错误详情
                if (!result.getErrors().isEmpty()) {
                    importErrorDetailService.saveErrorDetails(taskId, result.getErrors());
                }

                // 更新任务完成状态
                ImportExportTaskUpdateDTO updateDTO = new ImportExportTaskUpdateDTO();
                updateDTO.setId(taskId);
                updateDTO.setStatus(TaskStatusEnum.COMPLETED.getCode());
                updateDTO.setProgressPercent(100);
                updateDTO.setTotalCount(result.getTotalCount());
                updateDTO.setSuccessCount(result.getSuccessCount());
                updateDTO.setFailureCount(result.getFailureCount());

                if (result.getFailureCount() > 0) {
                    updateDTO.setErrorMessage(String.format("增强导入完成，共 %d 条数据，成功 %d 条，失败 %d 条", 
                                                          result.getTotalCount(), result.getSuccessCount(), result.getFailureCount()));
                }

                importExportTaskService.updateTask(updateDTO);

                // 记录成功的性能数据
                performanceMonitor.endProcessing(perfContext, result.getTotalCount(), true);

                log.info("增强导入任务执行完成，任务ID: {}, 总数: {}, 成功: {}, 失败: {}",
                        taskId, result.getTotalCount(), result.getSuccessCount(), result.getFailureCount());
            }

        } catch (Exception e) {
            log.error("增强导入任务执行失败，任务ID: {}", taskId, e);

            // 记录错误的性能数据
            performanceMonitor.recordError("IMPORT", taskId.toString(), e.getMessage());
            performanceMonitor.endProcessing(perfContext, 0, false);

            // 更新任务失败状态
            try {
                ImportExportTaskUpdateDTO updateDTO = new ImportExportTaskUpdateDTO();
                updateDTO.setId(taskId);
                updateDTO.setStatus(TaskStatusEnum.FAILED.getCode());
                updateDTO.setErrorMessage("增强导入任务执行失败: " + e.getMessage());
                importExportTaskService.updateTask(updateDTO);
            } catch (Exception updateEx) {
                log.error("更新任务失败状态异常，任务ID: {}", taskId, updateEx);
            }
        } finally {
            // 清理临时文件
            if (taskVO != null && taskVO.getFilePath() != null) {
                try {
                    FileUtils.deleteFile(taskVO.getFilePath());
                } catch (Exception e) {
                    log.warn("清理导入文件失败，任务ID: {}, 文件路径: {}", taskId, taskVO.getFilePath(), e);
                }
            }
        }
    }

    @Async("importExportTaskExecutor")
    public void executeExportTask(Long taskId) {
        log.info("开始执行导出任务，任务ID: {}", taskId);

        ImportExportTaskVO taskVO = null;
        String exportFilePath = null;

        try {
            // 获取任务详情
            taskVO = importExportTaskService.getTask(taskId);
            if (taskVO == null) {
                throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "任务不存在");
            }

            // 检查任务状态
            if (!TaskStatusEnum.PENDING.getCode().equals(taskVO.getStatus()) &&
                !TaskStatusEnum.PROCESSING.getCode().equals(taskVO.getStatus())) {
                log.warn("任务状态不正确，无法执行，任务ID: {}, 状态: {}", taskId, taskVO.getStatus());
                return;
            }

            // 更新任务状态为处理中
            updateTaskStatus(taskId, TaskStatusEnum.PROCESSING, 0, "开始处理导出任务");

            // 解析导出配置
            ExportConfigDTO exportConfig = parseExportConfig(taskVO.getExportConfig());
            
            // 获取处理器
            ExportDataProcessor processor = exportProcessorFactory.getProcessor(taskVO.getDataType());

            // 生成导出文件路径
            exportFilePath = generateExportFilePath(taskVO.getDataType(), exportConfig.getFileFormat());
            FileUtils.createDirectories(FileUtils.getParent(exportFilePath));

            // 执行导出
            try (OutputStream outputStream = new FileOutputStream(exportFilePath)) {
                ExportDataProcessor.ExportProcessResult result = processor.processExport(taskId, exportConfig, outputStream,
                    (processedCount, totalCount, currentOperation) -> {
                        // 进度回调
                        int progressPercent = totalCount > 0 ? (processedCount * 100 / totalCount) : 0;
                        updateTaskProgress(taskId, progressPercent, currentOperation);
                    });

                // 更新任务完成状态
                ImportExportTaskUpdateDTO updateDTO = new ImportExportTaskUpdateDTO();
                updateDTO.setId(taskId);
                updateDTO.setStatus(TaskStatusEnum.COMPLETED.getCode());
                updateDTO.setProgressPercent(100);
                updateDTO.setTotalCount(result.getTotalCount());
                updateDTO.setSuccessCount(result.getTotalCount());
                updateDTO.setFailureCount(0);
                updateDTO.setResultFilePath(exportFilePath);

                importExportTaskService.updateTask(updateDTO);

                log.info("导出任务执行完成，任务ID: {}, 总数: {}, 文件大小: {} bytes, 文件路径: {}",
                        taskId, result.getTotalCount(), result.getFileSize(), exportFilePath);
            }

        } catch (Exception e) {
            log.error("导出任务执行失败，任务ID: {}", taskId, e);

            // 更新任务失败状态
            try {
                ImportExportTaskUpdateDTO updateDTO = new ImportExportTaskUpdateDTO();
                updateDTO.setId(taskId);
                updateDTO.setStatus(TaskStatusEnum.FAILED.getCode());
                updateDTO.setErrorMessage("导出任务执行失败: " + e.getMessage());
                importExportTaskService.updateTask(updateDTO);
            } catch (Exception updateEx) {
                log.error("更新任务失败状态异常，任务ID: {}", taskId, updateEx);
            }

            // 清理失败的导出文件
            if (exportFilePath != null) {
                try {
                    FileUtils.deleteFile(exportFilePath);
                } catch (Exception deleteEx) {
                    log.warn("清理失败导出文件异常，任务ID: {}, 文件路径: {}", taskId, exportFilePath, deleteEx);
                }
            }
        }
    }

    private void updateTaskStatus(Long taskId, TaskStatusEnum status, Integer progressPercent, String operation) {
        try {
            ImportExportTaskUpdateDTO updateDTO = new ImportExportTaskUpdateDTO();
            updateDTO.setId(taskId);
            updateDTO.setStatus(status.getCode());
            if (progressPercent != null) {
                updateDTO.setProgressPercent(progressPercent);
            }
            importExportTaskService.updateTask(updateDTO);
            
            log.debug("更新任务状态，任务ID: {}, 状态: {}, 进度: {}, 操作: {}", 
                     taskId, status.getDescription(), progressPercent, operation);
        } catch (Exception e) {
            log.error("更新任务状态失败，任务ID: {}", taskId, e);
        }
    }

    private void updateTaskProgress(Long taskId, Integer progressPercent, String operation) {
        updateTaskStatus(taskId, null, progressPercent, operation);
    }

    private ExportConfigDTO parseExportConfig(String exportConfigJson) {
        try {
            if (exportConfigJson == null || exportConfigJson.trim().isEmpty()) {
                // 返回默认配置
                ExportConfigDTO defaultConfig = new ExportConfigDTO();
                defaultConfig.setFileFormat("xlsx");
                return defaultConfig;
            }
            return objectMapper.readValue(exportConfigJson, ExportConfigDTO.class);
        } catch (Exception e) {
            log.warn("解析导出配置失败，使用默认配置: {}", exportConfigJson, e);
            ExportConfigDTO defaultConfig = new ExportConfigDTO();
            defaultConfig.setFileFormat("xlsx");
            return defaultConfig;
        }
    }

    private String generateExportFilePath(String dataType, String fileFormat) {
        String fileName = FileUtils.generateUniqueFilename(dataType + "_export." + fileFormat);
        return importExportConfig.getExportPath() + "/" + fileName;
    }
}