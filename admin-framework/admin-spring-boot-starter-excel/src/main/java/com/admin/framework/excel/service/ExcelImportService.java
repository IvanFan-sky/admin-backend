package com.admin.framework.excel.service;

import com.admin.common.result.excel.BatchProcessor;
import com.admin.common.result.excel.ImportResult;
import com.admin.common.result.excel.ValidationResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Excel导入服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ExcelImportService {

    /**
     * 同步导入Excel文件
     * 
     * @param file 上传的文件
     * @param clazz 数据类型
     * @param <T> 数据类型泛型
     * @return 导入结果
     */
    <T> ImportResult<T> importExcel(MultipartFile file, Class<T> clazz);

    /**
     * 同步导入Excel输入流
     * 
     * @param inputStream 输入流
     * @param clazz 数据类型
     * @param <T> 数据类型泛型
     * @return 导入结果
     */
    <T> ImportResult<T> importExcel(InputStream inputStream, Class<T> clazz);

    /**
     * 异步导入Excel文件
     * 
     * @param file 上传的文件
     * @param clazz 数据类型
     * @param <T> 数据类型泛型
     * @return 异步导入结果
     */
    <T> CompletableFuture<ImportResult<T>> importExcelAsync(MultipartFile file, Class<T> clazz);

    /**
     * 批量导入Excel文件（适用于大文件）
     * 
     * @param file 上传的文件
     * @param clazz 数据类型
     * @param batchSize 批处理大小
     * @param processor 批处理器
     * @param <T> 数据类型泛型
     * @return 导入结果
     */
    <T> ImportResult<T> importExcelBatch(MultipartFile file, Class<T> clazz, 
                                        int batchSize, BatchProcessor<T> processor);

    /**
     * 验证Excel文件格式
     * 
     * @param file 上传的文件
     * @param clazz 数据类型
     * @param <T> 数据类型泛型
     * @return 验证结果
     */
    <T> ValidationResult validateExcel(MultipartFile file, Class<T> clazz);
}
