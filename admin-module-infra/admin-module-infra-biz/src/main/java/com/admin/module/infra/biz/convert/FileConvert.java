package com.admin.module.infra.biz.convert;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文件转换器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface FileConvert {

    FileConvert INSTANCE = Mappers.getMapper(FileConvert.class);

    /**
     * 转换为文件信息 VO
     */
    FileInfoVO convertToVO(FileInfoDO fileDO);

    /**
     * 转换为文件信息 VO 列表
     */
    List<FileInfoVO> convertToVOList(List<FileInfoDO> fileList);

    /**
     * 转换为分页结果
     */
    PageResult<FileInfoVO> convertToPageResult(PageResult<FileInfoDO> pageResult);

    /**
     * 转换为文件上传结果 VO
     */
    FileUploadVO convertToUploadVO(FileInfoDO fileDO);
}