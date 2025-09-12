package com.admin.common.result.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Excel导入结果封装类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult<T> {

    /**
     * 导入是否成功
     */
    private boolean success;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 导入的数据列表
     */
    private List<T> data;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 总行数
     */
    private int totalRows;

    /**
     * 成功行数
     */
    private int successRows;

    /**
     * 错误行数
     */
    private int errorRows;

    /**
     * 构造成功结果
     * 
     * @param data 导入的数据
     * @param totalRows 总行数
     * @param successRows 成功行数
     * @param <T> 数据类型
     * @return 导入结果
     */
    public static <T> ImportResult<T> success(List<T> data, int totalRows, int successRows) {
        ImportResult<T> result = new ImportResult<>();
        result.setSuccess(true);
        result.setMessage("导入成功");
        result.setData(data);
        result.setTotalRows(totalRows);
        result.setSuccessRows(successRows);
        result.setErrorRows(0);
        return result;
    }

    /**
     * 构造部分成功结果
     * 
     * @param data 导入的数据
     * @param errors 错误信息
     * @param totalRows 总行数
     * @param successRows 成功行数
     * @param errorRows 错误行数
     * @param <T> 数据类型
     * @return 导入结果
     */
    public static <T> ImportResult<T> partialSuccess(List<T> data, List<String> errors, 
                                                   int totalRows, int successRows, int errorRows) {
        ImportResult<T> result = new ImportResult<>();
        result.setSuccess(false);
        result.setMessage("导入完成，存在 " + errorRows + " 行错误");
        result.setData(data);
        result.setErrors(errors);
        result.setTotalRows(totalRows);
        result.setSuccessRows(successRows);
        result.setErrorRows(errorRows);
        return result;
    }

    /**
     * 构造失败结果
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 导入结果
     */
    public static <T> ImportResult<T> failure(String message) {
        ImportResult<T> result = new ImportResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setTotalRows(0);
        result.setSuccessRows(0);
        result.setErrorRows(0);
        return result;
    }
}
