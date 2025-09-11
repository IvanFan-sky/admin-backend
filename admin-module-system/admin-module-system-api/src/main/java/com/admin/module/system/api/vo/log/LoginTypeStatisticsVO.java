package com.admin.module.system.api.vo.log;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 登录方式统计结果VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginTypeStatisticsVO {
    
    private String loginType;
    private String loginTypeName;
    private Long count;
    private Double percentage;


}