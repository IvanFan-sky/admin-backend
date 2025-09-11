package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.api.vo.log.BusinessTypeStatisticsVO;
import com.admin.module.system.api.vo.log.OperatorStatisticsVO;
import com.admin.module.system.api.vo.log.StatisticsResult;
import com.admin.module.system.biz.dal.dataobject.SysOperationLogDO;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统操作日志Mapper接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLogDO> {

    /**
     * 根据时间范围删除操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 清空操作日志
     * 
     * @return 删除数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteAll();

    /**
     * 清理过期操作日志
     * 
     * @param beforeTime 截止时间
     * @return 清理数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 获取操作日志统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    StatisticsResult getStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按业务类型统计操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 业务类型统计列表
     */
    List<BusinessTypeStatisticsVO> getBusinessTypeStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按操作人统计操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回条数
     * @return 操作人统计列表
     */
    List<OperatorStatisticsVO> getOperatorStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("limit") Integer limit);

    /**
     * 获取用户操作日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回条数
     * @return 操作日志列表
     */
    List<SysOperationLogDO> selectUserLogs(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("limit") Integer limit);

}