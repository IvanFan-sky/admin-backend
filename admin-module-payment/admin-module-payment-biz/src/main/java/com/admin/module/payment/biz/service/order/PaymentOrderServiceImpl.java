package com.admin.module.payment.biz.service.order;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.common.core.domain.PageResult;
import com.admin.common.utils.PageUtils;
import com.admin.module.payment.api.dto.order.PaymentOrderCreateDTO;
import com.admin.module.payment.api.dto.order.PaymentOrderQueryDTO;
import com.admin.module.payment.api.enums.PaymentChannelEnum;
import com.admin.module.payment.api.enums.PaymentMethodEnum;
import com.admin.module.payment.api.enums.PaymentOrderStatusEnum;
import com.admin.module.payment.api.service.order.PaymentOrderService;
import com.admin.module.payment.api.vo.order.PaymentOrderCreateVO;
import com.admin.module.payment.api.vo.order.PaymentOrderVO;
import com.admin.module.payment.biz.channel.core.PaymentChannelConfig;
import com.admin.module.payment.biz.channel.core.PaymentChannelService;
import com.admin.module.payment.biz.channel.core.PaymentQueryResult;
import com.admin.module.payment.biz.channel.core.PaymentResult;
import com.admin.module.payment.biz.convert.order.PaymentOrderConvert;
import com.admin.module.payment.biz.dal.dataobject.PaymentChannelConfigDO;
import com.admin.module.payment.biz.dal.dataobject.PaymentOrderDO;
import com.admin.module.payment.biz.dal.mapper.PaymentChannelConfigMapper;
import com.admin.module.payment.biz.dal.mapper.PaymentOrderMapper;
import com.admin.module.payment.biz.service.channel.PaymentChannelConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付订单服务实现
 *
 * @author admin
 * @since 2025/09/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl implements PaymentOrderService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentChannelConfigMapper paymentChannelConfigMapper;
    private final PaymentChannelConfigService paymentChannelConfigService;
    private final Map<String, PaymentChannelService> paymentChannelServices;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderCreateVO createPaymentOrder(PaymentOrderCreateDTO createDTO) {
        // 1. 参数校验
        validateCreateRequest(createDTO);

        // 2. 检查订单是否已存在
        PaymentOrderDO existingOrder = paymentOrderMapper.selectByMerchantOrderNo(createDTO.getMerchantOrderNo());
        if (existingOrder != null) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_EXISTS);
        }

        // 3. 获取支付方式和渠道
        PaymentMethodEnum paymentMethod = PaymentMethodEnum.getByCode(createDTO.getPaymentMethod());
        if (paymentMethod == null) {
            throw new ServiceException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED);
        }

        PaymentChannelEnum paymentChannel = paymentMethod.getChannel();
        if (paymentChannel == null) {
            throw new ServiceException(ErrorCode.PAYMENT_CHANNEL_NOT_FOUND);
        }

        // 4. 获取支付渠道服务
        PaymentChannelService channelService = paymentChannelServices.get(paymentChannel.getCode() + "PaymentChannelService");
        if (channelService == null) {
            throw new ServiceException(ErrorCode.PAYMENT_CHANNEL_SERVICE_NOT_FOUND);
        }

        // 5. 创建支付订单
        PaymentOrderDO paymentOrder = buildPaymentOrder(createDTO, paymentChannel);
        paymentOrderMapper.insert(paymentOrder);

        // 6. 调用支付渠道
        try {
            PaymentChannelConfig channelConfig = paymentChannelConfigService.getChannelConfig(paymentChannel.getCode());
            PaymentResult paymentResult = channelService.pay(paymentOrder, channelConfig);

            if (paymentResult.getSuccess()) {
                // 更新订单状态为支付中
                paymentOrder.setStatus(PaymentOrderStatusEnum.PAYING.getStatus());
                paymentOrder.setChannelOrderNo(paymentResult.getChannelOrderNo());
                paymentOrderMapper.updateById(paymentOrder);

                // 构建返回结果
                PaymentOrderCreateVO result = PaymentOrderConvert.INSTANCE.convertCreateVO(paymentOrder);
                result.setPayParams(paymentResult.getPayParams());
                result.setPayUrl(paymentResult.getPayUrl());
                result.setQrCode(paymentResult.getQrCode());
                return result;
            } else {
                // 支付失败，更新订单状态
                paymentOrder.setStatus(PaymentOrderStatusEnum.FAILED.getStatus());
                paymentOrderMapper.updateById(paymentOrder);
                throw new ServiceException(ErrorCode.PAYMENT_CREATE_FAILED.getCode(), paymentResult.getErrorMsg());
            }
        } catch (Exception e) {
            log.error("创建支付订单失败，订单号：{}", paymentOrder.getOrderNo(), e);
            // 更新订单状态为失败
            paymentOrder.setStatus(PaymentOrderStatusEnum.FAILED.getStatus());
            paymentOrderMapper.updateById(paymentOrder);
            throw new ServiceException(ErrorCode.PAYMENT_CREATE_FAILED);
        }
    }

    @Override
    public PaymentOrderVO getPaymentOrderByOrderNo(String orderNo) {
        PaymentOrderDO paymentOrder = paymentOrderMapper.selectByOrderNo(orderNo);
        if (paymentOrder == null) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_NOT_FOUND);
        }
        return PaymentOrderConvert.INSTANCE.convert(paymentOrder);
    }

    @Override
    public PaymentOrderVO getPaymentOrderByMerchantOrderNo(String merchantOrderNo) {
        PaymentOrderDO paymentOrder = paymentOrderMapper.selectByMerchantOrderNo(merchantOrderNo);
        if (paymentOrder == null) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_NOT_FOUND);
        }
        return PaymentOrderConvert.INSTANCE.convert(paymentOrder);
    }

    @Override
    public PageResult<PaymentOrderVO> getPaymentOrderPage(PaymentOrderQueryDTO queryDTO) {
        PageResult<PaymentOrderDO> pageResult = paymentOrderMapper.selectPage(queryDTO);
        return PaymentOrderConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePaymentOrder(String orderNo) {
        PaymentOrderDO paymentOrder = paymentOrderMapper.selectByOrderNo(orderNo);
        if (paymentOrder == null) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_NOT_FOUND);
        }

        // 只有待支付和支付中的订单才能关闭
        if (!PaymentOrderStatusEnum.WAITING.getStatus().equals(paymentOrder.getStatus()) &&
            !PaymentOrderStatusEnum.PAYING.getStatus().equals(paymentOrder.getStatus())) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_STATUS_ERROR);
        }

        // 更新订单状态为已关闭
        paymentOrderMapper.updateStatus(orderNo, PaymentOrderStatusEnum.CLOSED.getStatus(), "system");
    }

    @Override
    public boolean syncPaymentOrderStatus(String orderNo) {
        PaymentOrderDO paymentOrder = paymentOrderMapper.selectByOrderNo(orderNo);
        if (paymentOrder == null) {
            return false;
        }

        // 只同步非最终状态的订单
        PaymentOrderStatusEnum currentStatus = PaymentOrderStatusEnum.getByStatus(paymentOrder.getStatus());
        if (currentStatus != null && currentStatus.isFinalStatus()) {
            return true;
        }

        try {
            // 获取支付渠道服务
            PaymentChannelEnum paymentChannel = PaymentChannelEnum.getByCode(paymentOrder.getChannelCode());
            if (paymentChannel == null) {
                return false;
            }

            PaymentChannelService channelService = paymentChannelServices.get(paymentChannel.getCode() + "PaymentChannelService");
            if (channelService == null) {
                return false;
            }

            // 查询支付状态
            PaymentChannelConfig channelConfig = paymentChannelConfigService.getChannelConfig(paymentChannel.getCode());
            PaymentQueryResult queryResult = channelService.queryPayment(paymentOrder, channelConfig);

            if (queryResult.getSuccess()) {
                // 根据查询结果更新订单状态
                if (PaymentOrderStatusEnum.SUCCESS.getStatus().equals(queryResult.getOrderStatus())) {
                    paymentOrderMapper.updatePaymentSuccess(
                            orderNo,
                            queryResult.getChannelOrderNo(),
                            queryResult.getPayTime() != null ? queryResult.getPayTime() : LocalDateTime.now(),
                            "system"
                    );
                } else if (PaymentOrderStatusEnum.FAILED.getStatus().equals(queryResult.getOrderStatus())) {
                    paymentOrderMapper.updateStatus(orderNo, PaymentOrderStatusEnum.FAILED.getStatus(), "system");
                }
                return true;
            }
        } catch (Exception e) {
            log.error("同步支付订单状态失败，订单号：{}", orderNo, e);
        }

        return false;
    }

    /**
     * 校验创建请求参数
     */
    private void validateCreateRequest(PaymentOrderCreateDTO createDTO) {
        if (createDTO.getExpireTime() != null && createDTO.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ErrorCode.PAYMENT_ORDER_EXPIRED);
        }
    }

    /**
     * 构建支付订单
     */
    private PaymentOrderDO buildPaymentOrder(PaymentOrderCreateDTO createDTO, PaymentChannelEnum paymentChannel) {
        PaymentOrderDO paymentOrder = PaymentOrderConvert.INSTANCE.convert(createDTO);
        
        // 生成订单号
        paymentOrder.setOrderNo(generateOrderNo());
        
        // 设置渠道信息
        paymentOrder.setChannelCode(paymentChannel.getCode());
        
        // 设置初始状态
        paymentOrder.setStatus(PaymentOrderStatusEnum.WAITING.getStatus());
        
        // 设置默认过期时间（30分钟）
        if (paymentOrder.getExpireTime() == null) {
            paymentOrder.setExpireTime(LocalDateTime.now().plusMinutes(30));
        }
        
        return paymentOrder;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "PAY" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

}
