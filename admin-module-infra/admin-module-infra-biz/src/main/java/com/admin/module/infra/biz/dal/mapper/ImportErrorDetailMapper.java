package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.ImportErrorDetailDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导入错误详情Mapper
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface ImportErrorDetailMapper extends BaseMapper<ImportErrorDetailDO> {

    /**
     * 根据任务ID查找错误详情
     * 
     * @param taskId 任务ID
     * @return 错误详情列表
     */
    List<ImportErrorDetailDO> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 批量插入错误详情
     * 
     * @param errorDetails 错误详情列表
     * @return 插入行数
     */
    int batchInsert(@Param("errorDetails") List<ImportErrorDetailDO> errorDetails);

    /**
     * 统计任务的错误数量
     * 
     * @param taskId 任务ID
     * @return 错误数量
     */
    int countByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据错误类型统计
     * 
     * @param taskId 任务ID
     * @param errorType 错误类型
     * @return 错误数量
     */
    int countByTaskIdAndErrorType(@Param("taskId") Long taskId,
                                 @Param("errorType") String errorType);

    /**
     * 删除任务的所有错误详情
     * 
     * @param taskId 任务ID
     * @return 删除行数
     */
    int deleteByTaskId(@Param("taskId") Long taskId);
}
