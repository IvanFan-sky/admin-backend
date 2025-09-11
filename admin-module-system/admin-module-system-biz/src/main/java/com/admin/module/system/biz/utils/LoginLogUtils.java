package com.admin.module.system.biz.utils;

import com.admin.common.utils.IpUtils;
import com.admin.common.utils.ServletUtils;
import com.admin.common.utils.UserAgentUtils;
import com.admin.module.system.api.dto.log.SysLoginLogQueryDTO;
import com.admin.module.system.biz.dal.dataobject.SysLoginLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 登录日志工具类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
public class LoginLogUtils {

    /**
     * 设置登录日志的基础信息
     *
     * @param loginLog 登录日志对象
     */
    public static void setBasicInfo(SysLoginLogDO loginLog) {
        if (loginLog == null) {
            return;
        }

        try {
            // 设置IP地址
            String clientIP = IpUtils.getIpAddr();
            loginLog.setIpAddress(clientIP);
            
            // 设置用户代理信息
            String userAgent = ServletUtils.getUserAgent();
            loginLog.setUserAgent(userAgent);
            
            // 设置浏览器和操作系统信息
            setBrowserAndOsInfo(loginLog, userAgent);
            
            // 设置登录时间
            if (loginLog.getLoginTime() == null) {
                loginLog.setLoginTime(LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("设置登录日志基础信息失败", e);
        }
    }

    /**
     * 设置浏览器和操作系统信息
     *
     * @param loginLog 登录日志对象
     * @param userAgent 用户代理字符串
     */
    public static void setBrowserAndOsInfo(SysLoginLogDO loginLog, String userAgent) {
        if (loginLog == null || !StringUtils.hasText(userAgent)) {
            return;
        }

        try {
            // 解析浏览器信息
            String browser = UserAgentUtils.parseBrowser(userAgent);
            String os = UserAgentUtils.parseOperatingSystem(userAgent);
            String deviceType = UserAgentUtils.parseDeviceType(userAgent);
            
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            
            // 如果有设备类型字段，可以设置
            // loginLog.setDeviceType(deviceType);
        } catch (Exception e) {
            log.error("设置浏览器和操作系统信息失败", e);
        }
    }

    /**
     * 构建登录日志查询条件
     *
     * @param queryDTO 查询条件
     * @return 查询包装器
     */
    public static LambdaQueryWrapper<SysLoginLogDO> buildQueryWrapper(SysLoginLogQueryDTO queryDTO) {
        if (queryDTO == null) {
            return new LambdaQueryWrapper<SysLoginLogDO>()
                .orderByDesc(SysLoginLogDO::getLoginTime);
        }

        return new LambdaQueryWrapper<SysLoginLogDO>()
            .like(queryDTO.getUsername() != null, SysLoginLogDO::getUsername, queryDTO.getUsername())
            .eq(queryDTO.getStatus() != null, SysLoginLogDO::getStatus, queryDTO.getStatus())
            .eq(queryDTO.getLoginType() != null, SysLoginLogDO::getLoginType, queryDTO.getLoginType())
            .like(queryDTO.getIpAddress() != null, SysLoginLogDO::getIpAddress, queryDTO.getIpAddress())
            .like(queryDTO.getLoginLocation() != null, SysLoginLogDO::getLoginLocation, queryDTO.getLoginLocation())
            .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null, SysLoginLogDO::getLoginTime, queryDTO.getStartTime(), queryDTO.getEndTime())
            .orderByDesc(SysLoginLogDO::getLoginTime);
    }

    /**
     * 判断是否为成功登录
     *
     * @param loginResult 登录结果
     * @return 是否成功
     */
    public static boolean isLoginSuccess(String loginResult) {
        return "SUCCESS".equals(loginResult) || "成功".equals(loginResult);
    }

    /**
     * 判断是否为失败登录
     *
     * @param loginResult 登录结果
     * @return 是否失败
     */
    public static boolean isLoginFailure(String loginResult) {
        return "FAILURE".equals(loginResult) || "失败".equals(loginResult);
    }

    /**
     * 获取登录结果描述
     *
     * @param loginResult 登录结果
     * @return 结果描述
     */
    public static String getLoginResultDesc(String loginResult) {
        if (isLoginSuccess(loginResult)) {
            return "登录成功";
        } else if (isLoginFailure(loginResult)) {
            return "登录失败";
        } else {
            return "未知状态";
        }
    }

    /**
     * 获取登录结果描述
     *
     * @param status 登录状态（1成功 0失败）
     * @return 结果描述
     */
    public static String getLoginResultDesc(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        return status == 1 ? "登录成功" : "登录失败";
    }

    /**
     * 获取登录类型描述
     *
     * @param loginType 登录类型
     * @return 类型描述
     */
    public static String getLoginTypeDesc(String loginType) {
        if (loginType == null) {
            return "未知类型";
        }
        
        switch (loginType.toUpperCase()) {
            case "PASSWORD":
                return "密码登录";
            case "SMS":
                return "短信登录";
            case "EMAIL":
                return "邮箱登录";
            case "WECHAT":
                return "微信登录";
            case "QQ":
                return "QQ登录";
            case "GITHUB":
                return "GitHub登录";
            default:
                return loginType;
        }
    }

    /**
     * 格式化IP地址显示
     *
     * @param ip IP地址
     * @return 格式化后的IP
     */
    public static String formatIpAddress(String ip) {
        if (!StringUtils.hasText(ip)) {
            return "未知";
        }
        
        if (IpUtils.isInternalIp(ip)) {
            return ip + " (内网)";
        } else {
            return ip + " (外网)";
        }
    }

    /**
     * 生成登录日志摘要
     *
     * @param loginLog 登录日志
     * @return 日志摘要
     */
    public static String generateLogSummary(SysLoginLogDO loginLog) {
        if (loginLog == null) {
            return "无效的登录日志";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("用户 ").append(loginLog.getUsername())
               .append(" 于 ").append(loginLog.getLoginTime())
               .append(" 通过 ").append(getLoginTypeDesc(loginLog.getLoginType()))
               .append(" 从 ").append(formatIpAddress(loginLog.getIpAddress()))
               .append(" 使用 ").append(loginLog.getBrowser())
               .append(" 浏览器在 ").append(loginLog.getOs())
               .append(" 系统上 ").append(getLoginResultDesc(loginLog.getStatus()));
        
        return summary.toString();
    }
}