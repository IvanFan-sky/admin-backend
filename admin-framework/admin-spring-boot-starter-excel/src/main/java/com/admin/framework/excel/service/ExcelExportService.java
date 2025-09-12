package com.admin.framework.excel.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel导出服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ExcelExportService {

    /**
     * 导出Excel到HTTP响应
     * 
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param sheetName 工作表名称
     * @param clazz 数据类型
     * @param data 数据列表
     * @param <T> 数据类型泛型
     */
    <T> void export(HttpServletResponse response, String fileName, String sheetName, 
                   Class<T> clazz, List<T> data);

    /**
     * 导出Excel到输出流
     * 
     * @param outputStream 输出流
     * @param sheetName 工作表名称
     * @param clazz 数据类型
     * @param data 数据列表
     * @param <T> 数据类型泛型
     */
    <T> void export(OutputStream outputStream, String sheetName, Class<T> clazz, List<T> data);

    /**
     * 导出多个工作表到HTTP响应
     * 
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param sheets 工作表数据
     */
    void exportMultiSheet(HttpServletResponse response, String fileName, List<SheetData<?>> sheets);

    /**
     * 导出模板到HTTP响应
     * 
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param sheetName 工作表名称
     * @param clazz 数据类型
     * @param <T> 数据类型泛型
     */
    <T> void exportTemplate(HttpServletResponse response, String fileName, String sheetName, Class<T> clazz);

    /**
     * 工作表数据封装类
     */
    class SheetData<T> {
        private String sheetName;
        private Class<T> clazz;
        private List<T> data;

        public SheetData(String sheetName, Class<T> clazz, List<T> data) {
            this.sheetName = sheetName;
            this.clazz = clazz;
            this.data = data;
        }

        // Getters
        public String getSheetName() { return sheetName; }
        public Class<T> getClazz() { return clazz; }
        public List<T> getData() { return data; }
    }
}
