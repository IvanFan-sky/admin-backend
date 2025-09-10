package com.admin.module.system.api.dto.dict;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 创建字典数据请求DTO
 * 
 * 用于接收创建字典数据的请求参数
 * 包含字典数据的基本信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysDictDataCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典排序
     * 数值越小越靠前显示
     */
    private Integer dictSort = 0;

    /**
     * 字典标签
     * 显示给用户看的名称
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    /**
     * 字典键值
     * 实际存储的值
     */
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    /**
     * 字典类型
     * 关联字典类型表的dict_type字段
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    /**
     * 样式属性
     * 前端显示时使用的CSS类名
     */
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    /**
     * 表格回显样式
     * 在表格中显示时使用的样式
     */
    @Size(max = 100, message = "表格回显样式长度不能超过100个字符")
    private String listClass;

    /**
     * 是否默认
     * 0-否，1-是，默认为0
     */
    private Integer isDefault = 0;

    /**
     * 状态
     * 0-禁用，1-启用，默认为1
     */
    private Integer status = 1;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}