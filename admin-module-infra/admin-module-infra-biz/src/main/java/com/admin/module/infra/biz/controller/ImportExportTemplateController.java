package com.admin.module.infra.biz.controller;

import com.admin.common.core.domain.R;
import com.admin.module.infra.api.dto.ImportExportTemplateCreateDTO;
import com.admin.module.infra.api.service.ImportExportTemplateService;
import com.admin.module.infra.api.vo.ImportExportTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导入导出模板管理控制器
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "导入导出模板管理")
@RestController
@RequestMapping("/admin-api/infra/import-export-template")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ImportExportTemplateController {

    private final ImportExportTemplateService templateService;

    @PostMapping("/create")
    @Operation(summary = "创建导入导出模板")
    @PreAuthorize("@ss.hasPermission('infra:template:create')")
    public R<Long> createTemplate(@Valid @RequestBody ImportExportTemplateCreateDTO createDTO) {
        Long templateId = templateService.createTemplate(createDTO);
        return R.ok(templateId);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "更新导入导出模板")
    @PreAuthorize("@ss.hasPermission('infra:template:update')")
    public R<Boolean> updateTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ImportExportTemplateCreateDTO createDTO) {
        templateService.updateTemplate(id, createDTO);
        return R.ok(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除导入导出模板")
    @PreAuthorize("@ss.hasPermission('infra:template:delete')")
    public R<Boolean> deleteTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id) {
        templateService.deleteTemplate(id);
        return R.ok(true);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取导入导出模板详情")
    @PreAuthorize("@ss.hasPermission('infra:template:query')")
    public R<ImportExportTemplateVO> getTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id) {
        ImportExportTemplateVO templateVO = templateService.getTemplate(id);
        return R.ok(templateVO);
    }

    @GetMapping("/list")
    @Operation(summary = "获取导入导出模板列表")
    @PreAuthorize("@ss.hasPermission('infra:template:query')")
    public R<List<ImportExportTemplateVO>> getTemplateList(
            @Parameter(description = "数据类型") @RequestParam(required = false) String dataType) {
        List<ImportExportTemplateVO> templateList = templateService.getTemplateList(dataType);
        return R.ok(templateList);
    }

    @GetMapping("/default")
    @Operation(summary = "获取默认模板")
    public R<ImportExportTemplateVO> getDefaultTemplate(
            @Parameter(description = "数据类型", required = true) @RequestParam String dataType,
            @Parameter(description = "文件格式", required = true) @RequestParam String fileFormat) {
        ImportExportTemplateVO templateVO = templateService.getDefaultTemplate(dataType, fileFormat);
        return R.ok(templateVO);
    }

    @GetMapping("/download-default")
    @Operation(summary = "下载默认模板文件")
    public void downloadDefaultTemplate(
            @Parameter(description = "数据类型", required = true) @RequestParam String dataType,
            @Parameter(description = "文件格式", required = true) @RequestParam String fileFormat,
            HttpServletResponse response) {
        
        try {
            // 设置响应头
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", 
                String.format("attachment; filename=\"%s_template.%s\"", dataType, fileFormat));

            // 生成并输出模板文件
            templateService.generateTemplateFile(dataType, fileFormat, response.getOutputStream());
            response.getOutputStream().flush();

            log.info("下载默认模板文件成功，数据类型: {}, 文件格式: {}", dataType, fileFormat);

        } catch (Exception e) {
            log.error("下载默认模板文件失败，数据类型: {}, 文件格式: {}", dataType, fileFormat, e);
            throw new RuntimeException("下载模板文件失败", e);
        }
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载模板文件")
    @PreAuthorize("@ss.hasPermission('infra:template:download')")
    public void downloadTemplate(
            @Parameter(description = "模板ID", required = true) @PathVariable Long id,
            HttpServletResponse response) {
        
        try {
            // 获取模板信息
            ImportExportTemplateVO template = templateService.getTemplate(id);
            
            // 设置响应头
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", 
                String.format("attachment; filename=\"%s.%s\"", 
                             template.getTemplateName(), template.getFileFormat()));

            // 下载模板文件
            templateService.downloadTemplateFile(id, response.getOutputStream());
            response.getOutputStream().flush();

            log.info("下载模板文件成功，模板ID: {}, 模板名称: {}", id, template.getTemplateName());

        } catch (Exception e) {
            log.error("下载模板文件失败，模板ID: {}", id, e);
            throw new RuntimeException("下载模板文件失败", e);
        }
    }
}