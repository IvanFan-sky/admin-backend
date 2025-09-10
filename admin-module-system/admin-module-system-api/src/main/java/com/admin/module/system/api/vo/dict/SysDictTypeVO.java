package com.admin.module.system.api.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型响应VO
 * 
 * 用于返回字典类型的完整信息
 * 包含数据库中的所有字段
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysDictTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    private Long id;

    /**
     * 字典名称
     * 字典类型的显示名称
     */
    private String dictName;

    /**
     * 字典类型
     * 字典类型的唯一标识符
     */
    private String dictType;

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