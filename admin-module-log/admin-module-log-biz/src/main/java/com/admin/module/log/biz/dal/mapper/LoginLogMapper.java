package com.admin.module.log.biz.dal.mapper;

import com.admin.module.log.api.dto.LoginLogQueryDTO;
import com.admin.module.log.biz.dal.dataobject.LoginLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录日志Mapper
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogDO> {

    /**
     * 分页查询登录日志
     */
    IPage<LoginLogDO> selectPageByQuery(Page<LoginLogDO> page, @Param("query") LoginLogQueryDTO query);

    /**
     * 查询登录日志总数
     */
    Long selectCountByQuery(@Param("query") LoginLogQueryDTO query);

    /**
     * 查询登录日志列表
     */
    List<LoginLogDO> selectListByQuery(@Param("query") LoginLogQueryDTO query);
}