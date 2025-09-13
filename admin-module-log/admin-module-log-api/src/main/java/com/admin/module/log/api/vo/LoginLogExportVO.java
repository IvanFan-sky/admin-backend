package com.admin.module.log.api.vo;

import com.admin.framework.excel.annotation.ExcelExport;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志导出VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class LoginLogExportVO {

    /**
     * 日志ID
     */
    @ExcelProperty(value = "日志ID", index = 0)
    private Long id;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "用户账号", index = 1)
    private String userName;

    /**
     * 登录类型
     */
    @ExcelProperty(value = "登录类型", index = 2)
    private String loginTypeText;

    /**
     * 登录IP地址
     */
    @ExcelProperty(value = "登录IP", index = 3)
    private String ipaddr;

    /**
     * 登录地点
     */
    @ExcelProperty(value = "登录地点", index = 4)
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ExcelProperty(value = "浏览器", index = 5)
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty(value = "操作系统", index = 6)
    private String os;

    /**
     * 登录状态
     */
    @ExcelProperty(value = "登录状态", index = 7)
    private String statusText;

    /**
     * 提示消息
     */
    @ExcelProperty(value = "提示消息", index = 8)
    private String msg;

    /**
     * 访问时间
     */
    @ExcelProperty(value = "登录时间", index = 9)
    private LocalDateTime loginTime;

    /**
     * 登录类型字段(用于内部映射)
     */
    private Integer loginType;
    
    /**
     * 状态字段(用于内部映射)
     */
    private Integer status;

    /**
     * 设置登录类型文本
     */
    public void setLoginTypeText(Integer loginType) {
        this.loginType = loginType;
        if (loginType == null) {
            this.loginTypeText = "未知";
            return;
        }
        
        switch (loginType) {
            case 1:
                this.loginTypeText = "用户名密码";
                break;
            case 2:
                this.loginTypeText = "邮箱密码";
                break;
            case 3:
                this.loginTypeText = "手机验证码";
                break;
            case 4:
                this.loginTypeText = "第三方登录";
                break;
            default:
                this.loginTypeText = "未知";
        }
    }

    /**
     * 设置状态文本
     */
    public void setStatusText(Integer status) {
        this.status = status;
        this.statusText = (status != null && status == 0) ? "成功" : "失败";
    }

    public Integer getLoginType() {
        return loginType;
    }

    public Integer getStatus() {
        return status;
    }
}