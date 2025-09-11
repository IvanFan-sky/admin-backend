package com.admin.module.system.api.service.log;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.log.OperationLogDTO;
import com.admin.module.system.api.dto.log.SysOperationLogQueryDTO;
import com.admin.module.system.api.vo.log.SysOperationLogVO;
import com.admin.module.system.api.vo.log.OperationLogStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 系统操作日志服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysOperationLogService {

    /**
     * 保存操作日志
     * 
     * @param logDTO 操作日志信息
     */
    void saveOperationLog(OperationLogDTO logDTO);

    /**
     * 获取操作日志分页列表
     * 
     * @param queryDTO 查询条件
     * @return 操作日志分页结果
     */
    PageResult<SysOperationLogVO> getOperationLogPage(SysOperationLogQueryDTO queryDTO);

    /**
     * 获取操作日志详情
     * 
     * @param id 日志ID
     * @return 操作日志详情
     */
    SysOperationLogVO getOperationLog(Long id);

    /**
     * 批量删除操作日志
     * 
     * @param ids 日志ID列表
     * @return 删除数量
     */
    int deleteOperationLogsBatch(Set<Long> ids);

    /**
     * 根据时间范围删除操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除数量
     */
    int deleteOperationLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 清空操作日志
     * 
     * @return 删除数量
     */
    int clearOperationLogs();

    /**
     * 清理过期操作日志
     * 
     * @param retentionDays 保留天数
     * @return 清理数量
     */
    int cleanExpiredOperationLogs(int retentionDays);

    /**
     * 导出操作日志
     * 
     * @param queryDTO 查询条件
     * @return 操作日志列表
     */
    List<SysOperationLogVO> exportOperationLogs(SysOperationLogQueryDTO queryDTO);

    /**
     * 获取操作日志统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    OperationLogStatisticsVO getOperationLogStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户操作日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<SysOperationLogVO> getUserOperationLogs(Long userId, LocalDateTime startTime, LocalDateTime endTime);


}