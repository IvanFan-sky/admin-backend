package com.admin.framework.excel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.framework.excel.service.ExcelExportService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel导出服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public <T> void export(HttpServletResponse response, String fileName, String sheetName, 
                          Class<T> clazz, List<T> data) {
        try {
            // 设置响应头
            setupExcelResponse(response, fileName);
            
            // 导出Excel
            export(response.getOutputStream(), sheetName, clazz, data);
            
        } catch (IOException e) {
            log.error("Excel导出失败", e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }

    @Override
    public <T> void export(OutputStream outputStream, String sheetName, Class<T> clazz, List<T> data) {
        try {
            // 使用EasyExcel进行导出
            EasyExcel.write(outputStream, clazz)
                    .sheet(StrUtil.isNotBlank(sheetName) ? sheetName : "Sheet1")
                    .doWrite(data);
                    
            log.info("Excel导出完成，导出数据: {}行", CollectionUtil.size(data));
            
        } catch (Exception e) {
            log.error("Excel导出失败", e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }

    @Override
    public void exportMultiSheet(HttpServletResponse response, String fileName, List<SheetData<?>> sheets) {
        if (CollectionUtil.isEmpty(sheets)) {
            throw new IllegalArgumentException("导出数据不能为空");
        }

        try {
            // 设置响应头
            setupExcelResponse(response, fileName);
            
            // 创建ExcelWriter
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            
            try {
                // 遍历每个工作表
                for (int i = 0; i < sheets.size(); i++) {
                    SheetData<?> sheetData = sheets.get(i);
                    
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetData.getSheetName())
                            .head(sheetData.getClazz())
                            .build();
                    
                    excelWriter.write(sheetData.getData(), writeSheet);
                }
                
                log.info("多工作表Excel导出完成，工作表数量: {}", sheets.size());
                
            } finally {
                excelWriter.close();
            }
            
        } catch (IOException e) {
            log.error("多工作表Excel导出失败", e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }

    @Override
    public <T> void exportTemplate(HttpServletResponse response, String fileName, String sheetName, Class<T> clazz) {
        try {
            // 设置响应头
            setupExcelResponse(response, fileName);
            
            // 导出空模板（只有表头）
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(StrUtil.isNotBlank(sheetName) ? sheetName : "Sheet1")
                    .doWrite(List.of()); // 空数据，只导出表头
                    
            log.info("Excel模板导出完成，模板类: {}", clazz.getSimpleName());
            
        } catch (IOException e) {
            log.error("Excel模板导出失败", e);
            throw new RuntimeException("Excel模板导出失败: " + e.getMessage());
        }
    }

    /**
     * 设置Excel导出响应头
     */
    private void setupExcelResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        
        // 处理文件名编码
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        
        // 禁用缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 批量导出大数据量
     */
    public <T> void exportLargeData(HttpServletResponse response, String fileName, String sheetName,
                                   Class<T> clazz, DataProvider<T> dataProvider) {
        try {
            // 设置响应头
            setupExcelResponse(response, fileName);
            
            // 创建ExcelWriter，支持流式写入
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), clazz).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            
            try {
                int pageNum = 1;
                int pageSize = 1000; // 每批处理1000条
                int totalCount = 0;
                
                while (true) {
                    // 分批获取数据
                    List<T> batchData = dataProvider.getData(pageNum, pageSize);
                    if (CollectionUtil.isEmpty(batchData)) {
                        break;
                    }
                    
                    // 写入当前批次数据
                    excelWriter.write(batchData, writeSheet);
                    totalCount += batchData.size();
                    pageNum++;
                    
                    log.debug("已导出{}条数据", totalCount);
                    
                    // 如果返回数据少于pageSize，说明是最后一批
                    if (batchData.size() < pageSize) {
                        break;
                    }
                }
                
                log.info("大数据量Excel导出完成，总计: {}行", totalCount);
                
            } finally {
                excelWriter.close();
            }
            
        } catch (IOException e) {
            log.error("大数据量Excel导出失败", e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }

    /**
     * 数据提供者接口，用于大数据量分批导出
     */
    @FunctionalInterface
    public interface DataProvider<T> {
        /**
         * 分页获取数据
         * 
         * @param pageNum 页码（从1开始）
         * @param pageSize 页大小
         * @return 数据列表
         */
        List<T> getData(int pageNum, int pageSize);
    }

    /**
     * 导出带样式的Excel
     */
    public <T> void exportWithStyle(HttpServletResponse response, String fileName, String sheetName,
                                   Class<T> clazz, List<T> data, ExcelStyleProvider styleProvider) {
        try {
            // 设置响应头
            setupExcelResponse(response, fileName);
            
            // 创建带样式的ExcelWriter
            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(styleProvider.getStyleHandler()) // 注册样式处理器
                    .sheet(sheetName)
                    .doWrite(data);
                    
            log.info("带样式Excel导出完成，导出数据: {}行", CollectionUtil.size(data));
            
        } catch (IOException e) {
            log.error("带样式Excel导出失败", e);
            throw new RuntimeException("Excel导出失败: " + e.getMessage());
        }
    }

    /**
     * Excel样式提供者接口
     */
    @FunctionalInterface
    public interface ExcelStyleProvider {
        /**
         * 获取样式处理器
         * 
         * @return 样式处理器
         */
        com.alibaba.excel.write.handler.WriteHandler getStyleHandler();
    }
}