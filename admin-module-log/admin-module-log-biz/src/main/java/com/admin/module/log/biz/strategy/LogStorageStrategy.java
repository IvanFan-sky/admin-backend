package com.admin.module.log.biz.strategy;

import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;

/**
 * 日志存储策略接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogStorageStrategy {

    /**
     * 存储操作日志
     *
     * @param logDTO 操作日志数据
     */
    void storeOperationLog(OperationLogCreateDTO logDTO);

    /**
     * 存储登录日志
     *
     * @param logDTO 登录日志数据
     */
    void storeLoginLog(LoginLogCreateDTO logDTO);

    /**
     * 获取存储类型
     *
     * @return 存储类型
     */
    String getStorageType();
}