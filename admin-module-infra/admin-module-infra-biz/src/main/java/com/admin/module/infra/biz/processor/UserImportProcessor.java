package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.UserImportDTO;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.biz.stream.StreamingDataProcessor;
import com.admin.module.infra.biz.transaction.SegmentedTransactionManager;
import com.admin.module.infra.biz.util.DataValidationUtils;
import com.admin.module.infra.biz.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 增强的用户导入处理器
 * 支持并行处理、分段事务和流式处理
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserImportProcessor extends EnhancedImportDataProcessor {

    @Override
    public String getSupportedDataType() {
        return ImportExportConstants.DataType.USER;
    }

    // 继承父类的增强处理逻辑，同时保留原有处理方法作为fallback
    @Override
    protected ImportProcessResult processOriginalImport(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("使用原有逻辑处理用户导入，任务ID: {}", taskId);

        List<ImportErrorDetailVO> allErrors = new ArrayList<>();
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger currentBatchStartRow = new AtomicInteger(2); // Excel从第2行开始是数据

        try {
            // 读取Excel文件
            ExcelUtils.readExcel(inputStream, UserImportDTO.class,
                // 数据处理器
                dataList -> processBatch(taskId, dataList, currentBatchStartRow.get(), 
                                       allErrors, totalCount, successCount, failureCount, progressCallback),
                // 错误处理器  
                errors -> {
                    allErrors.addAll(errors);
                    failureCount.addAndGet(errors.size());
                }
            );

            // 最终进度回调
            if (progressCallback != null) {
                progressCallback.onProgress(totalCount.get(), totalCount.get(), "导入完成");
            }

            log.info("用户导入处理完成，任务ID: {}, 总数: {}, 成功: {}, 失败: {}", 
                    taskId, totalCount.get(), successCount.get(), failureCount.get());

            return new ImportProcessResult(totalCount.get(), successCount.get(), 
                                         failureCount.get(), allErrors);

        } catch (Exception e) {
            log.error("用户导入处理失败，任务ID: {}", taskId, e);
            throw new RuntimeException("用户导入处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<ParsedData> parseInputData(InputStream inputStream) {
        List<ParsedData> parsedDataList = new ArrayList<>();
        
        try {
            ExcelUtils.readExcel(inputStream, UserImportDTO.class,
                dataList -> {
                    for (int i = 0; i < dataList.size(); i++) {
                        ParsedData parsedData = new ParsedData();
                        parsedData.setData(dataList.get(i));
                        parsedData.setRowIndex(i + 2); // Excel从第2行开始
                        parsedDataList.add(parsedData);
                    }
                },
                errors -> {
                    // 错误处理在上层统一处理
                    log.debug("解析Excel过程中发现 {} 个错误", errors.size());
                }
            );
        } catch (Exception e) {
            log.error("解析用户导入数据失败", e);
            throw new RuntimeException("解析用户导入数据失败", e);
        }
        
        return parsedDataList;
    }

    @Override
    protected ParsedData parseLineToData(String line) {
        // 对于Excel文件，这个方法主要用于CSV格式的流式处理
        try {
            // 简单的CSV解析逻辑
            String[] fields = line.split(",");
            if (fields.length < 5) { // 至少需要用户名、邮箱、手机、姓名、状态
                return null;
            }
            
            UserImportDTO userImportDTO = new UserImportDTO();
            userImportDTO.setUsername(fields[0].trim());
            userImportDTO.setEmail(fields[1].trim());
            userImportDTO.setPhone(fields[2].trim());
            userImportDTO.setNickname(fields[3].trim());
            userImportDTO.setStatus(fields[4].trim());
            
            if (fields.length > 5) userImportDTO.setGender(fields[5].trim());
            if (fields.length > 6) userImportDTO.setBirthday(fields[6].trim());
            if (fields.length > 7) userImportDTO.setRoleCodes(fields[7].trim());
            
            ParsedData parsedData = new ParsedData();
            parsedData.setData(userImportDTO);
            return parsedData;
            
        } catch (Exception e) {
            log.debug("解析行数据失败: {}", line, e);
            return null;
        }
    }

    @Override
    protected List<ProcessedData> processBatchData(List<ParsedData> batch) {
        List<ProcessedData> results = new ArrayList<>();
        
        // 提取UserImportDTO列表
        List<UserImportDTO> userImportDTOs = batch.stream()
            .map(parsedData -> (UserImportDTO) parsedData.getData())
            .collect(Collectors.toList());
        
        // 批量数据验证
        int startRow = batch.get(0).getRowIndex();
        List<ImportErrorDetailVO> validationErrors = DataValidationUtils.batchValidateData(
            userImportDTOs, ImportExportConstants.DataType.USER, startRow);
        
        List<ImportErrorDetailVO> duplicateErrors = DataValidationUtils.checkDuplicateData(
            userImportDTOs, ImportExportConstants.DataType.USER, startRow);
        
        // 处理每个数据项
        for (int i = 0; i < batch.size(); i++) {
            ParsedData parsedData = batch.get(i);
            UserImportDTO userImportDTO = (UserImportDTO) parsedData.getData();
            int rowNumber = parsedData.getRowIndex();
            
            ProcessedData processedData = new ProcessedData();
            
            try {
                // 检查是否有验证错误
                boolean hasError = validationErrors.stream()
                    .anyMatch(error -> error.getRowNumber().equals(rowNumber))
                    || duplicateErrors.stream()
                    .anyMatch(error -> error.getRowNumber().equals(rowNumber));
                
                if (hasError) {
                    processedData.setSuccess(false);
                } else {
                    // 执行业务处理
                    processUserDataForBatch(userImportDTO, rowNumber);
                    processedData.setResult(userImportDTO.getUsername());
                    processedData.setSuccess(true);
                }
                
            } catch (Exception e) {
                log.error("批处理用户数据失败，行号: {}, 用户名: {}", rowNumber, userImportDTO.getUsername(), e);
                processedData.setSuccess(false);
            }
            
            results.add(processedData);
        }
        
        return results;
    }

    @Override
    protected void compensateBatchData(List<ParsedData> batch) {
        log.info("执行用户导入批次补偿逻辑，批次大小: {}", batch.size());
        
        // 实现补偿逻辑，例如：
        // 1. 清理已插入但事务失败的数据
        // 2. 记录补偿操作日志
        // 3. 发送补偿通知
        
        for (ParsedData parsedData : batch) {
            try {
                UserImportDTO userImportDTO = (UserImportDTO) parsedData.getData();
                // TODO: 实现具体的补偿逻辑
                log.debug("补偿用户数据: {}", userImportDTO.getUsername());
            } catch (Exception e) {
                log.error("补偿用户数据失败", e);
            }
        }
    }

    @Override
    protected ProcessedData processSingleData(ParsedData data) {
        UserImportDTO userImportDTO = (UserImportDTO) data.getData();
        ProcessedData processedData = new ProcessedData();
        
        try {
            // 数据验证
            if (!isValidUserData(userImportDTO)) {
                processedData.setSuccess(false);
                return processedData;
            }
            
            // 业务处理
            processUserDataForStream(userImportDTO);
            processedData.setResult(userImportDTO.getUsername());
            processedData.setSuccess(true);
            
        } catch (Exception e) {
            log.error("流式处理用户数据失败，用户名: {}", userImportDTO.getUsername(), e);
            processedData.setSuccess(false);
        }
        
        return processedData;
    }

    private void processBatch(Long taskId, List<UserImportDTO> dataList, int startRowNumber,
                             List<ImportErrorDetailVO> allErrors, AtomicInteger totalCount,
                             AtomicInteger successCount, AtomicInteger failureCount,
                             ProgressCallback progressCallback) {
        
        log.debug("处理用户导入批次，任务ID: {}, 批次大小: {}, 起始行: {}", taskId, dataList.size(), startRowNumber);

        // 数据验证
        List<ImportErrorDetailVO> validationErrors = DataValidationUtils.batchValidateData(
            dataList, ImportExportConstants.DataType.USER, startRowNumber);
        
        // 重复性检查
        List<ImportErrorDetailVO> duplicateErrors = DataValidationUtils.checkDuplicateData(
            dataList, ImportExportConstants.DataType.USER, startRowNumber);

        // 合并错误
        List<ImportErrorDetailVO> batchErrors = new ArrayList<>();
        batchErrors.addAll(validationErrors);
        batchErrors.addAll(duplicateErrors);

        // 处理每行数据
        for (int i = 0; i < dataList.size(); i++) {
            UserImportDTO userImportDTO = dataList.get(i);
            int currentRow = startRowNumber + i;
            
            totalCount.incrementAndGet();

            // 检查当前行是否有验证错误
            boolean hasError = batchErrors.stream()
                .anyMatch(error -> error.getRowNumber().equals(currentRow));

            if (hasError) {
                failureCount.incrementAndGet();
                continue;
            }

            // 执行业务处理
            try {
                processUserData(taskId, userImportDTO, currentRow, batchErrors);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
                
                ImportErrorDetailVO error = new ImportErrorDetailVO();
                error.setRowNumber(currentRow);
                error.setErrorType(ImportExportConstants.ErrorType.BUSINESS_ERROR);
                error.setErrorMessage("业务处理失败: " + e.getMessage());
                batchErrors.add(error);
                
                log.warn("用户数据处理失败，任务ID: {}, 行号: {}, 用户名: {}", 
                        taskId, currentRow, userImportDTO.getUsername(), e);
            }

            // 进度回调
            if (progressCallback != null) {
                progressCallback.onProgress(totalCount.get(), -1, "正在处理第" + currentRow + "行数据");
            }
        }

        // 添加批次错误到总错误列表
        allErrors.addAll(batchErrors);

        // 更新下一批次的起始行号
        startRowNumber += dataList.size();

        log.debug("用户导入批次处理完成，任务ID: {}, 批次错误数: {}", taskId, batchErrors.size());
    }

    private void processUserData(Long taskId, UserImportDTO userImportDTO, int rowNumber, 
                               List<ImportErrorDetailVO> errors) {
        // TODO: 实现具体的用户数据处理逻辑
        // 1. 检查用户名、邮箱、手机号是否已存在
        // 2. 转换和设置默认值
        // 3. 处理角色关联
        // 4. 保存用户数据

        try {
            // 模拟业务处理
            if (checkUserExists(userImportDTO.getUsername(), userImportDTO.getEmail(), userImportDTO.getPhone())) {
                ImportErrorDetailVO error = new ImportErrorDetailVO();
                error.setRowNumber(rowNumber);
                error.setErrorType(ImportExportConstants.ErrorType.DUPLICATE_ERROR);
                error.setErrorMessage("用户名、邮箱或手机号已存在");
                errors.add(error);
                return;
            }

            // 数据转换处理
            processUserDataConversion(userImportDTO);

            // 保存用户（模拟）
            saveUserData(userImportDTO);

            log.debug("用户数据处理成功，任务ID: {}, 行号: {}, 用户名: {}", 
                     taskId, rowNumber, userImportDTO.getUsername());

        } catch (Exception e) {
            log.error("用户数据处理异常，任务ID: {}, 行号: {}, 用户名: {}", 
                     taskId, rowNumber, userImportDTO.getUsername(), e);
            throw e;
        }
    }

    private boolean checkUserExists(String username, String email, String phone) {
        // TODO: 实现实际的重复性检查
        // 查询数据库检查用户名、邮箱、手机号是否已存在
        return false; // 暂时返回不存在
    }

    private void processUserDataConversion(UserImportDTO userImportDTO) {
        // 处理性别转换
        if ("未知".equals(userImportDTO.getGender())) {
            // gender = 0
        } else if ("男".equals(userImportDTO.getGender())) {
            // gender = 1
        } else if ("女".equals(userImportDTO.getGender())) {
            // gender = 2
        }

        // 处理状态转换
        if ("启用".equals(userImportDTO.getStatus())) {
            // status = 1
        } else if ("禁用".equals(userImportDTO.getStatus())) {
            // status = 0
        } else {
            // 默认启用
            // status = 1
        }

        // 处理生日转换
        if (userImportDTO.getBirthday() != null) {
            try {
                LocalDate birthday = LocalDate.parse(userImportDTO.getBirthday(), 
                                                   DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // 转换为Date或LocalDate存储
            } catch (Exception e) {
                log.warn("生日格式转换失败: {}", userImportDTO.getBirthday());
            }
        }

        // TODO: 处理角色编码转换为角色ID
        if (userImportDTO.getRoleCodes() != null) {
            String[] roleCodes = userImportDTO.getRoleCodes().split(",");
            // 根据角色编码查询角色ID
        }
    }

    private void saveUserData(UserImportDTO userImportDTO) {
        // TODO: 实现实际的用户数据保存
        // 1. 创建UserDO对象
        // 2. 设置默认密码
        // 3. 保存用户基本信息
        // 4. 保存用户角色关联
        
        log.debug("保存用户数据: {}", userImportDTO.getUsername());
    }

    // 增强处理器的辅助方法
    private void processUserDataForBatch(UserImportDTO userImportDTO, int rowNumber) {
        // 批处理专用的用户数据处理
        List<ImportErrorDetailVO> errors = new ArrayList<>();
        processUserData(null, userImportDTO, rowNumber, errors);
        
        if (!errors.isEmpty()) {
            throw new RuntimeException("批处理用户数据验证失败: " + 
                errors.stream()
                    .map(ImportErrorDetailVO::getErrorMessage)
                    .collect(Collectors.joining(", ")));
        }
    }

    private void processUserDataForStream(UserImportDTO userImportDTO) {
        // 流式处理专用的用户数据处理
        if (!isValidUserData(userImportDTO)) {
            throw new RuntimeException("用户数据验证失败");
        }
        
        // 数据转换处理
        processUserDataConversion(userImportDTO);
        
        // 保存用户数据
        saveUserData(userImportDTO);
    }

    private boolean isValidUserData(UserImportDTO userImportDTO) {
        // 快速数据有效性检查
        if (userImportDTO == null) {
            return false;
        }
        
        if (userImportDTO.getUsername() == null || userImportDTO.getUsername().trim().isEmpty()) {
            return false;
        }
        
        if (userImportDTO.getEmail() == null || userImportDTO.getEmail().trim().isEmpty()) {
            return false;
        }
        
        // 检查用户是否已存在
        if (checkUserExists(userImportDTO.getUsername(), userImportDTO.getEmail(), userImportDTO.getPhone())) {
            return false;
        }
        
        return true;
    }
}