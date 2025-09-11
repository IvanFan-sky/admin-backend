package com.admin.module.system.api.dto.dict;

import com.admin.common.core.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统字典数据查询请求对象")
public class SysDictDataQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典类型", example = "sys_user_sex")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "字典标签", example = "男")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    @Schema(description = "字典键值", example = "1")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    @Schema(description = "字典状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}