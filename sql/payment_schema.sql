-- ===========================
-- 支付模块数据库表结构
-- ===========================

-- 支付订单表
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    `merchant_order_no` VARCHAR(64) NOT NULL COMMENT '商户订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `channel_code` VARCHAR(32) NOT NULL COMMENT '支付渠道编码',
    `payment_method` VARCHAR(32) NOT NULL COMMENT '支付方式',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `currency` VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    `subject` VARCHAR(256) NOT NULL COMMENT '订单标题',
    `body` VARCHAR(512) COMMENT '订单描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态 0-待支付 1-支付中 2-支付成功 3-支付失败 4-已关闭 5-已退款',
    `channel_order_no` VARCHAR(64) COMMENT '渠道订单号',
    `success_time` DATETIME COMMENT '支付成功时间',
    `expire_time` DATETIME COMMENT '过期时间',
    `notify_url` VARCHAR(512) COMMENT '异步通知地址',
    `return_url` VARCHAR(512) COMMENT '同步跳转地址',
    `extra_data` JSON COMMENT '扩展数据',
    `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_merchant_order_no` (`merchant_order_no`),
    INDEX `idx_channel_code` (`channel_code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_success_time` (`success_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单表';

-- 支付渠道配置表
DROP TABLE IF EXISTS `payment_channel_config`;
CREATE TABLE `payment_channel_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    `channel_code` VARCHAR(32) NOT NULL COMMENT '渠道编码',
    `channel_name` VARCHAR(64) NOT NULL COMMENT '渠道名称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `config` JSON NOT NULL COMMENT '配置信息',
    `remark` VARCHAR(512) COMMENT '备注',
    `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    UNIQUE KEY `uk_channel_code` (`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付渠道配置表';

-- 退款订单表
DROP TABLE IF EXISTS `refund_order`;
CREATE TABLE `refund_order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '退款ID',
    `refund_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '退款单号',
    `payment_order_id` BIGINT NOT NULL COMMENT '支付订单ID',
    `payment_order_no` VARCHAR(64) NOT NULL COMMENT '支付订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `refund_reason` VARCHAR(256) COMMENT '退款原因',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '退款状态 0-退款中 1-退款成功 2-退款失败',
    `channel_refund_no` VARCHAR(64) COMMENT '渠道退款号',
    `success_time` DATETIME COMMENT '退款成功时间',
    `extra_data` JSON COMMENT '扩展数据',
    `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    INDEX `idx_payment_order_id` (`payment_order_id`),
    INDEX `idx_payment_order_no` (`payment_order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款订单表';

-- 支付回调记录表
DROP TABLE IF EXISTS `payment_notify_log`;
CREATE TABLE `payment_notify_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `channel_code` VARCHAR(32) NOT NULL COMMENT '支付渠道编码',
    `notify_type` TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型 1-支付回调 2-退款回调',
    `notify_data` TEXT NOT NULL COMMENT '通知数据',
    `process_status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态 0-处理中 1-处理成功 2-处理失败',
    `process_result` VARCHAR(512) COMMENT '处理结果',
    `process_time` DATETIME COMMENT '处理时间',
    `retry_count` INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_channel_code` (`channel_code`),
    INDEX `idx_notify_type` (`notify_type`),
    INDEX `idx_process_status` (`process_status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付回调记录表';

-- 初始化支付渠道配置数据
INSERT INTO `payment_channel_config` (`channel_code`, `channel_name`, `status`, `config`, `remark`) VALUES
('mock', '模拟支付', 1, '{"success_rate": 100, "delay_seconds": 2}', '开发环境模拟支付渠道'),
('wechat_pay', '微信支付', 0, '{"app_id": "", "mch_id": "", "api_key": "", "cert_path": ""}', '微信支付渠道配置'),
('alipay', '支付宝', 0, '{"app_id": "", "private_key": "", "public_key": "", "sign_type": "RSA2"}', '支付宝支付渠道配置');
