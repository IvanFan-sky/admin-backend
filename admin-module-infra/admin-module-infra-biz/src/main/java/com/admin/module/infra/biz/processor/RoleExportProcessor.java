package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.ExportConfigDTO;
import com.admin.module.infra.biz.util.ExcelUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色导出处理器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleExportProcessor implements ExportDataProcessor {

    @Override
    public String getSupportedDataType() {
        return ImportExportConstants.DataType.ROLE;
    }

    @Override
    public ExportProcessResult processExport(Long taskId, ExportConfigDTO exportConfig, 
                                           OutputStream outputStream, ProgressCallback progressCallback) {
        log.info("开始处理角色导出，任务ID: {}", taskId);

        try {
            // 查询总数
            int totalCount = getRoleTotalCount(exportConfig);
            
            if (progressCallback != null) {
                progressCallback.onProgress(0, totalCount, "开始导出角色数据");
            }

            // 生成文件名
            String fileName = generateFileName(exportConfig.getFileFormat());

            // 分批导出数据
            ExcelUtils.writeExcelInBatches(outputStream, RoleExportVO.class,
                (offset, limit) -> getRoleExportData(exportConfig, offset, limit, progressCallback, totalCount),
                ImportExportConstants.Excel.SHEET_NAME_ROLES,
                ImportExportConstants.RowLimit.BATCH_PROCESS_SIZE
            );

            // 模拟文件大小计算
            long fileSize = totalCount * 150L; // 估算每行约150字节

            if (progressCallback != null) {
                progressCallback.onProgress(totalCount, totalCount, "导出完成");
            }

            log.info("角色导出处理完成，任务ID: {}, 总数: {}, 文件大小: {} bytes", 
                    taskId, totalCount, fileSize);

            return new ExportProcessResult(totalCount, fileSize, fileName);

        } catch (Exception e) {
            log.error("角色导出处理失败，任务ID: {}", taskId, e);
            throw new RuntimeException("角色导出处理失败: " + e.getMessage(), e);
        }
    }

    private int getRoleTotalCount(ExportConfigDTO exportConfig) {
        // TODO: 根据查询条件统计角色总数
        // 实现实际的角色数量查询
        
        // 模拟数据
        return 50;
    }

    private List<RoleExportVO> getRoleExportData(ExportConfigDTO exportConfig, int offset, int limit,
                                               ProgressCallback progressCallback, int totalCount) {
        // TODO: 根据查询条件和分页参数查询角色数据
        // 1. 构建查询条件
        // 2. 分页查询角色数据
        // 3. 转换为导出VO对象
        // 4. 根据导出列配置过滤字段

        List<RoleExportVO> exportList = new ArrayList<>();

        // 模拟数据查询
        for (int i = 0; i < Math.min(limit, 10); i++) { // 限制模拟数据数量
            RoleExportVO roleExport = new RoleExportVO();
            roleExport.setRoleCode("role_" + (offset + i + 1));
            roleExport.setRoleName("角色" + (offset + i + 1));
            roleExport.setRoleDesc("角色" + (offset + i + 1) + "描述");
            roleExport.setSortOrder(offset + i + 1);
            roleExport.setStatus("启用");
            roleExport.setRemark("导出测试角色");
            roleExport.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            exportList.add(roleExport);
        }

        // 进度回调
        if (progressCallback != null) {
            int processedCount = offset + exportList.size();
            progressCallback.onProgress(processedCount, totalCount, 
                "已处理 " + processedCount + " / " + totalCount + " 条角色数据");
        }

        log.debug("查询角色导出数据，offset: {}, limit: {}, 实际返回: {}", offset, limit, exportList.size());
        
        return exportList;
    }

    private String generateFileName(String fileFormat) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("角色数据_%s.%s", timestamp, fileFormat);
    }

    /**
     * 角色导出VO
     */
    @Data
    public static class RoleExportVO {
        
        private String roleCode;
        private String roleName;
        private String roleDesc;
        private Integer sortOrder;
        private String status;
        private String remark;
        private String createTime;
    }
}