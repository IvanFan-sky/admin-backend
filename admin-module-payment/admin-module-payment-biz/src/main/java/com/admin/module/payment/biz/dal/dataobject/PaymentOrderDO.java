package com.admin.module.payment.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单数据对象
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("payment_order")
@KeySequence("payment_order_seq")
public class PaymentOrderDO extends BaseEntity {

    /**
     * 订单ID
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商户订单号
     */
    private String merchantOrderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付渠道编码
     */
    private String channelCode;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付成功时间
     */
    private LocalDateTime successTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;

    /**
     * 扩展数据
     */
    private String extraData;

}
