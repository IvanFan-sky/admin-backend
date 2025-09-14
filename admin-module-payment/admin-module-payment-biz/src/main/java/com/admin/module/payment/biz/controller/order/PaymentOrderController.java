package com.admin.module.payment.biz.controller.order;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.common.annotation.OperationLog;
import org.springframework.security.access.prepost.PreAuthorize;
import com.admin.module.payment.api.dto.order.PaymentOrderCreateDTO;
import com.admin.module.payment.api.dto.order.PaymentOrderQueryDTO;
import com.admin.module.payment.api.service.order.PaymentOrderService;
import com.admin.module.payment.api.vo.order.PaymentOrderCreateVO;
import com.admin.module.payment.api.vo.order.PaymentOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 支付订单 Controller
 *
 * @author admin
 * @since 2025/09/14
 */
@Tag(name = "支付订单管理")
@RestController
@RequestMapping("/payment/order")
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    @Operation(summary = "创建支付订单")
    @PostMapping("/create")
    @OperationLog(title = "创建支付订单")
    public R<PaymentOrderCreateVO> createPaymentOrder(@Valid @RequestBody PaymentOrderCreateDTO createDTO) {
        PaymentOrderCreateVO result = paymentOrderService.createPaymentOrder(createDTO);
        return R.ok(result);
    }

    @Operation(summary = "根据订单号获取支付订单")
    @GetMapping("/get-by-order-no")
    @Parameter(name = "orderNo", description = "订单号", required = true)
    public R<PaymentOrderVO> getPaymentOrderByOrderNo(@RequestParam("orderNo") String orderNo) {
        PaymentOrderVO result = paymentOrderService.getPaymentOrderByOrderNo(orderNo);
        return R.ok(result);
    }

    @Operation(summary = "根据商户订单号获取支付订单")
    @GetMapping("/get-by-merchant-order-no")
    @Parameter(name = "merchantOrderNo", description = "商户订单号", required = true)
    public R<PaymentOrderVO> getPaymentOrderByMerchantOrderNo(@RequestParam("merchantOrderNo") String merchantOrderNo) {
        PaymentOrderVO result = paymentOrderService.getPaymentOrderByMerchantOrderNo(merchantOrderNo);
        return R.ok(result);
    }

    @Operation(summary = "分页查询支付订单")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('payment:order:query')")
    public R<PageResult<PaymentOrderVO>> getPaymentOrderPage(@Valid PaymentOrderQueryDTO queryDTO) {
        PageResult<PaymentOrderVO> result = paymentOrderService.getPaymentOrderPage(queryDTO);
        return R.ok(result);
    }

    @Operation(summary = "关闭支付订单")
    @PostMapping("/close")
    @Parameter(name = "orderNo", description = "订单号", required = true)
    @PreAuthorize("@ss.hasPermission('payment:order:close')")
    @OperationLog(title = "关闭支付订单")
    public R<Void> closePaymentOrder(@RequestParam("orderNo") String orderNo) {
        paymentOrderService.closePaymentOrder(orderNo);
        return R.ok();
    }

    @Operation(summary = "同步支付订单状态")
    @PostMapping("/sync-status")
    @Parameter(name = "orderNo", description = "订单号", required = true)
    @PreAuthorize("@ss.hasPermission('payment:order:sync')")
    @OperationLog(title = "同步支付订单状态")
    public R<Boolean> syncPaymentOrderStatus(@RequestParam("orderNo") String orderNo) {
        boolean result = paymentOrderService.syncPaymentOrderStatus(orderNo);
        return R.ok(result);
    }

}
