package com.admin.module.system.api.vo.log;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 登录日志统计信息VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogStatisticsVO {
    
    private Long totalCount;
    private Long successCount;
    private Long failCount;
    private Double successRate;
    private Long onlineCount;
    private Double avgOnlineDuration;
    private List<LoginTypeStatisticsVO> loginTypeStats;
    private List<LoginLocationStatisticsVO> locationStats;
    private List<BrowserStatisticsVO> browserStats;
}