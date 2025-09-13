package com.admin.module.log.biz.dal.mapper;

import com.admin.common.core.domain.PageResult;
import com.admin.module.log.api.dto.OperationLogQueryDTO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志Mapper
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogDO> {

    /**
     * 分页查询操作日志
     */
    IPage<OperationLogDO> selectPageByQuery(Page<OperationLogDO> page, @Param("query") OperationLogQueryDTO query);

    /**
     * 查询操作日志总数
     */
    Long selectCountByQuery(@Param("query") OperationLogQueryDTO query);

    /**
     * 查询操作日志列表
     */
    List<OperationLogDO> selectListByQuery(@Param("query") OperationLogQueryDTO query);
}