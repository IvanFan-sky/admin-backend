package com.admin.module.system.api.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统字典类型展示对象")
public class SysDictTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典主键", example = "1")
    private Long id;

    @Schema(description = "字典名称", example = "用户性别")
    private String dictName;

    @Schema(description = "字典类型标识", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "备注信息", example = "用户性别字典")
    private String remark;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "创建时间", example = "2024-01-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @Schema(description = "更新时间", example = "2024-01-15 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}