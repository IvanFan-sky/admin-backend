package com.admin.module.system.api.vo.log;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 操作日志统计VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogStatisticsVO {
    
    /**
     * 总数量
     */
    private Long totalCount;
    
    /**
     * 成功数量
     */
    private Long successCount;
    
    /**
     * 失败数量
     */
    private Long failCount;
    
    /**
     * 平均耗时
     */
    private Double avgCostTime;
    
    /**
     * 最大耗时
     */
    private Long maxCostTime;
    
    /**
     * 最小耗时
     */
    private Long minCostTime;
    
    /**
     * 业务类型统计
     */
    private List<BusinessTypeStatisticsVO> businessTypeStats;
    
    /**
     * 操作人统计
     */
    private List<OperatorStatisticsVO> operatorStats;
}