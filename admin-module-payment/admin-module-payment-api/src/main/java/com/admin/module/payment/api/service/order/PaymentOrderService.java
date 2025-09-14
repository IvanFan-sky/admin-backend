package com.admin.module.payment.api.service.order;

import com.admin.common.core.domain.PageResult;
import com.admin.module.payment.api.dto.order.PaymentOrderCreateDTO;
import com.admin.module.payment.api.dto.order.PaymentOrderQueryDTO;
import com.admin.module.payment.api.vo.order.PaymentOrderCreateVO;
import com.admin.module.payment.api.vo.order.PaymentOrderVO;

/**
 * 支付订单服务接口
 *
 * @author admin
 * @since 2025/09/14
 */
public interface PaymentOrderService {

    /**
     * 创建支付订单
     *
     * @param createDTO 创建参数
     * @return 创建结果
     */
    PaymentOrderCreateVO createPaymentOrder(PaymentOrderCreateDTO createDTO);

    /**
     * 根据订单号获取支付订单
     *
     * @param orderNo 订单号
     * @return 支付订单
     */
    PaymentOrderVO getPaymentOrderByOrderNo(String orderNo);

    /**
     * 根据商户订单号获取支付订单
     *
     * @param merchantOrderNo 商户订单号
     * @return 支付订单
     */
    PaymentOrderVO getPaymentOrderByMerchantOrderNo(String merchantOrderNo);

    /**
     * 分页查询支付订单
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    PageResult<PaymentOrderVO> getPaymentOrderPage(PaymentOrderQueryDTO queryDTO);

    /**
     * 关闭支付订单
     *
     * @param orderNo 订单号
     */
    void closePaymentOrder(String orderNo);

    /**
     * 同步支付订单状态
     *
     * @param orderNo 订单号
     * @return 是否同步成功
     */
    boolean syncPaymentOrderStatus(String orderNo);

}
