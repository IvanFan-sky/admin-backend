package com.admin.module.payment.api.dto.order;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 支付订单查询DTO
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付订单查询DTO")
public class PaymentOrderQueryDTO extends PageQuery {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商户订单号")
    private String merchantOrderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "支付渠道编码")
    private String channelCode;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "创建时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

    @Schema(description = "支付成功时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime successTimeStart;

    @Schema(description = "支付成功时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime successTimeEnd;

}
