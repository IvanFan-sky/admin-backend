package com.admin.module.infra.biz.security;

import java.io.InputStream;
import java.util.Set;

/**
 * 文件安全扫描器接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface FileSecurityScanner {

    /**
     * 扫描文件安全性
     *
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @return 扫描结果
     */
    FileScanResult scanFile(InputStream inputStream, String fileName, String contentType);

    /**
     * 检测文件真实类型
     *
     * @param inputStream 文件输入流
     * @param declaredType 声明的文件类型
     * @return 检测结果
     */
    FileTypeDetectionResult detectFileType(InputStream inputStream, String declaredType);

    /**
     * 扫描恶意内容
     *
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @return 扫描结果
     */
    MalwareScanResult scanMalware(InputStream inputStream, String fileName);

    /**
     * 检测敏感信息
     *
     * @param inputStream 文件输入流
     * @param contentType 文件类型
     * @return 检测结果
     */
    SensitiveInfoDetectionResult detectSensitiveInfo(InputStream inputStream, String contentType);

    /**
     * 文件扫描结果
     */
    class FileScanResult {
        private boolean safe;
        private Set<SecurityRisk> risks;
        private String message;
        private FileTypeDetectionResult typeDetection;
        private MalwareScanResult malwareResult;
        private SensitiveInfoDetectionResult sensitiveInfoResult;

        // Constructors
        public FileScanResult(boolean safe, Set<SecurityRisk> risks, String message) {
            this.safe = safe;
            this.risks = risks;
            this.message = message;
        }

        public static FileScanResult safe() {
            return new FileScanResult(true, Set.of(), "文件安全");
        }

        public static FileScanResult unsafe(Set<SecurityRisk> risks, String message) {
            return new FileScanResult(false, risks, message);
        }

        // Getters and Setters
        public boolean isSafe() { return safe; }
        public void setSafe(boolean safe) { this.safe = safe; }
        public Set<SecurityRisk> getRisks() { return risks; }
        public void setRisks(Set<SecurityRisk> risks) { this.risks = risks; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public FileTypeDetectionResult getTypeDetection() { return typeDetection; }
        public void setTypeDetection(FileTypeDetectionResult typeDetection) { this.typeDetection = typeDetection; }
        public MalwareScanResult getMalwareResult() { return malwareResult; }
        public void setMalwareResult(MalwareScanResult malwareResult) { this.malwareResult = malwareResult; }
        public SensitiveInfoDetectionResult getSensitiveInfoResult() { return sensitiveInfoResult; }
        public void setSensitiveInfoResult(SensitiveInfoDetectionResult sensitiveInfoResult) { this.sensitiveInfoResult = sensitiveInfoResult; }
    }

    /**
     * 文件类型检测结果
     */
    class FileTypeDetectionResult {
        private String detectedType;
        private boolean typeMatches;
        private double confidence;

        public FileTypeDetectionResult(String detectedType, boolean typeMatches, double confidence) {
            this.detectedType = detectedType;
            this.typeMatches = typeMatches;
            this.confidence = confidence;
        }

        // Getters
        public String getDetectedType() { return detectedType; }
        public boolean isTypeMatches() { return typeMatches; }
        public double getConfidence() { return confidence; }
    }

    /**
     * 恶意软件扫描结果
     */
    class MalwareScanResult {
        private boolean clean;
        private String threatName;
        private ThreatLevel threatLevel;
        private String scanEngine;

        public MalwareScanResult(boolean clean, String threatName, ThreatLevel threatLevel, String scanEngine) {
            this.clean = clean;
            this.threatName = threatName;
            this.threatLevel = threatLevel;
            this.scanEngine = scanEngine;
        }

        public static MalwareScanResult clean(String scanEngine) {
            return new MalwareScanResult(true, null, ThreatLevel.NONE, scanEngine);
        }

        public static MalwareScanResult threat(String threatName, ThreatLevel level, String scanEngine) {
            return new MalwareScanResult(false, threatName, level, scanEngine);
        }

        // Getters
        public boolean isClean() { return clean; }
        public String getThreatName() { return threatName; }
        public ThreatLevel getThreatLevel() { return threatLevel; }
        public String getScanEngine() { return scanEngine; }
    }

    /**
     * 敏感信息检测结果
     */
    class SensitiveInfoDetectionResult {
        private boolean hasSensitiveInfo;
        private Set<SensitiveInfoType> detectedTypes;
        private int sensitiveItemCount;

        public SensitiveInfoDetectionResult(boolean hasSensitiveInfo, Set<SensitiveInfoType> detectedTypes, int sensitiveItemCount) {
            this.hasSensitiveInfo = hasSensitiveInfo;
            this.detectedTypes = detectedTypes;
            this.sensitiveItemCount = sensitiveItemCount;
        }

        public static SensitiveInfoDetectionResult clean() {
            return new SensitiveInfoDetectionResult(false, Set.of(), 0);
        }

        public static SensitiveInfoDetectionResult detected(Set<SensitiveInfoType> types, int count) {
            return new SensitiveInfoDetectionResult(true, types, count);
        }

        // Getters
        public boolean hasSensitiveInfo() { return hasSensitiveInfo; }
        public Set<SensitiveInfoType> getDetectedTypes() { return detectedTypes; }
        public int getSensitiveItemCount() { return sensitiveItemCount; }
    }

    /**
     * 安全风险类型
     */
    enum SecurityRisk {
        FILE_TYPE_MISMATCH("文件类型不匹配"),
        MALWARE_DETECTED("检测到恶意软件"),
        VIRUS_DETECTED("检测到病毒"),
        SENSITIVE_INFO_DETECTED("包含敏感信息"),
        SUSPICIOUS_CONTENT("包含可疑内容"),
        CORRUPTED_FILE("文件已损坏"),
        OVERSIZED_FILE("文件过大"),
        INVALID_FORMAT("无效的文件格式");

        private final String description;

        SecurityRisk(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    /**
     * 威胁等级
     */
    enum ThreatLevel {
        NONE("无威胁"),
        LOW("低风险"),
        MEDIUM("中风险"),
        HIGH("高风险"),
        CRITICAL("严重威胁");

        private final String description;

        ThreatLevel(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    /**
     * 敏感信息类型
     */
    enum SensitiveInfoType {
        ID_CARD("身份证号"),
        PHONE_NUMBER("手机号码"),
        EMAIL("邮箱地址"),
        BANK_CARD("银行卡号"),
        PASSWORD("密码"),
        PRIVATE_KEY("私钥"),
        API_KEY("API密钥"),
        DATABASE_CONNECTION("数据库连接"),
        PERSONAL_INFO("个人信息");

        private final String description;

        SensitiveInfoType(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}