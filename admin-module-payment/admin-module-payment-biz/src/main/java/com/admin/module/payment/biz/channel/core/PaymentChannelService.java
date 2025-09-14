package com.admin.module.payment.biz.channel.core;

import com.admin.module.payment.biz.dal.dataobject.PaymentOrderDO;

/**
 * 支付渠道服务接口
 * 
 * 借鉴JeePay的设计思想，定义统一的支付渠道接口
 *
 * @author admin
 * @since 2025/09/14
 */
public interface PaymentChannelService {

    /**
     * 获取渠道编码
     *
     * @return 渠道编码
     */
    String getChannelCode();

    /**
     * 获取渠道名称
     *
     * @return 渠道名称
     */
    String getChannelName();

    /**
     * 检查是否支持该支付方式
     *
     * @param paymentMethod 支付方式
     * @return 是否支持
     */
    boolean isSupport(String paymentMethod);

    /**
     * 前置检查
     *
     * @param paymentOrder 支付订单
     * @return 检查结果，null表示通过，非null表示错误信息
     */
    String preCheck(PaymentOrderDO paymentOrder);

    /**
     * 调起支付
     *
     * @param paymentOrder 支付订单
     * @param channelConfig 渠道配置
     * @return 支付结果
     * @throws Exception 支付异常
     */
    PaymentResult pay(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception;

    /**
     * 查询支付订单状态
     *
     * @param paymentOrder 支付订单
     * @param channelConfig 渠道配置
     * @return 查询结果
     * @throws Exception 查询异常
     */
    PaymentQueryResult queryPayment(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception;

    /**
     * 关闭支付订单
     *
     * @param paymentOrder 支付订单
     * @param channelConfig 渠道配置
     * @return 关闭结果
     * @throws Exception 关闭异常
     */
    PaymentCloseResult closePayment(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception;

}
