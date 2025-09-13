package com.admin.module.system.api.vo.imports;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色导出VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class RoleExportVO {

    /**
     * 角色ID
     */
    @ExcelProperty(value = "角色ID", index = 0)
    private Long id;

    /**
     * 角色名称
     */
    @ExcelProperty(value = "角色名称", index = 1)
    private String roleName;

    /**
     * 角色编码
     */
    @ExcelProperty(value = "角色编码", index = 2)
    private String roleCode;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序", index = 3)
    private Integer roleSort;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", index = 4)
    private String statusText;

    /**
     * 权限字符串
     */
    @ExcelProperty(value = "权限", index = 5)
    private String permissions;

    /**
     * 用户数量
     */
    @ExcelProperty(value = "用户数量", index = 6)
    private Integer userCount;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间", index = 7)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 8)
    private String remark;

    /**
     * 设置状态文本
     */
    public void setStatusText(Integer status) {
        this.statusText = (status != null && status == 1) ? "启用" : "禁用";
    }
}