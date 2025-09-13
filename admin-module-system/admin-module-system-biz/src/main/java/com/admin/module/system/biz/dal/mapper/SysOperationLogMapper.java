package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.api.vo.log.BusinessTypeStatisticsVO;
import com.admin.module.system.api.vo.log.OperatorStatisticsVO;
import com.admin.module.system.api.vo.log.StatisticsResult;
import com.admin.module.system.biz.dal.dataobject.SysOperationLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统操作日志 Mapper
 *
 * @author admin
 */
@Mapper
public interface SysOperationLogMapper {

    /**
     * 获取操作日志统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     */
    StatisticsResult getStatistics(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 按业务类型统计操作日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param limit     限制数量
     * @return 业务类型统计列表
     */
    List<BusinessTypeStatisticsVO> getBusinessTypeStatistics(@Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime,
                                                             @Param("limit") Integer limit);

    /**
     * 按操作人统计操作日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param limit     限制数量
     * @return 操作人统计列表
     */
    List<OperatorStatisticsVO> getOperatorStatistics(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);
}