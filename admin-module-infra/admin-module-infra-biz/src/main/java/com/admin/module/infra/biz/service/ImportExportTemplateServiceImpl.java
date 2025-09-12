package com.admin.module.infra.biz.service;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.infra.api.constants.ImportExportConstants;
import com.admin.module.infra.api.dto.ImportExportTemplateCreateDTO;
import com.admin.module.infra.api.dto.RoleImportDTO;
import com.admin.module.infra.api.dto.UserImportDTO;
import com.admin.module.infra.api.enums.DataTypeEnum;
import com.admin.module.infra.api.enums.FileFormatEnum;
import com.admin.module.infra.api.service.ImportExportTemplateService;
import com.admin.module.infra.api.vo.ImportExportTemplateVO;
import com.admin.module.infra.biz.convert.ImportExportTemplateConvert;
import com.admin.module.infra.biz.dal.dataobject.ImportExportTemplateDO;
import com.admin.module.infra.biz.dal.mapper.ImportExportTemplateMapper;
import com.admin.module.infra.biz.util.ExcelUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入导出模板服务实现类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportTemplateServiceImpl implements ImportExportTemplateService {

    private final ImportExportTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(ImportExportTemplateCreateDTO createDTO) {
        // 验证数据类型和文件格式
        validateCreateDTO(createDTO);

        // 检查是否已存在相同的模板
        if (existsTemplate(createDTO.getDataType(), createDTO.getFileFormat(), null)) {
            throw new ServiceException(ErrorCode.DATA_ALREADY_EXISTS, "该数据类型和文件格式的模板已存在");
        }

        // 创建模板DO
        ImportExportTemplateDO templateDO = ImportExportTemplateConvert.INSTANCE.convert(createDTO);
        templateDO.setStatus(1); // 启用状态
        templateDO.setCreateBy(getCurrentUsername());
        templateDO.setCreateTime(LocalDateTime.now());

        templateMapper.insert(templateDO);

        log.info("创建导入导出模板成功，模板ID: {}, 模板名称: {}", templateDO.getId(), templateDO.getTemplateName());
        return templateDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(Long id, ImportExportTemplateCreateDTO createDTO) {
        ImportExportTemplateDO existTemplate = templateMapper.selectById(id);
        if (existTemplate == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "模板不存在");
        }

        // 验证数据类型和文件格式
        validateCreateDTO(createDTO);

        // 检查是否已存在相同的模板（排除当前模板）
        if (existsTemplate(createDTO.getDataType(), createDTO.getFileFormat(), id)) {
            throw new ServiceException(ErrorCode.DATA_ALREADY_EXISTS, "该数据类型和文件格式的模板已存在");
        }

        // 更新模板
        ImportExportTemplateDO updateDO = ImportExportTemplateConvert.INSTANCE.convert(createDTO);
        updateDO.setId(id);
        updateDO.setUpdateBy(getCurrentUsername());
        updateDO.setUpdateTime(LocalDateTime.now());

        templateMapper.updateById(updateDO);

        log.info("更新导入导出模板成功，模板ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        ImportExportTemplateDO templateDO = templateMapper.selectById(id);
        if (templateDO == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "模板不存在");
        }

        templateMapper.deleteById(id);

        log.info("删除导入导出模板成功，模板ID: {}", id);
    }

    @Override
    public ImportExportTemplateVO getTemplate(Long id) {
        ImportExportTemplateDO templateDO = templateMapper.selectById(id);
        if (templateDO == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "模板不存在");
        }

        return ImportExportTemplateConvert.INSTANCE.convert(templateDO);
    }

    @Override
    public List<ImportExportTemplateVO> getTemplateList(String dataType) {
        LambdaQueryWrapper<ImportExportTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dataType != null, ImportExportTemplateDO::getDataType, dataType)
                   .eq(ImportExportTemplateDO::getStatus, 1)
                   .orderByDesc(ImportExportTemplateDO::getCreateTime);

        List<ImportExportTemplateDO> templateList = templateMapper.selectList(queryWrapper);
        return ImportExportTemplateConvert.INSTANCE.convertList(templateList);
    }

    @Override
    public ImportExportTemplateVO getDefaultTemplate(String dataType, String fileFormat) {
        LambdaQueryWrapper<ImportExportTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportExportTemplateDO::getDataType, dataType)
                   .eq(ImportExportTemplateDO::getFileFormat, fileFormat)
                   .eq(ImportExportTemplateDO::getStatus, 1)
                   .orderByDesc(ImportExportTemplateDO::getCreateTime)
                   .last("LIMIT 1");

        ImportExportTemplateDO templateDO = templateMapper.selectOne(queryWrapper);
        
        if (templateDO != null) {
            return ImportExportTemplateConvert.INSTANCE.convert(templateDO);
        }

        // 如果没有找到模板，生成默认模板信息
        return generateDefaultTemplateInfo(dataType, fileFormat);
    }

    @Override
    public void generateTemplateFile(String dataType, String fileFormat, OutputStream outputStream) {
        // 验证参数
        if (!DataTypeEnum.isSupported(dataType)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的数据类型");
        }
        if (!FileFormatEnum.isSupported(fileFormat)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的文件格式");
        }

        try {
            // 根据数据类型生成对应的模板
            switch (dataType) {
                case ImportExportConstants.DataType.USER -> 
                    ExcelUtils.createTemplate(outputStream, UserImportDTO.class, 
                                            ImportExportConstants.Excel.SHEET_NAME_USERS);
                case ImportExportConstants.DataType.ROLE -> 
                    ExcelUtils.createTemplate(outputStream, RoleImportDTO.class, 
                                            ImportExportConstants.Excel.SHEET_NAME_ROLES);
                default -> 
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的数据类型: " + dataType);
            }

            log.info("生成模板文件成功，数据类型: {}, 文件格式: {}", dataType, fileFormat);

        } catch (Exception e) {
            log.error("生成模板文件失败，数据类型: {}, 文件格式: {}", dataType, fileFormat, e);
            throw new ServiceException(ErrorCode.SYSTEM_ERROR, "生成模板文件失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadTemplateFile(Long templateId, OutputStream outputStream) {
        ImportExportTemplateDO templateDO = templateMapper.selectById(templateId);
        if (templateDO == null) {
            throw new ServiceException(ErrorCode.DATA_NOT_FOUND, "模板不存在");
        }

        // 使用模板配置生成文件
        generateTemplateFile(templateDO.getDataType(), templateDO.getFileFormat(), outputStream);

        log.info("下载模板文件成功，模板ID: {}, 模板名称: {}", templateId, templateDO.getTemplateName());
    }

    private void validateCreateDTO(ImportExportTemplateCreateDTO createDTO) {
        if (!DataTypeEnum.isSupported(createDTO.getDataType())) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的数据类型");
        }
        if (createDTO.getFileFormat() != null && !FileFormatEnum.isSupported(createDTO.getFileFormat())) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的文件格式");
        }
    }

    private boolean existsTemplate(String dataType, String fileFormat, Long excludeId) {
        LambdaQueryWrapper<ImportExportTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportExportTemplateDO::getDataType, dataType)
                   .eq(fileFormat != null, ImportExportTemplateDO::getFileFormat, fileFormat)
                   .ne(excludeId != null, ImportExportTemplateDO::getId, excludeId);

        return templateMapper.selectCount(queryWrapper) > 0;
    }

    private ImportExportTemplateVO generateDefaultTemplateInfo(String dataType, String fileFormat) {
        ImportExportTemplateVO templateVO = new ImportExportTemplateVO();
        templateVO.setTemplateName("默认" + getDataTypeDesc(dataType) + "模板");
        templateVO.setDataType(dataType);
        templateVO.setDataTypeDesc(getDataTypeDesc(dataType));
        templateVO.setFileFormat(fileFormat);
        templateVO.setFileFormatDesc(getFileFormatDesc(fileFormat));
        templateVO.setStatus(1);
        templateVO.setStatusDesc("启用");
        templateVO.setRemark("系统生成的默认模板");
        templateVO.setCreateTime(LocalDateTime.now());
        
        return templateVO;
    }

    private String getDataTypeDesc(String dataType) {
        DataTypeEnum dataTypeEnum = DataTypeEnum.getByCode(dataType);
        return dataTypeEnum != null ? dataTypeEnum.getDescription() : dataType;
    }

    private String getFileFormatDesc(String fileFormat) {
        FileFormatEnum fileFormatEnum = FileFormatEnum.getByExtension(fileFormat);
        return fileFormatEnum != null ? fileFormatEnum.getDescription() : fileFormat;
    }

    private String getCurrentUsername() {
        // TODO: 获取当前登录用户
        return "admin";
    }
}