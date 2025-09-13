package com.admin.framework.excel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.result.excel.BatchProcessor;
import com.admin.common.result.excel.BatchResult;
import com.admin.common.result.excel.ImportResult;
import com.admin.common.result.excel.ValidationResult;
import com.admin.framework.excel.service.ExcelImportService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Excel导入服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private final Validator validator;

    public ExcelImportServiceImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> ImportResult<T> importExcel(MultipartFile file, Class<T> clazz) {
        try {
            return importExcel(file.getInputStream(), clazz);
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            return ImportResult.failure("文件读取失败: " + e.getMessage());
        }
    }

    @Override
    public <T> ImportResult<T> importExcel(InputStream inputStream, Class<T> clazz) {
        List<T> dataList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try {
            // 使用EasyExcel读取数据
            EasyExcel.read(inputStream, clazz, new ReadListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    // 验证数据
                    Set<ConstraintViolation<T>> violations = validator.validate(data);
                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<T> violation : violations) {
                            String error = String.format("第%d行 %s: %s", 
                                context.readRowHolder().getRowIndex() + 1,
                                violation.getPropertyPath(), 
                                violation.getMessage());
                            errors.add(error);
                        }
                    } else {
                        dataList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel读取完成，共读取{}行数据", context.readRowHolder().getRowIndex());
                }
            }).sheet().doRead();

            // 构建返回结果
            int totalRows = dataList.size() + errors.size();
            int successRows = dataList.size();
            int errorRows = errors.size();

            if (errorRows == 0) {
                return ImportResult.success(dataList, totalRows, successRows);
            } else {
                return ImportResult.partialSuccess(dataList, errors, totalRows, successRows, errorRows);
            }

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            return ImportResult.failure("Excel解析失败: " + e.getMessage());
        }
    }

    @Override
    public <T> CompletableFuture<ImportResult<T>> importExcelAsync(MultipartFile file, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> importExcel(file, clazz));
    }

    @Override
    public <T> ImportResult<T> importExcelBatch(MultipartFile file, Class<T> clazz, 
                                              int batchSize, BatchProcessor<T> processor) {
        List<T> allData = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            List<T> batchData = new ArrayList<>();
            
            EasyExcel.read(inputStream, clazz, new ReadListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    // 验证数据
                    Set<ConstraintViolation<T>> violations = validator.validate(data);
                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<T> violation : violations) {
                            String error = String.format("第%d行 %s: %s", 
                                context.readRowHolder().getRowIndex() + 1,
                                violation.getPropertyPath(), 
                                violation.getMessage());
                            allErrors.add(error);
                        }
                        return;
                    }

                    batchData.add(data);
                    
                    // 达到批次大小，执行批处理
                    if (batchData.size() >= batchSize) {
                        processBatch(new ArrayList<>(batchData), processor, allData, allErrors);
                        batchData.clear();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 处理剩余数据
                    if (!batchData.isEmpty()) {
                        processBatch(batchData, processor, allData, allErrors);
                    }
                    log.info("批量Excel导入完成，共处理{}行数据", context.readRowHolder().getRowIndex());
                }
            }).sheet().doRead();

            // 构建返回结果
            int totalRows = allData.size() + allErrors.size();
            int successRows = allData.size();
            int errorRows = allErrors.size();

            if (errorRows == 0) {
                return ImportResult.success(allData, totalRows, successRows);
            } else {
                return ImportResult.partialSuccess(allData, allErrors, totalRows, successRows, errorRows);
            }

        } catch (Exception e) {
            log.error("批量Excel导入失败", e);
            return ImportResult.failure("Excel批量处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理批次数据
     */
    private <T> void processBatch(List<T> batch, BatchProcessor<T> processor, 
                                List<T> allData, List<String> allErrors) {
        try {
            // 执行批处理器
            BatchResult<T> result = processor.process(batch);
            
            // 收集成功数据
            if (CollectionUtil.isNotEmpty(result.getSuccessData())) {
                allData.addAll(result.getSuccessData());
            }
            
            // 收集错误信息
            if (CollectionUtil.isNotEmpty(result.getErrors())) {
                allErrors.addAll(result.getErrors());
            }
            
        } catch (Exception e) {
            log.error("批处理执行失败", e);
            allErrors.add("批处理失败: " + e.getMessage());
        }
    }

    @Override
    public <T> ValidationResult validateExcel(MultipartFile file, Class<T> clazz) {
        if (file.isEmpty()) {
            return ValidationResult.failure("文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (StrUtil.isBlank(filename)) {
            return ValidationResult.failure("文件名不能为空");
        }

        // 检查文件扩展名
        if (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls")) {
            return ValidationResult.failure("只支持Excel文件格式(.xlsx/.xls)");
        }

        // 检查文件大小（10MB限制）
        if (file.getSize() > 10 * 1024 * 1024) {
            return ValidationResult.failure("文件大小不能超过10MB");
        }

        try {
            // 尝试读取Excel文件头部验证格式
            List<T> sampleData = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            EasyExcel.read(file.getInputStream(), clazz, new ReadListener<T>() {
                private int readCount = 0;
                private static final int SAMPLE_SIZE = 10; // 只读取前10行进行验证

                @Override
                public void invoke(T data, AnalysisContext context) {
                    if (readCount >= SAMPLE_SIZE) {
                        return;
                    }
                    
                    // 验证数据格式
                    Set<ConstraintViolation<T>> violations = validator.validate(data);
                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<T> violation : violations) {
                            String error = String.format("第%d行 %s: %s", 
                                context.readRowHolder().getRowIndex() + 1,
                                violation.getPropertyPath(), 
                                violation.getMessage());
                            errors.add(error);
                        }
                    } else {
                        sampleData.add(data);
                    }
                    
                    readCount++;
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.debug("Excel格式验证完成，样本数据: {}行", readCount);
                }
            }).sheet().doRead();

            // 如果有严重错误（所有样本都失败），则验证失败
            if (sampleData.isEmpty() && !errors.isEmpty()) {
                return ValidationResult.failure("Excel格式错误: " + String.join("; ", errors));
            }

            return ValidationResult.success("Excel文件格式验证通过");

        } catch (Exception e) {
            log.error("Excel文件验证失败", e);
            return ValidationResult.failure("文件格式验证失败: " + e.getMessage());
        }
    }
}