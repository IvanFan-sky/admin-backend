package com.admin.module.infra.api.exception;

import com.admin.module.infra.api.enums.FileErrorCode;
import lombok.Getter;

/**
 * 文件业务异常
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Getter
public class FileBusinessException extends RuntimeException {

    private final FileErrorCode errorCode;
    private final Object[] args;

    public FileBusinessException(FileErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    public FileBusinessException(FileErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public FileBusinessException(FileErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    public FileBusinessException(FileErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public FileBusinessException(FileErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * 获取完整的错误信息
     */
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorCode.getCode()).append("] ");
        sb.append(getMessage());
        
        if (args != null && args.length > 0) {
            sb.append(" (");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(args[i]);
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
}