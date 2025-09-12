package com.admin.module.infra.api.service;

import com.admin.module.infra.api.dto.ImportExportTemplateCreateDTO;
import com.admin.module.infra.api.vo.ImportExportTemplateVO;

import java.io.OutputStream;
import java.util.List;

/**
 * 导入导出模板服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportExportTemplateService {

    /**
     * 创建模板
     *
     * @param createDTO 创建DTO
     * @return 模板ID
     */
    Long createTemplate(ImportExportTemplateCreateDTO createDTO);

    /**
     * 更新模板
     *
     * @param id 模板ID
     * @param createDTO 更新数据
     */
    void updateTemplate(Long id, ImportExportTemplateCreateDTO createDTO);

    /**
     * 删除模板
     *
     * @param id 模板ID
     */
    void deleteTemplate(Long id);

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板VO
     */
    ImportExportTemplateVO getTemplate(Long id);

    /**
     * 获取模板列表
     *
     * @param dataType 数据类型（可选）
     * @return 模板列表
     */
    List<ImportExportTemplateVO> getTemplateList(String dataType);

    /**
     * 根据数据类型和文件格式获取默认模板
     *
     * @param dataType 数据类型
     * @param fileFormat 文件格式
     * @return 模板VO
     */
    ImportExportTemplateVO getDefaultTemplate(String dataType, String fileFormat);

    /**
     * 生成模板文件
     *
     * @param dataType 数据类型
     * @param fileFormat 文件格式
     * @param outputStream 输出流
     */
    void generateTemplateFile(String dataType, String fileFormat, OutputStream outputStream);

    /**
     * 下载模板文件
     *
     * @param templateId 模板ID
     * @param outputStream 输出流
     */
    void downloadTemplateFile(Long templateId, OutputStream outputStream);
}