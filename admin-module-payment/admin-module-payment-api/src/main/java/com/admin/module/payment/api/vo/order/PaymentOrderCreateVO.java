package com.admin.module.payment.api.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单创建结果VO
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Schema(description = "支付订单创建结果VO")
public class PaymentOrderCreateVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商户订单号")
    private String merchantOrderNo;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "支付参数（JSON格式）")
    private String payParams;

    @Schema(description = "支付页面URL")
    private String payUrl;

    @Schema(description = "二维码内容")
    private String qrCode;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
