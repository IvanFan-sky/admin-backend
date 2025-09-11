package com.admin.module.system.api.vo.log;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 业务类型统计VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessTypeStatisticsVO {
    
    /**
     * 业务类型
     */
    private Integer businessType;
    
    /**
     * 业务类型名称
     */
    private String businessTypeName;
    
    /**
     * 数量
     */
    private Long count;
    
    /**
     * 百分比
     */
    private Double percentage;
}