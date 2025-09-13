package com.admin.module.infra.biz.convert;

import com.admin.module.infra.api.enums.FileUploadStatusEnum;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文件转换器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface FileConvert {

    FileConvert INSTANCE = Mappers.getMapper(FileConvert.class);

    /**
     * DO转VO
     */
    @Mapping(target = "fileSizeFormatted", source = "fileSize", qualifiedByName = "formatFileSize")
    @Mapping(target = "uploadStatusDesc", source = "uploadStatus", qualifiedByName = "formatUploadStatus")
    FileInfoVO convert(FileInfoDO fileInfoDO);

    /**
     * DO列表转VO列表
     */
    List<FileInfoVO> convertList(List<FileInfoDO> fileInfoDOList);

    /**
     * 格式化文件大小
     */
    @Named("formatFileSize")
    default String formatFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * 格式化上传状态
     */
    @Named("formatUploadStatus")
    default String formatUploadStatus(Integer uploadStatus) {
        if (uploadStatus == null) {
            return "未知";
        }
        
        FileUploadStatusEnum statusEnum = FileUploadStatusEnum.getByCode(uploadStatus);
        return statusEnum != null ? statusEnum.getMessage() : "未知";
    }
}
