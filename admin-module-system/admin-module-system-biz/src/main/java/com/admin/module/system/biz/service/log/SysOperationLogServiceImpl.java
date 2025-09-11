package com.admin.module.system.biz.service.log;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.annotation.OperationLog;
import com.admin.common.constants.ErrorCodes;
import com.admin.common.constants.SystemConstants;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.framework.redis.constants.CacheConstants;
import com.admin.module.system.api.dto.log.OperationLogDTO;
import com.admin.module.system.api.dto.log.SysOperationLogQueryDTO;
import com.admin.module.system.api.service.log.SysOperationLogService;
import com.admin.module.system.api.vo.log.*;
import com.admin.module.system.biz.convert.log.SysOperationLogConvert;
import com.admin.module.system.biz.dal.dataobject.SysOperationLogDO;
import com.admin.module.system.biz.dal.mapper.SysOperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 系统操作日志服务实现类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SysOperationLogServiceImpl implements SysOperationLogService {

    private final SysOperationLogMapper operationLogMapper;
    private final SysOperationLogConvert operationLogConvert;

    @Override
    public void saveOperationLog(OperationLogDTO logDTO) {
        try {
            SysOperationLogDO logDO = operationLogConvert.convert(logDTO);
            operationLogMapper.insert(logDO);
            log.debug("保存操作日志成功，日志ID: {}", logDO.getId());
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.OPERATION_LOG_RECORD_FAILED, "操作日志保存失败");
        }
    }

    @Override
    @Cacheable(value = CacheConstants.OPERATION_LOG_CACHE, key = "'page:' + #queryDTO.hashCode()", unless = "#result == null")
    public PageResult<SysOperationLogVO> getOperationLogPage(SysOperationLogQueryDTO queryDTO) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<SysOperationLogDO> wrapper = new LambdaQueryWrapper<SysOperationLogDO>()
                    .like(StrUtil.isNotBlank(queryDTO.getTitle()), SysOperationLogDO::getTitle, queryDTO.getTitle())
                    .eq(queryDTO.getBusinessType() != null, SysOperationLogDO::getBusinessType, queryDTO.getBusinessType())
                    .like(StrUtil.isNotBlank(queryDTO.getOperatorName()), SysOperationLogDO::getOperatorName, queryDTO.getOperatorName())
                    .eq(queryDTO.getOperatorId() != null, SysOperationLogDO::getOperatorId, queryDTO.getOperatorId())
                    .eq(queryDTO.getStatus() != null, SysOperationLogDO::getStatus, queryDTO.getStatus())
                    .like(StrUtil.isNotBlank(queryDTO.getOperationIp()), SysOperationLogDO::getOperationIp, queryDTO.getOperationIp())
                    .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null, 
                            SysOperationLogDO::getCreateTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                    .eq(StrUtil.isNotBlank(queryDTO.getRequestMethod()), SysOperationLogDO::getRequestMethod, queryDTO.getRequestMethod())
                    .eq(queryDTO.getOperatorType() != null, SysOperationLogDO::getOperatorType, queryDTO.getOperatorType())
                    .like(StrUtil.isNotBlank(queryDTO.getDeptName()), SysOperationLogDO::getDeptName, queryDTO.getDeptName())
                    .between(queryDTO.getMinCostTime() != null && queryDTO.getMaxCostTime() != null,
                            SysOperationLogDO::getCostTime, queryDTO.getMinCostTime(), queryDTO.getMaxCostTime())
                    .orderByDesc(SysOperationLogDO::getCreateTime);
            
            // 分页查询
            Page<SysOperationLogDO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            Page<SysOperationLogDO> pageResult = operationLogMapper.selectPage(page, wrapper);
            
            // 转换结果
            PageResult<SysOperationLogDO> result = new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
            
            return operationLogConvert.convertPage(result);
        } catch (Exception e) {
            log.error("查询操作日志分页失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_QUERY_FAILED, "操作日志查询失败");
        }
    }

    @Override
    @Cacheable(value = CacheConstants.OPERATION_LOG_CACHE, key = "#id", unless = "#result == null")
    public SysOperationLogVO getOperationLog(Long id) {
        try {
            SysOperationLogDO logDO = operationLogMapper.selectById(id);
            if (logDO == null) {
                throw new ServiceException(ErrorCodes.OPERATION_LOG_NOT_FOUND, "操作日志不存在");
            }
            return operationLogConvert.convert(logDO);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取操作日志详情失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_QUERY_FAILED, "获取操作日志详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.OPERATION_LOG_CACHE, allEntries = true)
    public int deleteOperationLogsBatch(Set<Long> ids) {
        try {
            if (CollUtil.isEmpty(ids)) {
                return 0;
            }
            
            int deleteCount = operationLogMapper.deleteBatchIds(ids);
            log.info("批量删除操作日志成功，删除数量: {}", deleteCount);
            return deleteCount;
            
        } catch (Exception e) {
            log.error("批量删除操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_DELETE_FAILED, "删除操作日志失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.OPERATION_LOG_CACHE, allEntries = true)
    public int deleteOperationLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            if (startTime == null || endTime == null) {
                throw new ServiceException(ErrorCodes.LOG_DELETE_FAILED, "开始时间和结束时间不能为空");
            }
            
            int deleteCount = operationLogMapper.deleteByTimeRange(startTime, endTime);
            log.info("按时间范围删除操作日志成功，删除数量: {}", deleteCount);
            return deleteCount;
            
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("按时间范围删除操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_DELETE_FAILED, "删除操作日志失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.OPERATION_LOG_CACHE, allEntries = true)
    public int clearOperationLogs() {
        try {
            int deleteCount = operationLogMapper.deleteAll();
            log.info("清空操作日志成功，删除数量: {}", deleteCount);
            return deleteCount;
        } catch (Exception e) {
            log.error("清空操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_CLEAR_FAILED, "清空操作日志失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.OPERATION_LOG_CACHE, allEntries = true)
    public int cleanExpiredOperationLogs(int retentionDays) {
        try {
            if (retentionDays <= 0) {
                retentionDays = SystemConstants.LOG_RETENTION_DAYS;
            }
            
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(retentionDays);
            int cleanCount = operationLogMapper.deleteExpiredLogs(beforeTime);
            
            log.info("清理过期操作日志成功，清理数量: {}，保留天数: {}", cleanCount, retentionDays);
            return cleanCount;
            
        } catch (Exception e) {
            log.error("清理过期操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_CLEAR_FAILED, "清理过期操作日志失败");
        }
    }

    @Override
    public List<SysOperationLogVO> exportOperationLogs(SysOperationLogQueryDTO queryDTO) {
        try {
            // 设置导出的最大数量限制
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() > SystemConstants.MAX_PAGE_SIZE) {
                queryDTO.setPageSize(SystemConstants.MAX_PAGE_SIZE);
            }
            
            // 构建查询条件
            LambdaQueryWrapper<SysOperationLogDO> wrapper = new LambdaQueryWrapper<SysOperationLogDO>()
                    .like(StrUtil.isNotBlank(queryDTO.getTitle()), SysOperationLogDO::getTitle, queryDTO.getTitle())
                    .eq(queryDTO.getBusinessType() != null, SysOperationLogDO::getBusinessType, queryDTO.getBusinessType())
                    .like(StrUtil.isNotBlank(queryDTO.getOperatorName()), SysOperationLogDO::getOperatorName, queryDTO.getOperatorName())
                    .eq(queryDTO.getStatus() != null, SysOperationLogDO::getStatus, queryDTO.getStatus())
                    .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null, 
                            SysOperationLogDO::getOperationTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                    .orderByDesc(SysOperationLogDO::getOperationTime)
                    .last("LIMIT " + queryDTO.getPageSize());
            
            List<SysOperationLogDO> logList = operationLogMapper.selectList(wrapper);
            return operationLogConvert.convertList(logList);
            
        } catch (Exception e) {
            log.error("导出操作日志失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_EXPORT_FAILED, "导出操作日志失败");
        }
    }

    @Override
    @Cacheable(value = CacheConstants.LOG_STATISTICS_CACHE, key = "'operation:' + #startTime + ':' + #endTime", unless = "#result == null")
    public OperationLogStatisticsVO getOperationLogStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 简化统计实现 - 基于当前数据库查询
            LambdaQueryWrapper<SysOperationLogDO> wrapper = new LambdaQueryWrapper<SysOperationLogDO>()
                    .between(startTime != null && endTime != null, 
                            SysOperationLogDO::getOperationTime, startTime, endTime);
            
            List<SysOperationLogDO> logList = operationLogMapper.selectList(wrapper);
            
            OperationLogStatisticsVO statistics = new OperationLogStatisticsVO();
            statistics.setTotalCount((long) logList.size());
            statistics.setSuccessCount(logList.stream().mapToLong(log -> log.getStatus() == 1 ? 1 : 0).sum());
            statistics.setFailCount(statistics.getTotalCount() - statistics.getSuccessCount());
            
            if (!logList.isEmpty()) {
                double avgCostTime = logList.stream().mapToLong(SysOperationLogDO::getCostTime).average().orElse(0.0);
                statistics.setAvgCostTime(avgCostTime);
                statistics.setMaxCostTime(logList.stream().mapToLong(SysOperationLogDO::getCostTime).max().orElse(0L));
                statistics.setMinCostTime(logList.stream().mapToLong(SysOperationLogDO::getCostTime).min().orElse(0L));
            }
            
            // 设置空的详细统计列表
            statistics.setBusinessTypeStats(new ArrayList<>());
            statistics.setOperatorStats(new ArrayList<>());
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取操作日志统计信息失败: {}", e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_STATISTICS_FAILED, "获取操作日志统计信息失败");
        }
    }

    @Override
    @Cacheable(value = CacheConstants.OPERATION_LOG_CACHE, key = "'user:' + #userId + ':' + #startTime + ':' + #endTime", unless = "#result == null")
    public List<SysOperationLogVO> getUserOperationLogs(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            LambdaQueryWrapper<SysOperationLogDO> wrapper = new LambdaQueryWrapper<SysOperationLogDO>()
                    .eq(SysOperationLogDO::getOperatorId, userId)
                    .between(startTime != null && endTime != null, 
                            SysOperationLogDO::getOperationTime, startTime, endTime)
                    .orderByDesc(SysOperationLogDO::getOperationTime)
                    .last("LIMIT 100");
            
            List<SysOperationLogDO> logList = operationLogMapper.selectList(wrapper);
            return operationLogConvert.convertList(logList);
        } catch (Exception e) {
            log.error("获取用户操作日志失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new ServiceException(ErrorCodes.LOG_QUERY_FAILED, "获取用户操作日志失败");
        }
    }

    /**
     * 构建统计信息
     */
    private OperationLogStatisticsVO buildStatistics(StatisticsResult basicStats,
                                                     List<BusinessTypeStatisticsVO> businessTypeResults,
                                                     List<OperatorStatisticsVO> operatorResults) {
        
        OperationLogStatisticsVO statistics = new OperationLogStatisticsVO();
        
        // 基础统计
        statistics.setTotalCount(basicStats.getTotalCount() != null ? basicStats.getTotalCount() : 0L);
        statistics.setSuccessCount(basicStats.getSuccessCount() != null ? basicStats.getSuccessCount() : 0L);
        statistics.setFailCount(basicStats.getFailCount() != null ? basicStats.getFailCount() : 0L);
        statistics.setAvgCostTime(basicStats.getAvgCostTime());
        statistics.setMaxCostTime(basicStats.getMaxCostTime() != null ? basicStats.getMaxCostTime() : 0L);
        statistics.setMinCostTime(basicStats.getMinCostTime() != null ? basicStats.getMinCostTime() : 0L);
        
        // 业务类型统计
        List<BusinessTypeStatisticsVO> businessTypeStats = new ArrayList<>();
        Long totalCount = statistics.getTotalCount();
        
        for (BusinessTypeStatisticsVO result : businessTypeResults) {
            BusinessTypeStatisticsVO stat = new BusinessTypeStatisticsVO();
            stat.setBusinessType(result.getBusinessType());
            stat.setBusinessTypeName(getBusinessTypeName(result.getBusinessType()));
            stat.setCount(result.getCount());
            stat.setPercentage(totalCount > 0 ? (result.getCount().doubleValue() / totalCount * 100) : 0.0);
            businessTypeStats.add(stat);
        }
        statistics.setBusinessTypeStats(businessTypeStats);
        
        // 操作人统计
        List<OperatorStatisticsVO> operatorStats = new ArrayList<>();
        for (OperatorStatisticsVO result : operatorResults) {
            OperatorStatisticsVO stat = new OperatorStatisticsVO();
            stat.setOperatorId(result.getOperatorId());
            stat.setOperatorName(result.getOperatorName());
            stat.setCount(result.getCount());
            stat.setPercentage(totalCount > 0 ? (result.getCount().doubleValue() / totalCount * 100) : 0.0);
            operatorStats.add(stat);
        }
        statistics.setOperatorStats(operatorStats);
        
        return statistics;
    }

    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(Integer businessType) {
        if (businessType == null) {
            return "未知";
        }
        
        for (OperationLog.BusinessType type : OperationLog.BusinessType.values()) {
            if (type.getCode() == businessType) {
                return type.getDescription();
            }
        }
        return "未知";
    }
}