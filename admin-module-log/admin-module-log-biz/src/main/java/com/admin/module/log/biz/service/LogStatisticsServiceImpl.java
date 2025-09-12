package com.admin.module.log.biz.service;

import com.admin.module.log.api.service.LogStatisticsService;
import com.admin.module.log.api.vo.LogStatisticsVO;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日志统计服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogStatisticsServiceImpl implements LogStatisticsService {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public LogStatisticsVO getLogStatistics() {
        LogStatisticsVO statistics = new LogStatisticsVO();
        
        // 操作日志统计
        statistics.setOperationLog(buildOperationLogStats());
        
        // 登录日志统计
        statistics.setLoginLog(buildLoginLogStats());
        
        // 系统概览
        statistics.setSystemOverview(getSystemPerformanceStats(7));
        
        return statistics;
    }

    @Override
    public Map<String, Long> getOperationLogByBusinessType(Integer days) {
        if (days == null) days = 30;
        
        LocalDateTime startTime = LocalDate.now().minusDays(days).atStartOfDay();
        
        LambdaQueryWrapper<OperationLogDO> wrapper = new LambdaQueryWrapper<OperationLogDO>()
                .ge(OperationLogDO::getOperTime, startTime)
                .select(OperationLogDO::getBusinessType);
                
        List<OperationLogDO> logs = operationLogMapper.selectList(wrapper);
        
        return logs.stream()
                .collect(Collectors.groupingBy(
                    log -> getBusinessTypeName(log.getBusinessType()),
                    Collectors.counting()
                ));
    }

    @Override
    public List<LogStatisticsVO.DailyStats> getLoginLogTrend(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<LoginLogDO> wrapper = new LambdaQueryWrapper<LoginLogDO>()
                .between(LoginLogDO::getLoginTime, startTime, endTime)
                .select(LoginLogDO::getLoginTime);
                
        List<LoginLogDO> logs = loginLogMapper.selectList(wrapper);
        
        Map<LocalDate, Long> dailyCount = logs.stream()
                .collect(Collectors.groupingBy(
                    log -> log.getLoginTime().toLocalDate(),
                    Collectors.counting()
                ));
        
        return buildDailyStatsList(startDate, endDate, dailyCount);
    }

    @Override
    public List<LogStatisticsVO.DailyStats> getOperationLogTrend(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<OperationLogDO> wrapper = new LambdaQueryWrapper<OperationLogDO>()
                .between(OperationLogDO::getOperTime, startTime, endTime)
                .select(OperationLogDO::getOperTime);
                
        List<OperationLogDO> logs = operationLogMapper.selectList(wrapper);
        
        Map<LocalDate, Long> dailyCount = logs.stream()
                .collect(Collectors.groupingBy(
                    log -> log.getOperTime().toLocalDate(),
                    Collectors.counting()
                ));
        
        return buildDailyStatsList(startDate, endDate, dailyCount);
    }

    @Override
    public LogStatisticsVO.SystemOverview getSystemPerformanceStats(Integer days) {
        if (days == null) days = 7;
        
        LogStatisticsVO.SystemOverview overview = new LogStatisticsVO.SystemOverview();
        LocalDateTime startTime = LocalDate.now().minusDays(days).atStartOfDay();
        
        // 活跃用户数（最近30天）
        LocalDateTime monthAgo = LocalDate.now().minusDays(30).atStartOfDay();
        Long activeUserCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLogDO>()
                        .ge(LoginLogDO::getLoginTime, monthAgo)
                        .eq(LoginLogDO::getStatus, 0)
        );
        overview.setActiveUserCount(activeUserCount);
        
        // 平均响应时间
        LambdaQueryWrapper<OperationLogDO> timeWrapper = new LambdaQueryWrapper<OperationLogDO>()
                .ge(OperationLogDO::getOperTime, startTime)
                .isNotNull(OperationLogDO::getCostTime)
                .select(OperationLogDO::getCostTime);
                
        List<OperationLogDO> timeLogs = operationLogMapper.selectList(timeWrapper);
        Double avgResponseTime = timeLogs.stream()
                .mapToLong(OperationLogDO::getCostTime)
                .average()
                .orElse(0.0);
        overview.setAvgResponseTime(avgResponseTime);
        
        // 异常率
        Long totalCount = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLogDO>()
                        .ge(OperationLogDO::getOperTime, startTime)
        );
        Long errorCount = operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLogDO>()
                        .ge(OperationLogDO::getOperTime, startTime)
                        .eq(OperationLogDO::getStatus, 1)
        );
        Double errorRate = totalCount > 0 ? (errorCount.doubleValue() / totalCount.doubleValue() * 100) : 0.0;
        overview.setErrorRate(errorRate);
        
        // 最热门功能模块（Top5）
        LambdaQueryWrapper<OperationLogDO> moduleWrapper = new LambdaQueryWrapper<OperationLogDO>()
                .ge(OperationLogDO::getOperTime, startTime)
                .isNotNull(OperationLogDO::getTitle)
                .select(OperationLogDO::getTitle);
                
        List<OperationLogDO> moduleLogs = operationLogMapper.selectList(moduleWrapper);
        Map<String, Long> topModules = moduleLogs.stream()
                .collect(Collectors.groupingBy(
                    OperationLogDO::getTitle,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        overview.setTopModules(topModules);
        
        return overview;
    }

    /**
     * 构建操作日志统计
     */
    private LogStatisticsVO.OperationLogStats buildOperationLogStats() {
        LogStatisticsVO.OperationLogStats stats = new LogStatisticsVO.OperationLogStats();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        
        // 总记录数
        stats.setTotalCount(operationLogMapper.selectCount(null));
        
        // 今日操作数
        stats.setTodayCount(operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLogDO>()
                        .ge(OperationLogDO::getOperTime, todayStart)
        ));
        
        // 异常操作数
        stats.setErrorCount(operationLogMapper.selectCount(
                new LambdaQueryWrapper<OperationLogDO>()
                        .eq(OperationLogDO::getStatus, 1)
        ));
        
        // 按业务类型统计
        stats.setBusinessTypeStats(getOperationLogByBusinessType(30));
        
        // 按操作人员统计（Top10）
        List<OperationLogDO> operatorLogs = operationLogMapper.selectList(
                new LambdaQueryWrapper<OperationLogDO>()
                        .ge(OperationLogDO::getOperTime, sevenDaysAgo)
                        .isNotNull(OperationLogDO::getOperName)
                        .select(OperationLogDO::getOperName)
        );
        Map<String, Long> operatorStats = operatorLogs.stream()
                .collect(Collectors.groupingBy(
                    OperationLogDO::getOperName,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        stats.setOperatorStats(operatorStats);
        
        // 最近7天趋势
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgoDate = today.minusDays(6);
        stats.setDailyTrend(getOperationLogTrend(sevenDaysAgoDate, today));
        
        return stats;
    }

    /**
     * 构建登录日志统计
     */
    private LogStatisticsVO.LoginLogStats buildLoginLogStats() {
        LogStatisticsVO.LoginLogStats stats = new LogStatisticsVO.LoginLogStats();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        
        // 总登录次数
        stats.setTotalCount(loginLogMapper.selectCount(null));
        
        // 今日登录数
        stats.setTodayCount(loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLogDO>()
                        .ge(LoginLogDO::getLoginTime, todayStart)
        ));
        
        // 失败登录数
        stats.setFailureCount(loginLogMapper.selectCount(
                new LambdaQueryWrapper<LoginLogDO>()
                        .eq(LoginLogDO::getStatus, 1)
        ));
        
        // 唯一用户数
        List<LoginLogDO> userLogs = loginLogMapper.selectList(
                new LambdaQueryWrapper<LoginLogDO>()
                        .isNotNull(LoginLogDO::getUserName)
                        .select(LoginLogDO::getUserName)
        );
        Long uniqueUserCount = userLogs.stream()
                .map(LoginLogDO::getUserName)
                .distinct()
                .count();
        stats.setUniqueUserCount(uniqueUserCount);
        
        // 按登录类型统计
        List<LoginLogDO> typeLogs = loginLogMapper.selectList(
                new LambdaQueryWrapper<LoginLogDO>()
                        .ge(LoginLogDO::getLoginTime, sevenDaysAgo)
                        .select(LoginLogDO::getLoginType)
        );
        Map<String, Long> loginTypeStats = typeLogs.stream()
                .collect(Collectors.groupingBy(
                    log -> getLoginTypeName(log.getLoginType()),
                    Collectors.counting()
                ));
        stats.setLoginTypeStats(loginTypeStats);
        
        // 按浏览器统计（Top10）
        List<LoginLogDO> browserLogs = loginLogMapper.selectList(
                new LambdaQueryWrapper<LoginLogDO>()
                        .ge(LoginLogDO::getLoginTime, sevenDaysAgo)
                        .isNotNull(LoginLogDO::getBrowser)
                        .select(LoginLogDO::getBrowser)
        );
        Map<String, Long> browserStats = browserLogs.stream()
                .collect(Collectors.groupingBy(
                    LoginLogDO::getBrowser,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        stats.setBrowserStats(browserStats);
        
        // 最近7天趋势
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgoDate = today.minusDays(6);
        stats.setDailyTrend(getLoginLogTrend(sevenDaysAgoDate, today));
        
        return stats;
    }

    /**
     * 构建每日统计列表
     */
    private List<LogStatisticsVO.DailyStats> buildDailyStatsList(LocalDate startDate, LocalDate endDate, Map<LocalDate, Long> dailyCount) {
        List<LogStatisticsVO.DailyStats> result = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LogStatisticsVO.DailyStats dailyStats = new LogStatisticsVO.DailyStats();
            dailyStats.setDate(current);
            dailyStats.setCount(dailyCount.getOrDefault(current, 0L));
            result.add(dailyStats);
            current = current.plusDays(1);
        }
        
        return result;
    }

    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(Integer businessType) {
        if (businessType == null) return "未知";
        switch (businessType) {
            case 0: return "其它";
            case 1: return "新增";
            case 2: return "修改";
            case 3: return "删除";
            case 4: return "授权";
            case 5: return "导出";
            case 6: return "导入";
            case 7: return "强退";
            case 8: return "生成代码";
            case 9: return "清空数据";
            default: return "未知";
        }
    }

    /**
     * 获取登录类型名称
     */
    private String getLoginTypeName(Integer loginType) {
        if (loginType == null) return "未知";
        switch (loginType) {
            case 1: return "用户名密码";
            case 2: return "邮箱密码";
            case 3: return "手机验证码";
            case 4: return "第三方登录";
            default: return "未知";
        }
    }
}