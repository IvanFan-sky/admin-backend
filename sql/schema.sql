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
-- 文件管理模块
-- =============================================

-- 文件信息表
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) NOT NULL COMMENT '文件类型',
  `file_suffix` varchar(20) NOT NULL COMMENT '文件后缀',
  `storage_type` tinyint DEFAULT '1' COMMENT '存储类型：1-本地，2-MinIO，3-OSS',
  `bucket_name` varchar(100) DEFAULT NULL COMMENT '存储桶名称',
  `md5` varchar(32) DEFAULT NULL COMMENT '文件MD5',
  `upload_by` varchar(50) DEFAULT NULL COMMENT '上传者',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_file_md5` (`md5`),
  KEY `idx_sys_file_upload_by` (`upload_by`),
  KEY `idx_sys_file_upload_time` (`upload_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

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
-- 导入导出管理模块
-- =============================================

-- 导入导出任务表
CREATE TABLE `sys_import_export_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `task_type` tinyint NOT NULL COMMENT '任务类型：1-导入，2-导出',
  `data_type` varchar(50) NOT NULL COMMENT '数据类型：user, role, operation_log',
  `file_format` varchar(20) NOT NULL COMMENT '文件格式：xlsx, xls, csv',
  `status` tinyint DEFAULT '0' COMMENT '任务状态：0-待处理，1-处理中，2-已完成，3-失败',
  `total_count` int DEFAULT '0' COMMENT '总记录数',
  `success_count` int DEFAULT '0' COMMENT '成功记录数',
  `fail_count` int DEFAULT '0' COMMENT '失败记录数',
  `file_path` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID（关联文件管理系统）',
  `result_file_path` varchar(500) DEFAULT NULL COMMENT '结果文件路径',
  `error_message` text COMMENT '错误信息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `progress` decimal(5,2) DEFAULT '0.00' COMMENT '处理进度（百分比）',
  `export_conditions` json DEFAULT NULL COMMENT '导出条件（JSON格式存储查询条件）',
  `selected_fields` json DEFAULT NULL COMMENT '导出字段（JSON格式存储选中的字段）',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_type_status` (`task_type`, `status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_data_type` (`data_type`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出任务表';

-- 导入错误详情表
CREATE TABLE `sys_import_error_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `row_number` int NOT NULL COMMENT '行号',
  `field_name` varchar(100) DEFAULT NULL COMMENT '字段名称',
  `field_value` varchar(500) DEFAULT NULL COMMENT '字段值',
  `error_type` varchar(50) NOT NULL COMMENT '错误类型：FORMAT_ERROR,DUPLICATE_ERROR,VALIDATION_ERROR',
  `error_message` varchar(1000) NOT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_row_number` (`row_number`),
  KEY `idx_error_type` (`error_type`),
  FOREIGN KEY (`task_id`) REFERENCES `sys_import_export_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入错误详情表';

-- 导入导出模板表
CREATE TABLE `sys_import_export_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `data_type` varchar(50) NOT NULL COMMENT '数据类型：user, role',
  `template_type` tinyint NOT NULL COMMENT '模板类型：1-导入模板，2-导出模板',
  `file_format` varchar(20) NOT NULL COMMENT '文件格式：xlsx, csv',
  `template_config` json NOT NULL COMMENT '模板配置（字段映射、校验规则等）',
  `template_path` varchar(500) DEFAULT NULL COMMENT '模板文件路径',
  `is_system` tinyint DEFAULT '0' COMMENT '是否系统模板：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_name_type` (`template_name`, `data_type`, `template_type`),
  KEY `idx_data_type` (`data_type`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出模板表';

-- 文件信息表
CREATE TABLE `sys_file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件原始名称',
  `file_key` varchar(500) NOT NULL COMMENT '文件存储键（包含路径）',
  `file_url` varchar(1000) DEFAULT NULL COMMENT '文件访问URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `content_type` varchar(100) NOT NULL COMMENT '文件MIME类型',
  `file_extension` varchar(10) DEFAULT NULL COMMENT '文件扩展名',
  `file_hash` varchar(64) DEFAULT NULL COMMENT '文件SHA256哈希值（用于去重）',
  `storage_type` varchar(20) NOT NULL DEFAULT 'MINIO' COMMENT '存储类型：MINIO, OSS',
  `storage_bucket` varchar(100) NOT NULL COMMENT '存储桶名称',
  `storage_path` varchar(500) NOT NULL COMMENT '存储路径',
  `upload_status` tinyint NOT NULL DEFAULT '1' COMMENT '上传状态：1-上传中，2-上传完成，3-上传失败',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型：avatar, document, import_template',
  `business_id` varchar(100) DEFAULT NULL COMMENT '业务关联ID',
  `is_public` tinyint DEFAULT '0' COMMENT '是否公开：0-私有，1-公开',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_key` (`file_key`),
  KEY `idx_file_hash` (`file_hash`),
  KEY `idx_business_type_id` (`business_type`, `business_id`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_storage_type` (`storage_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 文件分片表
CREATE TABLE `sys_file_chunk` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分片ID',
  `upload_id` varchar(100) NOT NULL COMMENT '上传会话ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `chunk_number` int NOT NULL COMMENT '分片序号（从1开始）',
  `chunk_size` bigint NOT NULL COMMENT '分片大小（字节）',
  `total_chunks` int NOT NULL COMMENT '总分片数',
  `total_size` bigint NOT NULL COMMENT '文件总大小',
  `file_hash` varchar(64) DEFAULT NULL COMMENT '文件完整哈希值',
  `chunk_hash` varchar(64) NOT NULL COMMENT '分片哈希值',
  `chunk_key` varchar(500) NOT NULL COMMENT '分片存储键',
  `upload_status` tinyint NOT NULL DEFAULT '1' COMMENT '上传状态：1-上传中，2-上传完成，3-上传失败',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
  `business_id` varchar(100) DEFAULT NULL COMMENT '业务关联ID',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upload_chunk` (`upload_id`, `chunk_number`),
  KEY `idx_upload_id` (`upload_id`),
  KEY `idx_chunk_hash` (`chunk_hash`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_business_type_id` (`business_type`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分片表';