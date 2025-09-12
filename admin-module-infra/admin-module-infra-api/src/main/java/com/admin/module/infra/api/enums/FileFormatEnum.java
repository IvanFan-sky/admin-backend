package com.admin.module.infra.api.enums;

/**
 * 文件格式枚举
 * 
 * 定义支持的文件格式类型
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public enum FileFormatEnum {

    /**
     * Excel 2007+ 格式
     */
    XLSX("xlsx", "Excel 2007+", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    /**
     * Excel 97-2003 格式
     */
    XLS("xls", "Excel 97-2003", "application/vnd.ms-excel"),

    /**
     * CSV 逗号分隔值格式
     */
    CSV("csv", "逗号分隔值", "text/csv");

    /**
     * 文件扩展名
     */
    private final String extension;

    /**
     * 格式描述
     */
    private final String description;

    /**
     * MIME类型
     */
    private final String mimeType;

    FileFormatEnum(String extension, String description, String mimeType) {
        this.extension = extension;
        this.description = description;
        this.mimeType = mimeType;
    }

    /**
     * 获取文件扩展名
     *
     * @return 文件扩展名
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 获取格式描述
     *
     * @return 格式描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取MIME类型
     *
     * @return MIME类型
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * 根据文件扩展名获取枚举
     *
     * @param extension 文件扩展名
     * @return FileFormatEnum枚举，如果未找到返回null
     */
    public static FileFormatEnum getByExtension(String extension) {
        if (extension == null) {
            return null;
        }
        String cleanExt = extension.toLowerCase().replaceAll("^\\.", "");
        for (FileFormatEnum format : values()) {
            if (format.getExtension().equals(cleanExt)) {
                return format;
            }
        }
        return null;
    }

    /**
     * 根据MIME类型获取枚举
     *
     * @param mimeType MIME类型
     * @return FileFormatEnum枚举，如果未找到返回null
     */
    public static FileFormatEnum getByMimeType(String mimeType) {
        for (FileFormatEnum format : values()) {
            if (format.getMimeType().equals(mimeType)) {
                return format;
            }
        }
        return null;
    }

    /**
     * 判断是否为支持的文件格式
     *
     * @param extension 文件扩展名
     * @return true-支持，false-不支持
     */
    public static boolean isSupported(String extension) {
        return getByExtension(extension) != null;
    }

    /**
     * 判断是否为Excel格式
     *
     * @return true-Excel格式，false-非Excel格式
     */
    public boolean isExcel() {
        return this == XLSX || this == XLS;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", extension, description);
    }
}