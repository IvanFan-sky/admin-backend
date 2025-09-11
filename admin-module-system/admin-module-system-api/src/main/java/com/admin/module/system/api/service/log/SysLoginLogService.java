package com.admin.module.system.api.service.log;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.vo.log.BrowserStatisticsVO;
import com.admin.module.system.api.vo.log.LoginLocationStatisticsVO;
import com.admin.module.system.api.vo.log.LoginLogInfoVO;
import com.admin.module.system.api.vo.log.LoginLogStatisticsVO;
import com.admin.module.system.api.vo.log.LoginTypeStatisticsVO;
import com.admin.module.system.api.dto.log.SysLoginLogQueryDTO;
import com.admin.module.system.api.vo.log.SysLoginLogVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 系统登录日志服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysLoginLogService {

    /**
     * 记录登录日志
     * 
     * @param logInfo 登录日志信息
     */
    void saveLoginLog(LoginLogInfoVO logInfo);

    /**
     * 更新登出信息
     * 
     * @param tokenId 令牌ID
     * @param logoutType 登出方式
     */
    void updateLogoutInfo(String tokenId, String logoutType);

    /**
     * 获取登录日志分页列表
     * 
     * @param queryDTO 查询条件
     * @return 登录日志分页结果
     */
    PageResult<SysLoginLogVO> getLoginLogPage(SysLoginLogQueryDTO queryDTO);

    /**
     * 获取登录日志详情
     * 
     * @param id 日志ID
     * @return 登录日志详情
     */
    SysLoginLogVO getLoginLog(Long id);

    /**
     * 批量删除登录日志
     * 
     * @param ids 日志ID列表
     * @return 删除数量
     */
    int deleteLoginLogsBatch(Set<Long> ids);

    /**
     * 根据时间范围删除登录日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除数量
     */
    int deleteLoginLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 清空登录日志
     * 
     * @return 删除数量
     */
    int clearLoginLogs();

    /**
     * 清理过期登录日志
     * 
     * @param retentionDays 保留天数
     * @return 清理数量
     */
    int cleanExpiredLoginLogs(int retentionDays);

    /**
     * 导出登录日志
     * 
     * @param queryDTO 查询条件
     * @return 登录日志列表
     */
    List<SysLoginLogVO> exportLoginLogs(SysLoginLogQueryDTO queryDTO);

    /**
     * 获取登录日志统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    LoginLogStatisticsVO getLoginLogStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户登录日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 用户登录日志列表
     */
    List<SysLoginLogVO> getUserLoginLogs(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取登录方式统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录方式统计列表
     */
    List<LoginTypeStatisticsVO> getLoginTypeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取登录地点统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 登录地点统计列表
     */
    List<LoginLocationStatisticsVO> getLocationStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取浏览器统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 浏览器统计列表
     */
    List<BrowserStatisticsVO> getBrowserStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);



    /**
     * 获取在线用户列表
     * 
     * @return 在线用户列表
     */
    List<SysLoginLogVO> getOnlineUsers();

    /**
     * 强制用户下线
     * 
     * @param tokenId 令牌ID
     */
    void forceLogout(String tokenId);


}