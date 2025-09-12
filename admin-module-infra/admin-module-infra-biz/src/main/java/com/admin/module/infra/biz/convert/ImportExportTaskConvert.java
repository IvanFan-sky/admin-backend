package com.admin.module.infra.biz.convert;

import com.admin.module.infra.api.dto.*;
import com.admin.module.infra.api.vo.*;
import com.admin.module.infra.biz.dal.dataobject.ImportExportTaskDO;
import com.admin.module.infra.api.enums.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 导入导出任务转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface ImportExportTaskConvert {

    ImportExportTaskConvert INSTANCE = Mappers.getMapper(ImportExportTaskConvert.class);

    ImportExportTaskDO convert(ImportExportTaskCreateDTO createDTO);

    ImportExportTaskDO convert(ImportExportTaskUpdateDTO updateDTO);

    @Mapping(target = "taskTypeDesc", expression = "java(getTaskTypeDesc(taskDO.getTaskType()))")
    @Mapping(target = "dataTypeDesc", expression = "java(getDataTypeDesc(taskDO.getDataType()))")
    @Mapping(target = "fileFormatDesc", expression = "java(getFileFormatDesc(taskDO.getFileFormat()))")
    @Mapping(target = "statusDesc", expression = "java(getStatusDesc(taskDO.getStatus()))")
    ImportExportTaskVO convert(ImportExportTaskDO taskDO);

    List<ImportExportTaskVO> convertList(List<ImportExportTaskDO> taskList);

    default String getTaskTypeDesc(Integer taskType) {
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getByCode(taskType);
        return taskTypeEnum != null ? taskTypeEnum.getDescription() : "";
    }

    default String getDataTypeDesc(String dataType) {
        DataTypeEnum dataTypeEnum = DataTypeEnum.getByCode(dataType);
        return dataTypeEnum != null ? dataTypeEnum.getDescription() : "";
    }

    default String getFileFormatDesc(String fileFormat) {
        FileFormatEnum fileFormatEnum = FileFormatEnum.getByExtension(fileFormat);
        return fileFormatEnum != null ? fileFormatEnum.getDescription() : "";
    }

    default String getStatusDesc(Integer status) {
        TaskStatusEnum statusEnum = TaskStatusEnum.getByCode(status);
        return statusEnum != null ? statusEnum.getDescription() : "";
    }
}