package com.admin.module.payment.biz.dal.mapper;

import com.admin.common.core.domain.PageResult;
import com.admin.module.payment.api.dto.order.PaymentOrderQueryDTO;
import com.admin.module.payment.biz.dal.dataobject.PaymentOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付订单 Mapper
 *
 * @author admin
 * @since 2025/09/14
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrderDO> {

    /**
     * 根据订单号查询支付订单
     *
     * @param orderNo 订单号
     * @return 支付订单
     */
    PaymentOrderDO selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据商户订单号查询支付订单
     *
     * @param merchantOrderNo 商户订单号
     * @return 支付订单
     */
    PaymentOrderDO selectByMerchantOrderNo(@Param("merchantOrderNo") String merchantOrderNo);

    /**
     * 分页查询支付订单
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    PageResult<PaymentOrderDO> selectPage(@Param("query") PaymentOrderQueryDTO queryDTO);

    /**
     * 查询过期的待支付订单
     *
     * @param expireTime 过期时间
     * @param limit 限制数量
     * @return 过期订单列表
     */
    List<PaymentOrderDO> selectExpiredOrders(@Param("expireTime") LocalDateTime expireTime,
                                             @Param("limit") Integer limit);

    /**
     * 批量更新订单状态为已关闭
     *
     * @param orderIds 订单ID列表
     * @param updater 更新者
     * @return 更新数量
     */
    int batchUpdateStatusToClosed(@Param("orderIds") List<Long> orderIds,
                                  @Param("updater") String updater);

    /**
     * 更新订单支付成功信息
     *
     * @param orderNo 订单号
     * @param channelOrderNo 渠道订单号
     * @param successTime 支付成功时间
     * @param updater 更新者
     * @return 更新数量
     */
    int updatePaymentSuccess(@Param("orderNo") String orderNo,
                             @Param("channelOrderNo") String channelOrderNo,
                             @Param("successTime") LocalDateTime successTime,
                             @Param("updater") String updater);

    /**
     * 更新订单状态
     *
     * @param orderNo 订单号
     * @param status 新状态
     * @param updater 更新者
     * @return 更新数量
     */
    int updateStatus(@Param("orderNo") String orderNo,
                     @Param("status") Integer status,
                     @Param("updater") String updater);

}
