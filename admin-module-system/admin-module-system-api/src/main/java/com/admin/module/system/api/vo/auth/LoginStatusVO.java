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

    @Schema(description = "账户是否被锁定", example = "false")
    private boolean locked;
    
    @Schema(description = "账户锁定详细信息")
    private LoginLimitService.AccountLockInfo lockInfo;
    
    @Schema(description = "剩余锁定时间（秒）", example = "1800")
    private long remainingLockTime;
    
    @Schema(description = "登录失败次数", example = "3")
    private int failCount;
    
    @Schema(description = "是否需要输入验证码", example = "true")
    private boolean needCaptcha;
}