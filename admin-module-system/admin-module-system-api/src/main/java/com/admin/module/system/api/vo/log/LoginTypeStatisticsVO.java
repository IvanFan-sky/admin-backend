package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录类型统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "登录类型统计")
public class LoginTypeStatisticsVO {

    @Schema(description = "登录类型", example = "1")
    private Integer loginType;

    @Schema(description = "登录类型名称", example = "账号密码")
    private String loginTypeName;

    @Schema(description = "登录次数", example = "500")
    private Long count;

    @Schema(description = "占比", example = "50.5")
    private Double percentage;
}