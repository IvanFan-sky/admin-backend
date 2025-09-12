package com.admin.module.infra.biz.service;

import com.admin.module.infra.api.service.ImportErrorDetailService;
import com.admin.module.infra.api.vo.ImportErrorDetailVO;
import com.admin.module.infra.biz.convert.ImportErrorDetailConvert;
import com.admin.module.infra.biz.dal.dataobject.ImportErrorDetailDO;
import com.admin.module.infra.biz.dal.mapper.ImportErrorDetailMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入错误详情服务实现类
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportErrorDetailServiceImpl implements ImportErrorDetailService {

    private final ImportErrorDetailMapper importErrorDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveErrorDetails(Long taskId, List<ImportErrorDetailVO> errorDetails) {
        if (errorDetails == null || errorDetails.isEmpty()) {
            return;
        }

        List<ImportErrorDetailDO> errorDetailDOList = ImportErrorDetailConvert.INSTANCE.convertList(errorDetails);
        errorDetailDOList.forEach(errorDetail -> {
            errorDetail.setTaskId(taskId);
            errorDetail.setCreateTime(LocalDateTime.now());
        });

        // 批量插入
        for (ImportErrorDetailDO errorDetail : errorDetailDOList) {
            importErrorDetailMapper.insert(errorDetail);
        }

        log.info("保存导入错误详情成功，任务ID: {}, 错误数量: {}", taskId, errorDetails.size());
    }

    @Override
    public List<ImportErrorDetailVO> getErrorDetailsByTaskId(Long taskId) {
        LambdaQueryWrapper<ImportErrorDetailDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportErrorDetailDO::getTaskId, taskId)
                   .orderByAsc(ImportErrorDetailDO::getRowNumber);

        List<ImportErrorDetailDO> errorDetailList = importErrorDetailMapper.selectList(queryWrapper);
        return ImportErrorDetailConvert.INSTANCE.convertToVOList(errorDetailList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteErrorDetailsByTaskId(Long taskId) {
        LambdaQueryWrapper<ImportErrorDetailDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportErrorDetailDO::getTaskId, taskId);

        int deletedCount = importErrorDetailMapper.delete(queryWrapper);
        log.info("删除导入错误详情成功，任务ID: {}, 删除数量: {}", taskId, deletedCount);
    }
}