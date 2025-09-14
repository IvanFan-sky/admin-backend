package com.admin.module.payment.biz.dal.mapper;

import com.admin.module.payment.biz.dal.dataobject.PaymentChannelConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付渠道配置 Mapper
 *
 * @author admin
 * @since 2025/09/14
 */
@Mapper
public interface PaymentChannelConfigMapper extends BaseMapper<PaymentChannelConfigDO> {

    /**
     * 根据渠道编码查询配置
     *
     * @param channelCode 渠道编码
     * @return 渠道配置
     */
    PaymentChannelConfigDO selectByChannelCode(@Param("channelCode") String channelCode);

    /**
     * 查询启用的渠道配置列表
     *
     * @return 启用的渠道配置列表
     */
    List<PaymentChannelConfigDO> selectEnabledChannels();

}
