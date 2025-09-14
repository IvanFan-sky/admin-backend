-- =============================================
-- 通知管理模块 - 数据库建表脚本
-- =============================================

-- 通知类型表
CREATE TABLE `sys_notification_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知类型ID',
  `type_code` varchar(50) NOT NULL COMMENT '类型编码',
  `type_name` varchar(100) NOT NULL COMMENT '类型名称',
  `description` varchar(500) DEFAULT NULL COMMENT '类型描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `color` varchar(20) DEFAULT NULL COMMENT '颜色',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_notification_type_code` (`type_code`),
  KEY `idx_sys_notification_type_status` (`status`),
  KEY `idx_sys_notification_type_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知类型表';

-- 通知表
CREATE TABLE `sys_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `type_id` bigint NOT NULL COMMENT '通知类型ID',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `summary` varchar(500) DEFAULT NULL COMMENT '通知摘要',
  `level` tinyint DEFAULT '1' COMMENT '通知级别：1-普通，2-重要，3-紧急',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-全部用户，2-指定用户，3-指定角色',
  `target_ids` text COMMENT '目标ID列表（JSON格式）',
  `publish_type` tinyint DEFAULT '1' COMMENT '发布类型：1-立即发布，2-定时发布',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-草稿，1-已发布，2-已撤回',
  `read_count` int DEFAULT '0' COMMENT '已读数量',
  `total_count` int DEFAULT '0' COMMENT '总推送数量',
  `extra_data` json DEFAULT NULL COMMENT '扩展数据',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_notification_type_id` (`type_id`),
  KEY `idx_sys_notification_status` (`status`),
  KEY `idx_sys_notification_publish_time` (`publish_time`),
  KEY `idx_sys_notification_expire_time` (`expire_time`),
  KEY `idx_sys_notification_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 用户通知关联表
CREATE TABLE `sys_user_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `notification_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_status` tinyint DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `push_status` tinyint DEFAULT '0' COMMENT '推送状态：0-未推送，1-已推送，2-推送失败',
  `push_time` datetime DEFAULT NULL COMMENT '推送时间',
  `push_channel` varchar(50) DEFAULT NULL COMMENT '推送渠道',
  `push_result` varchar(500) DEFAULT NULL COMMENT '推送结果',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_notification` (`notification_id`, `user_id`),
  KEY `idx_sys_user_notification_user_id` (`user_id`),
  KEY `idx_sys_user_notification_read_status` (`read_status`),
  KEY `idx_sys_user_notification_push_status` (`push_status`),
  KEY `idx_sys_user_notification_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知关联表';

-- 添加外键约束
ALTER TABLE `sys_notification` ADD CONSTRAINT `fk_sys_notification_type` FOREIGN KEY (`type_id`) REFERENCES `sys_notification_type` (`id`) ON DELETE RESTRICT;
ALTER TABLE `sys_user_notification` ADD CONSTRAINT `fk_sys_user_notification_notification` FOREIGN KEY (`notification_id`) REFERENCES `sys_notification` (`id`) ON DELETE CASCADE;
ALTER TABLE `sys_user_notification` ADD CONSTRAINT `fk_sys_user_notification_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE;

-- 初始化通知类型数据
INSERT INTO `sys_notification_type` (`type_code`, `type_name`, `description`, `icon`, `color`, `sort`, `status`, `create_by`) VALUES
('SYSTEM_ANNOUNCEMENT', '系统公告', '系统级别的重要公告通知', 'announcement', '#1890ff', 1, 1, 'system'),
('PRIVATE_MESSAGE', '站内信', '用户之间的私信通知', 'message', '#52c41a', 2, 1, 'system'),
('SYSTEM_NOTICE', '系统通知', '系统操作相关的通知', 'notification', '#faad14', 3, 1, 'system'),
('SECURITY_ALERT', '安全提醒', '账户安全相关的提醒', 'security-scan', '#f5222d', 4, 1, 'system');