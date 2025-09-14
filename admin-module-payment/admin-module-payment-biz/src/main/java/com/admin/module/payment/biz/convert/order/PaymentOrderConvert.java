package com.admin.module.payment.biz.convert.order;

import com.admin.common.core.domain.PageResult;
import com.admin.module.payment.api.dto.order.PaymentOrderCreateDTO;
import com.admin.module.payment.api.enums.PaymentChannelEnum;
import com.admin.module.payment.api.enums.PaymentMethodEnum;
import com.admin.module.payment.api.enums.PaymentOrderStatusEnum;
import com.admin.module.payment.api.vo.order.PaymentOrderCreateVO;
import com.admin.module.payment.api.vo.order.PaymentOrderVO;
import com.admin.module.payment.biz.dal.dataobject.PaymentOrderDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 支付订单转换器
 *
 * @author admin
 * @since 2025/09/14
 */
@Mapper
public interface PaymentOrderConvert {

    PaymentOrderConvert INSTANCE = Mappers.getMapper(PaymentOrderConvert.class);

    /**
     * 转换为DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "channelCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "channelOrderNo", ignore = true)
    @Mapping(target = "successTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PaymentOrderDO convert(PaymentOrderCreateDTO createDTO);

    /**
     * 转换为VO
     */
    @Mapping(target = "channelName", expression = "java(getChannelName(paymentOrder.getChannelCode()))")
    @Mapping(target = "paymentMethodName", expression = "java(getPaymentMethodName(paymentOrder.getPaymentMethod()))")
    @Mapping(target = "statusDesc", expression = "java(getStatusDesc(paymentOrder.getStatus()))")
    PaymentOrderVO convert(PaymentOrderDO paymentOrder);

    /**
     * 转换为创建结果VO
     */
    @Mapping(target = "payParams", ignore = true)
    @Mapping(target = "payUrl", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    PaymentOrderCreateVO convertCreateVO(PaymentOrderDO paymentOrder);

    /**
     * 转换列表
     */
    List<PaymentOrderVO> convertList(List<PaymentOrderDO> list);

    /**
     * 转换分页结果
     */
    default PageResult<PaymentOrderVO> convertPage(PageResult<PaymentOrderDO> pageResult) {
        if (pageResult == null) {
            return null;
        }
        PageResult<PaymentOrderVO> result = new PageResult<>();
        result.setRecords(convertList(pageResult.getRecords()));
        result.setTotal(pageResult.getTotal());
        return result;
    }

    /**
     * 获取渠道名称
     */
    default String getChannelName(String channelCode) {
        if (channelCode == null) {
            return null;
        }
        PaymentChannelEnum channel = PaymentChannelEnum.getByCode(channelCode);
        return channel != null ? channel.getName() : channelCode;
    }

    /**
     * 获取支付方式名称
     */
    default String getPaymentMethodName(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        PaymentMethodEnum method = PaymentMethodEnum.getByCode(paymentMethod);
        return method != null ? method.getName() : paymentMethod;
    }

    /**
     * 获取状态描述
     */
    default String getStatusDesc(Integer status) {
        if (status == null) {
            return null;
        }
        PaymentOrderStatusEnum statusEnum = PaymentOrderStatusEnum.getByStatus(status);
        return statusEnum != null ? statusEnum.getDescription() : "未知状态";
    }

}
