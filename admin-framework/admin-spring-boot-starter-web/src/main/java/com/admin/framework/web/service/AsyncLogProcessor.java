package com.admin.framework.web.service;

import com.admin.common.log.StructuredLogger;
import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.admin.module.log.api.service.LogRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步日志处理器
 * 
 * 负责异步处理操作日志和登录日志的记录
 * 支持失败降级策略和异常隔离
 * 
 * @author admin
 * @version 2.0
 * @since 2024-01-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(LogRecordService.class)
public class AsyncLogProcessor {

    private final LogRecordService logRecordService;

    /**
     * 异步处理操作日志
     * 
     * @param logDTO 操作日志DTO
     */
    @Async("asyncLogExecutor")
    public void processOperationLog(OperationLogCreateDTO logDTO) {
        if (logDTO == null) {
            return;
        }

        try {
            logRecordService.recordOperationLogAsync(logDTO);
            
            // 记录结构化日志
            StructuredLogger.logBusiness(
                "OPERATION_LOG",
                logDTO.getTitle(),
                logDTO.getStatus() == 1 ? "SUCCESS" : "FAILED",
                String.format("用户=%s, URL=%s, 耗时=%dms", 
                    logDTO.getOperName(), logDTO.getOperUrl(), logDTO.getCostTime())
            );
            
            log.debug("操作日志记录成功: 操作={}, 用户={}, 耗时={}ms", 
                logDTO.getTitle(), logDTO.getOperName(), logDTO.getCostTime());
                
        } catch (Exception e) {
            log.error("操作日志记录失败: 操作={}, 用户={}, 错误={}", 
                logDTO.getTitle(), logDTO.getOperName(), e.getMessage(), e);
            // 不再抛出异常，避免影响主业务流程
            fallbackToFileLog("OPERATION", logDTO, e);
        }
    }

    /**
     * 异步处理登录日志
     * 
     * @param logDTO 登录日志DTO
     */
    @Async("asyncLogExecutor")
    public void processLoginLog(LoginLogCreateDTO logDTO) {
        if (logDTO == null) {
            return;
        }

        try {
            logRecordService.recordLoginLogAsync(logDTO);
            
            // 记录结构化日志
            StructuredLogger.logLogin(
                logDTO.getUserName(),
                logDTO.getStatus() == 1,
                logDTO.getIpaddr(),
                logDTO.getBrowser(),
                logDTO.getMsg()
            );
            
            if (logDTO.getStatus() == 1) {
                log.info("登录成功日志已记录: 用户={}, IP={}", logDTO.getUserName(), logDTO.getIpaddr());
            } else {
                log.warn("登录失败日志已记录: 用户={}, IP={}, 原因={}", 
                    logDTO.getUserName(), logDTO.getIpaddr(), logDTO.getMsg());
            }
            
        } catch (Exception e) {
            log.error("登录日志记录失败: 用户={}, 错误={}", logDTO.getUserName(), e.getMessage(), e);
            // 不再抛出异常，避免影响主业务流程
            fallbackToFileLog("LOGIN", logDTO, e);
        }
    }

    /**
     * 同步记录操作日志（降级方案）
     * 
     * @param logDTO 操作日志DTO
     */
    public void processOperationLogSync(OperationLogCreateDTO logDTO) {
        if (logDTO == null) {
            return;
        }

        try {
            logRecordService.recordOperationLog(logDTO);
            log.debug("同步操作日志记录成功: 操作={}, 用户={}", 
                logDTO.getTitle(), logDTO.getOperName());
        } catch (Exception e) {
            log.error("同步操作日志记录失败: 操作={}, 用户={}, 错误={}", 
                logDTO.getTitle(), logDTO.getOperName(), e.getMessage(), e);
            fallbackToFileLog("OPERATION", logDTO, e);
        }
    }

    /**
     * 同步记录登录日志（降级方案）
     * 
     * @param logDTO 登录日志DTO
     */
    public void processLoginLogSync(LoginLogCreateDTO logDTO) {
        if (logDTO == null) {
            return;
        }

        try {
            logRecordService.recordLoginLog(logDTO);
            log.debug("同步登录日志记录成功: 用户={}", logDTO.getUserName());
        } catch (Exception e) {
            log.error("同步登录日志记录失败: 用户={}, 错误={}", 
                logDTO.getUserName(), e.getMessage(), e);
            fallbackToFileLog("LOGIN", logDTO, e);
        }
    }

    /**
     * 降级到文件日志
     * 
     * @param logType 日志类型
     * @param logDTO 日志数据
     * @param e 异常信息
     */
    private void fallbackToFileLog(String logType, Object logDTO, Exception e) {
        try {
            // 使用结构化日志记录失败信息
            StructuredLogger.logException(
                logType + "_LOG_FAILED", 
                e, 
                logDTO
            );
            
            log.error("{}日志处理失败，已记录到异常日志文件: {}", logType, e.getMessage());
        } catch (Exception ex) {
            // 最后的降级策略：简单的错误日志
            log.error("{}日志处理失败，降级日志记录也失败了: 原始异常={}, 降级异常={}", 
                logType, e.getMessage(), ex.getMessage());
        }
    }
}