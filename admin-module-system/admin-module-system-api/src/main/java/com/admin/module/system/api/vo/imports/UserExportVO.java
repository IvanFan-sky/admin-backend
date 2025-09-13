package com.admin.module.system.api.vo.imports;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户导出VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class UserExportVO {

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID", index = 0)
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名", index = 1)
    private String username;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称", index = 2)
    private String nickname;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 4)
    private String phone;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 5)
    private String genderText;



    /**
     * 角色名称
     */
    @ExcelProperty(value = "角色", index = 7)
    private String roleNames;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", index = 8)
    private String statusText;

    /**
     * 最后登录IP
     */
    @ExcelProperty(value = "最后登录IP", index = 9)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @ExcelProperty(value = "最后登录时间", index = 10)
    private LocalDateTime loginDate;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间", index = 11)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 12)
    private String remark;

    /**
     * 性别字段（用于转换）
     */
    private Integer gender;

    /**
     * 状态字段（用于转换）
     */
    private Integer status;

    /**
     * 设置性别文本
     */
    public void setGenderText(Integer gender) {
        if (gender == null) {
            this.genderText = "未知";
        } else if (gender == 1) {
            this.genderText = "男";
        } else if (gender == 2) {
            this.genderText = "女";
        } else {
            this.genderText = "未知";
        }
    }

    /**
     * 设置状态文本
     */
    public void setStatusText(Integer status) {
        this.statusText = (status != null && status == 1) ? "启用" : "禁用";
    }
}