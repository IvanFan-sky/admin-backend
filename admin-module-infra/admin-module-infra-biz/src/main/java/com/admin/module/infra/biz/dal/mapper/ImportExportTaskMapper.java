package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.ImportExportTaskDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导入导出任务Mapper接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface ImportExportTaskMapper extends BaseMapper<ImportExportTaskDO> {

}

