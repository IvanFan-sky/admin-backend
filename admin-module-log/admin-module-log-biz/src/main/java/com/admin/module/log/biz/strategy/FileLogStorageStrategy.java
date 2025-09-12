package com.admin.module.log.biz.strategy;

import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件日志存储策略
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileLogStorageStrategy implements LogStorageStrategy {

    private final ObjectMapper objectMapper;

    @Value("${admin.log.file.base-path:./logs}")
    private String basePath;

    @Override
    public void storeOperationLog(OperationLogCreateDTO logDTO) {
        try {
            String fileName = "operation_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            Path filePath = ensureDirectory(Paths.get(basePath, "operation", fileName));
            
            String logJson = objectMapper.writeValueAsString(logDTO);
            appendToFile(filePath, logJson);
            
            log.debug("操作日志已存储到文件: {}", filePath);
        } catch (Exception e) {
            log.error("文件存储操作日志失败", e);
            throw new RuntimeException("文件存储操作日志失败", e);
        }
    }

    @Override
    public void storeLoginLog(LoginLogCreateDTO logDTO) {
        try {
            String fileName = "login_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            Path filePath = ensureDirectory(Paths.get(basePath, "login", fileName));
            
            String logJson = objectMapper.writeValueAsString(logDTO);
            appendToFile(filePath, logJson);
            
            log.debug("登录日志已存储到文件: {}", filePath);
        } catch (Exception e) {
            log.error("文件存储登录日志失败", e);
            throw new RuntimeException("文件存储登录日志失败", e);
        }
    }

    @Override
    public String getStorageType() {
        return "file";
    }

    /**
     * 确保目录存在
     */
    private Path ensureDirectory(Path filePath) throws IOException {
        Path directory = filePath.getParent();
        if (directory != null && !Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        return filePath;
    }

    /**
     * 追加内容到文件
     */
    private void appendToFile(Path filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath.toFile(), true)) {
            writer.write(content);
            writer.write(System.lineSeparator());
            writer.flush();
        }
    }
}