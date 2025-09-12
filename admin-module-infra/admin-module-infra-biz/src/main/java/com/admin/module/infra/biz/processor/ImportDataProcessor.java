package com.admin.module.infra.biz.processor;

import com.admin.module.infra.api.vo.ImportErrorDetailVO;

import java.io.InputStream;
import java.util.List;

/**
 * 导入数据处理器接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportDataProcessor {

    /**
     * 支持的数据类型
     *
     * @return 数据类型
     */
    String getSupportedDataType();

    /**
     * 处理导入数据
     *
     * @param taskId 任务ID
     * @param inputStream 文件输入流
     * @param progressCallback 进度回调
     * @return 处理结果
     */
    ImportProcessResult processImport(Long taskId, InputStream inputStream, ProgressCallback progressCallback);

    /**
     * 进度回调接口
     */
    @FunctionalInterface
    interface ProgressCallback {
        void onProgress(int processedCount, int totalCount, String currentOperation);
    }

    /**
     * 导入处理结果
     */
    class ImportProcessResult {
        private final int totalCount;
        private final int successCount;
        private final int failureCount;
        private final List<ImportErrorDetailVO> errors;

        public ImportProcessResult(int totalCount, int successCount, int failureCount, 
                                 List<ImportErrorDetailVO> errors) {
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public List<ImportErrorDetailVO> getErrors() {
            return errors;
        }
    }
}