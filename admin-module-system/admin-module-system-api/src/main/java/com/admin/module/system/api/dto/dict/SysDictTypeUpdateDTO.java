package com.admin.module.system.api.dto.dict;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新字典类型请求DTO
 * 
 * 用于接收更新字典类型的请求参数
 * 包含字典类型的完整信息和主键ID
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class SysDictTypeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     * 更新操作时必须提供
     */
    @NotNull(message = "字典ID不能为空")
    private Long id;

    /**
     * 字典名称
     * 字典类型的显示名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    private String dictName;

    /**
     * 字典类型
     * 字典类型的唯一标识符
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

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

    /**
     * 乐观锁版本号
     * 用于并发控制
     */
    @NotNull(message = "版本号不能为空")
    private Integer version;
}