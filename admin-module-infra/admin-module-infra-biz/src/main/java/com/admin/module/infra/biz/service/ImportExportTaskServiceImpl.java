package com.admin.module.infra.biz.service;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.dto.*;
import com.admin.module.infra.api.service.ImportExportTaskService;
import com.admin.module.infra.api.vo.*;
import com.admin.module.infra.biz.convert.ImportExportTaskConvert;
import com.admin.module.infra.biz.dal.dataobject.ImportExportTaskDO;
import com.admin.module.infra.biz.dal.mapper.ImportExportTaskMapper;
import com.admin.module.infra.api.enums.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 导入导出任务服务实现类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportTaskServiceImpl implements ImportExportTaskService {

    private final ImportExportTaskMapper importExportTaskMapper;
    private final ImportExportAsyncService importExportAsyncService;
    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(ImportExportTaskCreateDTO createDTO) {
        // 参数校验
        validateCreateDTO(createDTO);

        // 构建DO对象
        ImportExportTaskDO taskDO = ImportExportTaskConvert.INSTANCE.convert(createDTO);
        taskDO.setStatus(TaskStatusEnum.PENDING.getCode());
        taskDO.setProgressPercent(0);
        taskDO.setCreateBy(SecurityUtils.getUsername());
        taskDO.setCreateTime(LocalDateTime.now());

        // 保存任务
        importExportTaskMapper.insert(taskDO);
        
        log.info("创建导入导出任务成功，任务ID: {}, 任务名称: {}", taskDO.getId(), taskDO.getTaskName());
        return taskDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(ImportExportTaskUpdateDTO updateDTO) {
        // 检查任务是否存在
        ImportExportTaskDO existTask = importExportTaskMapper.selectById(updateDTO.getId());
        if (existTask == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND);
        }

        // 构建更新对象
        ImportExportTaskDO updateDO = ImportExportTaskConvert.INSTANCE.convert(updateDTO);
        updateDO.setUpdateBy(SecurityUtils.getUsername());
        updateDO.setUpdateTime(LocalDateTime.now());

        // 如果状态变为完成或失败，设置结束时间
        if (TaskStatusEnum.COMPLETED.getCode().equals(updateDTO.getStatus()) || 
            TaskStatusEnum.FAILED.getCode().equals(updateDTO.getStatus())) {
            updateDO.setEndTime(LocalDateTime.now());
        }

        importExportTaskMapper.updateById(updateDO);
        
        log.info("更新导入导出任务成功，任务ID: {}", updateDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        ImportExportTaskDO taskDO = importExportTaskMapper.selectById(id);
        if (taskDO == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND);
        }

        // 只有已完成或失败的任务才能删除
        TaskStatusEnum status = TaskStatusEnum.getByCode(taskDO.getStatus());
        if (status != null && !status.isFinalStatus()) {
            throw new ServiceException(ErrorCode.BUSINESS_ERROR, "只能删除已完成或失败的任务");
        }

        importExportTaskMapper.deleteById(id);
        
        log.info("删除导入导出任务成功，任务ID: {}", id);
    }

    @Override
    public ImportExportTaskVO getTask(Long id) {
        ImportExportTaskDO taskDO = importExportTaskMapper.selectById(id);
        if (taskDO == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND);
        }
        
        return ImportExportTaskConvert.INSTANCE.convert(taskDO);
    }

    @Override
    public PageResult<ImportExportTaskVO> getTaskPage(ImportExportTaskPageDTO pageDTO) {
        // 构建查询条件
        LambdaQueryWrapper<ImportExportTaskDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(pageDTO.getTaskName() != null, ImportExportTaskDO::getTaskName, pageDTO.getTaskName())
                   .eq(pageDTO.getTaskType() != null, ImportExportTaskDO::getTaskType, pageDTO.getTaskType())
                   .eq(pageDTO.getDataType() != null, ImportExportTaskDO::getDataType, pageDTO.getDataType())
                   .eq(pageDTO.getStatus() != null, ImportExportTaskDO::getStatus, pageDTO.getStatus())
                   .like(pageDTO.getCreateBy() != null, ImportExportTaskDO::getCreateBy, pageDTO.getCreateBy())
                   .ge(pageDTO.getBeginCreateTime() != null, ImportExportTaskDO::getCreateTime, pageDTO.getBeginCreateTime())
                   .le(pageDTO.getEndCreateTime() != null, ImportExportTaskDO::getCreateTime, pageDTO.getEndCreateTime())
                   .orderByDesc(ImportExportTaskDO::getId);

        // 分页查询
        Page<ImportExportTaskDO> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Page<ImportExportTaskDO> result = importExportTaskMapper.selectPage(page, queryWrapper);

        // 转换结果
        List<ImportExportTaskVO> voList = ImportExportTaskConvert.INSTANCE.convertList(result.getRecords());
        return new PageResult<>(voList, result.getTotal());
    }

    @Override
    public List<ImportExportTaskVO> getUserTasks(String createBy) {
        LambdaQueryWrapper<ImportExportTaskDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportExportTaskDO::getCreateBy, createBy)
                   .orderByDesc(ImportExportTaskDO::getCreateTime);
        
        List<ImportExportTaskDO> taskList = importExportTaskMapper.selectList(queryWrapper);
        return ImportExportTaskConvert.INSTANCE.convertList(taskList);
    }

    @Override
    public ImportExportStatisticsVO getStatistics() {
        ImportExportStatisticsVO statisticsVO = new ImportExportStatisticsVO();

        // 统计今日任务数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        LambdaQueryWrapper<ImportExportTaskDO> todayImportQuery = new LambdaQueryWrapper<>();
        todayImportQuery.eq(ImportExportTaskDO::getTaskType, TaskTypeEnum.IMPORT.getCode())
                       .ge(ImportExportTaskDO::getCreateTime, todayStart)
                       .lt(ImportExportTaskDO::getCreateTime, todayEnd);
        statisticsVO.setTodayImportCount((long) importExportTaskMapper.selectCount(todayImportQuery));

        LambdaQueryWrapper<ImportExportTaskDO> todayExportQuery = new LambdaQueryWrapper<>();
        todayExportQuery.eq(ImportExportTaskDO::getTaskType, TaskTypeEnum.EXPORT.getCode())
                       .ge(ImportExportTaskDO::getCreateTime, todayStart)
                       .lt(ImportExportTaskDO::getCreateTime, todayEnd);
        statisticsVO.setTodayExportCount((long) importExportTaskMapper.selectCount(todayExportQuery));

        // 统计本月任务数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        
        LambdaQueryWrapper<ImportExportTaskDO> monthImportQuery = new LambdaQueryWrapper<>();
        monthImportQuery.eq(ImportExportTaskDO::getTaskType, TaskTypeEnum.IMPORT.getCode())
                       .ge(ImportExportTaskDO::getCreateTime, monthStart)
                       .lt(ImportExportTaskDO::getCreateTime, monthEnd);
        statisticsVO.setMonthImportCount((long) importExportTaskMapper.selectCount(monthImportQuery));

        LambdaQueryWrapper<ImportExportTaskDO> monthExportQuery = new LambdaQueryWrapper<>();
        monthExportQuery.eq(ImportExportTaskDO::getTaskType, TaskTypeEnum.EXPORT.getCode())
                       .ge(ImportExportTaskDO::getCreateTime, monthStart)
                       .lt(ImportExportTaskDO::getCreateTime, monthEnd);
        statisticsVO.setMonthExportCount((long) importExportTaskMapper.selectCount(monthExportQuery));

        // 统计待处理和处理中任务数
        LambdaQueryWrapper<ImportExportTaskDO> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(ImportExportTaskDO::getStatus, TaskStatusEnum.PENDING.getCode());
        statisticsVO.setPendingCount((long) importExportTaskMapper.selectCount(pendingQuery));

        LambdaQueryWrapper<ImportExportTaskDO> processingQuery = new LambdaQueryWrapper<>();
        processingQuery.eq(ImportExportTaskDO::getStatus, TaskStatusEnum.PROCESSING.getCode());
        statisticsVO.setProcessingCount((long) importExportTaskMapper.selectCount(processingQuery));

        // 计算成功率
        statisticsVO.setTodaySuccessRate(calculateSuccessRate(todayStart, todayEnd));
        statisticsVO.setMonthSuccessRate(calculateSuccessRate(monthStart, monthEnd));

        return statisticsVO;
    }

    @Override
    public void startImportTask(Long taskId) {
        updateTaskStatus(taskId, TaskStatusEnum.PROCESSING);
        // 启动异步导入任务处理
        importExportAsyncService.executeImportTask(taskId);
        log.info("启动导入任务，任务ID: {}", taskId);
    }

    @Override
    public void startExportTask(Long taskId) {
        updateTaskStatus(taskId, TaskStatusEnum.PROCESSING);
        // 启动异步导出任务处理
        importExportAsyncService.executeExportTask(taskId);
        log.info("启动导出任务，任务ID: {}", taskId);
    }

    @Override
    public void cancelTask(Long taskId) {
        updateTaskStatus(taskId, TaskStatusEnum.FAILED);
        log.info("取消任务，任务ID: {}", taskId);
    }

    private void validateCreateDTO(ImportExportTaskCreateDTO createDTO) {
        // 验证任务类型
        TaskTypeEnum taskType = TaskTypeEnum.getByCode(createDTO.getTaskType());
        if (taskType == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "无效的任务类型");
        }

        // 验证数据类型
        if (!DataTypeEnum.isSupported(createDTO.getDataType())) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的数据类型");
        }

        // 验证文件格式
        if (createDTO.getFileFormat() != null && !FileFormatEnum.isSupported(createDTO.getFileFormat())) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的文件格式");
        }

        // 导入任务必须提供文件ID或文件路径
        if (TaskTypeEnum.IMPORT.equals(taskType) && createDTO.getFilePath() == null && createDTO.getFileId() == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "导入任务必须提供文件ID或文件路径");
        }
    }

    private void updateTaskStatus(Long taskId, TaskStatusEnum status) {
        LambdaUpdateWrapper<ImportExportTaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ImportExportTaskDO::getId, taskId)
                    .set(ImportExportTaskDO::getStatus, status.getCode())
                    .set(ImportExportTaskDO::getUpdateBy, SecurityUtils.getUsername())
                    .set(ImportExportTaskDO::getUpdateTime, LocalDateTime.now());

        if (status == TaskStatusEnum.PROCESSING) {
            updateWrapper.set(ImportExportTaskDO::getStartTime, LocalDateTime.now());
        }

        int updated = importExportTaskMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "任务不存在");
        }
    }

    private Double calculateSuccessRate(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ImportExportTaskDO> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.ge(ImportExportTaskDO::getCreateTime, startTime)
                 .lt(ImportExportTaskDO::getCreateTime, endTime)
                 .in(ImportExportTaskDO::getStatus, TaskStatusEnum.COMPLETED.getCode(), TaskStatusEnum.FAILED.getCode());
        
        long totalCount = importExportTaskMapper.selectCount(totalQuery);
        if (totalCount == 0) {
            return 0.0;
        }

        LambdaQueryWrapper<ImportExportTaskDO> successQuery = new LambdaQueryWrapper<>();
        successQuery.ge(ImportExportTaskDO::getCreateTime, startTime)
                   .lt(ImportExportTaskDO::getCreateTime, endTime)
                   .eq(ImportExportTaskDO::getStatus, TaskStatusEnum.COMPLETED.getCode());
        
        long successCount = importExportTaskMapper.selectCount(successQuery);
        
        return Math.round((double) successCount / totalCount * 100 * 100.0) / 100.0;
    }
}