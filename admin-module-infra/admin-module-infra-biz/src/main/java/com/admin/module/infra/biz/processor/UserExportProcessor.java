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
 * 用户导出处理器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserExportProcessor implements ExportDataProcessor {

    @Override
    public String getSupportedDataType() {
        return ImportExportConstants.DataType.USER;
    }

    @Override
    public ExportProcessResult processExport(Long taskId, ExportConfigDTO exportConfig, 
                                           OutputStream outputStream, ProgressCallback progressCallback) {
        log.info("开始处理用户导出，任务ID: {}", taskId);

        try {
            // 查询总数
            int totalCount = getUserTotalCount(exportConfig);
            
            if (progressCallback != null) {
                progressCallback.onProgress(0, totalCount, "开始导出用户数据");
            }

            // 生成文件名
            String fileName = generateFileName(exportConfig.getFileFormat());

            // 分批导出数据
            ExcelUtils.writeExcelInBatches(outputStream, UserExportVO.class,
                (offset, limit) -> getUserExportData(exportConfig, offset, limit, progressCallback, totalCount),
                ImportExportConstants.Excel.SHEET_NAME_USERS,
                ImportExportConstants.RowLimit.BATCH_PROCESS_SIZE
            );

            // 模拟文件大小计算
            long fileSize = totalCount * 200L; // 估算每行约200字节

            if (progressCallback != null) {
                progressCallback.onProgress(totalCount, totalCount, "导出完成");
            }

            log.info("用户导出处理完成，任务ID: {}, 总数: {}, 文件大小: {} bytes", 
                    taskId, totalCount, fileSize);

            return new ExportProcessResult(totalCount, fileSize, fileName);

        } catch (Exception e) {
            log.error("用户导出处理失败，任务ID: {}", taskId, e);
            throw new RuntimeException("用户导出处理失败: " + e.getMessage(), e);
        }
    }

    private int getUserTotalCount(ExportConfigDTO exportConfig) {
        // TODO: 根据查询条件统计用户总数
        // 实现实际的用户数量查询
        
        // 模拟数据
        return 1000;
    }

    private List<UserExportVO> getUserExportData(ExportConfigDTO exportConfig, int offset, int limit,
                                               ProgressCallback progressCallback, int totalCount) {
        // TODO: 根据查询条件和分页参数查询用户数据
        // 1. 构建查询条件
        // 2. 分页查询用户数据
        // 3. 转换为导出VO对象
        // 4. 根据导出列配置过滤字段

        List<UserExportVO> exportList = new ArrayList<>();

        // 模拟数据查询
        for (int i = 0; i < Math.min(limit, 50); i++) { // 限制模拟数据数量
            UserExportVO userExport = new UserExportVO();
            userExport.setUsername("user" + (offset + i + 1));
            userExport.setNickname("用户" + (offset + i + 1));
            userExport.setEmail("user" + (offset + i + 1) + "@example.com");
            userExport.setPhone("1380013800" + String.format("%02d", (offset + i + 1) % 100));
            userExport.setGender("男");
            userExport.setStatus("启用");
            userExport.setBirthday("1990-01-01");
            userExport.setRoleCodes("user,member");
            userExport.setRemark("导出测试数据");
            userExport.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            exportList.add(userExport);
        }

        // 进度回调
        if (progressCallback != null) {
            int processedCount = offset + exportList.size();
            progressCallback.onProgress(processedCount, totalCount, 
                "已处理 " + processedCount + " / " + totalCount + " 条用户数据");
        }

        log.debug("查询用户导出数据，offset: {}, limit: {}, 实际返回: {}", offset, limit, exportList.size());
        
        return exportList;
    }

    private String generateFileName(String fileFormat) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("用户数据_%s.%s", timestamp, fileFormat);
    }

    /**
     * 用户导出VO
     */
    @Data
    public static class UserExportVO {
        
        private String username;
        private String nickname;
        private String email; 
        private String phone;
        private String gender;
        private String birthday;
        private String status;
        private String roleCodes;
        private String remark;
        private String createTime;
    }
}