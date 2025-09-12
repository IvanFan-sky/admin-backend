package com.admin.module.log.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 日志统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Schema(description = "日志统计信息")
@Data
public class LogStatisticsVO {

    @Schema(description = "操作日志统计")
    private OperationLogStats operationLog;

    @Schema(description = "登录日志统计")
    private LoginLogStats loginLog;

    @Schema(description = "系统概览")
    private SystemOverview systemOverview;

    @Data
    @Schema(description = "操作日志统计")
    public static class OperationLogStats {
        
        @Schema(description = "总记录数")
        private Long totalCount;

        @Schema(description = "今日操作数")
        private Long todayCount;

        @Schema(description = "异常操作数")
        private Long errorCount;

        @Schema(description = "按业务类型统计")
        private Map<String, Long> businessTypeStats;

        @Schema(description = "按操作人员统计（Top10）")
        private Map<String, Long> operatorStats;

        @Schema(description = "最近7天趋势")
        private List<DailyStats> dailyTrend;
    }

    @Data
    @Schema(description = "登录日志统计")
    public static class LoginLogStats {
        
        @Schema(description = "总登录次数")
        private Long totalCount;

        @Schema(description = "今日登录数")
        private Long todayCount;

        @Schema(description = "失败登录数")
        private Long failureCount;

        @Schema(description = "唯一用户数")
        private Long uniqueUserCount;

        @Schema(description = "按登录类型统计")
        private Map<String, Long> loginTypeStats;

        @Schema(description = "按浏览器统计（Top10）")
        private Map<String, Long> browserStats;

        @Schema(description = "最近7天趋势")
        private List<DailyStats> dailyTrend;
    }

    @Data
    @Schema(description = "系统概览")
    public static class SystemOverview {
        
        @Schema(description = "活跃用户数（最近30天）")
        private Long activeUserCount;

        @Schema(description = "平均响应时间（毫秒）")
        private Double avgResponseTime;

        @Schema(description = "异常率（%）")
        private Double errorRate;

        @Schema(description = "最热门功能模块（Top5）")
        private Map<String, Long> topModules;
    }

    @Data
    @Schema(description = "每日统计")
    public static class DailyStats {
        
        @Schema(description = "日期")
        private LocalDate date;

        @Schema(description = "数量")
        private Long count;
    }
}