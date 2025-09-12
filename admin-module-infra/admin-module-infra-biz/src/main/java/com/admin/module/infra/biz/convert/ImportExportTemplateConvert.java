package com.admin.module.infra.biz.convert;

import com.admin.module.infra.api.dto.ImportExportTemplateCreateDTO;
import com.admin.module.infra.api.enums.DataTypeEnum;
import com.admin.module.infra.api.enums.FileFormatEnum;
import com.admin.module.infra.api.vo.ImportExportTemplateVO;
import com.admin.module.infra.biz.dal.dataobject.ImportExportTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 导入导出模板转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface ImportExportTemplateConvert {

    ImportExportTemplateConvert INSTANCE = Mappers.getMapper(ImportExportTemplateConvert.class);

    ImportExportTemplateDO convert(ImportExportTemplateCreateDTO createDTO);

    @Mapping(target = "dataTypeDesc", expression = "java(getDataTypeDesc(templateDO.getDataType()))")
    @Mapping(target = "fileFormatDesc", expression = "java(getFileFormatDesc(templateDO.getFileFormat()))")
    @Mapping(target = "statusDesc", expression = "java(getStatusDesc(templateDO.getStatus()))")
    ImportExportTemplateVO convert(ImportExportTemplateDO templateDO);

    List<ImportExportTemplateVO> convertList(List<ImportExportTemplateDO> templateList);

    default String getDataTypeDesc(String dataType) {
        DataTypeEnum dataTypeEnum = DataTypeEnum.getByCode(dataType);
        return dataTypeEnum != null ? dataTypeEnum.getDescription() : "";
    }

    default String getFileFormatDesc(String fileFormat) {
        FileFormatEnum fileFormatEnum = FileFormatEnum.getByExtension(fileFormat);
        return fileFormatEnum != null ? fileFormatEnum.getDescription() : "";
    }

    default String getStatusDesc(Integer status) {
        return status != null && status == 1 ? "启用" : "禁用";
    }
}