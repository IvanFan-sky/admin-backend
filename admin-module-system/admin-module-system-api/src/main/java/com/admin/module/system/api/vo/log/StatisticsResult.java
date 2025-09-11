package com.admin.module.system.api.vo.log;

import lombok.Data;

/**
 * 统计结果VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class StatisticsResult {
    
    private Long totalCount;
    private Long successCount;
    private Long failCount;
    private Long onlineCount;
    private Double avgOnlineDuration;
    private Double avgCostTime;
    private Long maxCostTime;
    private Long minCostTime;
    
    public StatisticsResult() {}
}