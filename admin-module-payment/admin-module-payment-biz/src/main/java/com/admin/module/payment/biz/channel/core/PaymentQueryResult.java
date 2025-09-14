package com.admin.module.payment.biz.channel.core;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付查询结果
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Builder
public class PaymentQueryResult {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 扩展数据
     */
    private String extraData;

    /**
     * 创建成功结果
     */
    public static PaymentQueryResult success(Integer orderStatus, String channelOrderNo) {
        return PaymentQueryResult.builder()
                .success(true)
                .orderStatus(orderStatus)
                .channelOrderNo(channelOrderNo)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static PaymentQueryResult failure(String errorCode, String errorMsg) {
        return PaymentQueryResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }

}
