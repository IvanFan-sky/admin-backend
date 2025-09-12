package com.admin.module.log.biz.service;


import com.admin.common.core.domain.PageResult;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.service.LogQueryService;
import com.admin.module.log.api.vo.LoginLogVO;
import com.admin.module.log.api.vo.OperationLogVO;
import com.admin.module.log.biz.convert.LoginLogConvert;
import com.admin.module.log.biz.convert.OperationLogConvert;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.admin.module.log.biz.dal.mapper.LoginLogMapper;
import com.admin.module.log.biz.dal.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 日志查询服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
public class LogQueryServiceImpl implements LogQueryService {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public PageResult<OperationLogVO> getOperationLogPage(OperationLogQueryDTO queryDTO) {
        LambdaQueryWrapper<OperationLogDO> wrapper = new LambdaQueryWrapper<OperationLogDO>()
                .like(queryDTO.getTitle() != null, OperationLogDO::getTitle, queryDTO.getTitle())
                .eq(queryDTO.getBusinessType() != null, OperationLogDO::getBusinessType, queryDTO.getBusinessType())
                .like(queryDTO.getOperName() != null, OperationLogDO::getOperName, queryDTO.getOperName())
                .eq(queryDTO.getStatus() != null, OperationLogDO::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getRequestMethod() != null, OperationLogDO::getRequestMethod, queryDTO.getRequestMethod())
                .eq(queryDTO.getOperatorType() != null, OperationLogDO::getOperatorType, queryDTO.getOperatorType())
                .like(queryDTO.getOperLocation() != null, OperationLogDO::getOperLocation, queryDTO.getOperLocation())
                .ge(queryDTO.getMinCostTime() != null, OperationLogDO::getCostTime, queryDTO.getMinCostTime())
                .le(queryDTO.getMaxCostTime() != null, OperationLogDO::getCostTime, queryDTO.getMaxCostTime())
                .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null,
                        OperationLogDO::getOperTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                .orderByDesc(OperationLogDO::getId);

        Page<OperationLogDO> page = operationLogMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);
        List<OperationLogVO> voList = OperationLogConvert.INSTANCE.convertList(page.getRecords());
        return new PageResult<>(voList, page.getTotal());
    }

    @Override
    public PageResult<LoginLogVO> getLoginLogPage(LoginLogQueryDTO queryDTO) {
        LambdaQueryWrapper<LoginLogDO> wrapper = new LambdaQueryWrapper<LoginLogDO>()
                .like(queryDTO.getUserName() != null, LoginLogDO::getUserName, queryDTO.getUserName())
                .eq(queryDTO.getLoginType() != null, LoginLogDO::getLoginType, queryDTO.getLoginType())
                .like(queryDTO.getIpaddr() != null, LoginLogDO::getIpaddr, queryDTO.getIpaddr())
                .eq(queryDTO.getStatus() != null, LoginLogDO::getStatus, queryDTO.getStatus())
                .like(queryDTO.getBrowser() != null, LoginLogDO::getBrowser, queryDTO.getBrowser())
                .like(queryDTO.getOs() != null, LoginLogDO::getOs, queryDTO.getOs())
                .between(queryDTO.getStartTime() != null && queryDTO.getEndTime() != null,
                        LoginLogDO::getLoginTime, queryDTO.getStartTime(), queryDTO.getEndTime())
                .orderByDesc(LoginLogDO::getId);

        Page<LoginLogDO> page = loginLogMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);
        List<LoginLogVO> voList = LoginLogConvert.INSTANCE.convertList(page.getRecords());
        return new PageResult<>(voList, page.getTotal());
    }

    @Override
    public OperationLogVO getOperationLogById(Long id) {
        OperationLogDO operationLog = operationLogMapper.selectById(id);
        return OperationLogConvert.INSTANCE.convert(operationLog);
    }

    @Override
    public LoginLogVO getLoginLogById(Long id) {
        LoginLogDO loginLog = loginLogMapper.selectById(id);
        return LoginLogConvert.INSTANCE.convert(loginLog);
    }

    @Override
    public void deleteOperationLogs(Long[] ids) {
        operationLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public void deleteLoginLogs(Long[] ids) {
        loginLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public void clearOperationLogs() {
        operationLogMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    public void clearLoginLogs() {
        loginLogMapper.delete(new LambdaQueryWrapper<>());
    }
}