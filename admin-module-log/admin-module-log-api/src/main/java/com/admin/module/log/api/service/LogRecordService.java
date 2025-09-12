package com.admin.module.log.api.service;

import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.admin.module.log.api.dto.LoginLogCreateDTO;

/**
 * 日志记录服务接口
 * 
 * 提供日志记录的核心功能，支持操作日志和登录日志的异步记录
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogRecordService {

    /**
     * 记录操作日志
     * 
     * @param logDTO 操作日志信息
     */
    void recordOperationLog(OperationLogCreateDTO logDTO);

    /**
     * 记录登录日志
     * 
     * @param logDTO 登录日志信息
     */
    void recordLoginLog(LoginLogCreateDTO logDTO);

    /**
     * 异步记录操作日志
     * 
     * @param logDTO 操作日志信息
     */
    void recordOperationLogAsync(OperationLogCreateDTO logDTO);

    /**
     * 异步记录登录日志
     * 
     * @param logDTO 登录日志信息
     */
    void recordLoginLogAsync(LoginLogCreateDTO logDTO);
}