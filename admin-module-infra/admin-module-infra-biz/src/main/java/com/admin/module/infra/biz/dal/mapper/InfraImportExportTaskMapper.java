package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.ImportExportTaskDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础设施模块 - 导入导出任务数据访问层
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
@Component("infraImportExportTaskMapper")
public interface InfraImportExportTaskMapper extends BaseMapper<ImportExportTaskDO> {

    /**
     * 根据状态查找任务
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<ImportExportTaskDO> selectByStatus(@Param("status") String status);

    /**
     * 根据用户ID查找任务
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 任务列表
     */
    List<ImportExportTaskDO> selectByUserId(@Param("userId") Long userId,
                                           @Param("limit") Integer limit);

    /**
     * 统计正在执行的任务数量
     * 
     * @param taskType 任务类型
     * @param userId 用户ID
     * @return 任务数量
     */
    int countRunningTasks(@Param("taskType") String taskType,
                         @Param("userId") Long userId);

    /**
     * 查找超时任务
     * 
     * @param timeoutThreshold 超时时间阈值
     * @return 超时任务列表
     */
    List<ImportExportTaskDO> selectTimeoutTasks(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    /**
     * 批量更新任务状态
     * 
     * @param taskIds 任务ID列表
     * @param status 新状态
     * @param updateBy 更新人
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("taskIds") List<Long> taskIds,
                         @Param("status") String status,
                         @Param("updateBy") String updateBy);
}
