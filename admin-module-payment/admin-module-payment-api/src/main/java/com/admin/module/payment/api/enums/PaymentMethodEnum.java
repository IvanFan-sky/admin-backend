package com.admin.module.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @author admin
 * @since 2025/09/14
 */
@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {

    /**
     * 模拟支付
     */
    MOCK("mock", "模拟支付"),
    
    /**
     * 微信扫码支付
     */
    WECHAT_NATIVE("wechat_native", "微信扫码支付"),
    
    /**
     * 微信H5支付
     */
    WECHAT_H5("wechat_h5", "微信H5支付"),
    
    /**
     * 微信小程序支付
     */
    WECHAT_MINI("wechat_mini", "微信小程序支付"),
    
    /**
     * 微信APP支付
     */
    WECHAT_APP("wechat_app", "微信APP支付"),
    
    /**
     * 支付宝扫码支付
     */
    ALIPAY_PC("alipay_pc", "支付宝扫码支付"),
    
    /**
     * 支付宝H5支付
     */
    ALIPAY_WAP("alipay_wap", "支付宝H5支付"),
    
    /**
     * 支付宝APP支付
     */
    ALIPAY_APP("alipay_app", "支付宝APP支付"),
    
    ;

    /**
     * 支付方式编码
     */
    private final String code;

    /**
     * 支付方式名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static PaymentMethodEnum getByCode(String code) {
        for (PaymentMethodEnum methodEnum : values()) {
            if (methodEnum.getCode().equals(code)) {
                return methodEnum;
            }
        }
        return null;
    }

    /**
     * 获取所属支付渠道
     */
    public PaymentChannelEnum getChannel() {
        if (this == MOCK) {
            return PaymentChannelEnum.MOCK;
        } else if (code.startsWith("wechat")) {
            return PaymentChannelEnum.WECHAT_PAY;
        } else if (code.startsWith("alipay")) {
            return PaymentChannelEnum.ALIPAY;
        }
        return null;
    }
}
