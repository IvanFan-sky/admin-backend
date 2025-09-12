package com.admin.module.infra.biz.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.dto.FileChunkUploadDTO;
import com.admin.module.infra.api.dto.FilePageDTO;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.enums.FileUploadStatusEnum;
import com.admin.module.infra.api.vo.FileChunkUploadVO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.config.FileStorageConfig;
import com.admin.module.infra.biz.convert.FileConvert;
import com.admin.module.infra.biz.dal.dataobject.FileChunkDO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileChunkMapper;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import com.admin.module.infra.biz.service.FileService;
import com.admin.module.infra.biz.storage.FileStorageFactory;
import com.admin.module.infra.biz.storage.FileStorageStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final FileChunkMapper fileChunkMapper;
    private final FileStorageFactory storageFactory;
    private final FileStorageConfig fileStorageConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public FileUploadVO uploadFile(FileUploadDTO uploadDTO) {
        MultipartFile file = uploadDTO.getFile();
        
        // 文件校验
        validateFile(file);
        
        try {
            // 计算文件哈希值
            String fileHash = calculateFileHash(file.getInputStream());
            
            // 检查是否为去重文件
            FileInfoDO existingFile = findFileByHash(fileHash);
            if (existingFile != null && fileStorageConfig.getUpload().isEnableDeduplication()) {
                log.info("发现重复文件，直接返回现有文件信息，hash: {}", fileHash);
                FileUploadVO uploadVO = FileConvert.INSTANCE.convertToUploadVO(existingFile);
                uploadVO.setIsDuplicate(true);
                return uploadVO;
            }
            
            // 获取存储策略
            String storageType = StrUtil.isNotBlank(uploadDTO.getStorageType()) 
                    ? uploadDTO.getStorageType() 
                    : fileStorageConfig.getDefaultType();
            FileStorageStrategy strategy = storageFactory.getStrategy(storageType);
            
            // 生成文件路径和key
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String objectKey = generateObjectKey(uploadDTO.getBusinessType(), fileExtension);
            String bucketName = getBucketName(storageType);
            
            // 上传文件
            String fileUrl = strategy.uploadFile(bucketName, objectKey, file.getInputStream(), 
                    file.getContentType(), file.getSize());
            
            // 保存文件信息
            FileInfoDO fileInfoDO = new FileInfoDO();
            fileInfoDO.setFileName(file.getOriginalFilename());
            fileInfoDO.setFileKey(objectKey);
            fileInfoDO.setFileUrl(fileUrl);
            fileInfoDO.setFileSize(file.getSize());
            fileInfoDO.setContentType(file.getContentType());
            fileInfoDO.setFileExtension(fileExtension);
            fileInfoDO.setFileHash(fileHash);
            fileInfoDO.setStorageType(storageType.toUpperCase());
            fileInfoDO.setStorageBucket(bucketName);
            fileInfoDO.setStoragePath(objectKey);
            fileInfoDO.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
            fileInfoDO.setBusinessType(uploadDTO.getBusinessType());
            fileInfoDO.setBusinessId(uploadDTO.getBusinessId());
            fileInfoDO.setIsPublic(uploadDTO.getIsPublic());
            fileInfoDO.setDownloadCount(0);
            fileInfoDO.setRemark(uploadDTO.getRemark());
            fileInfoDO.setCreateBy(SecurityUtils.getUserId().toString());
            
            fileInfoMapper.insert(fileInfoDO);
            
            log.info("文件上传成功，文件ID: {}, 文件名: {}, 大小: {} bytes", 
                    fileInfoDO.getId(), file.getOriginalFilename(), file.getSize());
            
            return FileConvert.INSTANCE.convertToUploadVO(fileInfoDO);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String initiateChunkUpload(String fileName, Long fileSize, String contentType, 
                                     String businessType, String businessId) {
        
        // 校验分片上传条件
        if (fileSize < fileStorageConfig.getUpload().getChunkThreshold()) {
            throw new ServiceException("文件大小小于分片上传阈值，请使用普通上传");
        }
        
        // 生成上传会话ID
        String uploadId = UUID.randomUUID().toString();
        
        // 获取存储策略
        FileStorageStrategy strategy = storageFactory.getStrategy(fileStorageConfig.getDefaultType());
        
        // 生成对象键
        String fileExtension = getFileExtension(fileName);
        String objectKey = generateObjectKey(businessType, fileExtension);
        String bucketName = getBucketName(fileStorageConfig.getDefaultType());
        
        // 初始化多部分上传
        String multipartUploadId = strategy.initiateMultipartUpload(bucketName, objectKey, contentType);
        
        // 缓存上传会话信息
        String cacheKey = "chunk_upload:" + uploadId;
        ChunkUploadSession session = new ChunkUploadSession();
        session.setUploadId(uploadId);
        session.setMultipartUploadId(multipartUploadId);
        session.setFileName(fileName);
        session.setFileSize(fileSize);
        session.setContentType(contentType);
        session.setBusinessType(businessType);
        session.setBusinessId(businessId);
        session.setObjectKey(objectKey);
        session.setBucketName(bucketName);
        session.setStorageType(fileStorageConfig.getDefaultType());
        session.setCreateBy(SecurityUtils.getUserId().toString());
        
        redisTemplate.opsForValue().set(cacheKey, session, 24, TimeUnit.HOURS);
        
        log.info("初始化分片上传成功，uploadId: {}, fileName: {}, fileSize: {}", 
                uploadId, fileName, fileSize);
        
        return uploadId;
    }

    @Override
    @Transactional
    public FileChunkUploadVO uploadChunk(FileChunkUploadDTO chunkUploadDTO) {
        // 获取上传会话
        String cacheKey = "chunk_upload:" + chunkUploadDTO.getUploadId();
        ChunkUploadSession session = (ChunkUploadSession) redisTemplate.opsForValue().get(cacheKey);
        
        if (session == null) {
            throw new ServiceException("上传会话不存在或已过期");
        }
        
        try {
            // 获取存储策略
            FileStorageStrategy strategy = storageFactory.getStrategy(session.getStorageType());
            
            // 上传分片
            String etag = strategy.uploadPart(
                    session.getBucketName(),
                    session.getObjectKey(),
                    session.getMultipartUploadId(),
                    chunkUploadDTO.getChunkNumber(),
                    chunkUploadDTO.getChunkFile().getInputStream(),
                    chunkUploadDTO.getChunkFile().getSize()
            );
            
            // 保存分片信息
            FileChunkDO chunkDO = new FileChunkDO();
            chunkDO.setUploadId(chunkUploadDTO.getUploadId());
            chunkDO.setFileName(chunkUploadDTO.getFileName());
            chunkDO.setChunkNumber(chunkUploadDTO.getChunkNumber());
            chunkDO.setChunkSize(chunkUploadDTO.getChunkFile().getSize());
            chunkDO.setTotalChunks(chunkUploadDTO.getTotalChunks());
            chunkDO.setTotalSize(chunkUploadDTO.getTotalSize());
            chunkDO.setFileHash(chunkUploadDTO.getFileHash());
            chunkDO.setChunkHash(chunkUploadDTO.getChunkHash());
            chunkDO.setChunkKey(etag);
            chunkDO.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
            chunkDO.setBusinessType(chunkUploadDTO.getBusinessType());
            chunkDO.setBusinessId(chunkUploadDTO.getBusinessId());
            chunkDO.setCreateBy(SecurityUtils.getUserId().toString());
            chunkDO.setCreateTime(LocalDateTime.now());
            chunkDO.setUpdateTime(LocalDateTime.now());
            
            fileChunkMapper.insert(chunkDO);
            
            // 构建返回结果
            FileChunkUploadVO uploadVO = new FileChunkUploadVO();
            uploadVO.setUploadId(chunkUploadDTO.getUploadId());
            uploadVO.setChunkNumber(chunkUploadDTO.getChunkNumber());
            uploadVO.setEtag(etag);
            uploadVO.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
            
            // 检查是否为最后一个分片
            boolean isLastChunk = chunkUploadDTO.getChunkNumber().equals(chunkUploadDTO.getTotalChunks());
            uploadVO.setIsLastChunk(isLastChunk);
            
            if (isLastChunk) {
                // 自动完成分片上传
                FileInfoVO fileInfo = completeChunkUpload(chunkUploadDTO.getUploadId());
                uploadVO.setFileInfo(fileInfo);
            }
            
            log.info("分片上传成功，uploadId: {}, chunkNumber: {}/{}", 
                    chunkUploadDTO.getUploadId(), chunkUploadDTO.getChunkNumber(), chunkUploadDTO.getTotalChunks());
            
            return uploadVO;
            
        } catch (IOException e) {
            log.error("分片上传失败", e);
            throw new ServiceException("分片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public FileInfoVO completeChunkUpload(String uploadId) {
        // 获取上传会话
        String cacheKey = "chunk_upload:" + uploadId;
        ChunkUploadSession session = (ChunkUploadSession) redisTemplate.opsForValue().get(cacheKey);
        
        if (session == null) {
            throw new ServiceException("上传会话不存在或已过期");
        }
        
        try {
            // 查询所有分片
            List<FileChunkDO> chunks = fileChunkMapper.selectList(
                    new LambdaQueryWrapper<FileChunkDO>()
                            .eq(FileChunkDO::getUploadId, uploadId)
                            .orderByAsc(FileChunkDO::getChunkNumber)
            );
            
            if (chunks.isEmpty()) {
                throw new ServiceException("未找到分片信息");
            }
            
            // 检查分片完整性
            int totalChunks = chunks.get(0).getTotalChunks();
            if (chunks.size() != totalChunks) {
                throw new ServiceException("分片数量不完整");
            }
            
            // 转换为 PartETag
            List<FileStorageStrategy.PartETag> partETags = chunks.stream()
                    .sorted(Comparator.comparing(FileChunkDO::getChunkNumber))
                    .map(chunk -> new FileStorageStrategy.PartETag(chunk.getChunkNumber(), chunk.getChunkKey()))
                    .collect(Collectors.toList());
            
            // 获取存储策略并完成多部分上传
            FileStorageStrategy strategy = storageFactory.getStrategy(session.getStorageType());
            String fileUrl = strategy.completeMultipartUpload(
                    session.getBucketName(),
                    session.getObjectKey(),
                    session.getMultipartUploadId(),
                    partETags
            );
            
            // 保存文件信息
            FileInfoDO fileInfoDO = new FileInfoDO();
            fileInfoDO.setFileName(session.getFileName());
            fileInfoDO.setFileKey(session.getObjectKey());
            fileInfoDO.setFileUrl(fileUrl);
            fileInfoDO.setFileSize(session.getFileSize());
            fileInfoDO.setContentType(session.getContentType());
            fileInfoDO.setFileExtension(getFileExtension(session.getFileName()));
            fileInfoDO.setFileHash(chunks.get(0).getFileHash());
            fileInfoDO.setStorageType(session.getStorageType().toUpperCase());
            fileInfoDO.setStorageBucket(session.getBucketName());
            fileInfoDO.setStoragePath(session.getObjectKey());
            fileInfoDO.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
            fileInfoDO.setBusinessType(session.getBusinessType());
            fileInfoDO.setBusinessId(session.getBusinessId());
            fileInfoDO.setIsPublic(0);
            fileInfoDO.setDownloadCount(0);
            fileInfoDO.setCreateBy(session.getCreateBy());
            
            fileInfoMapper.insert(fileInfoDO);
            
            // 清理缓存和分片记录
            redisTemplate.delete(cacheKey);
            fileChunkMapper.delete(new LambdaQueryWrapper<FileChunkDO>().eq(FileChunkDO::getUploadId, uploadId));
            
            log.info("完成分片上传，文件ID: {}, 文件名: {}, 大小: {} bytes", 
                    fileInfoDO.getId(), session.getFileName(), session.getFileSize());
            
            return FileConvert.INSTANCE.convertToVO(fileInfoDO);
            
        } catch (Exception e) {
            log.error("完成分片上传失败", e);
            // 取消多部分上传
            abortChunkUpload(uploadId);
            throw new ServiceException("完成分片上传失败: " + e.getMessage());
        }
    }

    @Override
    public void abortChunkUpload(String uploadId) {
        // 获取上传会话
        String cacheKey = "chunk_upload:" + uploadId;
        ChunkUploadSession session = (ChunkUploadSession) redisTemplate.opsForValue().get(cacheKey);
        
        if (session != null) {
            try {
                // 取消多部分上传
                FileStorageStrategy strategy = storageFactory.getStrategy(session.getStorageType());
                strategy.abortMultipartUpload(session.getBucketName(), session.getObjectKey(), session.getMultipartUploadId());
                
            } catch (Exception e) {
                log.error("取消多部分上传失败", e);
            }
            
            // 清理缓存和分片记录
            redisTemplate.delete(cacheKey);
        }
        
        // 删除分片记录
        fileChunkMapper.delete(new LambdaQueryWrapper<FileChunkDO>().eq(FileChunkDO::getUploadId, uploadId));
        
        log.info("取消分片上传，uploadId: {}", uploadId);
    }

    @Override
    public void downloadFile(Long fileId, Boolean inline, String downloadName, HttpServletResponse response) {
        // 获取文件信息
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        
        try {
            // 获取存储策略
            FileStorageStrategy strategy = storageFactory.getStrategy(fileInfo.getStorageType().toLowerCase());
            
            // 下载文件流
            InputStream inputStream = strategy.downloadFile(fileInfo.getStorageBucket(), fileInfo.getStoragePath());
            
            // 设置响应头
            String fileName = StrUtil.isNotBlank(downloadName) ? downloadName : fileInfo.getFileName();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            
            response.setContentType(fileInfo.getContentType());
            response.setContentLengthLong(fileInfo.getFileSize());
            
            String disposition = (inline != null && inline) ? "inline" : "attachment";
            response.setHeader("Content-Disposition", disposition + "; filename=\"" + encodedFileName + "\"");
            response.setHeader("Cache-Control", "max-age=3600");
            
            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                IoUtil.copy(inputStream, outputStream);
            }
            
            // 更新下载次数
            updateDownloadCount(fileId);
            
            log.info("文件下载成功，文件ID: {}, 文件名: {}", fileId, fileName);
            
        } catch (Exception e) {
            log.error("文件下载失败，文件ID: {}", fileId, e);
            throw new ServiceException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteFile(Long fileId) {
        // 获取文件信息
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            return false;
        }
        
        try {
            // 获取存储策略并删除存储文件
            FileStorageStrategy strategy = storageFactory.getStrategy(fileInfo.getStorageType().toLowerCase());
            strategy.deleteFile(fileInfo.getStorageBucket(), fileInfo.getStoragePath());
            
            // 删除数据库记录
            fileInfoMapper.deleteById(fileId);
            
            log.info("删除文件成功，文件ID: {}, 文件名: {}", fileId, fileInfo.getFileName());
            return true;
            
        } catch (Exception e) {
            log.error("删除文件失败，文件ID: {}", fileId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public int deleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return 0;
        }
        
        int deletedCount = 0;
        for (Long fileId : fileIds) {
            if (deleteFile(fileId)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }

    @Override
    public FileInfoVO getFileInfo(Long fileId) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        
        return FileConvert.INSTANCE.convertToVO(fileInfo);
    }

    @Override
    public List<FileInfoVO> getFilesByBusiness(String businessType, String businessId) {
        List<FileInfoDO> fileList = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getBusinessType, businessType)
                        .eq(FileInfoDO::getBusinessId, businessId)
                        .eq(FileInfoDO::getUploadStatus, FileUploadStatusEnum.COMPLETED.getCode())
                        .orderByDesc(FileInfoDO::getCreateTime)
        );
        
        return FileConvert.INSTANCE.convertToVOList(fileList);
    }

    @Override
    public PageResult<FileInfoVO> getFilePage(FilePageDTO pageDTO) {
        LambdaQueryWrapper<FileInfoDO> wrapper = new LambdaQueryWrapper<FileInfoDO>()
                .like(StrUtil.isNotBlank(pageDTO.getFileName()), FileInfoDO::getFileName, pageDTO.getFileName())
                .eq(StrUtil.isNotBlank(pageDTO.getContentType()), FileInfoDO::getContentType, pageDTO.getContentType())
                .eq(StrUtil.isNotBlank(pageDTO.getBusinessType()), FileInfoDO::getBusinessType, pageDTO.getBusinessType())
                .eq(StrUtil.isNotBlank(pageDTO.getBusinessId()), FileInfoDO::getBusinessId, pageDTO.getBusinessId())
                .eq(StrUtil.isNotBlank(pageDTO.getStorageType()), FileInfoDO::getStorageType, pageDTO.getStorageType())
                .eq(pageDTO.getUploadStatus() != null, FileInfoDO::getUploadStatus, pageDTO.getUploadStatus())
                .eq(pageDTO.getIsPublic() != null, FileInfoDO::getIsPublic, pageDTO.getIsPublic())
                .eq(StrUtil.isNotBlank(pageDTO.getCreateBy()), FileInfoDO::getCreateBy, pageDTO.getCreateBy())
                .ge(pageDTO.getStartTime() != null, FileInfoDO::getCreateTime, pageDTO.getStartTime())
                .le(pageDTO.getEndTime() != null, FileInfoDO::getCreateTime, pageDTO.getEndTime())
                .orderByDesc(FileInfoDO::getCreateTime);
        
        IPage<FileInfoDO> page = fileInfoMapper.selectPage(
                new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize()), wrapper);
        
        return new PageResult<>(
                FileConvert.INSTANCE.convertToVOList(page.getRecords()),
                page.getTotal()
        );
    }

    @Override
    public String generatePresignedUploadUrl(String fileName, String contentType, int expirationSeconds) {
        FileStorageStrategy strategy = storageFactory.getDefaultStrategy();
        String fileExtension = getFileExtension(fileName);
        String objectKey = generateObjectKey("temp", fileExtension);
        String bucketName = getBucketName(fileStorageConfig.getDefaultType());
        
        return strategy.generatePresignedUploadUrl(bucketName, objectKey, expirationSeconds);
    }

    @Override
    public String generatePresignedDownloadUrl(Long fileId, int expirationSeconds) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        
        FileStorageStrategy strategy = storageFactory.getStrategy(fileInfo.getStorageType().toLowerCase());
        return strategy.generatePresignedDownloadUrl(fileInfo.getStorageBucket(), fileInfo.getStoragePath(), expirationSeconds);
    }

    @Override
    public int cleanupExpiredFiles() {
        // 查询过期文件
        List<FileInfoDO> expiredFiles = fileInfoMapper.selectList(
                new LambdaQueryWrapper<FileInfoDO>()
                        .isNotNull(FileInfoDO::getExpireTime)
                        .lt(FileInfoDO::getExpireTime, LocalDateTime.now())
        );
        
        int cleanedCount = 0;
        for (FileInfoDO file : expiredFiles) {
            if (deleteFile(file.getId())) {
                cleanedCount++;
            }
        }
        
        log.info("清理过期文件完成，清理数量: {}", cleanedCount);
        return cleanedCount;
    }

    @Override
    public int cleanupTempFiles() {
        // 清理过期的分片上传会话
        LocalDateTime expireTime = LocalDateTime.now().minusHours(fileStorageConfig.getUpload().getTempFileRetentionHours());
        
        List<FileChunkDO> expiredChunks = fileChunkMapper.selectList(
                new LambdaQueryWrapper<FileChunkDO>()
                        .lt(FileChunkDO::getCreateTime, expireTime)
        );
        
        // 按上传ID分组
        expiredChunks.stream()
                .collect(Collectors.groupingBy(FileChunkDO::getUploadId))
                .keySet()
                .forEach(this::abortChunkUpload);
        
        log.info("清理临时文件完成，清理上传会话数量: {}", expiredChunks.size());
        return expiredChunks.size();
    }

    @Override
    public FileInfoDO findFileByHash(String fileHash) {
        if (StrUtil.isBlank(fileHash)) {
            return null;
        }
        
        return fileInfoMapper.selectOne(
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getFileHash, fileHash)
                        .eq(FileInfoDO::getUploadStatus, FileUploadStatusEnum.COMPLETED.getCode())
                        .orderByDesc(FileInfoDO::getCreateTime)
                        .last("LIMIT 1")
        );
    }

    /**
     * 文件校验
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        
        // 文件大小检查
        if (file.getSize() > fileStorageConfig.getUpload().getMaxFileSize()) {
            throw new ServiceException("文件大小超出限制");
        }
        
        // 文件类型检查
        String contentType = file.getContentType();
        if (contentType != null && !Arrays.asList(fileStorageConfig.getUpload().getAllowedTypes()).contains(contentType)) {
            throw new ServiceException("不支持的文件类型: " + contentType);
        }
        
        // 文件扩展名检查
        String fileName = file.getOriginalFilename();
        if (StrUtil.isNotBlank(fileName)) {
            String extension = getFileExtension(fileName);
            if (StrUtil.isNotBlank(extension) && 
                !Arrays.asList(fileStorageConfig.getUpload().getAllowedExtensions()).contains(extension)) {
                throw new ServiceException("不支持的文件扩展名: " + extension);
            }
        }
    }

    /**
     * 计算文件哈希值
     */
    private String calculateFileHash(InputStream inputStream) throws IOException {
        return DigestUtil.sha256Hex(inputStream);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * 生成对象键
     */
    private String generateObjectKey(String businessType, String fileExtension) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = String.format("%04d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String uuid = UUID.randomUUID().toString();
        
        return String.format("%s/%s/%s.%s", 
                StrUtil.isNotBlank(businessType) ? businessType : "common",
                datePath, uuid, fileExtension);
    }

    /**
     * 获取存储桶名称
     */
    private String getBucketName(String storageType) {
        return "minio".equalsIgnoreCase(storageType) 
                ? fileStorageConfig.getMinio().getDefaultBucket()
                : fileStorageConfig.getOss().getDefaultBucket();
    }

    /**
     * 更新下载次数
     */
    private void updateDownloadCount(Long fileId) {
        fileInfoMapper.selectById(fileId);
        // 使用乐观更新避免并发问题
        fileInfoMapper.update(null, 
                new LambdaQueryWrapper<FileInfoDO>()
                        .eq(FileInfoDO::getId, fileId)
                        .setSql("download_count = download_count + 1"));
    }

    /**
     * 分片上传会话
     */
    private static class ChunkUploadSession {
        private String uploadId;
        private String multipartUploadId;
        private String fileName;
        private Long fileSize;
        private String contentType;
        private String businessType;
        private String businessId;
        private String objectKey;
        private String bucketName;
        private String storageType;
        private String createBy;

        // Getters and Setters
        public String getUploadId() { return uploadId; }
        public void setUploadId(String uploadId) { this.uploadId = uploadId; }
        public String getMultipartUploadId() { return multipartUploadId; }
        public void setMultipartUploadId(String multipartUploadId) { this.multipartUploadId = multipartUploadId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }
        public String getBusinessId() { return businessId; }
        public void setBusinessId(String businessId) { this.businessId = businessId; }
        public String getObjectKey() { return objectKey; }
        public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
        public String getBucketName() { return bucketName; }
        public void setBucketName(String bucketName) { this.bucketName = bucketName; }
        public String getStorageType() { return storageType; }
        public void setStorageType(String storageType) { this.storageType = storageType; }
        public String getCreateBy() { return createBy; }
        public void setCreateBy(String createBy) { this.createBy = createBy; }
    }
}