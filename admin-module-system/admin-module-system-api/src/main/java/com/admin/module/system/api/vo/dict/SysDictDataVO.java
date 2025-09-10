package com.admin.module.system.api.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据响应VO
 * 
 * 用于返回字典数据的完整信息
 * 包含数据库中的所有字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysDictDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    private Long id;

    /**
     * 字典排序
     * 数值越小越靠前显示
     */
    private Integer dictSort;

    /**
     * 字典标签
     * 显示给用户看的名称
     */
    private String dictLabel;

    /**
     * 字典键值
     * 实际存储的值
     */
    private String dictValue;

    /**
     * 字典类型
     * 关联字典类型表的dict_type字段
     */
    private String dictType;

    /**
     * 样式属性
     * 前端显示时使用的CSS类名
     */
    private String cssClass;

    /**
     * 表格回显样式
     * 在表格中显示时使用的样式
     */
    private String listClass;

    /**
     * 是否默认
     * 0-否，1-是
     */
    private Integer isDefault;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}