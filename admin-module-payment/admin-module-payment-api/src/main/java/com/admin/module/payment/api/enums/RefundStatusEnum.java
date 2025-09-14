package com.admin.module.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款状态枚举
 *
 * @author admin
 * @since 2025/09/14
 */
@Getter
@AllArgsConstructor
public enum RefundStatusEnum {

    /**
     * 退款中
     */
    PROCESSING(0, "退款中"),
    
    /**
     * 退款成功
     */
    SUCCESS(1, "退款成功"),
    
    /**
     * 退款失败
     */
    FAILED(2, "退款失败"),
    
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
    public static RefundStatusEnum getByStatus(Integer status) {
        for (RefundStatusEnum statusEnum : values()) {
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
        return this == SUCCESS || this == FAILED;
    }
}
