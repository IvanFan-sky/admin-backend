package com.admin.module.system.api.dto.imports;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户导入DTO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class UserImportDTO {

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名", index = 0)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确，应为4-20位字母、数字或下划线")
    private String username;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称", index = 1)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱", index = 2)
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 3)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 4)
    private String gender;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门", index = 5)
    private String deptName;

    /**
     * 角色名称（多个用逗号分隔）
     */
    @ExcelProperty(value = "角色", index = 6)
    private String roleNames;

    /**
     * 状态（启用/禁用）
     */
    @ExcelProperty(value = "状态", index = 7)
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 8)
    private String remark;

    /**
     * 行号（用于错误定位）
     */
    private Integer rowNumber;

    /**
     * 解析性别
     */
    public Integer getGenderValue() {
        if ("男".equals(gender)) {
            return 1;
        } else if ("女".equals(gender)) {
            return 2;
        }
        return 0; // 未知
    }

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