package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    /**
     * 批量查询有效文件信息（用于批量删除）
     * 
     * @param fileIds 文件ID列表
     * @return 文件信息列表
     */
    List<FileInfoDO> selectValidFilesByIds(@Param("fileIds") List<Long> fileIds);

    /**
     * 根据文件路径批量查询文件
     * 
     * @param filePaths 文件路径列表
     * @return 文件信息列表
     */
    List<FileInfoDO> selectByFilePaths(@Param("filePaths") List<String> filePaths);

    /**
     * 批量逻辑删除文件
     * 
     * @param fileIds 文件ID列表
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int batchLogicalDelete(@Param("fileIds") List<Long> fileIds,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询过期文件（用于定时清理）
     * 
     * @param expireTime 过期时间
     * @param limit 查询限制数量
     * @return 过期文件列表
     */
    List<FileInfoDO> selectExpiredFiles(@Param("expireTime") LocalDateTime expireTime,
                                       @Param("limit") Integer limit);

    /**
     * 统计文件存储信息
     * 
     * @return 存储统计信息
     */
    FileStorageStatistics selectStorageStatistics();

    /**
     * 根据业务类型统计文件数量
     * 
     * @param businessType 业务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 文件数量
     */
    Long countByBusinessTypeAndTimeRange(@Param("businessType") String businessType,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 存储统计信息内部类
     */
    class FileStorageStatistics {
        private Long totalFiles;
        private Long totalSize;
        private Long imageFiles;
        private Long documentFiles;
        private Long videoFiles;
        private Long otherFiles;
        
        // getters and setters
        public Long getTotalFiles() { return totalFiles; }
        public void setTotalFiles(Long totalFiles) { this.totalFiles = totalFiles; }
        
        public Long getTotalSize() { return totalSize; }
        public void setTotalSize(Long totalSize) { this.totalSize = totalSize; }
        
        public Long getImageFiles() { return imageFiles; }
        public void setImageFiles(Long imageFiles) { this.imageFiles = imageFiles; }
        
        public Long getDocumentFiles() { return documentFiles; }
        public void setDocumentFiles(Long documentFiles) { this.documentFiles = documentFiles; }
        
        public Long getVideoFiles() { return videoFiles; }
        public void setVideoFiles(Long videoFiles) { this.videoFiles = videoFiles; }
        
        public Long getOtherFiles() { return otherFiles; }
        public void setOtherFiles(Long otherFiles) { this.otherFiles = otherFiles; }
    }
}
