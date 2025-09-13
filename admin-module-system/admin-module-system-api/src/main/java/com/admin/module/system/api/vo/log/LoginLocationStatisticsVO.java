package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录地区统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "登录地区统计")
public class LoginLocationStatisticsVO {

    @Schema(description = "登录地区", example = "北京")
    private String location;

    @Schema(description = "登录次数", example = "200")
    private Long count;

    @Schema(description = "占比", example = "20.5")
    private Double percentage;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;
}