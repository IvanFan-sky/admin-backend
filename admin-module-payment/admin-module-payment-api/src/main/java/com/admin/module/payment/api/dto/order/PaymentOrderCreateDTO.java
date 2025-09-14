package com.admin.module.payment.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单创建DTO
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Schema(description = "支付订单创建DTO")
public class PaymentOrderCreateDTO {

    @Schema(description = "商户订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商户订单号不能为空")
    @Size(max = 64, message = "商户订单号长度不能超过64个字符")
    private String merchantOrderNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "支付方式编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付方式编码不能为空")
    private String paymentMethod;

    @Schema(description = "支付金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01")
    @Digits(integer = 8, fraction = 2, message = "支付金额格式不正确")
    private BigDecimal amount;

    @Schema(description = "货币类型", example = "CNY")
    @Size(max = 8, message = "货币类型长度不能超过8个字符")
    private String currency = "CNY";

    @Schema(description = "订单标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 256, message = "订单标题长度不能超过256个字符")
    private String subject;

    @Schema(description = "订单描述")
    @Size(max = 512, message = "订单描述长度不能超过512个字符")
    private String body;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "异步通知地址")
    @Size(max = 512, message = "异步通知地址长度不能超过512个字符")
    private String notifyUrl;

    @Schema(description = "同步跳转地址")
    @Size(max = 512, message = "同步跳转地址长度不能超过512个字符")
    private String returnUrl;

    @Schema(description = "扩展数据")
    private String extraData;

}
