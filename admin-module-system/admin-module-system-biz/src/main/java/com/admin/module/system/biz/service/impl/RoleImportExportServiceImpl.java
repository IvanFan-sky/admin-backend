package com.admin.module.system.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.framework.excel.service.ExcelExportService;
import com.admin.framework.excel.service.ExcelImportService;
import com.admin.framework.excel.service.ImportExportTaskService;
import com.admin.framework.security.utils.SecurityContextHolder;
import com.admin.module.system.api.dto.RoleCreateDTO;
import com.admin.module.system.api.dto.RoleImportDTO;
import com.admin.module.system.api.dto.RolePageDTO;
import com.admin.module.system.api.service.RoleImportExportService;
import com.admin.module.system.api.service.role.SysRoleService;
import com.admin.module.system.api.vo.RoleExportVO;
import com.admin.module.system.biz.convert.role.RoleImportExportConvert;
import com.admin.module.system.biz.dal.dataobject.SysRoleDO;
import com.admin.module.system.biz.dal.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 角色导入导出服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleImportExportServiceImpl implements RoleImportExportService {

    private static final String BUSINESS_TYPE = "ROLE";
    private static final int BATCH_SIZE = 1000;
    private static final int PREVIEW_SIZE = 10;

    private final ExcelImportService excelImportService;
    private final ExcelExportService excelExportService;
    private final ImportExportTaskService taskService;
    private final SysRoleService roleService;
    private final SysRoleMapper roleMapper;

    @Override
    public void downloadImportTemplate(HttpServletResponse response) {
        log.info("下载角色导入模板");
        excelExportService.exportTemplate(response, "角色导入模板", "角色信息", RoleImportDTO.class);
    }

    @Override
    @Async
    public CompletableFuture<Long> importRolesAsync(MultipartFile file) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("用户[{}]开始异步导入角色，文件名: {}", userId, file.getOriginalFilename());

        // 检查并发限制
        if (!taskService.canCreateTask(userId, ImportExportTask.TaskType.IMPORT)) {
            throw new ServiceException("您有正在执行的导入任务，请稍后再试");
        }

        // 创建任务
        Long taskId = taskService.createTask("角色导入", ImportExportTask.TaskType.IMPORT, 
                                           BUSINESS_TYPE, file.getOriginalFilename());

        // 异步执行导入
        return taskService.executeImportTaskAsync(taskId, this::processImportTask);
    }

    /**
     * 处理导入任务
     */
    private void processImportTask(Long taskId) {
        try {
            ImportExportTask task = taskService.getTask(taskId);
            log.info("开始处理角色导入任务: {}", taskId);

            // 标记任务开始
            taskService.updateTaskStatus(taskId, ImportExportTask.TaskStatus.PROCESSING);

            // 读取并验证数据
            List<RoleImportDTO> importData = readAndValidateImportData(task.getFileName());
            
            if (CollectionUtil.isEmpty(importData)) {
                taskService.completeTask(taskId, false, "导入文件为空或格式错误");
                return;
            }

            // 更新总数
            taskService.updateTaskStatistics(taskId, importData.size(), 0, 0);

            // 分批处理数据
            List<String> errors = new ArrayList<>();
            int successCount = 0;
            int processedCount = 0;

            for (int i = 0; i < importData.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, importData.size());
                List<RoleImportDTO> batch = importData.subList(i, endIndex);

                // 处理当前批次
                BatchResult result = processBatch(batch, i + 1);
                successCount += result.getSuccessCount();
                errors.addAll(result.getErrors());
                processedCount += batch.size();

                // 更新进度
                taskService.updateTaskProgress(taskId, processedCount, importData.size());
                taskService.updateTaskStatistics(taskId, importData.size(), successCount, 
                                               processedCount - successCount);
            }

            // 完成任务
            boolean success = errors.isEmpty();
            String message = success ? "导入成功" : "导入完成，部分数据存在错误";
            
            if (!success) {
                // 生成错误报告文件
                generateErrorReport(taskId, errors);
            }

            taskService.completeTask(taskId, success, message);
            log.info("角色导入任务{}完成，成功: {}, 失败: {}", taskId, successCount, errors.size());

        } catch (Exception e) {
            log.error("角色导入任务{}执行失败", taskId, e);
            taskService.completeTask(taskId, false, "导入失败: " + e.getMessage());
        }
    }

    /**
     * 读取并验证导入数据
     */
    private List<RoleImportDTO> readAndValidateImportData(String fileName) {
        // 这里应该从文件系统或MinIO读取文件
        // 为了简化，此处返回模拟数据
        // 实际实现中需要根据fileName读取真实文件
        return new ArrayList<>();
    }

    /**
     * 处理批次数据
     */
    private BatchResult processBatch(List<RoleImportDTO> batch, int startRowNumber) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < batch.size(); i++) {
            RoleImportDTO importDTO = batch.get(i);
            importDTO.setRowNumber(startRowNumber + i);

            try {
                // 验证数据
                List<String> validationErrors = validateRoleData(importDTO);
                if (!validationErrors.isEmpty()) {
                    errors.addAll(validationErrors);
                    continue;
                }

                // 转换并创建角色
                RoleCreateDTO createDTO = RoleImportExportConvert.INSTANCE.toCreateDTO(importDTO);
                roleService.createRole(createDTO);
                successCount++;

            } catch (Exception e) {
                String error = String.format("第%d行: %s", importDTO.getRowNumber(), e.getMessage());
                errors.add(error);
                log.warn("导入角色失败: {}", error, e);
            }
        }

        return new BatchResult(successCount, errors);
    }

    /**
     * 验证角色数据
     */
    private List<String> validateRoleData(RoleImportDTO importDTO) {
        List<String> errors = new ArrayList<>();
        int rowNumber = importDTO.getRowNumber();

        // 角色编码重复检查
        if (StrUtil.isNotBlank(importDTO.getRoleCode())) {
            SysRoleDO existingRole = roleMapper.selectByRoleCode(importDTO.getRoleCode());
            if (existingRole != null) {
                errors.add(String.format("第%d行: 角色编码[%s]已存在", rowNumber, importDTO.getRoleCode()));
            }
        }

        // 角色名称重复检查
        if (StrUtil.isNotBlank(importDTO.getRoleName())) {
            SysRoleDO existingRole = roleMapper.selectByRoleName(importDTO.getRoleName());
            if (existingRole != null) {
                errors.add(String.format("第%d行: 角色名称[%s]已存在", rowNumber, importDTO.getRoleName()));
            }
        }

        return errors;
    }

    /**
     * 生成错误报告
     */
    private void generateErrorReport(Long taskId, List<String> errors) {
        // 实现错误报告文件生成逻辑
        // 保存到文件系统或MinIO，并更新任务的文件路径
        log.info("为任务{}生成错误报告，错误数量: {}", taskId, errors.size());
    }

    @Override
    public RoleImportValidationResult validateImportFile(MultipartFile file) {
        try {
            // 使用ExcelImportService验证文件
            var validationResult = excelImportService.validateExcel(file, RoleImportDTO.class);
            
            if (!validationResult.isSuccess()) {
                return new RoleImportValidationResult(false, validationResult.getMessage());
            }

            // 读取预览数据
            var importResult = excelImportService.importExcel(file, RoleImportDTO.class);
            List<RoleImportDTO> previewData = importResult.getData().stream()
                    .limit(PREVIEW_SIZE)
                    .collect(Collectors.toList());

            RoleImportValidationResult result = new RoleImportValidationResult(true, "验证成功");
            result.setPreviewData(previewData);
            result.setTotalRows(importResult.getTotalRows());
            result.setValidRows(importResult.getSuccessRows());
            result.setErrorRows(importResult.getErrorRows());
            result.setErrors(importResult.getErrors());

            return result;

        } catch (Exception e) {
            log.error("验证导入文件失败", e);
            return new RoleImportValidationResult(false, "文件验证失败: " + e.getMessage());
        }
    }

    @Override
    @Async
    public CompletableFuture<Long> exportRolesAsync(RolePageDTO queryCondition) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("用户[{}]开始异步导出角色数据", userId);

        // 检查并发限制
        if (!taskService.canCreateTask(userId, ImportExportTask.TaskType.EXPORT)) {
            throw new ServiceException("您有正在执行的导出任务，请稍后再试");
        }

        // 创建任务
        Long taskId = taskService.createTask("角色导出", ImportExportTask.TaskType.EXPORT, 
                                           BUSINESS_TYPE, "角色数据.xlsx");

        // 异步执行导出
        return taskService.executeExportTaskAsync(taskId, () -> processExportTask(taskId, queryCondition));
    }

    /**
     * 处理导出任务
     */
    private void processExportTask(Long taskId, RolePageDTO queryCondition) {
        try {
            log.info("开始处理角色导出任务: {}", taskId);
            taskService.updateTaskStatus(taskId, ImportExportTask.TaskStatus.PROCESSING);

            // 查询总数
            long totalCount = roleMapper.selectCount(queryCondition);
            taskService.updateTaskStatistics(taskId, (int) totalCount, 0, 0);

            // 分批查询并导出
            List<RoleExportVO> allExportData = new ArrayList<>();
            int pageSize = BATCH_SIZE;
            int pageNum = 1;
            int processedCount = 0;

            while (true) {
                queryCondition.setPageNum(pageNum);
                queryCondition.setPageSize(pageSize);
                
                PageResult<SysRoleDO> pageResult = roleMapper.selectPage(queryCondition);
                if (CollectionUtil.isEmpty(pageResult.getRecords())) {
                    break;
                }

                // 转换为导出VO
                List<RoleExportVO> exportVOs = RoleImportExportConvert.INSTANCE.toExportVOList(pageResult.getRecords());
                
                // 设置额外字段
                for (RoleExportVO exportVO : exportVOs) {
                    exportVO.setStatusText(exportVO.getStatusText());
                    // 设置权限信息和用户数量
                    // exportVO.setPermissions(...);
                    // exportVO.setUserCount(...);
                }
                
                allExportData.addAll(exportVOs);

                processedCount += pageResult.getRecords().size();
                taskService.updateTaskProgress(taskId, processedCount, (int) totalCount);

                pageNum++;
            }

            // 生成Excel文件
            String filePath = generateExportFile(taskId, allExportData);
            taskService.setTaskFilePath(taskId, filePath);
            taskService.completeTask(taskId, true, "导出成功");

            log.info("角色导出任务{}完成，导出数据: {}条", taskId, allExportData.size());

        } catch (Exception e) {
            log.error("角色导出任务{}执行失败", taskId, e);
            taskService.completeTask(taskId, false, "导出失败: " + e.getMessage());
        }
    }

    /**
     * 生成导出文件
     */
    private String generateExportFile(Long taskId, List<RoleExportVO> exportData) {
        // 实现文件生成逻辑，保存到文件系统或MinIO
        // 返回文件路径
        return "/exports/role_export_" + taskId + ".xlsx";
    }

    @Override
    public ImportExportTask getTaskDetail(Long taskId) {
        return taskService.getTask(taskId);
    }

    @Override
    public void downloadErrorReport(Long taskId, HttpServletResponse response) {
        // 实现错误报告下载逻辑
        log.info("下载任务{}的错误报告", taskId);
    }

    @Override
    public void downloadExportFile(Long taskId, HttpServletResponse response) {
        // 实现导出文件下载逻辑
        log.info("下载任务{}的导出文件", taskId);
    }

    @Override
    public PageResult<ImportExportTask> getUserTasks(int pageNum, int pageSize) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        return taskService.getTaskPage(pageNum, pageSize, null, BUSINESS_TYPE, null);
    }

    @Override
    public boolean cancelTask(Long taskId) {
        // 实现任务取消逻辑
        log.info("取消任务: {}", taskId);
        return true;
    }

    /**
     * 批处理结果
     */
    private static class BatchResult {
        private final int successCount;
        private final List<String> errors;

        public BatchResult(int successCount, List<String> errors) {
            this.successCount = successCount;
            this.errors = errors;
        }

        public int getSuccessCount() { return successCount; }
        public List<String> getErrors() { return errors; }
    }
}