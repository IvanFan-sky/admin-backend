package com.admin.module.system.biz.service.dict;

import cn.hutool.core.util.StrUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.module.system.api.dto.dict.SysDictDataCreateDTO;
import com.admin.module.system.api.dto.dict.SysDictDataQueryDTO;
import com.admin.module.system.api.dto.dict.SysDictDataUpdateDTO;
import com.admin.module.system.api.service.dict.SysDictDataService;
import com.admin.module.system.api.vo.dict.SysDictDataVO;
import com.admin.module.system.biz.convert.dict.SysDictDataConvert;
import com.admin.module.system.biz.dal.dataobject.SysDictDataDO;
import com.admin.module.system.biz.dal.mapper.SysDictDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.admin.framework.redis.constants.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 字典数据服务实现类
 * 
 * 实现字典数据的业务逻辑处理
 * 包括CRUD操作、业务校验和数据转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_DICT_CACHE, allEntries = true)
    public Long createDictData(SysDictDataCreateDTO createDTO) {
        // 校验字典值是否已存在
        if (existsDictValue(createDTO.getDictType(), createDTO.getDictValue())) {
            throw new ServiceException("字典值已存在");
        }
        
        // 校验字典标签是否已存在
        if (existsDictLabel(createDTO.getDictType(), createDTO.getDictLabel())) {
            throw new ServiceException("字典标签已存在");
        }

        // 如果没有设置排序值，自动获取下一个排序值
        if (createDTO.getDictSort() == null || createDTO.getDictSort() == 0) {
            createDTO.setDictSort(getNextSort(createDTO.getDictType()));
        }

        // 转换并保存
        SysDictDataDO dictDataDO = SysDictDataConvert.INSTANCE.convert(createDTO);
        dictDataMapper.insert(dictDataDO);
        
        log.info("创建字典数据成功，ID: {}, 字典类型: {}, 字典值: {}", 
                dictDataDO.getId(), dictDataDO.getDictType(), dictDataDO.getDictValue());
        return dictDataDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_DICT_CACHE, allEntries = true)
    public void updateDictData(SysDictDataUpdateDTO updateDTO) {
        // 校验字典数据是否存在
        SysDictDataDO existingDictData = dictDataMapper.selectById(updateDTO.getId());
        if (existingDictData == null) {
            throw new ServiceException("字典数据不存在");
        }

        // 如果字典值发生变化，需要校验新的字典值是否已存在
        if (!existingDictData.getDictValue().equals(updateDTO.getDictValue())) {
            if (existsDictValue(updateDTO.getDictType(), updateDTO.getDictValue())) {
                throw new ServiceException("字典值已存在");
            }
        }

        // 如果字典标签发生变化，需要校验新的字典标签是否已存在
        if (!existingDictData.getDictLabel().equals(updateDTO.getDictLabel())) {
            if (existsDictLabel(updateDTO.getDictType(), updateDTO.getDictLabel())) {
                throw new ServiceException("字典标签已存在");
            }
        }

        // 转换并更新
        SysDictDataDO dictDataDO = SysDictDataConvert.INSTANCE.convert(updateDTO);
        dictDataMapper.updateById(dictDataDO);
        
        log.info("更新字典数据成功，ID: {}, 字典类型: {}, 字典值: {}", 
                dictDataDO.getId(), dictDataDO.getDictType(), dictDataDO.getDictValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_DICT_CACHE, allEntries = true)
    public void deleteDictData(Long id) {
        // 校验字典数据是否存在
        SysDictDataDO dictDataDO = dictDataMapper.selectById(id);
        if (dictDataDO == null) {
            throw new ServiceException("字典数据不存在");
        }

        // 删除字典数据
        dictDataMapper.deleteById(id);
        
        log.info("删除字典数据成功，ID: {}, 字典类型: {}, 字典值: {}", 
                id, dictDataDO.getDictType(), dictDataDO.getDictValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictDatasBatch(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int deleteCount = 0;
        for (Long id : ids) {
            try {
                deleteDictData(id);
                deleteCount++;
            } catch (Exception e) {
                log.warn("删除字典数据失败，ID: {}, 错误信息: {}", id, e.getMessage());
            }
        }
        
        log.info("批量删除字典数据成功，删除数量: {}", deleteCount);
        return deleteCount;
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_DICT_CACHE, key = "'data:' + #id", unless = "#result == null")
    public SysDictDataVO getDictData(Long id) {
        SysDictDataDO dictDataDO = dictDataMapper.selectById(id);
        if (dictDataDO == null) {
            throw new ServiceException("字典数据不存在");
        }
        
        return SysDictDataConvert.INSTANCE.convert(dictDataDO);
    }

    @Override
    public PageResult<SysDictDataVO> getDictDataPage(SysDictDataQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysDictDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(queryDTO.getDictType()), SysDictDataDO::getDictType, queryDTO.getDictType())
                   .like(StrUtil.isNotBlank(queryDTO.getDictLabel()), SysDictDataDO::getDictLabel, queryDTO.getDictLabel())
                   .like(StrUtil.isNotBlank(queryDTO.getDictValue()), SysDictDataDO::getDictValue, queryDTO.getDictValue())
                   .eq(queryDTO.getStatus() != null, SysDictDataDO::getStatus, queryDTO.getStatus())
                   .orderByAsc(SysDictDataDO::getDictSort)
                   .orderByDesc(SysDictDataDO::getCreateTime);

        // 分页查询
        IPage<SysDictDataDO> page = dictDataMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryWrapper);
        
        // 转换结果
        List<SysDictDataVO> list = SysDictDataConvert.INSTANCE.convertList(page.getRecords());
        return new PageResult<>(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_DICT_CACHE, key = "'type:' + #dictType", unless = "#result == null || #result.isEmpty()")
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        List<SysDictDataDO> list = dictDataMapper.selectByDictType(dictType);
        return SysDictDataConvert.INSTANCE.convertList(list);
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_DICT_CACHE, key = "'enabled_type:' + #dictType", unless = "#result == null || #result.isEmpty()")
    public List<SysDictDataVO> getEnabledDictDataByType(String dictType) {
        List<SysDictDataDO> list = dictDataMapper.selectEnabledByDictType(dictType);
        return SysDictDataConvert.INSTANCE.convertList(list);
    }

    public SysDictDataVO getDictDataByTypeAndValue(String dictType, String dictValue) {
        SysDictDataDO dictDataDO = dictDataMapper.selectByDictTypeAndValue(dictType, dictValue);
        if (dictDataDO == null) {
            return null;
        }
        
        return SysDictDataConvert.INSTANCE.convert(dictDataDO);
    }

    public SysDictDataVO getDictDataByTypeAndLabel(String dictType, String dictLabel) {
        SysDictDataDO dictDataDO = dictDataMapper.selectByDictTypeAndLabel(dictType, dictLabel);
        if (dictDataDO == null) {
            return null;
        }
        
        return SysDictDataConvert.INSTANCE.convert(dictDataDO);
    }

    private boolean existsDictValue(String dictType, String dictValue) {
        if (StrUtil.isBlank(dictType) || StrUtil.isBlank(dictValue)) {
            return false;
        }
        
        SysDictDataDO dictDataDO = dictDataMapper.selectByDictTypeAndValue(dictType, dictValue);
        return dictDataDO != null;
    }

    public boolean existsDictLabel(String dictType, String dictLabel) {
        if (StrUtil.isBlank(dictType) || StrUtil.isBlank(dictLabel)) {
            return false;
        }
        
        SysDictDataDO dictDataDO = dictDataMapper.selectByDictTypeAndLabel(dictType, dictLabel);
        return dictDataDO != null;
    }

    public Integer getNextSort(String dictType) {
        if (StrUtil.isBlank(dictType)) {
            return 1;
        }
        
        Integer maxSort = dictDataMapper.selectMaxSortByDictType(dictType);
        return maxSort == null ? 1 : maxSort + 1;
    }

    @Override
    public void updateDictDataStatus(Long id, Integer status) {
        // 校验字典数据是否存在
        SysDictDataDO dictDataDO = dictDataMapper.selectById(id);
        if (dictDataDO == null) {
            throw new ServiceException("字典数据不存在");
        }

        // 更新状态
        dictDataDO.setStatus(status);
        dictDataMapper.updateById(dictDataDO);
        
        log.info("更新字典数据状态成功，ID: {}, 新状态: {}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictDataByType(String dictType) {
        if (StrUtil.isBlank(dictType)) {
            return 0;
        }
        
        int deletedCount = dictDataMapper.deleteByDictType(dictType);
        log.info("根据字典类型删除字典数据成功，字典类型: {}, 删除数量: {}", dictType, deletedCount);
        return deletedCount;
    }
}