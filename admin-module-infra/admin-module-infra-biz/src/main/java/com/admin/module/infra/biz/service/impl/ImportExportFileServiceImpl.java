package com.admin.module.infra.biz.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.core.exception.ServiceException;
import com.admin.module.infra.api.dto.FileUploadDTO;
import com.admin.module.infra.api.vo.FileInfoVO;
import com.admin.module.infra.api.vo.FileUploadVO;
import com.admin.module.infra.biz.dal.dataobject.ImportExportTaskDO;
import com.admin.module.infra.biz.dal.mapper.ImportExportTaskMapper;
import com.admin.module.infra.biz.service.FileService;
import com.admin.module.infra.biz.service.ImportExportFileService;
import com.admin.module.infra.biz.storage.FileStorageFactory;
import com.admin.module.infra.biz.storage.FileStorageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入导出文件服务实现
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportFileServiceImpl implements ImportExportFileService {

    private final FileService fileService;
    private final ImportExportTaskMapper importExportTaskMapper;
    private final FileStorageFactory storageFactory;

    @Override
    public FileUploadVO uploadImportFile(MultipartFile file, String dataType) {
        FileUploadDTO uploadDTO = new FileUploadDTO();
        uploadDTO.setFile(file);
        uploadDTO.setBusinessType("import_" + dataType);
        uploadDTO.setIsPublic(0);
        uploadDTO.setRemark("导入文件：" + dataType);

        FileUploadVO uploadVO = fileService.uploadFile(uploadDTO);
        
        log.info("上传导入文件成功，文件ID: {}, 数据类型: {}, 文件名: {}", 
                uploadVO.getFileId(), dataType, file.getOriginalFilename());
        
        return uploadVO;
    }

    @Override
    public FileUploadVO uploadExportFile(Long taskId, String fileName, InputStream fileData, String contentType) {
        try {
            // 将输入流转换为MultipartFile
            byte[] fileBytes = IoUtil.readBytes(fileData);
            MultipartFile multipartFile = new MockMultipartFile(
                    "file", 
                    fileName, 
                    contentType, 
                    new ByteArrayInputStream(fileBytes)
            );

            FileUploadDTO uploadDTO = new FileUploadDTO();
            uploadDTO.setFile(multipartFile);
            uploadDTO.setBusinessType("export_result");
            uploadDTO.setBusinessId(taskId.toString());
            uploadDTO.setIsPublic(0);
            uploadDTO.setRemark("导出结果文件，任务ID：" + taskId);

            FileUploadVO uploadVO = fileService.uploadFile(uploadDTO);
            
            log.info("上传导出文件成功，任务ID: {}, 文件ID: {}, 文件名: {}", 
                    taskId, uploadVO.getFileId(), fileName);
            
            return uploadVO;
            
        } catch (IOException e) {
            log.error("上传导出文件失败，任务ID: {}", taskId, e);
            throw new ServiceException("上传导出文件失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadImportTemplate(String dataType, String fileFormat, HttpServletResponse response) {
        try {
            // 根据数据类型和文件格式生成模板
            String templateContent = generateTemplateContent(dataType, fileFormat);
            String fileName = generateTemplateFileName(dataType, fileFormat);
            String contentType = getContentTypeByFormat(fileFormat);
            
            // 设置响应头
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");
            
            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(templateContent.getBytes(StandardCharsets.UTF_8));
            }
            
            log.info("下载导入模板成功，数据类型: {}, 文件格式: {}", dataType, fileFormat);
            
        } catch (Exception e) {
            log.error("下载导入模板失败，数据类型: {}, 文件格式: {}", dataType, fileFormat, e);
            throw new ServiceException("下载导入模板失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream getFileInputStream(Long fileId) {
        if (fileId == null) {
            throw new ServiceException("文件ID不能为空");
        }
        
        try {
            FileInfoVO fileInfo = fileService.getFileInfo(fileId);
            if (fileInfo == null) {
                throw new ServiceException("文件不存在");
            }
            
            // 获取存储策略
            FileStorageStrategy strategy = storageFactory.getStrategy(fileInfo.getStorageType().toLowerCase());
            
            // 下载文件流
            return strategy.downloadFile(fileInfo.getStorageBucket(), fileInfo.getStoragePath());
            
        } catch (Exception e) {
            log.error("获取文件输入流失败，文件ID: {}", fileId, e);
            throw new ServiceException("获取文件输入流失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream getImportFileInputStream(Long taskId) {
        ImportExportTaskDO taskDO = importExportTaskMapper.selectById(taskId);
        if (taskDO == null) {
            throw new ServiceException("任务不存在");
        }
        
        // 优先使用文件ID
        if (taskDO.getFileId() != null) {
            return getFileInputStream(taskDO.getFileId());
        }
        
        // 兜底使用文件路径（旧版本兼容）
        if (StrUtil.isNotBlank(taskDO.getFilePath())) {
            // TODO: 实现文件路径获取流的逻辑（如果需要支持旧版本）
            throw new ServiceException("暂不支持通过文件路径获取文件流，请使用文件ID");
        }
        
        throw new ServiceException("任务未关联文件");
    }

    @Override
    public void downloadResultFile(Long taskId, HttpServletResponse response) {
        ImportExportTaskDO taskDO = importExportTaskMapper.selectById(taskId);
        if (taskDO == null) {
            throw new ServiceException("任务不存在");
        }
        
        // 查找结果文件
        List<FileInfoVO> resultFiles = fileService.getFilesByBusiness("export_result", taskId.toString());
        if (resultFiles.isEmpty()) {
            throw new ServiceException("结果文件不存在");
        }
        
        FileInfoVO resultFile = resultFiles.get(0); // 取第一个结果文件
        
        // 下载文件
        fileService.downloadFile(resultFile.getId(), false, null, response);
        
        log.info("下载结果文件成功，任务ID: {}, 文件ID: {}", taskId, resultFile.getId());
    }

    @Override
    public int cleanupExpiredImportExportFiles() {
        // 清理30天前的导入导出相关文件
        LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
        
        // 查找过期的导入文件
        List<FileInfoVO> importFiles = fileService.getFilePage(
                createFilePageDTO("import_", expireTime)
        ).getList();
        
        // 查找过期的导出结果文件
        List<FileInfoVO> exportFiles = fileService.getFilePage(
                createFilePageDTO("export_result", expireTime)
        ).getList();
        
        int cleanedCount = 0;
        
        // 删除导入文件
        for (FileInfoVO file : importFiles) {
            if (fileService.deleteFile(file.getId())) {
                cleanedCount++;
            }
        }
        
        // 删除导出文件
        for (FileInfoVO file : exportFiles) {
            if (fileService.deleteFile(file.getId())) {
                cleanedCount++;
            }
        }
        
        log.info("清理过期导入导出文件完成，清理数量: {}", cleanedCount);
        return cleanedCount;
    }

    @Override
    public FileInfoVO getFileInfoByTask(Long taskId) {
        ImportExportTaskDO taskDO = importExportTaskMapper.selectById(taskId);
        if (taskDO == null) {
            throw new ServiceException("任务不存在");
        }
        
        if (taskDO.getFileId() != null) {
            return fileService.getFileInfo(taskDO.getFileId());
        }
        
        return null;
    }

    /**
     * 生成模板内容
     */
    private String generateTemplateContent(String dataType, String fileFormat) {
        StringBuilder content = new StringBuilder();
        
        switch (dataType.toLowerCase()) {
            case "user":
                if ("csv".equalsIgnoreCase(fileFormat)) {
                    content.append("用户名,姓名,邮箱,手机号,部门名称,角色编码\n");
                    content.append("admin,管理员,admin@example.com,13800138000,技术部,admin\n");
                } else {
                    // Excel格式的简化处理
                    content.append("用户名\t姓名\t邮箱\t手机号\t部门名称\t角色编码\n");
                    content.append("admin\t管理员\tadmin@example.com\t13800138000\t技术部\tadmin\n");
                }
                break;
            case "role":
                if ("csv".equalsIgnoreCase(fileFormat)) {
                    content.append("角色编码,角色名称,角色描述,状态\n");
                    content.append("admin,超级管理员,系统管理员角色,1\n");
                } else {
                    content.append("角色编码\t角色名称\t角色描述\t状态\n");
                    content.append("admin\t超级管理员\t系统管理员角色\t1\n");
                }
                break;
            default:
                throw new ServiceException("不支持的数据类型：" + dataType);
        }
        
        return content.toString();
    }

    /**
     * 生成模板文件名
     */
    private String generateTemplateFileName(String dataType, String fileFormat) {
        String dataTypeName = getDataTypeName(dataType);
        return String.format("%s导入模板.%s", dataTypeName, fileFormat);
    }

    /**
     * 获取数据类型中文名
     */
    private String getDataTypeName(String dataType) {
        switch (dataType.toLowerCase()) {
            case "user": return "用户";
            case "role": return "角色";
            case "operation_log": return "操作日志";
            default: return dataType;
        }
    }

    /**
     * 根据文件格式获取Content-Type
     */
    private String getContentTypeByFormat(String fileFormat) {
        switch (fileFormat.toLowerCase()) {
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xls":
                return "application/vnd.ms-excel";
            case "csv":
                return "text/csv";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 创建文件分页查询DTO
     */
    private com.admin.module.infra.api.dto.FilePageDTO createFilePageDTO(String businessTypePrefix, LocalDateTime endTime) {
        com.admin.module.infra.api.dto.FilePageDTO pageDTO = new com.admin.module.infra.api.dto.FilePageDTO();
        pageDTO.setEndTime(endTime);
        pageDTO.setPageNo(1);
        pageDTO.setPageSize(1000); // 大批量查询
        return pageDTO;
    }
}