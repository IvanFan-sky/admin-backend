package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.RoleImportDTO;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.biz.util.DataValidationUtils;
import com.admin.module.infra.biz.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 角色导入处理器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleImportProcessor implements ImportDataProcessor {

    @Override
    public String getSupportedDataType() {
        return ImportExportConstants.DataType.ROLE;
    }

    @Override
    public ImportProcessResult processImport(Long taskId, InputStream inputStream, ProgressCallback progressCallback) {
        log.info("开始处理角色导入，任务ID: {}", taskId);

        List<ImportErrorDetailVO> allErrors = new ArrayList<>();
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger currentBatchStartRow = new AtomicInteger(2); // Excel从第2行开始是数据

        try {
            // 读取Excel文件
            ExcelUtils.readExcel(inputStream, RoleImportDTO.class,
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

            log.info("角色导入处理完成，任务ID: {}, 总数: {}, 成功: {}, 失败: {}", 
                    taskId, totalCount.get(), successCount.get(), failureCount.get());

            return new ImportProcessResult(totalCount.get(), successCount.get(), 
                                         failureCount.get(), allErrors);

        } catch (Exception e) {
            log.error("角色导入处理失败，任务ID: {}", taskId, e);
            throw new RuntimeException("角色导入处理失败: " + e.getMessage(), e);
        }
    }

    private void processBatch(Long taskId, List<RoleImportDTO> dataList, int startRowNumber,
                             List<ImportErrorDetailVO> allErrors, AtomicInteger totalCount,
                             AtomicInteger successCount, AtomicInteger failureCount,
                             ProgressCallback progressCallback) {
        
        log.debug("处理角色导入批次，任务ID: {}, 批次大小: {}, 起始行: {}", taskId, dataList.size(), startRowNumber);

        // 数据验证
        List<ImportErrorDetailVO> validationErrors = DataValidationUtils.batchValidateData(
            dataList, ImportExportConstants.DataType.ROLE, startRowNumber);
        
        // 重复性检查
        List<ImportErrorDetailVO> duplicateErrors = DataValidationUtils.checkDuplicateData(
            dataList, ImportExportConstants.DataType.ROLE, startRowNumber);

        // 合并错误
        List<ImportErrorDetailVO> batchErrors = new ArrayList<>();
        batchErrors.addAll(validationErrors);
        batchErrors.addAll(duplicateErrors);

        // 处理每行数据
        for (int i = 0; i < dataList.size(); i++) {
            RoleImportDTO roleImportDTO = dataList.get(i);
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
                processRoleData(taskId, roleImportDTO, currentRow, batchErrors);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
                
                ImportErrorDetailVO error = new ImportErrorDetailVO();
                error.setRowNumber(currentRow);
                error.setErrorType(ImportExportConstants.ErrorType.BUSINESS_ERROR);
                error.setErrorMessage("业务处理失败: " + e.getMessage());
                batchErrors.add(error);
                
                log.warn("角色数据处理失败，任务ID: {}, 行号: {}, 角色编码: {}", 
                        taskId, currentRow, roleImportDTO.getRoleCode(), e);
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

        log.debug("角色导入批次处理完成，任务ID: {}, 批次错误数: {}", taskId, batchErrors.size());
    }

    private void processRoleData(Long taskId, RoleImportDTO roleImportDTO, int rowNumber, 
                               List<ImportErrorDetailVO> errors) {
        // TODO: 实现具体的角色数据处理逻辑
        // 1. 检查角色编码、角色名称是否已存在
        // 2. 转换和设置默认值
        // 3. 保存角色数据

        try {
            // 模拟业务处理
            if (checkRoleExists(roleImportDTO.getRoleCode(), roleImportDTO.getRoleName())) {
                ImportErrorDetailVO error = new ImportErrorDetailVO();
                error.setRowNumber(rowNumber);
                error.setErrorType(ImportExportConstants.ErrorType.DUPLICATE_ERROR);
                error.setErrorMessage("角色编码或角色名称已存在");
                errors.add(error);
                return;
            }

            // 数据转换处理
            processRoleDataConversion(roleImportDTO);

            // 保存角色（模拟）
            saveRoleData(roleImportDTO);

            log.debug("角色数据处理成功，任务ID: {}, 行号: {}, 角色编码: {}", 
                     taskId, rowNumber, roleImportDTO.getRoleCode());

        } catch (Exception e) {
            log.error("角色数据处理异常，任务ID: {}, 行号: {}, 角色编码: {}", 
                     taskId, rowNumber, roleImportDTO.getRoleCode(), e);
            throw e;
        }
    }

    private boolean checkRoleExists(String roleCode, String roleName) {
        // TODO: 实现实际的重复性检查
        // 查询数据库检查角色编码、角色名称是否已存在
        return false; // 暂时返回不存在
    }

    private void processRoleDataConversion(RoleImportDTO roleImportDTO) {
        // 处理状态转换
        if ("启用".equals(roleImportDTO.getStatus())) {
            // status = 1
        } else if ("禁用".equals(roleImportDTO.getStatus())) {
            // status = 0
        } else {
            // 默认启用
            // status = 1
        }

        // 设置默认显示顺序
        if (roleImportDTO.getSortOrder() == null) {
            roleImportDTO.setSortOrder(0);
        }

        // 处理角色描述
        if (roleImportDTO.getRoleDesc() == null || roleImportDTO.getRoleDesc().trim().isEmpty()) {
            roleImportDTO.setRoleDesc(roleImportDTO.getRoleName()); // 默认使用角色名称作为描述
        }
    }

    private void saveRoleData(RoleImportDTO roleImportDTO) {
        // TODO: 实现实际的角色数据保存
        // 1. 创建RoleDO对象
        // 2. 保存角色基本信息
        // 3. 如果需要，可以设置默认权限
        
        log.debug("保存角色数据: {}", roleImportDTO.getRoleCode());
    }
}