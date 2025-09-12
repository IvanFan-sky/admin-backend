package com.admin.module.infra.biz.security.impl;

import com.admin.module.infra.api.enums.FileErrorCode;
import com.admin.module.infra.api.exception.FileBusinessException;
import com.admin.module.infra.biz.security.FileSecurityScanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 默认文件安全扫描器实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@Slf4j
public class DefaultFileSecurityScanner implements FileSecurityScanner {

    private final Tika tika = new Tika();

    // 恶意文件特征
    private static final Set<String> MALICIOUS_PATTERNS = Set.of(
            "eval(", "exec(", "system(", "shell_exec(", 
            "<script>", "javascript:", "vbscript:",
            "<?php", "<%", "<% ", "<%@"
    );

    // 敏感信息正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\b[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b1[3-9]\\d{9}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\b\\d{16,19}\\b");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?i)(password|pwd|pass)\\s*[:=]\\s*['\"]?([^\\s'\"]+)['\"]?");

    @Override
    public FileScanResult scanFile(InputStream inputStream, String fileName, String contentType) {
        try {
            // 创建标记支持的输入流，以便多次读取
            InputStream markSupportedStream = inputStream.markSupported() 
                ? inputStream 
                : new BufferedInputStream(inputStream);

            Set<SecurityRisk> risks = new HashSet<>();
            StringBuilder messageBuilder = new StringBuilder();

            // 1. 文件类型检测
            FileTypeDetectionResult typeDetection = detectFileType(markSupportedStream, contentType);
            if (!typeDetection.isTypeMatches()) {
                risks.add(SecurityRisk.FILE_TYPE_MISMATCH);
                messageBuilder.append("文件类型不匹配；");
            }

            // 2. 恶意内容扫描
            markSupportedStream.reset();
            MalwareScanResult malwareResult = scanMalware(markSupportedStream, fileName);
            if (!malwareResult.isClean()) {
                risks.add(SecurityRisk.MALWARE_DETECTED);
                messageBuilder.append("检测到恶意内容；");
            }

            // 3. 敏感信息检测
            markSupportedStream.reset();
            SensitiveInfoDetectionResult sensitiveResult = detectSensitiveInfo(markSupportedStream, contentType);
            if (sensitiveResult.hasSensitiveInfo()) {
                risks.add(SecurityRisk.SENSITIVE_INFO_DETECTED);
                messageBuilder.append("包含敏感信息；");
            }

            // 构建结果
            boolean safe = risks.isEmpty();
            String message = safe ? "文件安全" : messageBuilder.toString();

            FileScanResult result = safe ? FileScanResult.safe() : FileScanResult.unsafe(risks, message);
            result.setTypeDetection(typeDetection);
            result.setMalwareResult(malwareResult);
            result.setSensitiveInfoResult(sensitiveResult);

            log.debug("文件安全扫描完成，文件: {}, 安全: {}, 风险: {}", fileName, safe, risks);
            return result;

        } catch (Exception e) {
            log.error("文件安全扫描失败，文件: {}", fileName, e);
            throw new FileBusinessException(FileErrorCode.FILE_SECURITY_SCAN_FAILED, "文件安全扫描失败", e);
        }
    }

    @Override
    public FileTypeDetectionResult detectFileType(InputStream inputStream, String declaredType) {
        try {
            inputStream.mark(8192); // 标记位置以便重置
            
            String detectedType = tika.detect(TikaInputStream.get(inputStream));
            inputStream.reset();

            boolean typeMatches = detectedType.equals(declaredType) || isCompatibleType(detectedType, declaredType);
            double confidence = typeMatches ? 1.0 : 0.0;

            return new FileTypeDetectionResult(detectedType, typeMatches, confidence);

        } catch (IOException e) {
            log.error("文件类型检测失败", e);
            return new FileTypeDetectionResult("unknown", false, 0.0);
        }
    }

    @Override
    public MalwareScanResult scanMalware(InputStream inputStream, String fileName) {
        try {
            inputStream.mark(1024 * 1024); // 标记1MB位置
            
            // 读取文件内容进行基础模式匹配
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null && lineCount < 1000) { // 最多读取1000行
                content.append(line).append("\n");
                lineCount++;
            }
            
            inputStream.reset();
            
            String fileContent = content.toString().toLowerCase();
            
            // 检查恶意模式
            for (String pattern : MALICIOUS_PATTERNS) {
                if (fileContent.contains(pattern.toLowerCase())) {
                    log.warn("检测到可疑内容，文件: {}, 模式: {}", fileName, pattern);
                    return MalwareScanResult.threat("Suspicious Pattern: " + pattern, ThreatLevel.MEDIUM, "内置扫描器");
                }
            }

            // 检查文件扩展名与内容的一致性
            if (fileName.toLowerCase().endsWith(".exe") || fileName.toLowerCase().endsWith(".bat") || 
                fileName.toLowerCase().endsWith(".com") || fileName.toLowerCase().endsWith(".scr")) {
                log.warn("检测到可执行文件，文件: {}", fileName);
                return MalwareScanResult.threat("Executable File", ThreatLevel.HIGH, "内置扫描器");
            }

            return MalwareScanResult.clean("内置扫描器");

        } catch (IOException e) {
            log.error("恶意软件扫描失败，文件: {}", fileName, e);
            return MalwareScanResult.threat("Scan Error", ThreatLevel.LOW, "内置扫描器");
        }
    }

    @Override
    public SensitiveInfoDetectionResult detectSensitiveInfo(InputStream inputStream, String contentType) {
        // 只对文本文件进行敏感信息检测
        if (!isTextFile(contentType)) {
            return SensitiveInfoDetectionResult.clean();
        }

        try {
            inputStream.mark(1024 * 1024); // 标记1MB位置
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null && lineCount < 1000) { // 最多读取1000行
                content.append(line).append("\n");
                lineCount++;
            }

            inputStream.reset();
            
            String fileContent = content.toString();
            Set<SensitiveInfoType> detectedTypes = new HashSet<>();
            int sensitiveItemCount = 0;

            // 检测身份证号
            if (ID_CARD_PATTERN.matcher(fileContent).find()) {
                detectedTypes.add(SensitiveInfoType.ID_CARD);
                sensitiveItemCount += countMatches(ID_CARD_PATTERN, fileContent);
            }

            // 检测手机号
            if (PHONE_PATTERN.matcher(fileContent).find()) {
                detectedTypes.add(SensitiveInfoType.PHONE_NUMBER);
                sensitiveItemCount += countMatches(PHONE_PATTERN, fileContent);
            }

            // 检测邮箱
            if (EMAIL_PATTERN.matcher(fileContent).find()) {
                detectedTypes.add(SensitiveInfoType.EMAIL);
                sensitiveItemCount += countMatches(EMAIL_PATTERN, fileContent);
            }

            // 检测银行卡号
            if (BANK_CARD_PATTERN.matcher(fileContent).find()) {
                detectedTypes.add(SensitiveInfoType.BANK_CARD);
                sensitiveItemCount += countMatches(BANK_CARD_PATTERN, fileContent);
            }

            // 检测密码
            if (PASSWORD_PATTERN.matcher(fileContent).find()) {
                detectedTypes.add(SensitiveInfoType.PASSWORD);
                sensitiveItemCount += countMatches(PASSWORD_PATTERN, fileContent);
            }

            return detectedTypes.isEmpty() 
                ? SensitiveInfoDetectionResult.clean()
                : SensitiveInfoDetectionResult.detected(detectedTypes, sensitiveItemCount);

        } catch (IOException e) {
            log.error("敏感信息检测失败", e);
            return SensitiveInfoDetectionResult.clean();
        }
    }

    /**
     * 判断是否为兼容的文件类型
     */
    private boolean isCompatibleType(String detectedType, String declaredType) {
        if (detectedType == null || declaredType == null) {
            return false;
        }

        // 处理一些常见的兼容情况
        if (detectedType.equals("application/zip") && 
            (declaredType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
             declaredType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为文本文件
     */
    private boolean isTextFile(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("text/") || 
               contentType.equals("application/json") ||
               contentType.equals("application/xml") ||
               contentType.contains("csv");
    }

    /**
     * 计算正则表达式匹配次数
     */
    private int countMatches(Pattern pattern, String content) {
        int count = 0;
        var matcher = pattern.matcher(content);
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}