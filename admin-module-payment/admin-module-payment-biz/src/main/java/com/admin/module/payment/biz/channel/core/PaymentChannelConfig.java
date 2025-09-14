package com.admin.module.payment.biz.channel.core;

import lombok.Data;

import java.util.Map;

/**
 * 支付渠道配置
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
public class PaymentChannelConfig {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 配置参数
     */
    private Map<String, Object> config;

    /**
     * 获取配置参数
     *
     * @param key 参数名
     * @return 参数值
     */
    public String getConfigValue(String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取配置参数（带默认值）
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }

}
