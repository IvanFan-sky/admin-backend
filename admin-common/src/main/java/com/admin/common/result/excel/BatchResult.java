package com.admin.common.result.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理结果
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
public class BatchResult<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 成功处理的数量
     */
    private int successCount;

    /**
     * 失败处理的数量
     */
    private int failureCount;

    /**
     * 成功处理的数据
     */
    private List<T> successData;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    public BatchResult() {
        this.success = true;
        this.successCount = 0;
        this.failureCount = 0;
        this.successData = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public BatchResult(boolean success, int successCount, int failureCount, List<T> successData, List<String> errors) {
        this.success = success;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.successData = successData != null ? successData : new ArrayList<>();
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    /**
     * 创建成功结果
     */
    public static <T> BatchResult<T> success(List<T> data) {
        BatchResult<T> result = new BatchResult<>();
        result.setSuccess(true);
        result.setSuccessData(data);
        result.setSuccessCount(data != null ? data.size() : 0);
        return result;
    }

    /**
     * 创建失败结果
     */
    public static <T> BatchResult<T> failure(int successCount, int failureCount, List<T> successData, List<String> errors) {
        BatchResult<T> result = new BatchResult<>();
        result.setSuccess(false);
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setSuccessData(successData);
        result.setErrors(errors);
        return result;
    }
}