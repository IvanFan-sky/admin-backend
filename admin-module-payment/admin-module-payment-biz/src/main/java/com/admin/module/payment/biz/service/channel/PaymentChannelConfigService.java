package com.admin.module.payment.biz.service.channel;

import com.admin.module.payment.biz.channel.core.PaymentChannelConfig;

/**
 * 支付渠道配置服务接口
 *
 * @author admin
 * @since 2025/09/14
 */
public interface PaymentChannelConfigService {

    /**
     * 获取渠道配置
     *
     * @param channelCode 渠道编码
     * @return 渠道配置
     */
    PaymentChannelConfig getChannelConfig(String channelCode);

    /**
     * 检查渠道是否启用
     *
     * @param channelCode 渠道编码
     * @return 是否启用
     */
    boolean isChannelEnabled(String channelCode);

}
