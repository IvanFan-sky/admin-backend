package com.admin.module.system.api.vo.log;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志信息VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogInfoVO {
    
    private String username;
    private Long userId;
    private String ipAddress;
    private String loginLocation;
    private String browser;
    private String os;
    private Integer status;
    private String msg;
    private LocalDateTime loginTime;
    private String loginType;
    private String userAgent;
    private String tokenId;
    private LocalDateTime sessionTimeout;
}