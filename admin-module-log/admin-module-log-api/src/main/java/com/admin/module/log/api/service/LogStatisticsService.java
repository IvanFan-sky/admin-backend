package com.admin.module.log.api.service;

import com.admin.module.log.api.vo.LogStatisticsVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 日志统计服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogStatisticsService {

    /**
     * 获取日志统计概览
     *
     * @return 日志统计信息
     */
    LogStatisticsVO getLogStatistics();

    /**
     * 获取操作日志按业务类型统计
     *
     * @param days 统计天数，默认30天
     * @return 业务类型统计
     */
    Map<String, Long> getOperationLogByBusinessType(Integer days);

    /**
     * 获取登录日志按时间统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日登录统计
     */
    List<LogStatisticsVO.DailyStats> getLoginLogTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取操作日志按时间统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日操作统计
     */
    List<LogStatisticsVO.DailyStats> getOperationLogTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取系统性能统计
     *
     * @param days 统计天数，默认7天
     * @return 性能统计信息
     */
    LogStatisticsVO.SystemOverview getSystemPerformanceStats(Integer days);
}