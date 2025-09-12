package com.admin.module.log.biz.service;

import com.admin.module.log.api.dto.LoginLogCreateDTO;
import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.admin.module.log.api.service.LogRecordService;
import com.admin.module.log.biz.convert.LoginLogConvert;
import com.admin.module.log.biz.convert.OperationLogConvert;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 日志记录服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogRecordServiceImpl implements LogRecordService {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public void recordOperationLog(OperationLogCreateDTO logDTO) {
        try {
            OperationLogDO logDO = OperationLogConvert.INSTANCE.convert(logDTO);
            operationLogMapper.insert(logDO);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    @Override
    public void recordLoginLog(LoginLogCreateDTO logDTO) {
        try {
            LoginLogDO logDO = LoginLogConvert.INSTANCE.convert(logDTO);
            loginLogMapper.insert(logDO);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    @Override
    @Async("asyncLogExecutor")
    public void recordOperationLogAsync(OperationLogCreateDTO logDTO) {
        recordOperationLog(logDTO);
    }

    @Override
    @Async("asyncLogExecutor")
    public void recordLoginLogAsync(LoginLogCreateDTO logDTO) {
        recordLoginLog(logDTO);
    }
}