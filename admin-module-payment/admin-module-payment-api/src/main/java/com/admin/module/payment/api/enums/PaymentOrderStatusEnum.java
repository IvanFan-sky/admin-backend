package com.admin.module.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付订单状态枚举
 *
 * @author admin
 * @since 2025/09/14
 */
@Getter
@AllArgsConstructor
public enum PaymentOrderStatusEnum {

    /**
     * 待支付
     */
    WAITING(0, "待支付"),
    
    /**
     * 支付中
     */
    PAYING(1, "支付中"),
    
    /**
     * 支付成功
     */
    SUCCESS(2, "支付成功"),
    
    /**
     * 支付失败
     */
    FAILED(3, "支付失败"),
    
    /**
     * 已关闭
     */
    CLOSED(4, "已关闭"),
    
    /**
     * 已退款
     */
    REFUNDED(5, "已退款"),
    
    ;

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态值获取枚举
     */
    public static PaymentOrderStatusEnum getByStatus(Integer status) {
        for (PaymentOrderStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == SUCCESS || this == FAILED || this == CLOSED || this == REFUNDED;
    }
}
