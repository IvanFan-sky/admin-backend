package com.admin.module.system.api.service.imports;

import com.admin.common.core.domain.PageResult;
import com.admin.framework.excel.domain.ImportExportTask;
import com.admin.module.system.api.dto.imports.RoleImportDTO;
import com.admin.module.system.api.vo.imports.RoleImportValidationResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

/**
 * 角色导入导出服务接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface RoleImportExportService {

    /**
     * 下载角色导入模板
     * 
     * @param response HTTP响应
     */
    void downloadImportTemplate(HttpServletResponse response);

    /**
     * 异步导入角色
     * 
     * @param file 导入文件
     * @return 任务ID
     */
    CompletableFuture<Long> importRolesAsync(MultipartFile file);

    /**
     * 验证角色导入文件
     * 
     * @param file 导入文件
     * @return 验证结果（包含预览数据和错误信息）
     */
    RoleImportValidationResult validateImportFile(MultipartFile file);

    /**
     * 异步导出角色数据
     * 
     * @param queryCondition 查询条件
     * @return 任务ID
     */
    CompletableFuture<Long> exportRolesAsync(Object queryCondition);

    /**
     * 获取任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    ImportExportTask getTaskDetail(Long taskId);

    /**
     * 下载导入错误报告
     * 
     * @param taskId 任务ID
     * @param response HTTP响应
     */
    void downloadErrorReport(Long taskId, HttpServletResponse response);

    /**
     * 下载导出文件
     * 
     * @param taskId 任务ID
     * @param response HTTP响应
     */
    void downloadExportFile(Long taskId, HttpServletResponse response);

    /**
     * 获取用户的导入导出任务列表
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 任务分页结果
     */
    PageResult<ImportExportTask> getUserTasks(int pageNum, int pageSize);

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean cancelTask(Long taskId);

}