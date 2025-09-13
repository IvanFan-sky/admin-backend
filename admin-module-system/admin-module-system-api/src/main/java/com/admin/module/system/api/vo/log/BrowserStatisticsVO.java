package com.admin.module.system.api.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 浏览器统计VO
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "浏览器统计")
public class BrowserStatisticsVO {

    @Schema(description = "浏览器类型", example = "Chrome")
    private String browser;

    @Schema(description = "浏览器版本", example = "120.0.0.0")
    private String version;

    @Schema(description = "登录次数", example = "300")
    private Long count;

    @Schema(description = "占比", example = "30.5")
    private Double percentage;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;
}