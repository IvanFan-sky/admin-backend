package com.admin.module.infra.biz.util;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.enums.FileFormatEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class FileUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    /**
     * 验证上传文件
     *
     * @param file 上传文件
     * @param maxSize 最大文件大小
     */
    public static void validateUploadFile(MultipartFile file, Long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "上传文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxSize) {
            throw new ServiceException(ErrorCode.FILE_SIZE_EXCEEDED, 
                String.format("文件大小超限，最大允许%dMB", maxSize / 1024 / 1024));
        }

        // 验证文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!FileFormatEnum.isSupported(extension)) {
            throw new ServiceException(ErrorCode.FILE_FORMAT_NOT_SUPPORTED, 
                "不支持的文件格式，仅支持：xlsx、xls、csv");
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);
        
        return String.format("%s_%s_%s.%s", date, time, uuid.substring(0, 8), extension);
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     */
    public static void createDirectories(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建目录成功: {}", dirPath);
            }
        } catch (IOException e) {
            log.error("创建目录失败: {}", dirPath, e);
            throw new ServiceException(ErrorCode.SYSTEM_ERROR, "创建目录失败");
        }
    }

    /**
     * 保存上传文件
     *
     * @param file 上传文件
     * @param savePath 保存路径
     * @param filename 文件名
     * @return 完整文件路径
     */
    public static String saveUploadFile(MultipartFile file, String savePath, String filename) {
        try {
            // 确保目录存在
            createDirectories(savePath);
            
            // 完整文件路径
            Path fullPath = Paths.get(savePath, filename);
            
            // 保存文件
            Files.copy(file.getInputStream(), fullPath);
            
            log.info("文件保存成功: {}", fullPath.toString());
            return fullPath.toString();
        } catch (IOException e) {
            log.error("文件保存失败: {}", filename, e);
            throw new ServiceException(ErrorCode.FILE_WRITE_FAILED, "文件保存失败");
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功: {}", filePath);
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", filePath, e);
            return 0L;
        }
    }

    /**
     * 获取文件父目录
     *
     * @param filePath 文件路径
     * @return 父目录路径
     */
    public static String getParent(String filePath) {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        return parent != null ? parent.toString() : "";
    }

    /**
     * 清理过期文件
     *
     * @param dirPath 目录路径
     * @param retentionDays 保留天数
     */
    public static void cleanupExpiredFiles(String dirPath, int retentionDays) {
        try {
            Path dir = Paths.get(dirPath);
            if (!Files.exists(dir)) {
                return;
            }

            LocalDateTime expireTime = LocalDateTime.now().minusDays(retentionDays);
            
            Files.walk(dir)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toInstant()
                            .isBefore(expireTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        log.info("删除过期文件: {}", path);
                    } catch (IOException e) {
                        log.error("删除过期文件失败: {}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("清理过期文件失败: {}", dirPath, e);
        }
    }
}