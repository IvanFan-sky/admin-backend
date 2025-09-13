-- =============================================
-- Admin管理系统 - 数据库建表脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `admin_dev` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `admin_dev`;

-- =============================================
-- 用户管理模块
-- =============================================

-- 用户表
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `password_update_time` datetime DEFAULT NULL COMMENT '密码更新时间',
  `login_fail_count` int DEFAULT '0' COMMENT '登录失败次数',
  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_email` (`email`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  KEY `idx_sys_user_status` (`status`),
  KEY `idx_sys_user_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 权限管理模块
-- =============================================

-- 角色表
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_desc` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`),
  KEY `idx_sys_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 菜单表
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint NOT NULL COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `visible` tinyint DEFAULT '1' COMMENT '菜单状态：0-隐藏，1-显示',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `is_frame` tinyint DEFAULT '0' COMMENT '是否为外链：0-否，1-是',
  `is_cache` tinyint DEFAULT '0' COMMENT '是否缓存：0-不缓存，1-缓存',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`),
  KEY `idx_sys_menu_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role` (`user_id`,`role_id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_menu` (`role_id`,`menu_id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- =============================================
-- 系统管理模块
-- =============================================

-- 字典类型表
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_data_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- 参数配置表
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) NOT NULL COMMENT '参数名称',
  `config_key` varchar(100) NOT NULL COMMENT '参数键名',
  `config_value` varchar(500) NOT NULL COMMENT '参数键值',
  `config_type` tinyint DEFAULT '0' COMMENT '系统内置：0-否，1-是',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置表';


-- =============================================
-- 通知管理模块
-- =============================================

-- 通知公告表
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(200) NOT NULL COMMENT '公告标题',
  `notice_type` tinyint NOT NULL COMMENT '公告类型：1-通知，2-公告',
  `notice_content` text COMMENT '公告内容',
  `status` tinyint DEFAULT '1' COMMENT '公告状态：0-关闭，1-正常',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `read_count` int DEFAULT '0' COMMENT '阅读次数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_notice_status` (`status`),
  KEY `idx_sys_notice_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- 用户通知表
CREATE TABLE `sys_user_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_notice` (`user_id`,`notice_id`),
  KEY `idx_sys_user_notice_user_id` (`user_id`),
  KEY `idx_sys_user_notice_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知表';

-- =============================================
-- 日志管理模块
-- =============================================

-- 操作日志表
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT NULL COMMENT '模块标题',
  `business_type` tinyint DEFAULT '0' COMMENT '业务类型：0-其它，1-新增，2-修改，3-删除，4-授权，5-导出，6-导入，7-强退，8-生成代码，9-清空数据',
  `method` varchar(100) DEFAULT NULL COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式',
  `operator_type` tinyint DEFAULT '0' COMMENT '操作类别：0-其它，1-后台用户，2-手机端用户',
  `oper_name` varchar(50) DEFAULT NULL COMMENT '操作人员',
  `oper_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `oper_ip` varchar(50) DEFAULT NULL COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT NULL COMMENT '操作地点',
  `oper_param` text COMMENT '请求参数',
  `json_result` text COMMENT '返回参数',
  `status` tinyint DEFAULT '0' COMMENT '操作状态：0-正常，1-异常',
  `error_msg` varchar(2000) DEFAULT NULL COMMENT '错误消息',
  `oper_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_oper_log_business_type` (`business_type`),
  KEY `idx_sys_oper_log_status` (`status`),
  KEY `idx_sys_oper_log_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志记录';

-- 登录日志表
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户账号',
  `login_type` tinyint DEFAULT '1' COMMENT '登录类型：1-用户名密码，2-邮箱密码，3-手机验证码，4-第三方登录',
  `ipaddr` varchar(50) DEFAULT NULL COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `status` tinyint DEFAULT '0' COMMENT '登录状态：0-成功，1-失败',
  `msg` varchar(255) DEFAULT NULL COMMENT '提示消息',
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_login_log_status` (`status`),
  KEY `idx_sys_login_log_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统访问记录';

-- =============================================
-- 文件管理模块
-- =============================================

-- 文件信息表
CREATE TABLE `infra_file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `original_file_name` varchar(255) NOT NULL COMMENT '文件原始名称',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `content_type` varchar(100) NOT NULL COMMENT '文件类型（MIME类型）',
  `file_extension` varchar(20) DEFAULT NULL COMMENT '文件扩展名',
  `file_hash` varchar(64) NOT NULL COMMENT '文件MD5哈希值',
  `storage_type` varchar(20) NOT NULL DEFAULT 'MINIO' COMMENT '存储类型（MINIO, OSS, LOCAL等）',
  `bucket_name` varchar(100) NOT NULL COMMENT '存储桶名称',
  `upload_status` tinyint NOT NULL DEFAULT '0' COMMENT '上传状态：0-上传中，1-上传完成，2-上传失败，3-已删除',
  `is_chunked` tinyint DEFAULT '0' COMMENT '是否为分片上传：0-否，1-是',
  `total_chunks` int DEFAULT NULL COMMENT '总分片数',
  `upload_id` varchar(100) DEFAULT NULL COMMENT '上传会话ID（分片上传使用）',
  `access_url` varchar(500) DEFAULT NULL COMMENT '访问URL',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
  `business_id` varchar(100) DEFAULT NULL COMMENT '业务ID',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` varchar(50) DEFAULT NULL COMMENT '上传用户名',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `last_download_time` datetime DEFAULT NULL COMMENT '最后下载时间',
  `tags` varchar(200) DEFAULT NULL COMMENT '文件标签',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_infra_file_hash` (`file_hash`),
  KEY `idx_infra_file_business` (`business_type`, `business_id`),
  KEY `idx_infra_file_upload_user` (`upload_user_id`),
  KEY `idx_infra_file_upload_status` (`upload_status`),
  KEY `idx_infra_file_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 文件分片信息表
CREATE TABLE `infra_file_chunk` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分片ID',
  `file_id` bigint DEFAULT NULL COMMENT '关联文件ID',
  `upload_id` varchar(100) NOT NULL COMMENT '上传会话ID',
  `chunk_number` int NOT NULL COMMENT '分片序号（从1开始）',
  `chunk_size` bigint NOT NULL COMMENT '分片大小（字节）',
  `chunk_hash` varchar(64) DEFAULT NULL COMMENT '分片MD5哈希值',
  `etag` varchar(100) DEFAULT NULL COMMENT '分片ETag（MinIO返回的标识）',
  `upload_status` tinyint NOT NULL DEFAULT '0' COMMENT '上传状态：0-未上传，1-上传完成，2-上传失败',
  `storage_path` varchar(500) DEFAULT NULL COMMENT '存储路径',
  `retry_count` int DEFAULT '0' COMMENT '重试次数',
  `upload_start_time` datetime DEFAULT NULL COMMENT '上传开始时间',
  `upload_end_time` datetime DEFAULT NULL COMMENT '上传完成时间',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_infra_chunk_upload` (`upload_id`, `chunk_number`),
  KEY `idx_infra_chunk_file_id` (`file_id`),
  KEY `idx_infra_chunk_upload_id` (`upload_id`),
  KEY `idx_infra_chunk_upload_status` (`upload_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分片信息表';

-- 导入导出任务表
CREATE TABLE `infra_import_export_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `task_type` varchar(20) NOT NULL COMMENT '任务类型：IMPORT-导入，EXPORT-导出',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型：USER-用户，ROLE-角色，LOG-日志等',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，FAILED-失败，CANCELLED-已取消',
  `progress_percent` int DEFAULT '0' COMMENT '进度百分比（0-100）',
  `current_operation` varchar(200) DEFAULT NULL COMMENT '当前操作描述',
  `source_file_id` bigint DEFAULT NULL COMMENT '源文件ID（导入时使用）',
  `result_file_id` bigint DEFAULT NULL COMMENT '结果文件ID（导出时使用）',
  `error_file_id` bigint DEFAULT NULL COMMENT '错误文件ID（导入失败时的错误详情文件）',
  `total_count` int DEFAULT '0' COMMENT '处理总数',
  `success_count` int DEFAULT '0' COMMENT '成功数量',
  `failure_count` int DEFAULT '0' COMMENT '失败数量',
  `skip_count` int DEFAULT '0' COMMENT '跳过数量',
  `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `execution_time` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `result_summary` text COMMENT '结果摘要',
  `task_params` text COMMENT '任务参数（JSON格式）',
  `error_message` varchar(1000) DEFAULT NULL COMMENT '错误信息',
  `execute_user_id` bigint DEFAULT NULL COMMENT '执行用户ID',
  `execute_user_name` varchar(50) DEFAULT NULL COMMENT '执行用户名',
  `allow_partial_failure` tinyint DEFAULT '1' COMMENT '是否允许部分失败：0-否，1-是',
  `priority` int DEFAULT '3' COMMENT '优先级（1-5，数字越大优先级越高）',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_infra_task_type` (`task_type`),
  KEY `idx_infra_task_business_type` (`business_type`),
  KEY `idx_infra_task_status` (`status`),
  KEY `idx_infra_task_execute_user` (`execute_user_id`),
  KEY `idx_infra_task_create_time` (`create_time`),
  KEY `idx_infra_task_priority_status` (`priority`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出任务表';

-- 导入错误详情表
CREATE TABLE `infra_import_error_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '错误详情ID',
  `task_id` bigint NOT NULL COMMENT '关联任务ID',
  `row_number` int NOT NULL COMMENT '错误行号',
  `column_name` varchar(100) DEFAULT NULL COMMENT '错误列名',
  `column_value` varchar(500) DEFAULT NULL COMMENT '错误列值',
  `error_type` varchar(50) NOT NULL COMMENT '错误类型：VALIDATION-校验错误，DUPLICATE-重复数据，CONSTRAINT-约束错误，BUSINESS-业务错误',
  `error_code` varchar(50) DEFAULT NULL COMMENT '错误代码',
  `error_message` varchar(1000) NOT NULL COMMENT '错误信息',
  `original_data` text COMMENT '原始数据（JSON格式）',
  `suggestion` varchar(500) DEFAULT NULL COMMENT '建议修复方案',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_infra_error_task_id` (`task_id`),
  KEY `idx_infra_error_type` (`error_type`),
  KEY `idx_infra_error_task_row` (`task_id`, `row_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入错误详情表';

-- 添加外键约束
ALTER TABLE `infra_file_chunk` ADD CONSTRAINT `fk_infra_chunk_file_id` FOREIGN KEY (`file_id`) REFERENCES `infra_file_info` (`id`) ON DELETE CASCADE;
ALTER TABLE `infra_import_export_task` ADD CONSTRAINT `fk_infra_task_source_file` FOREIGN KEY (`source_file_id`) REFERENCES `infra_file_info` (`id`) ON DELETE SET NULL;
ALTER TABLE `infra_import_export_task` ADD CONSTRAINT `fk_infra_task_result_file` FOREIGN KEY (`result_file_id`) REFERENCES `infra_file_info` (`id`) ON DELETE SET NULL;
ALTER TABLE `infra_import_export_task` ADD CONSTRAINT `fk_infra_task_error_file` FOREIGN KEY (`error_file_id`) REFERENCES `infra_file_info` (`id`) ON DELETE SET NULL;
ALTER TABLE `infra_import_error_detail` ADD CONSTRAINT `fk_infra_error_task` FOREIGN KEY (`task_id`) REFERENCES `infra_import_export_task` (`id`) ON DELETE CASCADE;

