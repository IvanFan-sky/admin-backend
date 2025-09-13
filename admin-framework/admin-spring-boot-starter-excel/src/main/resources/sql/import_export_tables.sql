-- 导入导出任务表
CREATE TABLE sys_import_export_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
    task_type ENUM('IMPORT', 'EXPORT') NOT NULL COMMENT '任务类型',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型(USER, ROLE, LOG)',
    file_name VARCHAR(500) COMMENT '文件名',
    file_path VARCHAR(1000) COMMENT '文件路径',
    status ENUM('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING' COMMENT '任务状态',
    total_count INT DEFAULT 0 COMMENT '总数据量',
    success_count INT DEFAULT 0 COMMENT '成功数量',
    fail_count INT DEFAULT 0 COMMENT '失败数量',
    error_message TEXT COMMENT '错误信息',
    progress INT DEFAULT 0 COMMENT '进度百分比',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_by VARCHAR(50) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    
    INDEX idx_user_type_status (create_by, task_type, status),
    INDEX idx_business_type (business_type),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出任务表';

-- 模板配置表
CREATE TABLE sys_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    template_type ENUM('IMPORT', 'EXPORT') NOT NULL COMMENT '模板类型',
    template_name VARCHAR(200) NOT NULL COMMENT '模板名称',
    field_config JSON NOT NULL COMMENT '字段配置JSON',
    validation_rules JSON COMMENT '校验规则JSON',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    template_version VARCHAR(10) DEFAULT '1.0' COMMENT '模板版本',
    description TEXT COMMENT '模板描述',
    create_by VARCHAR(50) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    
    UNIQUE KEY uk_business_template_type (business_type, template_type, deleted),
    INDEX idx_enabled (is_enabled),
    INDEX idx_business_type (business_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板配置表';

-- 插入默认模板配置
INSERT INTO sys_template_config (business_type, template_type, template_name, field_config, validation_rules, description) VALUES 
('USER', 'IMPORT', '用户导入模板', 
 '{"fields":[{"name":"username","title":"用户名","required":true,"type":"string","maxLength":20},{"name":"nickname","title":"昵称","required":true,"type":"string","maxLength":50},{"name":"email","title":"邮箱","required":false,"type":"email"},{"name":"mobile","title":"手机号","required":false,"type":"mobile"},{"name":"gender","title":"性别","required":false,"type":"enum","options":["男","女"]},{"name":"deptName","title":"部门","required":false,"type":"string"},{"name":"roleNames","title":"角色","required":false,"type":"string"},{"name":"status","title":"状态","required":false,"type":"enum","options":["启用","禁用"]},{"name":"remark","title":"备注","required":false,"type":"string","maxLength":500}]}',
 '{"rules":[{"field":"username","pattern":"^[a-zA-Z0-9_]{4,20}$","message":"用户名格式不正确"},{"field":"email","pattern":"^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$","message":"邮箱格式不正确"},{"field":"mobile","pattern":"^1[3-9]\\d{9}$","message":"手机号格式不正确"}]}',
 '用户批量导入模板'),
 
('USER', 'EXPORT', '用户导出模板',
 '{"fields":[{"name":"id","title":"用户ID"},{"name":"username","title":"用户名"},{"name":"nickname","title":"昵称"},{"name":"email","title":"邮箱"},{"name":"mobile","title":"手机号"},{"name":"genderText","title":"性别"},{"name":"deptName","title":"部门"},{"name":"roleNames","title":"角色"},{"name":"statusText","title":"状态"},{"name":"loginIp","title":"最后登录IP"},{"name":"loginDate","title":"最后登录时间"},{"name":"createTime","title":"创建时间"},{"name":"remark","title":"备注"}]}',
 '{}',
 '用户数据导出模板');

-- 创建任务执行日志表（可选，用于详细跟踪）
CREATE TABLE sys_import_export_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    log_level ENUM('INFO', 'WARN', 'ERROR') DEFAULT 'INFO' COMMENT '日志级别',
    log_message TEXT NOT NULL COMMENT '日志消息',
    log_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间',
    
    INDEX idx_task_id (task_id),
    INDEX idx_log_time (log_time),
    FOREIGN KEY (task_id) REFERENCES sys_import_export_task(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出执行日志表';