package com.admin.module.infra.biz.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.admin.common.exception.ServiceException;
import com.admin.common.result.minio.UploadResult;
import com.admin.framework.minio.service.MinioService;
import com.admin.module.infra.api.dto.ChunkUploadDTO;
import com.admin.module.infra.api.dto.ChunkUploadInitDTO;
import com.admin.module.infra.api.enums.FileUploadStatusEnum;
import com.admin.module.infra.api.service.ChunkUploadService;
import com.admin.module.infra.api.vo.ChunkInfoVO;
import com.admin.module.infra.api.vo.ChunkUploadInitVO;
import com.admin.module.infra.api.vo.ChunkUploadSessionVO;
import com.admin.module.infra.api.vo.ChunkUploadVO;

import com.admin.module.infra.biz.convert.FileConvert;
import com.admin.module.infra.biz.dal.dataobject.FileChunkDO;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileChunkMapper;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import com.admin.module.infra.biz.service.ChunkUploadCacheService;
import com.admin.module.infra.biz.service.ContentTypeDetectionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分片上传服务实现
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkUploadServiceImpl implements ChunkUploadService {

    private final MinioService minioService;
    private final ChunkUploadCacheService cacheService;
    private final ContentTypeDetectionService contentTypeDetectionService;
    private final FileInfoMapper fileInfoMapper;
    private final FileChunkMapper fileChunkMapper;

    @Value("${admin.minio.default-bucket}")
    private String defaultBucket;

    /**
     * 默认分片大小：5MB
     */
    private static final long DEFAULT_CHUNK_SIZE = 5 * 1024 * 1024L;

    /**
     * 最小分片大小：1MB
     */
    private static final long MIN_CHUNK_SIZE = 1024 * 1024L;

    /**
     * 最大分片大小：100MB
     */
    private static final long MAX_CHUNK_SIZE = 100 * 1024 * 1024L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChunkUploadInitVO initChunkUpload(ChunkUploadInitDTO initDTO) {
        log.info("初始化分片上传: fileName={}, fileSize={}, fileMd5={}", 
                initDTO.getFileName(), initDTO.getFileSize(), initDTO.getFileMd5());

        // 1. 参数验证
        validateInitRequest(initDTO);

        // 2. 检查文件是否已存在（秒传）
        FileInfoDO existingFile = checkFileExists(initDTO.getFileMd5());
        if (existingFile != null && existingFile.getUploadStatus().equals(FileUploadStatusEnum.COMPLETED.getCode())) {
            log.info("文件已存在，支持秒传: fileId={}", existingFile.getId());
            return ChunkUploadInitVO.instantUpload(existingFile.getId(), "文件已存在，秒传成功");
        }

        // 3. 计算分片参数
        long chunkSize = calculateChunkSize(initDTO.getFileSize(), initDTO.getChunkSize());
        int totalChunks = (int) Math.ceil((double) initDTO.getFileSize() / chunkSize);

        // 4. 创建文件记录
        FileInfoDO fileInfo = createFileRecord(initDTO, chunkSize, totalChunks);
        fileInfoMapper.insert(fileInfo);

        // 5. 生成上传会话ID
        String uploadId = generateUploadId();

        // 6. 缓存上传会话信息
       ChunkUploadSessionVO session = buildUploadSession(
                fileInfo, initDTO, chunkSize, totalChunks, uploadId);
        cacheService.cacheUploadSession(uploadId, session);

        // 7. 检查是否有已上传的分片（断点续传）
        List<Integer> uploadedChunks = getUploadedChunkNumbers(fileInfo.getId());

        // 8. 生成上传URL（可选，这里返回空，由前端直接调用上传接口）
        String uploadUrl = null;
        int expireTime = 3600; // 1小时

        log.info("分片上传初始化成功: uploadId={}, fileId={}, totalChunks={}, uploadedChunks={}", 
                uploadId, fileInfo.getId(), totalChunks, uploadedChunks.size());

        return ChunkUploadInitVO.needUpload(uploadId, fileInfo.getId(), chunkSize, 
                totalChunks, uploadedChunks, uploadUrl, expireTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChunkUploadVO uploadChunk(ChunkUploadDTO chunkDTO) {
        log.info("上传文件分片: uploadId={}, chunkNumber={}/{}", 
                chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), chunkDTO.getTotalChunks());

        // 1. 获取上传会话信息
        ChunkUploadSessionVO session = cacheService.getUploadSession(chunkDTO.getUploadId());
        if (session == null) {
            throw new ServiceException("上传会话不存在或已过期，请重新初始化上传");
        }

        // 2. 验证分片参数
        validateChunkRequest(chunkDTO, session);

        // 3. 检查分片是否已上传（避免重复上传）
        if (cacheService.isChunkUploaded(chunkDTO.getUploadId(), chunkDTO.getChunkNumber())) {
            log.info("分片已存在，跳过上传: uploadId={}, chunkNumber={}", 
                    chunkDTO.getUploadId(), chunkDTO.getChunkNumber());
            return buildChunkResponse(chunkDTO, session, "分片已存在", null);
        }

        // 4. 验证分片MD5（如果提供）
        if (StrUtil.isNotBlank(chunkDTO.getChunkMd5())) {
            validateChunkMd5(chunkDTO.getChunkFile(), chunkDTO.getChunkMd5());
        }

        // 5. 上传分片到MinIO
        String etag = uploadChunkToMinio(chunkDTO, session);

        // 6. 记录分片信息
        recordChunkInfo(session.getFileId(), chunkDTO, etag);

        // 7. 标记分片已上传（缓存）
        cacheService.markChunkUploaded(chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), etag);

        // 8. 检查是否所有分片都已上传
        Map<Integer, ChunkInfoVO> uploadedChunks =
                cacheService.getUploadedChunks(chunkDTO.getUploadId());
        
        boolean allUploaded = uploadedChunks.size() >= chunkDTO.getTotalChunks();
        
        if (allUploaded) {
            // 自动完成上传
            return completeChunkUpload(chunkDTO.getUploadId());
        }

        // 9. 延长缓存过期时间
        cacheService.extendCacheExpire(chunkDTO.getUploadId());

        log.info("分片上传成功: uploadId={}, chunkNumber={}, progress={}/{}", 
                chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), uploadedChunks.size(), chunkDTO.getTotalChunks());

        return buildChunkResponse(chunkDTO, session, "分片上传成功", etag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChunkUploadVO completeChunkUpload(String uploadId) {
        log.info("完成分片上传: uploadId={}", uploadId);

        // 1. 获取上传会话信息
        ChunkUploadSessionVO session = cacheService.getUploadSession(uploadId);
        if (session == null) {
            throw new ServiceException("上传会话不存在或已过期");
        }

        // 2. 验证所有分片都已上传
        Map<Integer, ChunkInfoVO> uploadedChunks =
                cacheService.getUploadedChunks(uploadId);
        
        if (uploadedChunks.size() < session.getTotalChunks()) {
            throw new ServiceException(String.format("分片上传未完成，已上传: %d/%d", 
                    uploadedChunks.size(), session.getTotalChunks()));
        }

        // 3. 合并分片为完整文件
        String finalObjectName = mergeChunks(session, uploadedChunks);

        // 4. 更新文件信息
        updateFileCompleted(session.getFileId(), finalObjectName);

        // 5. 清理分片文件和缓存
        cleanupChunks(session.getFileId(), uploadedChunks);
        cacheService.clearUploadCache(uploadId);

        log.info("分片上传完成: uploadId={}, fileId={}, finalObject={}", 
                uploadId, session.getFileId(), finalObjectName);

        return ChunkUploadVO.completed(uploadId, session.getFileId(), "文件上传完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelChunkUpload(String uploadId) {
        log.info("取消分片上传: uploadId={}", uploadId);

        // 1. 获取上传会话信息
        ChunkUploadSessionVO session = cacheService.getUploadSession(uploadId);
        if (session == null) {
            log.warn("上传会话不存在: uploadId={}", uploadId);
            return;
        }

        // 2. 删除已上传的分片
        Map<Integer, ChunkInfoVO> uploadedChunks =
                cacheService.getUploadedChunks(uploadId);
        cleanupChunks(session.getFileId(), uploadedChunks);

        // 3. 更新文件状态为已删除
        FileInfoDO fileInfo = new FileInfoDO();
        fileInfo.setId(session.getFileId());
        fileInfo.setUploadStatus(FileUploadStatusEnum.DELETED.getCode());
        fileInfo.setUpdateTime(LocalDateTime.now());
        fileInfoMapper.updateById(fileInfo);

        // 4. 清理缓存
        cacheService.clearUploadCache(uploadId);

        log.info("分片上传已取消: uploadId={}, fileId={}", uploadId, session.getFileId());
    }

    @Override
    public ChunkUploadVO getUploadProgress(String uploadId) {
        // 1. 获取上传会话信息
        ChunkUploadSessionVO session = cacheService.getUploadSession(uploadId);
        if (session == null) {
            throw new ServiceException("上传会话不存在或已过期");
        }

        // 2. 获取已上传分片信息
        Map<Integer, ChunkInfoVO> uploadedChunks =
                cacheService.getUploadedChunks(uploadId);

        // 3. 计算进度
        double progress = (double) uploadedChunks.size() / session.getTotalChunks() * 100;
        boolean completed = uploadedChunks.size() >= session.getTotalChunks();

        return ChunkUploadVO.success(uploadId, null, null, completed, progress, 
                uploadedChunks.size(), session.getTotalChunks(), "获取进度成功");
    }

    @Override
    public boolean isChunkExists(String uploadId, Integer chunkNumber) {
        return cacheService.isChunkUploaded(uploadId, chunkNumber);
    }

    /**
     * 验证初始化请求参数
     */
    private void validateInitRequest(ChunkUploadInitDTO initDTO) {
        if (initDTO.getFileSize() <= 0) {
            throw new ServiceException("文件大小必须大于0");
        }
        
        if (initDTO.getFileSize() > 10L * 1024 * 1024 * 1024) { // 10GB
            throw new ServiceException("文件大小不能超过10GB");
        }
        
        if (StrUtil.isBlank(initDTO.getFileMd5()) || initDTO.getFileMd5().length() != 32) {
            throw new ServiceException("文件MD5格式错误");
        }
    }

    /**
     * 检查文件是否已存在
     */
    private FileInfoDO checkFileExists(String fileMd5) {
        LambdaQueryWrapper<FileInfoDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfoDO::getFileHash, fileMd5)
               .eq(FileInfoDO::getDeleted, false)
               .orderByDesc(FileInfoDO::getCreateTime)
               .last("LIMIT 1");
        
        return fileInfoMapper.selectOne(wrapper);
    }

    /**
     * 计算分片大小
     */
    private long calculateChunkSize(long fileSize, Long requestChunkSize) {
        if (requestChunkSize != null && requestChunkSize >= MIN_CHUNK_SIZE && requestChunkSize <= MAX_CHUNK_SIZE) {
            return requestChunkSize;
        }
        
        // 根据文件大小自动计算合适的分片大小
        if (fileSize <= 100 * 1024 * 1024) { // 100MB以下
            return Math.min(DEFAULT_CHUNK_SIZE, fileSize);
        } else if (fileSize <= 1024 * 1024 * 1024) { // 1GB以下
            return 10 * 1024 * 1024L; // 10MB
        } else {
            return 50 * 1024 * 1024L; // 50MB
        }
    }

    /**
     * 创建文件记录
     */
    private FileInfoDO createFileRecord(ChunkUploadInitDTO initDTO, long chunkSize, int totalChunks) {
        FileInfoDO fileInfo = new FileInfoDO();
        fileInfo.setFileName(generateFileName(initDTO.getFileName()));
        fileInfo.setOriginalFileName(initDTO.getFileName());
        fileInfo.setFilePath(""); // 分片上传时暂时为空
        fileInfo.setFileSize(initDTO.getFileSize());
        fileInfo.setContentType(StrUtil.isNotBlank(initDTO.getContentType()) ? 
                initDTO.getContentType() : "application/octet-stream");
        fileInfo.setFileExtension(getFileExtension(initDTO.getFileName()));
        fileInfo.setFileHash(initDTO.getFileMd5());
        fileInfo.setStorageType("MINIO");
        fileInfo.setBucketName(defaultBucket);
        fileInfo.setUploadStatus(FileUploadStatusEnum.UPLOADING.getCode());
        fileInfo.setIsChunked(true);
        fileInfo.setTotalChunks(totalChunks);
        fileInfo.setUploadId(UUID.randomUUID().toString());
        fileInfo.setBusinessType(initDTO.getBusinessType());
        fileInfo.setBusinessId(initDTO.getBusinessId());
        // fileInfo.setUploadUserId(); // TODO: 从SecurityUtils获取
        // fileInfo.setUploadUserName(); // TODO: 从SecurityUtils获取
        fileInfo.setDownloadCount(0);
        fileInfo.setTags(initDTO.getTags());
        fileInfo.setRemark(initDTO.getRemark());
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setUpdateTime(LocalDateTime.now());
        fileInfo.setDeleted(0); // 0-未删除，1-已删除
        
        return fileInfo;
    }

    /**
     * 构建上传会话信息
     */
    private ChunkUploadSessionVO buildUploadSession(
            FileInfoDO fileInfo, ChunkUploadInitDTO initDTO, long chunkSize, int totalChunks, String uploadId) {
        
        ChunkUploadSessionVO session = new ChunkUploadSessionVO();
        session.setFileId(fileInfo.getId());
        session.setFileName(initDTO.getFileName());
        session.setFileSize(initDTO.getFileSize());
        session.setFileMd5(initDTO.getFileMd5());
        session.setContentType(initDTO.getContentType());
        session.setChunkSize(chunkSize);
        session.setTotalChunks(totalChunks);
        session.setBusinessType(initDTO.getBusinessType());
        session.setBusinessId(initDTO.getBusinessId());
        session.setTags(initDTO.getTags());
        session.setRemark(initDTO.getRemark());
        session.setCreateTime(System.currentTimeMillis());
        // session.setUploadUserId(); // TODO: 从SecurityUtils获取
        // session.setUploadUserName(); // TODO: 从SecurityUtils获取
        
        return session;
    }

    /**
     * 获取已上传的分片序号
     */
    private List<Integer> getUploadedChunkNumbers(Long fileId) {
        LambdaQueryWrapper<FileChunkDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileChunkDO::getFileId, fileId)
               .eq(FileChunkDO::getUploadStatus, 1) // 上传完成
               .select(FileChunkDO::getChunkNumber);
        
        List<FileChunkDO> chunks = fileChunkMapper.selectList(wrapper);
        return chunks.stream().map(FileChunkDO::getChunkNumber).toList();
    }

    /**
     * 生成上传会话ID
     */
    private String generateUploadId() {
        return "upload_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 验证分片请求参数
     */
    private void validateChunkRequest(ChunkUploadDTO chunkDTO, ChunkUploadSessionVO session) {
        if (chunkDTO.getChunkNumber() < 1 || chunkDTO.getChunkNumber() > session.getTotalChunks()) {
            throw new ServiceException("分片序号超出范围");
        }
        
        if (!chunkDTO.getTotalChunks().equals(session.getTotalChunks())) {
            throw new ServiceException("总分片数不匹配");
        }
        
        if (chunkDTO.getChunkFile() == null || chunkDTO.getChunkFile().isEmpty()) {
            throw new ServiceException("分片文件不能为空");
        }
    }

    /**
     * 验证分片MD5
     */
    private void validateChunkMd5(org.springframework.web.multipart.MultipartFile chunkFile, String expectedMd5) {
        try (InputStream inputStream = chunkFile.getInputStream()) {
            String actualMd5 = DigestUtil.md5Hex(inputStream);
            if (!expectedMd5.equalsIgnoreCase(actualMd5)) {
                throw new ServiceException("分片MD5校验失败");
            }
        } catch (IOException e) {
            throw new ServiceException("读取分片文件失败: " + e.getMessage());
        }
    }

    /**
     * 上传分片到MinIO
     */
    private String uploadChunkToMinio(ChunkUploadDTO chunkDTO, ChunkUploadSessionVO session) {
        try {
            String chunkObjectName = String.format("chunks/%s/%d", 
                    chunkDTO.getUploadId(), chunkDTO.getChunkNumber());
            
            UploadResult uploadResult = minioService.uploadFile(defaultBucket, chunkObjectName, 
                    chunkDTO.getChunkFile().getInputStream(), 
                    chunkDTO.getChunkFile().getSize(), 
                    chunkDTO.getChunkFile().getContentType());
            
            if (!uploadResult.isSuccess()) {
                throw new ServiceException("分片上传失败: " + uploadResult.getMessage());
            }
            
            String etag = uploadResult.getUrl(); // 使用URL作为etag标识
            
            return etag;
        } catch (Exception e) {
            log.error("上传分片到MinIO失败: uploadId={}, chunkNumber={}", 
                    chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), e);
            throw new ServiceException("分片上传失败: " + e.getMessage());
        }
    }

    /**
     * 记录分片信息
     */
    private void recordChunkInfo(Long fileId, ChunkUploadDTO chunkDTO, String etag) {
        FileChunkDO chunk = new FileChunkDO();
        chunk.setFileId(fileId);
        chunk.setUploadId(chunkDTO.getUploadId());
        chunk.setChunkNumber(chunkDTO.getChunkNumber());
        chunk.setChunkSize(chunkDTO.getChunkFile().getSize());
        chunk.setChunkHash(chunkDTO.getChunkMd5());
        chunk.setEtag(etag);
        chunk.setUploadStatus(1); // 上传完成
        chunk.setStoragePath(String.format("chunks/%s/%d", chunkDTO.getUploadId(), chunkDTO.getChunkNumber()));
        chunk.setRetryCount(0);
        chunk.setUploadStartTime(LocalDateTime.now());
        chunk.setUploadEndTime(LocalDateTime.now());
        chunk.setCreateTime(LocalDateTime.now());
        chunk.setUpdateTime(LocalDateTime.now());
        chunk.setDeleted(0); // 0-未删除，1-已删除
        
        fileChunkMapper.insert(chunk);
    }

    /**
     * 构建分片响应
     */
    private ChunkUploadVO buildChunkResponse(ChunkUploadDTO chunkDTO, 
                                           ChunkUploadSessionVO session,
                                           String message, String etag) {
        Map<Integer, ChunkInfoVO> uploadedChunks =
                cacheService.getUploadedChunks(chunkDTO.getUploadId());
        
        double progress = (double) uploadedChunks.size() / session.getTotalChunks() * 100;
        boolean completed = uploadedChunks.size() >= session.getTotalChunks();
        
        return ChunkUploadVO.success(chunkDTO.getUploadId(), chunkDTO.getChunkNumber(), etag, 
                completed, progress, uploadedChunks.size(), session.getTotalChunks(), message);
    }

    /**
     * 合并分片为完整文件
     */
    private String mergeChunks(ChunkUploadSessionVO session,
                              Map<Integer, ChunkInfoVO> uploadedChunks) {
        try {
            // 生成最终文件对象名称
            String finalObjectName = String.format("files/%s/%s", 
                    session.getFileMd5().substring(0, 2), session.getFileName());
            
            // 初始化分片上传
            String uploadId = minioService.initMultipartUpload(defaultBucket, finalObjectName, session.getContentType());
            
            // 准备分片信息列表
            List<MinioService.PartInfo> parts = new ArrayList<>();
            for (int i = 1; i <= session.getTotalChunks(); i++) {
                ChunkInfoVO chunkInfo = uploadedChunks.get(i);
                if (chunkInfo == null) {
                    throw new ServiceException("分片缺失: chunkNumber=" + i);
                }
                parts.add(new MinioService.PartInfo(i, chunkInfo.getEtag()));
            }
            
            // 按分片序号排序
            parts.sort((a, b) -> Integer.compare(a.getPartNumber(), b.getPartNumber()));
            
            // 完成分片上传（合并）
            String finalEtag = minioService.completeMultipartUpload(defaultBucket, finalObjectName, uploadId, parts);
            
            log.info("合并分片成功: fileId={}, finalObject={}, etag={}", 
                    session.getFileId(), finalObjectName, finalEtag);
            
            return finalObjectName;
        } catch (Exception e) {
            log.error("合并分片失败: fileId={}", session.getFileId(), e);
            throw new ServiceException("文件合并失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件完成状态
     */
    private void updateFileCompleted(Long fileId, String finalObjectName) {
        FileInfoDO fileInfo = new FileInfoDO();
        fileInfo.setId(fileId);
        fileInfo.setFilePath(finalObjectName);
        fileInfo.setUploadStatus(FileUploadStatusEnum.COMPLETED.getCode());
        fileInfo.setUpdateTime(LocalDateTime.now());
        
        fileInfoMapper.updateById(fileInfo);
    }

    /**
     * 清理分片文件
     */
    private void cleanupChunks(Long fileId, Map<Integer, ChunkInfoVO> uploadedChunks) {
        // 删除MinIO中的分片文件
        for (Integer chunkNumber : uploadedChunks.keySet()) {
            try {
                String chunkObjectName = String.format("chunks/%s/%d", fileId, chunkNumber);
                minioService.deleteFile(defaultBucket, chunkObjectName);
            } catch (Exception e) {
                log.warn("删除分片文件失败: fileId={}, chunkNumber={}", fileId, chunkNumber, e);
            }
        }
        
        // 删除数据库中的分片记录
        LambdaQueryWrapper<FileChunkDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileChunkDO::getFileId, fileId);
        fileChunkMapper.delete(wrapper);
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getFileExtension(originalFileName);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}
