package com.admin.module.infra.biz.service;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.ServiceException;
import com.admin.framework.minio.service.MinioService;
import com.admin.module.infra.biz.dal.dataobject.FileInfoDO;
import com.admin.module.infra.biz.dal.mapper.FileInfoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流式下载服务
 * 
 * 支持Range请求和断点续传
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingDownloadService {

    private final FileInfoMapper fileInfoMapper;
    private final MinioService minioService;
    
    private static final int BUFFER_SIZE = 8192; // 8KB
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d+)-(?<end>\\d*)");

    /**
     * 流式下载文件，支持Range请求
     * 
     * @param fileId 文件ID
     * @param request HTTP请求
     * @param response HTTP响应
     * @param inline 是否内联显示
     * @param downloadName 下载文件名
     */
    public void streamDownload(Long fileId, HttpServletRequest request, HttpServletResponse response,
                              Boolean inline, String downloadName) {
        // 获取文件信息
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new ServiceException("文件不存在");
        }
        
        try {
            // 获取文件输入流
            InputStream inputStream = minioService.downloadFile(fileInfo.getBucketName(), fileInfo.getFilePath());
            
            // 设置基本响应头
            setBasicHeaders(response, fileInfo, inline, downloadName);
            
            // 检查是否为Range请求
            String rangeHeader = request.getHeader("Range");
            if (StrUtil.isNotBlank(rangeHeader)) {
                handleRangeRequest(rangeHeader, inputStream, response, fileInfo.getFileSize());
            } else {
                handleFullDownload(inputStream, response, fileInfo.getFileSize());
            }
            
            log.info("文件下载完成: fileId={}, fileName={}, size={}", 
                    fileId, fileInfo.getFileName(), fileInfo.getFileSize());
            
        } catch (Exception e) {
            log.error("文件下载失败: fileId={}, fileName={}", fileId, fileInfo.getFileName(), e);
            throw new ServiceException("文件下载失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置基本响应头
     */
    private void setBasicHeaders(HttpServletResponse response, FileInfoDO fileInfo, 
                               Boolean inline, String downloadName) {
        // 设置内容类型
        response.setContentType(fileInfo.getContentType());
        
        // 设置文件名
        String fileName = StrUtil.isNotBlank(downloadName) ? downloadName : fileInfo.getOriginalFileName();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        
        String disposition = (inline != null && inline) ? "inline" : "attachment";
        response.setHeader("Content-Disposition", 
                String.format("%s; filename=\"%s\"; filename*=UTF-8''%s", 
                        disposition, fileName, encodedFileName));
        
        // 设置其他头
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "public, max-age=3600");
        response.setHeader("ETag", "\"" + fileInfo.getFileHash() + "\"");
        
        // 设置CORS头（如果需要）
        response.setHeader("Access-Control-Expose-Headers", "Content-Range, Content-Length, Accept-Ranges");
    }
    
    /**
     * 处理Range请求（断点续传）
     */
    private void handleRangeRequest(String rangeHeader, InputStream inputStream, 
                                  HttpServletResponse response, Long fileSize) throws IOException {
        Matcher matcher = RANGE_PATTERN.matcher(rangeHeader);
        if (!matcher.matches()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        long start = Long.parseLong(matcher.group("start"));
        String endGroup = matcher.group("end");
        long end = StrUtil.isBlank(endGroup) ? fileSize - 1 : Long.parseLong(endGroup);
        
        // 验证Range范围
        if (start >= fileSize || end >= fileSize || start > end) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + fileSize);
            return;
        }
        
        long contentLength = end - start + 1;
        
        // 设置206状态码和Range响应头
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
        response.setHeader("Content-Length", String.valueOf(contentLength));
        
        log.debug("Range下载: start={}, end={}, contentLength={}", start, end, contentLength);
        
        // 跳过不需要的字节
        long skipped = inputStream.skip(start);
        if (skipped != start) {
            log.warn("跳过字节数不匹配: expected={}, actual={}", start, skipped);
        }
        
        // 流式传输指定范围的数据
        streamData(inputStream, response.getOutputStream(), contentLength);
    }
    
    /**
     * 处理完整下载
     */
    private void handleFullDownload(InputStream inputStream, HttpServletResponse response, 
                                  Long fileSize) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Length", String.valueOf(fileSize));
        
        log.debug("完整下载: fileSize={}", fileSize);
        
        // 流式传输全部数据
        streamData(inputStream, response.getOutputStream(), fileSize);
    }
    
    /**
     * 流式传输数据
     */
    private void streamData(InputStream inputStream, OutputStream outputStream, long totalBytes) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long bytesWritten = 0;
        int bytesRead;
        
        try {
            while (bytesWritten < totalBytes && (bytesRead = inputStream.read(buffer)) != -1) {
                long remaining = totalBytes - bytesWritten;
                int toWrite = (int) Math.min(bytesRead, remaining);
                
                outputStream.write(buffer, 0, toWrite);
                bytesWritten += toWrite;
                
                // 定期刷新输出流
                if (bytesWritten % (BUFFER_SIZE * 10) == 0) {
                    outputStream.flush();
                }
            }
            
            outputStream.flush();
            
            log.debug("数据传输完成: bytesWritten={}, totalBytes={}", bytesWritten, totalBytes);
            
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("关闭输入流失败", e);
            }
        }
    }
}
