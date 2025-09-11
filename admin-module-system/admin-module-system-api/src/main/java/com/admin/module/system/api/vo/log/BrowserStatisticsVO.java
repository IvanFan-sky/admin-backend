package com.admin.module.system.api.vo.log;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 浏览器统计结果VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrowserStatisticsVO {
    
    private String browser;
    private Long count;
    private Double percentage;
}