package com.admin.common.result.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Excel验证结果封装类
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * 验证是否通过
     */
    private boolean valid;

    /**
     * 验证消息
     */
    private String message;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 构造验证成功结果
     * 
     * @return 验证结果
     */
    public static ValidationResult success() {
        return new ValidationResult(true, "验证通过", null);
    }

    /**
     * 构造验证成功结果
     * 
     * @param message 成功消息
     * @return 验证结果
     */
    public static ValidationResult success(String message) {
        return new ValidationResult(true, message, null);
    }

    /**
     * 构造验证失败结果
     * 
     * @param message 失败消息
     * @param errors 错误列表
     * @return 验证结果
     */
    public static ValidationResult failure(String message, List<String> errors) {
        return new ValidationResult(false, message, errors);
    }

    /**
     * 构造验证失败结果
     * 
     * @param errors 错误列表
     * @return 验证结果
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, "验证失败", errors);
    }

    /**
     * 构造验证失败结果
     * 
     * @param message 失败消息
     * @return 验证结果
     */
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message, null);
    }

    public boolean isSuccess() {
        return valid;
    }
}
