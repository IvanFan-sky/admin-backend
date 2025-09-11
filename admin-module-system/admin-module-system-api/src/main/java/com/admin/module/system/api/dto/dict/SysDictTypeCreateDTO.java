package com.admin.module.system.api.dto.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 创建字典类型请求DTO
 * 
 * 用于接收创建字典类型的请求参数
 * 包含字典类型的基本信息
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统字典类型创建请求对象")
public class SysDictTypeCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典名称", example = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    private String dictName;

    @Schema(description = "字典类型标识", example = "sys_user_sex", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status = 1;

    @Schema(description = "备注信息", example = "用户性别字典")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}