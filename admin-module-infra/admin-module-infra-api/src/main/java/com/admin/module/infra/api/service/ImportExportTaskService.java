package com.admin.module.infra.api.service;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.ImportExportTaskCreateDTO;
import com.admin.module.infra.api.dto.ImportExportTaskPageDTO;
import com.admin.module.infra.api.dto.ImportExportTaskUpdateDTO;
import com.admin.module.infra.api.vo.ImportExportStatisticsVO;
import com.admin.module.infra.api.vo.ImportExportTaskVO;

import java.util.List;

/**
 * 导入导出任务服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportExportTaskService {

    /**
     * 创建导入导出任务
     *
     * @param createDTO 创建DTO
     * @return 任务ID
     */
    Long createTask(ImportExportTaskCreateDTO createDTO);

    /**
     * 更新任务状态
     *
     * @param updateDTO 更新DTO
     */
    void updateTask(ImportExportTaskUpdateDTO updateDTO);

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    void deleteTask(Long id);

    /**
     * 获取任务详情
     *
     * @param id 任务ID
     * @return 任务VO
     */
    ImportExportTaskVO getTask(Long id);

    /**
     * 获取任务分页列表
     *
     * @param pageDTO 分页查询DTO
     * @return 任务分页结果
     */
    PageResult<ImportExportTaskVO> getTaskPage(ImportExportTaskPageDTO pageDTO);

    /**
     * 获取用户的任务列表
     *
     * @param createBy 创建者
     * @return 任务列表
     */
    List<ImportExportTaskVO> getUserTasks(String createBy);

    /**
     * 获取导入导出统计信息
     *
     * @return 统计VO
     */
    ImportExportStatisticsVO getStatistics();

    /**
     * 启动导入任务
     *
     * @param taskId 任务ID
     */
    void startImportTask(Long taskId);

    /**
     * 启动导出任务
     *
     * @param taskId 任务ID
     */
    void startExportTask(Long taskId);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     */
    void cancelTask(Long taskId);
}