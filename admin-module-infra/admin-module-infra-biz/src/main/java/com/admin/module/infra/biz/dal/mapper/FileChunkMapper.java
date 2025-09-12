package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.FileChunkDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件分片 Mapper
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface FileChunkMapper extends BaseMapper<FileChunkDO> {
}