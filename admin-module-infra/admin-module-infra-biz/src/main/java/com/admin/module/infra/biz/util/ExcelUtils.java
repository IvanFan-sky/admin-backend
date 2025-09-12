package com.admin.module.infra.biz.util;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Excel文件处理工具类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class ExcelUtils {

    /**
     * 读取Excel文件数据
     *
     * @param inputStream 输入流
     * @param clazz 数据类型
     * @param dataConsumer 数据消费者
     * @param errorConsumer 错误消费者
     * @param <T> 数据类型
     * @return 读取的总行数
     */
    public static <T> int readExcel(InputStream inputStream, 
                                   Class<T> clazz, 
                                   Consumer<List<T>> dataConsumer,
                                   Consumer<List<ImportErrorDetailVO>> errorConsumer) {
        
        DataReadListener<T> listener = new DataReadListener<>(dataConsumer, errorConsumer);
        
        try (ExcelReader excelReader = EasyExcel.read(inputStream, clazz, listener).build()) {
            excelReader.readAll();
        } catch (Exception e) {
            log.error("读取Excel文件失败", e);
            throw new ServiceException(ErrorCode.FILE_READ_FAILED, "Excel文件读取失败: " + e.getMessage());
        }
        
        return listener.getTotalRowCount();
    }

    /**
     * 写入Excel文件数据
     *
     * @param outputStream 输出流
     * @param clazz 数据类型
     * @param data 数据列表
     * @param sheetName 工作表名称
     * @param <T> 数据类型
     */
    public static <T> void writeExcel(OutputStream outputStream, 
                                     Class<T> clazz, 
                                     List<T> data, 
                                     String sheetName) {
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream, clazz).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).build();
            excelWriter.write(data, writeSheet);
        } catch (Exception e) {
            log.error("写入Excel文件失败", e);
            throw new ServiceException(ErrorCode.FILE_WRITE_FAILED, "Excel文件写入失败: " + e.getMessage());
        }
    }

    /**
     * 分批写入Excel文件数据
     *
     * @param outputStream 输出流
     * @param clazz 数据类型
     * @param dataProvider 数据提供者
     * @param sheetName 工作表名称
     * @param batchSize 批次大小
     * @param <T> 数据类型
     */
    public static <T> void writeExcelInBatches(OutputStream outputStream, 
                                              Class<T> clazz, 
                                              BatchDataProvider<T> dataProvider,
                                              String sheetName,
                                              int batchSize) {
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream, clazz).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).build();
            
            int offset = 0;
            List<T> batch;
            
            while (!(batch = dataProvider.getData(offset, batchSize)).isEmpty()) {
                excelWriter.write(batch, writeSheet);
                offset += batch.size();
                
                if (batch.size() < batchSize) {
                    break; // 最后一批数据
                }
            }
            
            log.info("分批写入Excel完成，总行数: {}", offset);
        } catch (Exception e) {
            log.error("分批写入Excel文件失败", e);
            throw new ServiceException(ErrorCode.FILE_WRITE_FAILED, "Excel文件写入失败: " + e.getMessage());
        }
    }

    /**
     * 创建模板文件
     *
     * @param outputStream 输出流
     * @param clazz 数据类型
     * @param sheetName 工作表名称
     * @param <T> 数据类型
     */
    public static <T> void createTemplate(OutputStream outputStream, 
                                         Class<T> clazz, 
                                         String sheetName) {
        writeExcel(outputStream, clazz, new ArrayList<>(), sheetName);
        log.info("创建模板文件成功，工作表: {}", sheetName);
    }

    /**
     * 数据读取监听器
     */
    private static class DataReadListener<T> extends AnalysisEventListener<T> {
        private final List<T> dataList = new ArrayList<>();
        private final List<ImportErrorDetailVO> errorList = new ArrayList<>();
        private final Consumer<List<T>> dataConsumer;
        private final Consumer<List<ImportErrorDetailVO>> errorConsumer;
        private int currentRowNum = 0;
        private int totalRowCount = 0;

        public DataReadListener(Consumer<List<T>> dataConsumer, 
                               Consumer<List<ImportErrorDetailVO>> errorConsumer) {
            this.dataConsumer = dataConsumer;
            this.errorConsumer = errorConsumer;
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            currentRowNum = context.readRowHolder().getRowIndex() + 1; // Excel行号从1开始
            totalRowCount++;
            
            // 检查行数限制
            if (totalRowCount > ImportExportConstants.RowLimit.MAX_IMPORT_ROWS) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, 
                    "导入数据行数超限，最大允许" + ImportExportConstants.RowLimit.MAX_IMPORT_ROWS + "行");
            }
            
            dataList.add(data);
            
            // 达到批处理大小时处理数据
            if (dataList.size() >= ImportExportConstants.RowLimit.BATCH_PROCESS_SIZE) {
                processDataBatch();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 处理剩余数据
            if (!dataList.isEmpty()) {
                processDataBatch();
            }
            
            // 处理剩余错误
            if (!errorList.isEmpty() && errorConsumer != null) {
                errorConsumer.accept(new ArrayList<>(errorList));
                errorList.clear();
            }
            
            log.info("Excel数据读取完成，总行数: {}", totalRowCount);
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            // 记录解析异常
            ImportErrorDetailVO errorDetail = new ImportErrorDetailVO();
            errorDetail.setRowNumber(currentRowNum);
            errorDetail.setErrorType(ImportExportConstants.ErrorType.FORMAT_ERROR);
            errorDetail.setErrorMessage("数据解析失败: " + exception.getMessage());
            
            errorList.add(errorDetail);
            
            log.warn("Excel数据解析异常，行号: {}, 错误: {}", currentRowNum, exception.getMessage());
        }

        private void processDataBatch() {
            if (dataConsumer != null && !dataList.isEmpty()) {
                dataConsumer.accept(new ArrayList<>(dataList));
            }
            dataList.clear();
        }

        public int getTotalRowCount() {
            return totalRowCount;
        }
    }

    /**
     * 批次数据提供者接口
     */
    @FunctionalInterface
    public interface BatchDataProvider<T> {
        List<T> getData(int offset, int limit);
    }
}