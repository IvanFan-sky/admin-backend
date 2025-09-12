package com.admin.module.log.biz.convert;

import com.admin.module.log.api.dto.OperationLogCreateDTO;
import com.admin.module.log.api.vo.OperationLogVO;
import com.admin.module.log.biz.dal.dataobject.OperationLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 操作日志转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface OperationLogConvert {

    OperationLogConvert INSTANCE = Mappers.getMapper(OperationLogConvert.class);

    /**
     * DTO转DO
     */
    OperationLogDO convert(OperationLogCreateDTO dto);

    /**
     * DO转VO
     */
    OperationLogVO convert(OperationLogDO dataObject);

    /**
     * DO列表转VO列表
     */
    List<OperationLogVO> convertList(List<OperationLogDO> list);
}