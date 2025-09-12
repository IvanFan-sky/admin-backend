package com.admin.module.infra.biz.dal.mapper;

import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文件信息 Mapper
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfoDO> {

    /**
     * 查询重复文件（优化版本）
     */
    @Select("""
        SELECT f1.* FROM sys_file_info f1 
        INNER JOIN (
            SELECT file_hash 
            FROM sys_file_info 
            WHERE file_hash IS NOT NULL AND upload_status = 2 
            GROUP BY file_hash 
            HAVING COUNT(*) > 1
        ) f2 ON f1.file_hash = f2.file_hash
        WHERE f1.upload_status = 2
        ORDER BY f1.file_hash, f1.create_time
        """)
    IPage<FileInfoDO> selectDuplicateFiles(Page<FileInfoDO> page);

    /**
     * 查询大文件（优化版本）
     */
    @Select("""
        SELECT * FROM sys_file_info 
        WHERE file_size >= #{minSize} AND upload_status = 2
        ORDER BY file_size DESC
        """)
    IPage<FileInfoDO> selectLargeFiles(Page<FileInfoDO> page, @Param("minSize") Long minSize);

    /**
     * 统计存储使用情况
     */
    @Select("""
        SELECT 
            COUNT(*) as totalFiles,
            COALESCE(SUM(file_size), 0) as totalSize,
            storage_type,
            business_type
        FROM sys_file_info 
        WHERE upload_status = 2 AND deleted = 0
        GROUP BY storage_type, business_type
        """)
    List<Map<String, Object>> selectStorageStatistics();

    /**
     * 按日期统计文件上传趋势
     */
    @Select("""
        SELECT 
            DATE(create_time) as uploadDate,
            COUNT(*) as fileCount,
            COALESCE(SUM(file_size), 0) as totalSize
        FROM sys_file_info 
        WHERE upload_status = 2 
        AND create_time >= #{startDate}
        AND create_time <= #{endDate}
        GROUP BY DATE(create_time)
        ORDER BY uploadDate
        """)
    List<Map<String, Object>> selectUploadTrend(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * 查询孤儿文件（存在于数据库但可能不存在于存储中的文件）
     */
    @Select("""
        SELECT * FROM sys_file_info 
        WHERE upload_status = 2 
        AND create_time < #{beforeTime}
        ORDER BY create_time
        """)
    IPage<FileInfoDO> selectPotentialOrphanFiles(Page<FileInfoDO> page, @Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 按业务类型统计文件数量和大小
     */
    @Select("""
        SELECT 
            COALESCE(business_type, '未分类') as businessType,
            COUNT(*) as fileCount,
            COALESCE(SUM(file_size), 0) as totalSize,
            AVG(file_size) as avgSize
        FROM sys_file_info 
        WHERE upload_status = 2 AND deleted = 0
        GROUP BY business_type
        ORDER BY totalSize DESC
        """)
    List<Map<String, Object>> selectBusinessStatistics();

    /**
     * 查询指定时间段内的文件
     */
    @Select("""
        SELECT COUNT(*) FROM sys_file_info 
        WHERE upload_status = 2 
        AND create_time >= #{startTime} 
        AND create_time <= #{endTime}
        """)
    Long countFilesByTimeRange(@Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
}