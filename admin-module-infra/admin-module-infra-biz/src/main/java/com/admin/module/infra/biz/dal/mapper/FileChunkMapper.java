package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.FileChunkDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件分片信息Mapper
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface FileChunkMapper extends BaseMapper<FileChunkDO> {

    /**
     * 根据上传ID查找所有分片
     * 
     * @param uploadId 上传ID
     * @return 分片列表
     */
    List<FileChunkDO> selectByUploadId(@Param("uploadId") String uploadId);

    /**
     * 根据上传ID和分片号查找分片
     * 
     * @param uploadId 上传ID
     * @param chunkNumber 分片号
     * @return 分片信息
     */
    FileChunkDO selectByUploadIdAndChunkNumber(@Param("uploadId") String uploadId,
                                              @Param("chunkNumber") Integer chunkNumber);

    /**
     * 统计已上传的分片数量
     * 
     * @param uploadId 上传ID
     * @param uploadStatus 上传状态
     * @return 分片数量
     */
    int countByUploadIdAndStatus(@Param("uploadId") String uploadId,
                                @Param("uploadStatus") Integer uploadStatus);

    /**
     * 批量删除分片
     * 
     * @param uploadId 上传ID
     * @return 删除行数
     */
    int deleteByUploadId(@Param("uploadId") String uploadId);
}
