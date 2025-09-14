package com.admin.module.payment.biz.service.channel;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.payment.biz.channel.core.PaymentChannelConfig;
import com.admin.module.payment.biz.dal.dataobject.PaymentChannelConfigDO;
import com.admin.module.payment.biz.dal.mapper.PaymentChannelConfigMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付渠道配置服务实现
 *
 * @author admin
 * @since 2025/09/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentChannelConfigServiceImpl implements PaymentChannelConfigService {

    private final PaymentChannelConfigMapper paymentChannelConfigMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentChannelConfig getChannelConfig(String channelCode) {
        PaymentChannelConfigDO configDO = paymentChannelConfigMapper.selectByChannelCode(channelCode);
        if (configDO == null) {
            throw new ServiceException(ErrorCode.PAYMENT_CHANNEL_CONFIG_NOT_FOUND);
        }

        PaymentChannelConfig config = new PaymentChannelConfig();
        config.setChannelCode(configDO.getChannelCode());
        config.setChannelName(configDO.getChannelName());
        config.setEnabled(configDO.getStatus() == 1);

        // 解析JSON配置
        try {
            if (configDO.getConfig() != null) {
                Map<String, Object> configMap = objectMapper.readValue(
                    configDO.getConfig(), 
                    new TypeReference<Map<String, Object>>() {}
                );
                config.setConfig(configMap);
            }
        } catch (Exception e) {
            log.error("解析支付渠道配置失败，渠道：{}", channelCode, e);
            throw new ServiceException(ErrorCode.SYSTEM_ERROR, "支付渠道配置解析失败");
        }

        return config;
    }

    @Override
    public boolean isChannelEnabled(String channelCode) {
        try {
            PaymentChannelConfig config = getChannelConfig(channelCode);
            return config.getEnabled();
        } catch (ServiceException e) {
            if (ErrorCode.PAYMENT_CHANNEL_CONFIG_NOT_FOUND.getCode().equals(e.getCode())) {
                return false;
            }
            throw e;
        }
    }

}
