package com.admin.module.log.biz.strategy;

import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.admin.module.log.biz.convert.LoginLogConvert;
import com.admin.module.log.biz.convert.OperationLogConvert;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 数据库日志存储策略
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseLogStorageStrategy implements LogStorageStrategy {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public void storeOperationLog(OperationLogCreateDTO logDTO) {
        try {
            OperationLogDO logDO = OperationLogConvert.INSTANCE.convert(logDTO);
            operationLogMapper.insert(logDO);
            log.debug("操作日志已存储到数据库, ID: {}", logDO.getId());
        } catch (Exception e) {
            log.error("数据库存储操作日志失败", e);
            throw new RuntimeException("数据库存储操作日志失败", e);
        }
    }

    @Override
    public void storeLoginLog(LoginLogCreateDTO logDTO) {
        try {
            LoginLogDO logDO = LoginLogConvert.INSTANCE.convert(logDTO);
            loginLogMapper.insert(logDO);
            log.debug("登录日志已存储到数据库, ID: {}", logDO.getId());
        } catch (Exception e) {
            log.error("数据库存储登录日志失败", e);
            throw new RuntimeException("数据库存储登录日志失败", e);
        }
    }

    @Override
    public String getStorageType() {
        return "database";
    }
}