package com.admin.framework.excel.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excel工具类
 * 
 * 提供Excel操作的通用工具方法
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public class ExcelUtils {

    /**
     * 设置Excel下载响应头
     * 
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @throws IOException IO异常
     */
    public static void setExcelResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        
        // 防止中文乱码
        String encodedFileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFileName);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * 生成带时间戳的文件名
     * 
     * @param baseName 基础文件名
     * @return 带时间戳的文件名
     */
    public static String generateFileName(String baseName) {
        if (StrUtil.isBlank(baseName)) {
            baseName = "export";
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return baseName + "_" + timestamp;
    }

    /**
     * 验证文件扩展名
     * 
     * @param fileName 文件名
     * @param allowedExtensions 允许的扩展名数组
     * @return 是否为允许的文件类型
     */
    public static boolean isValidFileExtension(String fileName, String[] allowedExtensions) {
        if (StrUtil.isBlank(fileName) || allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }
        
        String extension = StrUtil.subAfter(fileName, ".", true);
        if (StrUtil.isBlank(extension)) {
            return false;
        }
        
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 验证Excel文件格式
     * 
     * @param fileName 文件名
     * @return 是否为Excel文件
     */
    public static boolean isExcelFile(String fileName) {
        return isValidFileExtension(fileName, new String[]{"xlsx", "xls"});
    }

    /**
     * 验证CSV文件格式
     * 
     * @param fileName 文件名
     * @return 是否为CSV文件
     */
    public static boolean isCsvFile(String fileName) {
        return isValidFileExtension(fileName, new String[]{"csv"});
    }
}
