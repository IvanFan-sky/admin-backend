package com.admin.module.log.api.service;

import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.dto.LoginLogQueryDTO;

/**
 * 日志导出服务接口
 * 
 * 提供日志导出功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogExportService {

    /**
     * 导出操作日志
     * 
     * @param queryDTO 查询条件
     * @return 导出文件的字节数组
     */
    byte[] exportOperationLogs(OperationLogQueryDTO queryDTO);

    /**
     * 导出登录日志
     * 
     * @param queryDTO 查询条件
     * @return 导出文件的字节数组
     */
    byte[] exportLoginLogs(LoginLogQueryDTO queryDTO);
}