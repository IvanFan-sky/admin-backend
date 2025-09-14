package com.admin.module.payment.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 支付渠道配置数据对象
 *
 * @author admin
 * @since 2025/09/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("payment_channel_config")
@KeySequence("payment_channel_config_seq")
public class PaymentChannelConfigDO extends BaseEntity {

    /**
     * 配置ID
     */
    @TableId
    private Long id;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 配置信息
     */
    private String config;

    /**
     * 备注
     */
    private String remark;

}
