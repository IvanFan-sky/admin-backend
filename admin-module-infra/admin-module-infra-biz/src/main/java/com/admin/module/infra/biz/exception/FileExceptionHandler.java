package com.admin.module.infra.biz.exception;

import com.admin.common.core.domain.R;
import com.admin.module.infra.api.exception.FileBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * 文件异常处理器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@RestControllerAdvice
@Slf4j
@Order(1) // 优先级高于全局异常处理器
public class FileExceptionHandler {

    /**
     * 处理文件业务异常
     */
    @ExceptionHandler(FileBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleFileBusinessException(FileBusinessException e) {
        log.warn("文件业务异常: {}", e.getFullMessage(), e);
        return R.fail(e.getErrorCode().getCode(), e.getMessage());
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件上传大小超限: {}", e.getMessage());
        return R.fail("F005", "文件大小超出限制，最大允许: " + formatFileSize(e.getMaxUploadSize()));
    }

    /**
     * 处理文件未找到异常
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> handleFileNotFoundException(FileNotFoundException e) {
        log.warn("文件未找到: {}", e.getMessage());
        return R.fail("F001", "文件不存在");
    }

    /**
     * 处理文件访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("文件访问权限不足: {}", e.getMessage());
        return R.fail("F505", "文件访问权限不足");
    }

    /**
     * 处理IO异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleIOException(IOException e) {
        log.error("文件IO异常: {}", e.getMessage(), e);
        return R.fail("F701", "文件操作失败，请稍后重试");
    }

    /**
     * 处理存储相关异常
     */
    @ExceptionHandler({
        com.amazonaws.services.s3.model.AmazonS3Exception.class,
        io.minio.errors.MinioException.class
    })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public R<?> handleStorageException(Exception e) {
        log.error("存储服务异常: {}", e.getMessage(), e);
        return R.fail("F201", "存储服务暂时不可用，请稍后重试");
    }

    /**
     * 处理网络超时异常
     */
    @ExceptionHandler({
        java.net.SocketTimeoutException.class,
        java.net.ConnectException.class
    })
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public R<?> handleNetworkException(Exception e) {
        log.error("网络异常: {}", e.getMessage(), e);
        return R.fail("F205", "网络连接超时，请检查网络连接");
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}