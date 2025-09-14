package com.admin.module.payment.biz.channel.mock;

import com.admin.module.payment.api.enums.PaymentChannelEnum;
import com.admin.module.payment.api.enums.PaymentMethodEnum;
import com.admin.module.payment.api.enums.PaymentOrderStatusEnum;
import com.admin.module.payment.biz.channel.core.*;
import com.admin.module.payment.biz.dal.dataobject.PaymentOrderDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 模拟支付渠道服务
 * 
 * 开发阶段使用，模拟真实的支付流程
 *
 * @author admin
 * @since 2025/09/14
 */
@Slf4j
@Service
public class MockPaymentChannelService implements PaymentChannelService {

    @Override
    public String getChannelCode() {
        return PaymentChannelEnum.MOCK.getCode();
    }

    @Override
    public String getChannelName() {
        return PaymentChannelEnum.MOCK.getName();
    }

    @Override
    public boolean isSupport(String paymentMethod) {
        return PaymentMethodEnum.MOCK.getCode().equals(paymentMethod);
    }

    @Override
    public String preCheck(PaymentOrderDO paymentOrder) {
        // 模拟支付无需特殊检查
        if (paymentOrder.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return "支付金额必须大于0";
        }
        return null;
    }

    @Override
    public PaymentResult pay(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception {
        log.info("模拟支付开始，订单号：{}, 金额：{}", paymentOrder.getOrderNo(), paymentOrder.getAmount());

        // 模拟支付处理延迟
        String delaySecondsStr = channelConfig.getConfigValue("delay_seconds", "2");
        int delaySeconds = Integer.parseInt(delaySecondsStr);
        if (delaySeconds > 0) {
            Thread.sleep(delaySeconds * 1000L);
        }

        // 模拟成功率
        String successRateStr = channelConfig.getConfigValue("success_rate", "100");
        int successRate = Integer.parseInt(successRateStr);
        boolean success = Math.random() * 100 < successRate;

        if (success) {
            // 生成模拟的渠道订单号
            String channelOrderNo = "MOCK_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 构建支付参数（模拟微信扫码支付的返回格式）
            String payParams = String.format(
                "{\"code_url\":\"weixin://wxpay/bizpayurl?pr=%s\",\"prepay_id\":\"%s\"}", 
                channelOrderNo, 
                "wx" + System.currentTimeMillis()
            );

            log.info("模拟支付成功，订单号：{}, 渠道订单号：{}", paymentOrder.getOrderNo(), channelOrderNo);

            return PaymentResult.builder()
                    .success(true)
                    .channelOrderNo(channelOrderNo)
                    .payParams(payParams)
                    .qrCode("weixin://wxpay/bizpayurl?pr=" + channelOrderNo)
                    .build();
        } else {
            log.warn("模拟支付失败，订单号：{}", paymentOrder.getOrderNo());
            return PaymentResult.failure("MOCK_PAY_FAILED", "模拟支付失败");
        }
    }

    @Override
    public PaymentQueryResult queryPayment(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception {
        log.info("模拟支付查询，订单号：{}", paymentOrder.getOrderNo());

        // 模拟查询延迟
        Thread.sleep(500);

        // 如果订单已经有渠道订单号，表示支付成功
        if (paymentOrder.getChannelOrderNo() != null) {
            return PaymentQueryResult.builder()
                    .success(true)
                    .orderStatus(PaymentOrderStatusEnum.SUCCESS.getStatus())
                    .channelOrderNo(paymentOrder.getChannelOrderNo())
                    .amount(paymentOrder.getAmount())
                    .payTime(LocalDateTime.now())
                    .build();
        } else {
            // 模拟查询结果：80%概率支付成功
            boolean paySuccess = Math.random() < 0.8;
            if (paySuccess) {
                String channelOrderNo = "MOCK_QUERY_" + System.currentTimeMillis();
                return PaymentQueryResult.builder()
                        .success(true)
                        .orderStatus(PaymentOrderStatusEnum.SUCCESS.getStatus())
                        .channelOrderNo(channelOrderNo)
                        .amount(paymentOrder.getAmount())
                        .payTime(LocalDateTime.now())
                        .build();
            } else {
                return PaymentQueryResult.builder()
                        .success(true)
                        .orderStatus(PaymentOrderStatusEnum.WAITING.getStatus())
                        .build();
            }
        }
    }

    @Override
    public PaymentCloseResult closePayment(PaymentOrderDO paymentOrder, PaymentChannelConfig channelConfig) throws Exception {
        log.info("模拟支付关闭，订单号：{}", paymentOrder.getOrderNo());

        // 模拟关闭延迟
        Thread.sleep(300);

        return PaymentCloseResult.success(paymentOrder.getChannelOrderNo());
    }

}
