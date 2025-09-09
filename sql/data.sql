-- =============================================
-- Admin管理系统 - 初始数据脚本
-- =============================================

USE `admin_dev`;

-- =============================================
-- 用户初始数据
-- =============================================

-- 超级管理员用户 (密码: admin123)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `password`, `email`, `phone`, `status`, `create_by`, `create_time`) 
VALUES (1, 'admin', '超级管理员', '$2a$10$7JB720yubVSOfvVWbfXCL.VqGOZTH4QLSuDjK7qjGOdHrKRHQ/erg', 'admin@example.com', '13800138000', 1, 'system', NOW());

-- 普通管理员用户 (密码: test123)
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `password`, `email`, `phone`, `status`, `create_by`, `create_time`) 
VALUES (2, 'test', '测试用户', '$2a$10$mE7YqZd.8vDvl6w0bI7pDeQj2vg6G7fWfPLF4s9WH.TnYnFaY0EHq', 'test@example.com', '13800138001', 1, 'admin', NOW());

-- =============================================
-- 角色初始数据
-- =============================================

-- 超级管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `sort_order`, `status`, `create_by`, `create_time`) 
VALUES (1, '超级管理员', 'SUPER_ADMIN', '超级管理员角色，拥有所有权限', 1, 1, 'system', NOW());

-- 普通管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `sort_order`, `status`, `create_by`, `create_time`) 
VALUES (2, '普通管理员', 'ADMIN', '普通管理员角色', 2, 1, 'admin', NOW());

-- 普通用户角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `sort_order`, `status`, `create_by`, `create_time`) 
VALUES (3, '普通用户', 'USER', '普通用户角色', 3, 1, 'admin', NOW());

-- =============================================
-- 菜单初始数据
-- =============================================

-- 一级菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(1, 0, '系统管理', 1, '/system', NULL, NULL, 'system', 1, 1, 'system', NOW()),
(2, 0, '基础设施', 1, '/infra', NULL, NULL, 'monitor', 2, 1, 'system', NOW()),
(3, 0, '系统监控', 1, '/monitor', NULL, NULL, 'monitor', 3, 1, 'system', NOW());

-- 二级菜单 - 系统管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(11, 1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 'system', NOW()),
(12, 1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, 1, 'system', NOW()),
(13, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3, 1, 'system', NOW()),
(14, 1, '字典管理', 2, '/system/dict', 'system/dict/index', 'system:dict:list', 'dict', 4, 1, 'system', NOW()),
(15, 1, '参数设置', 2, '/system/config', 'system/config/index', 'system:config:list', 'edit', 5, 1, 'system', NOW());

-- 三级菜单 - 用户管理按钮
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(111, 11, '用户查询', 3, '', '', 'system:user:query', '', 1, 1, 'system', NOW()),
(112, 11, '用户新增', 3, '', '', 'system:user:add', '', 2, 1, 'system', NOW()),
(113, 11, '用户修改', 3, '', '', 'system:user:edit', '', 3, 1, 'system', NOW()),
(114, 11, '用户删除', 3, '', '', 'system:user:remove', '', 4, 1, 'system', NOW()),
(115, 11, '重置密码', 3, '', '', 'system:user:resetPwd', '', 5, 1, 'system', NOW());

-- 三级菜单 - 角色管理按钮
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(121, 12, '角色查询', 3, '', '', 'system:role:query', '', 1, 1, 'system', NOW()),
(122, 12, '角色新增', 3, '', '', 'system:role:add', '', 2, 1, 'system', NOW()),
(123, 12, '角色修改', 3, '', '', 'system:role:edit', '', 3, 1, 'system', NOW()),
(124, 12, '角色删除', 3, '', '', 'system:role:remove', '', 4, 1, 'system', NOW()),
(125, 12, '分配权限', 3, '', '', 'system:role:authorize', '', 5, 1, 'system', NOW());

-- 三级菜单 - 菜单管理按钮
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(131, 13, '菜单查询', 3, '', '', 'system:menu:query', '', 1, 1, 'system', NOW()),
(132, 13, '菜单新增', 3, '', '', 'system:menu:add', '', 2, 1, 'system', NOW()),
(133, 13, '菜单修改', 3, '', '', 'system:menu:edit', '', 3, 1, 'system', NOW()),
(134, 13, '菜单删除', 3, '', '', 'system:menu:remove', '', 4, 1, 'system', NOW());

-- 二级菜单 - 基础设施
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(21, 2, '文件管理', 2, '/infra/file', 'infra/file/index', 'infra:file:list', 'upload', 1, 1, 'system', NOW()),
(22, 2, '通知公告', 2, '/infra/notice', 'infra/notice/index', 'infra:notice:list', 'message', 2, 1, 'system', NOW());

-- 二级菜单 - 系统监控
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
(31, 3, '在线用户', 2, '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'online', 1, 1, 'system', NOW()),
(32, 3, '操作日志', 2, '/monitor/operlog', 'monitor/operlog/index', 'monitor:operlog:list', 'form', 2, 1, 'system', NOW()),
(33, 3, '登录日志', 2, '/monitor/logininfor', 'monitor/logininfor/index', 'monitor:logininfor:list', 'logininfor', 3, 1, 'system', NOW());

-- =============================================
-- 用户角色关联数据
-- =============================================

-- 超级管理员分配超级管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_by`, `create_time`) 
VALUES (1, 1, 'system', NOW());

-- 测试用户分配普通管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_by`, `create_time`) 
VALUES (2, 2, 'admin', NOW());

-- =============================================
-- 角色菜单关联数据 (超级管理员拥有所有权限)
-- =============================================

-- 超级管理员角色分配所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`) 
SELECT 1, id, 'system', NOW() FROM `sys_menu` WHERE status = 1;

-- 普通管理员角色分配部分菜单权限（除了系统管理的删除权限）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`, `create_time`) VALUES
(2, 1, 'admin', NOW()), (2, 2, 'admin', NOW()), (2, 3, 'admin', NOW()),
(2, 11, 'admin', NOW()), (2, 12, 'admin', NOW()), (2, 13, 'admin', NOW()), (2, 14, 'admin', NOW()), (2, 15, 'admin', NOW()),
(2, 21, 'admin', NOW()), (2, 22, 'admin', NOW()),
(2, 31, 'admin', NOW()), (2, 32, 'admin', NOW()), (2, 33, 'admin', NOW()),
(2, 111, 'admin', NOW()), (2, 112, 'admin', NOW()), (2, 113, 'admin', NOW()), (2, 115, 'admin', NOW()),
(2, 121, 'admin', NOW()), (2, 122, 'admin', NOW()), (2, 123, 'admin', NOW()),
(2, 131, 'admin', NOW()), (2, 132, 'admin', NOW()), (2, 133, 'admin', NOW());

-- =============================================
-- 字典类型初始数据
-- =============================================

INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `remark`, `create_by`, `create_time`) VALUES
('用户性别', 'sys_user_sex', 1, '用户性别列表', 'admin', NOW()),
('菜单状态', 'sys_show_hide', 1, '菜单状态列表', 'admin', NOW()),
('系统开关', 'sys_normal_disable', 1, '系统开关列表', 'admin', NOW()),
('任务状态', 'sys_job_status', 1, '任务状态列表', 'admin', NOW()),
('系统是否', 'sys_yes_no', 1, '系统是否列表', 'admin', NOW()),
('通知类型', 'sys_notice_type', 1, '通知类型列表', 'admin', NOW()),
('操作类型', 'sys_oper_type', 1, '操作类型列表', 'admin', NOW()),
('系统状态', 'sys_common_status', 1, '登录状态列表', 'admin', NOW());

-- =============================================
-- 字典数据初始数据
-- =============================================

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '男', '1', 'sys_user_sex', '', '', 1, 1, 'admin', NOW()),
(2, '女', '2', 'sys_user_sex', '', '', 0, 1, 'admin', NOW()),
(3, '未知', '0', 'sys_user_sex', '', '', 0, 1, 'admin', NOW()),

(1, '显示', '1', 'sys_show_hide', '', 'primary', 1, 1, 'admin', NOW()),
(2, '隐藏', '0', 'sys_show_hide', '', 'danger', 0, 1, 'admin', NOW()),

(1, '正常', '1', 'sys_normal_disable', '', 'primary', 1, 1, 'admin', NOW()),
(2, '停用', '0', 'sys_normal_disable', '', 'danger', 0, 1, 'admin', NOW()),

(1, '正常', '1', 'sys_job_status', '', 'primary', 1, 1, 'admin', NOW()),
(2, '暂停', '0', 'sys_job_status', '', 'danger', 0, 1, 'admin', NOW()),

(1, '是', 'Y', 'sys_yes_no', '', 'primary', 1, 1, 'admin', NOW()),
(2, '否', 'N', 'sys_yes_no', '', 'danger', 0, 1, 'admin', NOW()),

(1, '通知', '1', 'sys_notice_type', '', 'warning', 1, 1, 'admin', NOW()),
(2, '公告', '2', 'sys_notice_type', '', 'success', 0, 1, 'admin', NOW()),

(1, '新增', '1', 'sys_oper_type', '', 'info', 0, 1, 'admin', NOW()),
(2, '修改', '2', 'sys_oper_type', '', 'info', 0, 1, 'admin', NOW()),
(3, '删除', '3', 'sys_oper_type', '', 'danger', 0, 1, 'admin', NOW()),
(4, '授权', '4', 'sys_oper_type', '', 'primary', 0, 1, 'admin', NOW()),
(5, '导出', '5', 'sys_oper_type', '', 'warning', 0, 1, 'admin', NOW()),
(6, '导入', '6', 'sys_oper_type', '', 'warning', 0, 1, 'admin', NOW()),
(7, '强退', '7', 'sys_oper_type', '', 'danger', 0, 1, 'admin', NOW()),
(8, '生成代码', '8', 'sys_oper_type', '', 'warning', 0, 1, 'admin', NOW()),
(9, '清空数据', '9', 'sys_oper_type', '', 'danger', 0, 1, 'admin', NOW()),

(1, '成功', '0', 'sys_common_status', '', 'primary', 0, 1, 'admin', NOW()),
(2, '失败', '1', 'sys_common_status', '', 'danger', 0, 1, 'admin', NOW());

-- =============================================
-- 系统参数配置初始数据
-- =============================================

INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `remark`, `create_by`, `create_time`) VALUES
('主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 1, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 'admin', NOW()),
('用户管理-账号初始密码', 'sys.user.initPassword', '123456', 1, '初始化密码 123456', 'admin', NOW()),
('主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 1, '深色主题theme-dark，浅色主题theme-light', 'admin', NOW()),
('账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 1, '是否开启验证码功能（true开启，false关闭）', 'admin', NOW()),
('账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 1, '是否开启注册用户功能（true开启，false关闭）', 'admin', NOW()),
('用户登录-黑名单列表', 'sys.login.blackIPList', '', 1, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）', 'admin', NOW());