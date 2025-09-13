package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件信息Mapper
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfoDO> {

    /**
     * 根据文件哈希查找文件
     * 
     * @param fileHash 文件哈希值
     * @param uploadStatus 上传状态
     * @return 文件信息
     */
    FileInfoDO selectByFileHash(@Param("fileHash") String fileHash, 
                               @Param("uploadStatus") Integer uploadStatus);

    /**
     * 根据业务类型和业务ID查找文件
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<FileInfoDO> selectByBusiness(@Param("businessType") String businessType,
                                     @Param("businessId") String businessId);

    /**
     * 更新下载统计
     * 
     * @param fileId 文件ID
     * @return 更新行数
     */
    int updateDownloadCount(@Param("fileId") Long fileId);

    /**
     * 批量更新上传状态
     * 
     * @param fileIds 文件ID列表
     * @param uploadStatus 上传状态
     * @return 更新行数
     */
    int batchUpdateUploadStatus(@Param("fileIds") List<Long> fileIds,
                               @Param("uploadStatus") Integer uploadStatus);
}
