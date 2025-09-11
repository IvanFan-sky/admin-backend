package com.admin.module.system.api.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统字典数据展示对象")
public class SysDictDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典数据ID", example = "1")
    private Long id;

    @Schema(description = "字典排序", example = "1")
    private Integer dictSort;

    @Schema(description = "字典标签", example = "男")
    private String dictLabel;

    @Schema(description = "字典键值", example = "1")
    private String dictValue;

    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "CSS样式属性", example = "primary")
    private String cssClass;

    @Schema(description = "表格回显样式", example = "default")
    private String listClass;

    @Schema(description = "是否默认选项", example = "0", allowableValues = {"0", "1"})
    private Integer isDefault;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "备注信息", example = "男性用户")
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