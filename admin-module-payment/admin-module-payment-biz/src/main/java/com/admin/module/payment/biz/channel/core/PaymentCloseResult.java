package com.admin.module.payment.biz.channel.core;

import lombok.Builder;
import lombok.Data;

/**
 * 支付关闭结果
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Builder
public class PaymentCloseResult {

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
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 扩展数据
     */
    private String extraData;

    /**
     * 创建成功结果
     */
    public static PaymentCloseResult success() {
        return PaymentCloseResult.builder()
                .success(true)
                .build();
    }

    /**
     * 创建成功结果
     */
    public static PaymentCloseResult success(String channelOrderNo) {
        return PaymentCloseResult.builder()
                .success(true)
                .channelOrderNo(channelOrderNo)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static PaymentCloseResult failure(String errorCode, String errorMsg) {
        return PaymentCloseResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }

}
