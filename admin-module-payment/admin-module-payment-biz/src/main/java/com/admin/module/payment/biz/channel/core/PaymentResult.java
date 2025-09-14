package com.admin.module.payment.biz.channel.core;

import lombok.Builder;
import lombok.Data;

/**
 * 支付结果
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@Builder
public class PaymentResult {

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
     * 支付参数（JSON格式）
     */
    private String payParams;

    /**
     * 支付页面URL
     */
    private String payUrl;

    /**
     * 二维码内容
     */
    private String qrCode;

    /**
     * 扩展数据
     */
    private String extraData;

    /**
     * 创建成功结果
     */
    public static PaymentResult success() {
        return PaymentResult.builder()
                .success(true)
                .build();
    }

    /**
     * 创建成功结果
     */
    public static PaymentResult success(String channelOrderNo, String payParams) {
        return PaymentResult.builder()
                .success(true)
                .channelOrderNo(channelOrderNo)
                .payParams(payParams)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static PaymentResult failure(String errorCode, String errorMsg) {
        return PaymentResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }

}
