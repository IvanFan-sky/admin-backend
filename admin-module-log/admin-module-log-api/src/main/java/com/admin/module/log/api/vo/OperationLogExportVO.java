package com.admin.module.log.api.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志导出VO
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class OperationLogExportVO {

    /**
     * 日志ID
     */
    @ExcelProperty(value = "日志ID", index = 0)
    private Long id;

    /**
     * 操作模块
     */
    @ExcelProperty(value = "操作模块", index = 1)
    private String title;

    /**
     * 业务类型
     */
    @ExcelProperty(value = "业务类型", index = 2)
    private String businessTypeText;

    /**
     * 请求方法
     */
    @ExcelProperty(value = "请求方法", index = 3)
    private String method;

    /**
     * 请求方式
     */
    @ExcelProperty(value = "请求方式", index = 4)
    private String requestMethod;

    /**
     * 操作人员
     */
    @ExcelProperty(value = "操作人员", index = 5)
    private String operName;

    /**
     * 请求URL
     */
    @ExcelProperty(value = "请求URL", index = 6)
    private String operUrl;

    /**
     * 操作地址
     */
    @ExcelProperty(value = "操作IP", index = 7)
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelProperty(value = "操作地点", index = 8)
    private String operLocation;

    /**
     * 请求参数
     */
    @ExcelProperty(value = "请求参数", index = 9)
    private String operParam;

    /**
     * 返回参数
     */
    @ExcelProperty(value = "返回结果", index = 10)
    private String jsonResult;

    /**
     * 操作状态
     */
    @ExcelProperty(value = "操作状态", index = 11)
    private String statusText;

    /**
     * 错误消息
     */
    @ExcelProperty(value = "错误消息", index = 12)
    private String errorMsg;

    /**
     * 操作时间
     */
    @ExcelProperty(value = "操作时间", index = 13)
    private LocalDateTime operTime;

    /**
     * 消耗时间
     */
    @ExcelProperty(value = "消耗时间(ms)", index = 14)
    private Long costTime;

    /**
     * 业务类型字段(用于内部映射)
     */
    private Integer businessType;
    
    /**
     * 状态字段(用于内部映射)
     */
    private Integer status;

    /**
     * 设置业务类型文本
     */
    public void setBusinessTypeText(Integer businessType) {
        this.businessType = businessType;
        if (businessType == null) {
            this.businessTypeText = "其他";
            return;
        }
        
        switch (businessType) {
            case 0:
                this.businessTypeText = "其他";
                break;
            case 1:
                this.businessTypeText = "新增";
                break;
            case 2:
                this.businessTypeText = "修改";
                break;
            case 3:
                this.businessTypeText = "删除";
                break;
            case 4:
                this.businessTypeText = "授权";
                break;
            case 5:
                this.businessTypeText = "导出";
                break;
            case 6:
                this.businessTypeText = "导入";
                break;
            case 7:
                this.businessTypeText = "强退";
                break;
            case 8:
                this.businessTypeText = "生成代码";
                break;
            case 9:
                this.businessTypeText = "清空数据";
                break;
            default:
                this.businessTypeText = "其他";
        }
    }

    /**
     * 设置状态文本
     */
    public void setStatusText(Integer status) {
        this.status = status;
        this.statusText = (status != null && status == 0) ? "成功" : "失败";
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public Integer getStatus() {
        return status;
    }
}