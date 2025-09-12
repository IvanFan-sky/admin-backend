package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.dto.ExportConfigDTO;

import java.io.OutputStream;

/**
 * 导出数据处理器接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ExportDataProcessor {

    /**
     * 支持的数据类型
     *
     * @return 数据类型
     */
    String getSupportedDataType();

    /**
     * 处理导出数据
     *
     * @param taskId 任务ID
     * @param exportConfig 导出配置
     * @param outputStream 输出流
     * @param progressCallback 进度回调
     * @return 处理结果
     */
    ExportProcessResult processExport(Long taskId, ExportConfigDTO exportConfig, 
                                    OutputStream outputStream, ProgressCallback progressCallback);

    /**
     * 进度回调接口
     */
    @FunctionalInterface
    interface ProgressCallback {
        void onProgress(int processedCount, int totalCount, String currentOperation);
    }

    /**
     * 导出处理结果
     */
    class ExportProcessResult {
        private final int totalCount;
        private final long fileSize;
        private final String fileName;

        public ExportProcessResult(int totalCount, long fileSize, String fileName) {
            this.totalCount = totalCount;
            this.fileSize = fileSize;
            this.fileName = fileName;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getFileName() {
            return fileName;
        }
    }
}