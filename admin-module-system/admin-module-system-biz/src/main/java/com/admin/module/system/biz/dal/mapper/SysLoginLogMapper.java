package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysLoginLogDO;
import com.admin.module.system.api.vo.log.StatisticsResult;
import com.admin.module.system.api.vo.log.LoginTypeStatisticsVO;
import com.admin.module.system.api.vo.log.LoginLocationStatisticsVO;
import com.admin.module.system.api.vo.log.BrowserStatisticsVO;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统登录日志Mapper接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLogDO> {

    /**
     * 根据时间范围删除登录日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 清空登录日志
     * 
     * @return 删除数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteAll();

    /**
     * 清理过期登录日志
     * 
     * @param beforeTime 截止时间
     * @return 清理数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据令牌ID查询登录日志
     * 
     * @param tokenId 令牌ID
     * @return 登录日志
     */
    SysLoginLogDO selectByTokenId(@Param("tokenId") String tokenId);

    /**
     * 更新登出信息
     * 
     * @param tokenId 令牌ID
     * @param logoutTime 登出时间
     * @param logoutType 登出方式
     * @param onlineDuration 在线时长
     * @return 更新数量
     */
    int updateLogoutInfo(@Param("tokenId") String tokenId,
                        @Param("logoutTime") LocalDateTime logoutTime,
                        @Param("logoutType") String logoutType,
                        @Param("onlineDuration") Long onlineDuration);

    /**
     * 获取在线用户列表
     * 
     * @return 在线用户登录日志列表
     */
    List<SysLoginLogDO> selectOnlineUsers();

    /**
     * 获取用户登录日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回条数
     * @return 登录日志列表
     */
    List<SysLoginLogDO> selectUserLogs(@Param("userId") Long userId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("limit") Integer limit);

    /**
     * 获取登录统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    StatisticsResult getStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按登录方式统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录方式统计列表
     */
    List<LoginTypeStatisticsVO> getLoginTypeStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按登录地点统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回条数
     * @return 登录地点统计列表
     */
    List<LoginLocationStatisticsVO> getLocationStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("limit") Integer limit);

    /**
     * 按浏览器统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回条数
     * @return 浏览器统计列表
     */
    List<BrowserStatisticsVO> getBrowserStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("limit") Integer limit);
}