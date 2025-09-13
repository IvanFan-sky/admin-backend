package com.admin.module.system.api.dto.imports;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 角色导入DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class RoleImportDTO {

    /**
     * 角色名称
     */
    @ExcelProperty(value = "角色名称", index = 0)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @ExcelProperty(value = "角色编码", index = 1)
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z_]{2,50}$", message = "角色编码格式不正确，应为2-50位大写字母或下划线")
    private String roleCode;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序", index = 2)
    private Integer roleSort;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", index = 3)
    private String status;

    /**
     * 权限字符串
     */
    @ExcelProperty(value = "权限", index = 4)
    private String permissions;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 5)
    private String remark;

    /**
     * 行号（用于错误定位）
     */
    private Integer rowNumber;

    /**
     * 解析状态
     */
    public Integer getStatusValue() {
        if ("启用".equals(status)) {
            return 1;
        } else if ("禁用".equals(status)) {
            return 0;
        }
        return 1; // 默认启用
    }
}