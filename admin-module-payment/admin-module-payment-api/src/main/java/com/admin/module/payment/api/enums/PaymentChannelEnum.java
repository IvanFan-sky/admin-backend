package com.admin.module.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道枚举
 *
 * @author admin
 * @since 2025/09/14
 */
@Getter
@AllArgsConstructor
public enum PaymentChannelEnum {

    /**
     * 模拟支付
     */
    MOCK("mock", "模拟支付"),
    
    /**
     * 微信支付
     */
    WECHAT_PAY("wechat_pay", "微信支付"),
    
    /**
     * 支付宝
     */
    ALIPAY("alipay", "支付宝"),
    
    ;

    /**
     * 渠道编码
     */
    private final String code;

    /**
     * 渠道名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static PaymentChannelEnum getByCode(String code) {
        for (PaymentChannelEnum channelEnum : values()) {
            if (channelEnum.getCode().equals(code)) {
                return channelEnum;
            }
        }
        return null;
    }
}
