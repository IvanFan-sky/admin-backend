package com.admin.module.infra.biz.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.common.result.minio.UploadResult;
import com.admin.framework.minio.service.MinioService;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.enums.FileUploadStatusEnum;
import com.admin.module.infra.api.service.FileService;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.convert.FileConvert;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import com.admin.module.infra.biz.service.ContentTypeDetectionService;
import com.admin.module.infra.biz.service.StreamingDownloadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileInfoMapper fileInfoMapper;
    private final MinioService minioService;
    private final ContentTypeDetectionService contentTypeDetectionService;
    private final StreamingDownloadService streamingDownloadService;

    @Value("${admin.file.default-bucket:default}")
    private String defaultBucket;

    @Value("${admin.file.max-file-size:10485760}") // 10MB
    private Long maxFileSize;

    @Value("${admin.file.enable-deduplication:true}")
    private Boolean enableDeduplication;

    @Override
    @Transactional
    public FileUploadVO uploadFile(FileUploadDTO uploadDTO) {
        MultipartFile file = uploadDTO.getFile();
        
        // 文件校验
        validateFile(file);
        
        try {
            // 计算文件哈希值
            String fileHash = calculateFileHash(file);
            
            // 检查文件去重
            if (enableDeduplication) {
                FileInfoVO existingFile = findFileByHash(fileHash);
                if (existingFile != null) {
                    log.info("发现重复文件，直接返回现有文件: fileHash={}", fileHash);
                    return createUploadVO(existingFile, true);
                }
            }
            
            // 检测文件类型
            ContentTypeDetectionService.FileTypeValidationResult validationResult = 
                    contentTypeDetectionService.validateFileType(file);
            if (!validationResult.isAllowed()) {
                throw new ServiceException("文件类型不被允许: " + validationResult.getMessage());
            }
            
            // 生成文件路径
            String fileName = generateFileName(file.getOriginalFilename());
            String filePath = generateFilePath(fileName);
            
            // 上传到MinIO
            UploadResult uploadResult = minioService.uploadFile(defaultBucket, filePath, file.getInputStream(), 
                    file.getSize(), validationResult.getContentType());
            
            // 保存文件信息
            FileInfoDO fileInfo = buildFileInfo(file, fileName, filePath, fileHash, validationResult.getContentType());
            fileInfoMapper.insert(fileInfo);
            
            log.info("文件上传成功: fileId={}, fileName={}, size={}", 
                    fileInfo.getId(), fileName, file.getSize());
            
            return createUploadVO(FileConvert.INSTANCE.convert(fileInfo), false);
            
        } catch (Exception e) {
            log.error("文件上传失败: fileName={}", file.getOriginalFilename(), e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadFile(Long fileId, HttpServletRequest request, HttpServletResponse response,
                            Boolean inline, String downloadName) {
        // 获取文件信息
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null || fileInfo.getUploadStatus().equals(FileUploadStatusEnum.DELETED.getCode())) {
            throw new ServiceException("文件不存在或已删除");
        }
        
        // 更新下载统计
        fileInfoMapper.updateDownloadCount(fileId);
        
        // 使用流式下载服务
        streamingDownloadService.streamDownload(fileId, request, response, inline, downloadName);
    }

    @Override
    public FileInfoVO getFileInfo(Long fileId) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        return FileConvert.INSTANCE.convert(fileInfo);
    }

    @Override
    public FileInfoVO findFileByHash(String fileHash) {
        if (StrUtil.isBlank(fileHash)) {
            return null;
        }
        
        FileInfoDO fileInfo = fileInfoMapper.selectByFileHash(fileHash, FileUploadStatusEnum.COMPLETED.getCode());
        return fileInfo != null ? FileConvert.INSTANCE.convert(fileInfo) : null;
    }

    @Override
    public PageResult<FileInfoVO> getFileList(FilePageDTO pageDTO) {
        LambdaQueryWrapper<FileInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        queryWrapper.like(StrUtil.isNotBlank(pageDTO.getFileName()), 
                         FileInfoDO::getFileName, pageDTO.getFileName())
                   .eq(StrUtil.isNotBlank(pageDTO.getContentType()), 
                      FileInfoDO::getContentType, pageDTO.getContentType())
                   .eq(StrUtil.isNotBlank(pageDTO.getBusinessType()), 
                      FileInfoDO::getBusinessType, pageDTO.getBusinessType())
                   .eq(pageDTO.getUploadStatus() != null, 
                      FileInfoDO::getUploadStatus, pageDTO.getUploadStatus())
                   .ge(pageDTO.getStartTime() != null, 
                      FileInfoDO::getCreateTime, pageDTO.getStartTime())
                   .le(pageDTO.getEndTime() != null, 
                      FileInfoDO::getCreateTime, pageDTO.getEndTime())
                   .orderByDesc(FileInfoDO::getCreateTime);
        
        // 分页查询
        IPage<FileInfoDO> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        IPage<FileInfoDO> result = fileInfoMapper.selectPage(page, queryWrapper);
        
        // 转换结果
        List<FileInfoVO> fileList = FileConvert.INSTANCE.convertList(result.getRecords());
        return new PageResult<>(fileList, result.getTotal());
    }

    @Override
    @Transactional
    public Boolean deleteFile(Long fileId) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        
        try {
            // 删除MinIO中的文件
            minioService.deleteFile(fileInfo.getBucketName(), fileInfo.getFilePath());
            
            // 更新数据库状态
            FileInfoDO updateFile = new FileInfoDO();
            updateFile.setId(fileId);
            updateFile.setUploadStatus(FileUploadStatusEnum.DELETED.getCode());
            fileInfoMapper.updateById(updateFile);
            
            log.info("文件删除成功: fileId={}, fileName={}", fileId, fileInfo.getFileName());
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: fileId={}, fileName={}", fileId, fileInfo.getFileName(), e);
            throw new ServiceException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Integer batchDeleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Long fileId : fileIds) {
            try {
                if (deleteFile(fileId)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量删除文件失败: fileId={}", fileId, e);
            }
        }
        
        return successCount;
    }

    @Override
    public String getFileAccessUrl(Long fileId, Integer expireSeconds) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null || fileInfo.getUploadStatus().equals(FileUploadStatusEnum.DELETED.getCode())) {
            throw new ServiceException("文件不存在或已删除");
        }
        
        // 生成预签名URL
        return minioService.getPresignedDownloadUrl(fileInfo.getBucketName(), fileInfo.getFilePath(), 
                                          expireSeconds != null ? expireSeconds : 3600);
    }

    /**
     * 文件校验
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new ServiceException("文件大小超出限制，最大允许: " + (maxFileSize / 1024 / 1024) + "MB");
        }
    }

    /**
     * 计算文件哈希值
     */
    private String calculateFileHash(MultipartFile file) throws IOException {
        return DigestUtil.md5Hex(file.getInputStream());
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + (StrUtil.isNotBlank(extension) ? "." + extension : "");
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String fileName) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d/%02d/%02d/%s", now.getYear(), now.getMonthValue(), now.getDayOfMonth(), fileName);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    /**
     * 构建文件信息对象
     */
    private FileInfoDO buildFileInfo(MultipartFile file, String fileName, String filePath, 
                                   String fileHash, String contentType) {
        FileInfoDO fileInfo = new FileInfoDO();
        fileInfo.setFileName(fileName);
        fileInfo.setOriginalFileName(file.getOriginalFilename());
        fileInfo.setFilePath(filePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setContentType(contentType);
        fileInfo.setFileExtension(getFileExtension(file.getOriginalFilename()));
        fileInfo.setFileHash(fileHash);
        fileInfo.setStorageType("MINIO");
        fileInfo.setBucketName(defaultBucket);
        fileInfo.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
        fileInfo.setIsChunked(false);
        fileInfo.setDownloadCount(0);
        
        // 设置上传用户信息
        try {
            // TODO: 集成用户上下文获取用户信息
            // fileInfo.setUploadUserId(SecurityUtils.getUserId());
            // fileInfo.setUploadUserName(SecurityUtils.getUsername());
            fileInfo.setUploadUserId(1L);
            fileInfo.setUploadUserName("admin");
        } catch (Exception e) {
            log.warn("获取当前用户信息失败", e);
        }
        
        return fileInfo;
    }

    /**
     * 创建上传结果
     */
    private FileUploadVO createUploadVO(FileInfoVO fileInfo, boolean isDuplicate) {
        FileUploadVO uploadVO = new FileUploadVO();
        uploadVO.setFileId(fileInfo.getId());
        uploadVO.setFileName(fileInfo.getFileName());
        uploadVO.setOriginalFileName(fileInfo.getOriginalFileName());
        uploadVO.setFileSize(fileInfo.getFileSize());
        uploadVO.setContentType(fileInfo.getContentType());
        uploadVO.setFileHash(fileInfo.getFileHash());
        uploadVO.setAccessUrl(fileInfo.getAccessUrl());
        uploadVO.setIsDuplicate(isDuplicate);
        return uploadVO;
    }
}
