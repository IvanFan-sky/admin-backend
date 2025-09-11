package com.admin.module.system.api.dto.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新字典数据请求DTO
 * 
 * 用于接收更新字典数据的请求参数
 * 包含字典数据的完整信息和主键ID
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统字典数据更新请求对象")
public class SysDictDataUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典数据ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "字典编码不能为空")
    private Long id;

    @Schema(description = "字典排序", example = "1")
    private Integer dictSort = 0;

    @Schema(description = "字典标签", example = "男", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    @Schema(description = "字典键值", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    @Schema(description = "字典类型", example = "sys_user_sex", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "CSS样式属性", example = "primary")
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    @Schema(description = "表格回显样式", example = "default")
    @Size(max = 100, message = "表格回显样式长度不能超过100个字符")
    private String listClass;

    @Schema(description = "是否默认选项", example = "0", allowableValues = {"0", "1"})
    private Integer isDefault = 0;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status = 1;

    @Schema(description = "备注信息", example = "男性用户")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

    @Schema(description = "乐观锁版本号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer version;
}