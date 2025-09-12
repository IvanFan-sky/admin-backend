package com.admin.module.infra.biz.convert;

import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.biz.dal.dataobject.ImportErrorDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 导入错误详情转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface ImportErrorDetailConvert {

    ImportErrorDetailConvert INSTANCE = Mappers.getMapper(ImportErrorDetailConvert.class);

    ImportErrorDetailDO convert(ImportErrorDetailVO errorDetailVO);

    ImportErrorDetailVO convert(ImportErrorDetailDO errorDetailDO);

    List<ImportErrorDetailDO> convertList(List<ImportErrorDetailVO> errorDetailVOList);

    List<ImportErrorDetailVO> convertToVOList(List<ImportErrorDetailDO> errorDetailDOList);
}