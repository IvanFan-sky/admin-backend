package com.admin.module.log.api.service;


import com.admin.common.core.domain.PageResult;
import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.api.vo.LoginLogVO;
import com.admin.module.log.api.vo.OperationLogVO;

/**
 * 日志查询服务接口
 * 
 * 提供日志查询和管理功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface LogQueryService {

    /**
     * 分页查询操作日志
     * 
     * @param queryDTO 查询条件
     * @return 操作日志分页结果
     */
    PageResult<OperationLogVO> getOperationLogPage(OperationLogQueryDTO queryDTO);

    /**
     * 分页查询登录日志
     * 
     * @param queryDTO 查询条件
     * @return 登录日志分页结果
     */
    PageResult<LoginLogVO> getLoginLogPage(LoginLogQueryDTO queryDTO);

    /**
     * 根据ID获取操作日志详情
     * 
     * @param id 日志ID
     * @return 操作日志详情
     */
    OperationLogVO getOperationLogById(Long id);

    /**
     * 根据ID获取登录日志详情
     * 
     * @param id 日志ID
     * @return 登录日志详情
     */
    LoginLogVO getLoginLogById(Long id);

    /**
     * 删除操作日志
     * 
     * @param ids 日志ID集合
     */
    void deleteOperationLogs(Long[] ids);

    /**
     * 删除登录日志
     * 
     * @param ids 日志ID集合
     */
    void deleteLoginLogs(Long[] ids);

    /**
     * 清空操作日志
     */
    void clearOperationLogs();

    /**
     * 清空登录日志
     */
    void clearLoginLogs();
}