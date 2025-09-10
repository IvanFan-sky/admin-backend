package com.admin.module.system.api.dto.dict;

import com.admin.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

/**
 * 系统字典数据查询请求DTO
 * 
 * 用于接收字典数据分页查询的请求参数
 * 继承PageQuery获得分页参数
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDataQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     * 可选，最大100个字符
     */
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    /**
     * 字典标签
     * 支持模糊查询
     * 可选，最大100个字符
     */
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    /**
     * 字典键值
     * 支持模糊查询
     * 可选，最大100个字符
     */
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    /**
     * 字典状态
     * 0-禁用，1-启用
     * 可选，用于筛选特定状态的字典数据
     */
    private Integer status;
}