package com.admin.module.system.api.vo.auth;

import com.admin.framework.security.service.LoginLimitService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录状态响应VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录状态响应")
public class LoginStatusVO {

    /**
     * 是否被锁定
     */
    @Schema(description = "是否被锁定")
    private boolean locked;
    
    /**
     * 锁定信息
     */
    @Schema(description = "锁定信息")
    private LoginLimitService.AccountLockInfo lockInfo;
    
    /**
     * 剩余锁定时间（秒）
     */
    @Schema(description = "剩余锁定时间（秒）")
    private long remainingLockTime;
    
    /**
     * 登录失败次数
     */
    @Schema(description = "登录失败次数")
    private int failCount;
    
    /**
     * 是否需要验证码
     */
    @Schema(description = "是否需要验证码")
    private boolean needCaptcha;
}