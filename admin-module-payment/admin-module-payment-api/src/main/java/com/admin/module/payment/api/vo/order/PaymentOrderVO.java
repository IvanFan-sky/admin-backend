package com.admin.module.payment.api.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单VO
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Schema(description = "支付订单VO")
public class PaymentOrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商户订单号")
    private String merchantOrderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "支付渠道编码")
    private String channelCode;

    @Schema(description = "支付渠道名称")
    private String channelName;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付方式名称")
    private String paymentMethodName;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "货币类型")
    private String currency;

    @Schema(description = "订单标题")
    private String subject;

    @Schema(description = "订单描述")
    private String body;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "渠道订单号")
    private String channelOrderNo;

    @Schema(description = "支付成功时间")
    private LocalDateTime successTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "异步通知地址")
    private String notifyUrl;

    @Schema(description = "同步跳转地址")
    private String returnUrl;

    @Schema(description = "扩展数据")
    private String extraData;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
