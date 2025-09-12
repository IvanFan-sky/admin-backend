package com.admin.module.infra.api.service;

import com.admin.module.infra.api.vo.ImportErrorDetailVO;

import java.util.List;

/**
 * 导入错误详情服务接口
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface ImportErrorDetailService {

    /**
     * 批量保存导入错误详情
     *
     * @param taskId 任务ID
     * @param errorDetails 错误详情列表
     */
    void saveErrorDetails(Long taskId, List<ImportErrorDetailVO> errorDetails);

    /**
     * 获取任务的错误详情列表
     *
     * @param taskId 任务ID
     * @return 错误详情列表
     */
    List<ImportErrorDetailVO> getErrorDetailsByTaskId(Long taskId);

    /**
     * 删除任务的错误详情
     *
     * @param taskId 任务ID
     */
    void deleteErrorDetailsByTaskId(Long taskId);
}