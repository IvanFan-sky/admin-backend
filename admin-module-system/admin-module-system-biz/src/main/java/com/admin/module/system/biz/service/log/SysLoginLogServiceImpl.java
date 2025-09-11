package com.admin.module.system.biz.service.log;


import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.system.api.dto.log.SysLoginLogQueryDTO;
import com.admin.module.system.api.service.log.SysLoginLogService;
import com.admin.module.system.api.vo.log.*;
import com.admin.module.system.biz.convert.log.SysLoginLogConvert;
import com.admin.module.system.biz.dal.dataobject.SysLoginLogDO;
import com.admin.module.system.biz.dal.mapper.SysLoginLogMapper;
import com.admin.module.system.biz.utils.LoginLogUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 系统登录日志服务实现类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLogDO> implements SysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;
    private final SysLoginLogConvert loginLogConvert;

    @Override
    @Transactional
    public void saveLoginLog(LoginLogInfoVO logInfo) {
        try {
            SysLoginLogDO loginLog = loginLogConvert.toEntity(logInfo);
            LoginLogUtils.setBasicInfo(loginLog);
            save(loginLog);
        } catch (Exception e) {
            log.error("保存登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void updateLogoutInfo(String tokenId, String logoutType) {
        try {
            loginLogMapper.updateLogoutInfo(tokenId, LocalDateTime.now(), logoutType, null);
        } catch (Exception e) {
            log.error("更新登出信息失败", e);
            throw new ServiceException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "#queryDTO.toString()", unless = "#result.list.isEmpty()")
    public PageResult<SysLoginLogVO> getLoginLogPage(SysLoginLogQueryDTO queryDTO) {
        try {
            Page<SysLoginLogDO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
            LambdaQueryWrapper<SysLoginLogDO> wrapper = LoginLogUtils.buildQueryWrapper(queryDTO);
            
            Page<SysLoginLogDO> result = page(page, wrapper);
            
            return new PageResult<>(
                loginLogConvert.convertList(result.getRecords()),
                result.getTotal()
            );
        } catch (Exception e) {
            log.error("查询登录日志分页失败", e);
            throw new ServiceException(ErrorCode.LOG_QUERY_FAILED);
        }
    }

    @Override
    public int cleanExpiredLoginLogs(int retentionDays) {
        try {
            LocalDateTime expiredTime = LocalDateTime.now().minusDays(retentionDays);
            LambdaQueryWrapper<SysLoginLogDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(SysLoginLogDO::getLoginTime, expiredTime);
            int count = Math.toIntExact(count(wrapper));
            if (count > 0) {
                remove(wrapper);
                log.info("清理过期登录日志成功，清理数量: {}", count);
            }
            return count;
        } catch (Exception e) {
            log.error("清理过期登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_SAVE_FAILED);
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "'detail:' + #id")
    public SysLoginLogVO getLoginLog(Long id) {
        SysLoginLogDO loginLog = getById(id);
        if (loginLog == null) {
            throw new ServiceException(ErrorCode.LOGIN_LOG_NOT_FOUND);
        }
        return loginLogConvert.convert(loginLog);
    }

    @Override
    @Transactional
    @CacheEvict(value = "loginLog", allEntries = true)
    public int deleteLoginLogsBatch(Set<Long> ids) {
        try {
            boolean success = removeByIds(ids);
            if (!success) {
                throw new ServiceException(ErrorCode.LOG_DELETE_FAILED);
            }
            return ids.size();
        } catch (Exception e) {
            log.error("批量删除登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "loginLog", allEntries = true)
    public int deleteLoginLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            return loginLogMapper.deleteByTimeRange(startTime, endTime);
        } catch (Exception e) {
            log.error("按时间范围删除登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "loginLog", allEntries = true)
    public int clearLoginLogs() {
        try {
            return loginLogMapper.deleteAll();
        } catch (Exception e) {
            log.error("清空登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_CLEAR_FAILED);
        }
    }



    @Override
    @Cacheable(value = "loginLog", key = "'online'")
    public List<SysLoginLogVO> getOnlineUsers() {
        try {
            List<SysLoginLogDO> onlineUsers = loginLogMapper.selectOnlineUsers();
            return loginLogConvert.convertList(onlineUsers);
        } catch (Exception e) {
            log.error("获取在线用户列表失败", e);
            throw new ServiceException(ErrorCode.LOG_QUERY_FAILED);
        }
    }

    @Override
    public List<SysLoginLogVO> getUserLoginLogs(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        try {
            List<SysLoginLogDO> userLogs = loginLogMapper.selectUserLogs(userId, startTime, endTime, limit);
            return loginLogConvert.convertList(userLogs);
        } catch (Exception e) {
            log.error("获取用户登录日志失败，userId: {}", userId, e);
            throw new ServiceException(ErrorCode.LOG_QUERY_FAILED, "用户登录日志查询失败");
        }
    }

    @Override
    public List<SysLoginLogVO> exportLoginLogs(SysLoginLogQueryDTO queryDTO) {
        try {
            LambdaQueryWrapper<SysLoginLogDO> wrapper = LoginLogUtils.buildQueryWrapper(queryDTO);
            List<SysLoginLogDO> list = list(wrapper);
            return loginLogConvert.convertList(list);
        } catch (Exception e) {
            log.error("导出登录日志失败", e);
            throw new ServiceException(ErrorCode.LOG_EXPORT_FAILED, "登录日志导出失败");
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "'statistics:' + #startTime + ':' + #endTime")
    public LoginLogStatisticsVO getLoginLogStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            StatisticsResult statistics = loginLogMapper.getStatistics(startTime, endTime);
            
            LoginLogStatisticsVO result = new LoginLogStatisticsVO();
            result.setTotalCount(statistics.getTotalCount());
            result.setSuccessCount(statistics.getSuccessCount());
            result.setFailCount(statistics.getFailCount());
            result.setOnlineCount(statistics.getOnlineCount());
            result.setAvgOnlineDuration(statistics.getAvgOnlineDuration());
            return result;
        } catch (Exception e) {
            log.error("获取登录日志统计失败", e);
            throw new ServiceException(ErrorCode.LOG_STATISTICS_FAILED);
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "'typeStats:' + #startTime + ':' + #endTime")
    public List<LoginTypeStatisticsVO> getLoginTypeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<LoginTypeStatisticsVO> results =
                loginLogMapper.getLoginTypeStatistics(startTime, endTime);
            
            return results.stream()
                .map(r -> LoginTypeStatisticsVO.builder()
                    .loginType(r.getLoginType())
                    .count(r.getCount())
                    .build())
                .toList();
        } catch (Exception e) {
            log.error("获取登录方式统计失败", e);
            throw new ServiceException(ErrorCode.LOG_STATISTICS_FAILED, "登录方式统计失败");
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "'locationStats:' + #startTime + ':' + #endTime + ':' + #limit")
    public List<LoginLocationStatisticsVO> getLocationStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        try {
            List<LoginLocationStatisticsVO> results =
                loginLogMapper.getLocationStatistics(startTime, endTime, limit);
            
            return results.stream()
                .map(r -> LoginLocationStatisticsVO.builder()
                    .location(r.getLocation())
                    .count(r.getCount())
                    .build())
                .toList();
        } catch (Exception e) {
            log.error("获取登录地点统计失败", e);
            throw new ServiceException(ErrorCode.LOG_STATISTICS_FAILED, "登录地点统计失败");
        }
    }

    @Override
    @Cacheable(value = "loginLog", key = "'browserStats:' + #startTime + ':' + #endTime + ':' + #limit")
    public List<BrowserStatisticsVO> getBrowserStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        try {
            List<BrowserStatisticsVO> results =
                loginLogMapper.getBrowserStatistics(startTime, endTime, limit);
            
            return results.stream()
                .map(r -> BrowserStatisticsVO.builder()
                    .browser(r.getBrowser())
                    .count(r.getCount())
                    .build())
                .toList();
        } catch (Exception e) {
            log.error("获取浏览器统计失败", e);
            throw new ServiceException(ErrorCode.LOG_STATISTICS_FAILED, "浏览器统计失败");
        }
    }

    @Override
    @Transactional
    public void forceLogout(String tokenId) {
        try {
            updateLogoutInfo(tokenId, "force");
        } catch (Exception e) {
            log.error("强制登出失败", e);
            throw new ServiceException(ErrorCode.LOGOUT_FAILED, "强制登出失败");
        }
    }

}