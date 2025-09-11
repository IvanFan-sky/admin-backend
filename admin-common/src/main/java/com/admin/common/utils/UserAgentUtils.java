package com.admin.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 用户代理解析工具类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class UserAgentUtils {

    private static final String UNKNOWN = "Unknown";

    /**
     * 解析浏览器信息
     *
     * @param userAgent 用户代理字符串
     * @return 浏览器名称
     */
    public static String parseBrowser(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }

        try {
            // 按优先级检测浏览器
            if (userAgent.contains("Edg")) {
                return "Microsoft Edge";
            } else if (userAgent.contains("Chrome") && !userAgent.contains("Chromium")) {
                return "Google Chrome";
            } else if (userAgent.contains("Firefox")) {
                return "Mozilla Firefox";
            } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
                return "Safari";
            } else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
                return "Opera";
            } else if (userAgent.contains("Chromium")) {
                return "Chromium";
            } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                return "Internet Explorer";
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            log.warn("解析浏览器信息异常: {}", userAgent, e);
            return UNKNOWN;
        }
    }

    /**
     * 解析操作系统信息
     *
     * @param userAgent 用户代理字符串
     * @return 操作系统名称
     */
    public static String parseOperatingSystem(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }

        try {
            // 移动设备优先检测
            if (userAgent.contains("Android")) {
                return "Android";
            } else if (userAgent.contains("iPhone")) {
                return "iOS (iPhone)";
            } else if (userAgent.contains("iPad")) {
                return "iOS (iPad)";
            } else if (userAgent.contains("iPod")) {
                return "iOS (iPod)";
            }
            
            // 桌面操作系统
            if (userAgent.contains("Windows NT 10.0")) {
                return "Windows 10/11";
            } else if (userAgent.contains("Windows NT 6.3")) {
                return "Windows 8.1";
            } else if (userAgent.contains("Windows NT 6.2")) {
                return "Windows 8";
            } else if (userAgent.contains("Windows NT 6.1")) {
                return "Windows 7";
            } else if (userAgent.contains("Windows NT 6.0")) {
                return "Windows Vista";
            } else if (userAgent.contains("Windows NT 5.1")) {
                return "Windows XP";
            } else if (userAgent.contains("Windows")) {
                return "Windows";
            } else if (userAgent.contains("Mac OS X")) {
                return "macOS";
            } else if (userAgent.contains("Macintosh")) {
                return "Mac OS";
            } else if (userAgent.contains("Linux")) {
                return "Linux";
            } else if (userAgent.contains("Ubuntu")) {
                return "Ubuntu";
            } else if (userAgent.contains("CentOS")) {
                return "CentOS";
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            log.warn("解析操作系统信息异常: {}", userAgent, e);
            return UNKNOWN;
        }
    }

    /**
     * 解析设备类型
     *
     * @param userAgent 用户代理字符串
     * @return 设备类型
     */
    public static String parseDeviceType(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }

        try {
            if (userAgent.contains("Mobile") || userAgent.contains("Android") || 
                userAgent.contains("iPhone") || userAgent.contains("iPod")) {
                return "Mobile";
            } else if (userAgent.contains("iPad") || userAgent.contains("Tablet")) {
                return "Tablet";
            } else {
                return "Desktop";
            }
        } catch (Exception e) {
            log.warn("解析设备类型异常: {}", userAgent, e);
            return UNKNOWN;
        }
    }

    /**
     * 获取浏览器版本
     *
     * @param userAgent 用户代理字符串
     * @return 浏览器版本
     */
    public static String getBrowserVersion(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }

        try {
            if (userAgent.contains("Chrome")) {
                return extractVersion(userAgent, "Chrome/");
            } else if (userAgent.contains("Firefox")) {
                return extractVersion(userAgent, "Firefox/");
            } else if (userAgent.contains("Safari")) {
                return extractVersion(userAgent, "Version/");
            } else if (userAgent.contains("Edg")) {
                return extractVersion(userAgent, "Edg/");
            } else if (userAgent.contains("Opera")) {
                return extractVersion(userAgent, "Opera/");
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            log.warn("获取浏览器版本异常: {}", userAgent, e);
            return UNKNOWN;
        }
    }

    /**
     * 提取版本号
     *
     * @param userAgent 用户代理字符串
     * @param prefix 版本前缀
     * @return 版本号
     */
    private static String extractVersion(String userAgent, String prefix) {
        int start = userAgent.indexOf(prefix);
        if (start == -1) {
            return UNKNOWN;
        }
        
        start += prefix.length();
        int end = userAgent.indexOf(" ", start);
        if (end == -1) {
            end = userAgent.length();
        }
        
        String version = userAgent.substring(start, end);
        // 只取主版本号
        int dotIndex = version.indexOf(".");
        if (dotIndex > 0) {
            int secondDotIndex = version.indexOf(".", dotIndex + 1);
            if (secondDotIndex > 0) {
                version = version.substring(0, secondDotIndex);
            }
        }
        
        return version;
    }

    /**
     * 判断是否为移动设备
     *
     * @param userAgent 用户代理字符串
     * @return 是否为移动设备
     */
    public static boolean isMobile(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return false;
        }
        
        return userAgent.contains("Mobile") || userAgent.contains("Android") ||
               userAgent.contains("iPhone") || userAgent.contains("iPod");
    }

    /**
     * 判断是否为平板设备
     *
     * @param userAgent 用户代理字符串
     * @return 是否为平板设备
     */
    public static boolean isTablet(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return false;
        }
        
        return userAgent.contains("iPad") || userAgent.contains("Tablet");
    }
}