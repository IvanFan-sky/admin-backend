package com.admin.module.log.biz.dal.mapper;

import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogDO> {
}