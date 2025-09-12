package com.admin.module.infra.biz.service.impl;

import com.admin.common.core.domain.PageResult;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import com.admin.module.infra.biz.service.FileManagementService;
import com.admin.module.infra.biz.service.FileService;
import com.admin.module.infra.biz.storage.FileStorageFactory;
import com.admin.module.infra.biz.storage.FileStorageStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件管理服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileManagementServiceImpl implements FileManagementService {

    private final FileInfoMapper fileInfoMapper;
    private final FileService fileService;
    private final FileStorageFactory storageFactory;

    @Override
    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总文件数
        Long totalFiles = fileInfoMapper.selectCount(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
        );
        
        // 总存储大小
        Long totalSize = fileInfoMapper.selectObjs(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .select(FileInfoDO::getFileSize)
        ).stream()
        .filter(Objects::nonNull)
        .mapToLong(obj -> (Long) obj)
        .sum();
        
        // 今日上传文件数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Long todayFiles = fileInfoMapper.selectCount(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .ge(FileInfoDO::getCreateTime, todayStart)
        );
        
        // 本周上传文件数
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        Long weekFiles = fileInfoMapper.selectCount(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .ge(FileInfoDO::getCreateTime, weekStart)
        );
        
        statistics.put("totalFiles", totalFiles);
        statistics.put("totalSize", totalSize);
        statistics.put("totalSizeFormatted", formatFileSize(totalSize));
        statistics.put("todayFiles", todayFiles);
        statistics.put("weekFiles", weekFiles);
        statistics.put("averageFileSize", totalFiles > 0 ? totalSize / totalFiles : 0);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getBusinessStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 按业务类型统计文件数量
        List<FileInfoDO> allFiles = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .select(FileInfoDO::getBusinessType, FileInfoDO::getFileSize)
        );
        
        Map<String, Long> businessCounts = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getBusinessType() != null ? file.getBusinessType() : "未分类",
                        Collectors.counting()
                ));
        
        Map<String, Long> businessSizes = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getBusinessType() != null ? file.getBusinessType() : "未分类",
                        Collectors.summingLong(FileInfoDO::getFileSize)
                ));
        
        statistics.put("businessCounts", businessCounts);
        statistics.put("businessSizes", businessSizes);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStorageTypeDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<FileInfoDO> allFiles = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .select(FileInfoDO::getStorageType, FileInfoDO::getFileSize)
        );
        
        Map<String, Long> typeCounts = allFiles.stream()
                .collect(Collectors.groupingBy(
                        FileInfoDO::getStorageType,
                        Collectors.counting()
                ));
        
        Map<String, Long> typeSizes = allFiles.stream()
                .collect(Collectors.groupingBy(
                        FileInfoDO::getStorageType,
                        Collectors.summingLong(FileInfoDO::getFileSize)
                ));
        
        distribution.put("typeCounts", typeCounts);
        distribution.put("typeSizes", typeSizes);
        
        return distribution;
    }

    @Override
    public Map<String, Object> getUploadTrend() {
        Map<String, Object> trend = new HashMap<>();
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        List<FileInfoDO> recentFiles = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
                        .ge(FileInfoDO::getCreateTime, thirtyDaysAgo)
                        .select(FileInfoDO::getCreateTime, FileInfoDO::getFileSize)
        );
        
        // 按日期分组统计
        Map<String, Long> dailyCounts = new LinkedHashMap<>();
        Map<String, Long> dailySizes = new LinkedHashMap<>();
        
        // 初始化30天的数据
        for (int i = 29; i >= 0; i--) {
            String date = LocalDateTime.now().minusDays(i).toLocalDate().toString();
            dailyCounts.put(date, 0L);
            dailySizes.put(date, 0L);
        }
        
        // 填充实际数据
        recentFiles.forEach(file -> {
            String date = file.getCreateTime().toLocalDate().toString();
            if (dailyCounts.containsKey(date)) {
                dailyCounts.put(date, dailyCounts.get(date) + 1);
                dailySizes.put(date, dailySizes.get(date) + file.getFileSize());
            }
        });
        
        trend.put("dates", new ArrayList<>(dailyCounts.keySet()));
        trend.put("counts", new ArrayList<>(dailyCounts.values()));
        trend.put("sizes", new ArrayList<>(dailySizes.values()));
        
        return trend;
    }

    @Override
    public PageResult<FileInfoVO> getLargeFiles(Long minSize, FilePageDTO pageDTO) {
        if (minSize == null) {
            minSize = 100 * 1024 * 1024L; // 默认100MB
        }
        
        // 使用优化后的数据库查询
        Page<FileInfoDO> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        IPage<FileInfoDO> result = fileInfoMapper.selectLargeFiles(page, minSize);
        
        List<FileInfoVO> voList = result.getRecords().stream()
                .map(file -> {
                    FileInfoVO vo = new FileInfoVO();
                    vo.setId(file.getId());
                    vo.setFileName(file.getFileName());
                    vo.setFileSize(file.getFileSize());
                    vo.setContentType(file.getContentType());
                    vo.setCreateTime(file.getCreateTime());
                    vo.setStorageType(file.getStorageType());
                    vo.setBusinessType(file.getBusinessType());
                    return vo;
                })
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, result.getTotal());
    }

    @Override
    public PageResult<FileInfoVO> getDuplicateFiles(FilePageDTO pageDTO) {
        // 使用优化后的数据库查询
        Page<FileInfoDO> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        IPage<FileInfoDO> result = fileInfoMapper.selectDuplicateFiles(page);
        
        List<FileInfoVO> voList = result.getRecords().stream()
                .map(file -> {
                    FileInfoVO vo = new FileInfoVO();
                    vo.setId(file.getId());
                    vo.setFileName(file.getFileName());
                    vo.setFileSize(file.getFileSize());
                    vo.setFileHash(file.getFileHash());
                    vo.setCreateTime(file.getCreateTime());
                    vo.setStorageType(file.getStorageType());
                    vo.setBusinessType(file.getBusinessType());
                    return vo;
                })
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, result.getTotal());
    }

    @Override
    public PageResult<FileInfoVO> getOrphanFiles(FilePageDTO pageDTO) {
        // 查询数据库中的所有文件
        List<FileInfoDO> allFiles = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getUploadStatus, 2)
        );
        
        List<FileInfoVO> orphanFiles = new ArrayList<>();
        
        // 检查每个文件在存储中是否存在
        for (FileInfoDO file : allFiles) {
            try {
                FileStorageStrategy strategy = storageFactory.getStrategy(file.getStorageType().toLowerCase());
                if (!strategy.fileExists(file.getStorageBucket(), file.getStoragePath())) {
                    FileInfoVO vo = new FileInfoVO();
                    vo.setId(file.getId());
                    vo.setFileName(file.getFileName());
                    vo.setFileSize(file.getFileSize());
                    vo.setStorageType(file.getStorageType());
                    vo.setStoragePath(file.getStoragePath());
                    vo.setCreateTime(file.getCreateTime());
                    orphanFiles.add(vo);
                }
            } catch (Exception e) {
                log.warn("检查文件存在性失败，文件ID: {}", file.getId(), e);
            }
        }
        
        // 分页
        int start = (pageDTO.getPageNo() - 1) * pageDTO.getPageSize();
        int end = Math.min(start + pageDTO.getPageSize(), orphanFiles.size());
        
        List<FileInfoVO> pageFiles = orphanFiles.subList(start, end);
        
        return new PageResult<>(pageFiles, (long) orphanFiles.size());
    }

    @Override
    public Map<String, Object> performStorageCleanup() {
        Map<String, Object> result = new HashMap<>();
        int totalCleaned = 0;
        
        try {
            // 清理过期文件
            int expiredFiles = fileService.cleanupExpiredFiles();
            totalCleaned += expiredFiles;
            
            // 清理临时文件
            int tempFiles = fileService.cleanupTempFiles();
            totalCleaned += tempFiles;
            
            // 清理孤儿文件记录
            PageResult<FileInfoVO> orphanFiles = getOrphanFiles(new FilePageDTO() {{
                setPageNo(1);
                setPageSize(1000);
            }});
            
            int orphanCleaned = 0;
            for (FileInfoVO orphan : orphanFiles.getList()) {
                if (fileService.deleteFile(orphan.getId())) {
                    orphanCleaned++;
                }
            }
            totalCleaned += orphanCleaned;
            
            result.put("success", true);
            result.put("totalCleaned", totalCleaned);
            result.put("expiredFiles", expiredFiles);
            result.put("tempFiles", tempFiles);
            result.put("orphanFiles", orphanCleaned);
            
            log.info("存储空间清理完成，总清理文件数: {}", totalCleaned);
            
        } catch (Exception e) {
            log.error("存储空间清理失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> repairOrphanFiles() {
        Map<String, Object> result = new HashMap<>();
        int totalRepaired = 0;
        int totalDeleted = 0;
        
        try {
            PageResult<FileInfoVO> orphanFiles = getOrphanFiles(new FilePageDTO() {{
                setPageNo(1);
                setPageSize(1000);
            }});
            
            for (FileInfoVO orphan : orphanFiles.getList()) {
                // 尝试修复：重新检查文件是否存在
                try {
                    FileInfoDO file = fileInfoMapper.selectById(orphan.getId());
                    if (file != null) {
                        FileStorageStrategy strategy = storageFactory.getStrategy(file.getStorageType().toLowerCase());
                        if (strategy.fileExists(file.getStorageBucket(), file.getStoragePath())) {
                            // 文件实际存在，可能是临时的网络问题
                            totalRepaired++;
                        } else {
                            // 文件确实不存在，删除数据库记录
                            if (fileService.deleteFile(orphan.getId())) {
                                totalDeleted++;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("修复孤儿文件失败，文件ID: {}", orphan.getId(), e);
                }
            }
            
            result.put("success", true);
            result.put("totalRepaired", totalRepaired);
            result.put("totalDeleted", totalDeleted);
            
            log.info("孤儿文件修复完成，修复: {}, 删除: {}", totalRepaired, totalDeleted);
            
        } catch (Exception e) {
            log.error("孤儿文件修复失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}