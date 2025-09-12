package com.admin.module.infra.biz.dal.dataobject;

import com.admin.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入导出模板数据对象
 * 
 * 对应数据库表 sys_import_export_template
 * 用于管理导入导出模板的配置信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_import_export_template")
public class ImportExportTemplateDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 数据类型
     * user-用户数据，role-角色数据
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 模板类型
     * 1-导入模板，2-导出模板
     */
    @TableField("template_type")
    private Integer templateType;

    /**
     * 文件格式
     * xlsx, csv
     */
    @TableField("file_format")
    private String fileFormat;

    /**
     * 模板配置
     * JSON格式存储字段映射、校验规则等
     */
    @TableField("template_config")
    private String templateConfig;

    /**
     * 模板文件路径
     */
    @TableField("template_path")
    private String templatePath;

    /**
     * 是否系统模板
     * 0-否，1-是
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "version", fill = FieldFill.INSERT)
    @Version
    private Integer version;

    /**
     * 删除标识
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}