package com.admin.module.system.biz.service.dict;

import cn.hutool.core.util.StrUtil;
import com.admin.common.core.domain.PageResult;
import com.admin.common.exception.ServiceException;
import com.admin.module.system.api.dto.dict.SysDictTypeCreateDTO;
import com.admin.module.system.api.dto.dict.SysDictTypeQueryDTO;
import com.admin.module.system.api.dto.dict.SysDictTypeUpdateDTO;
import com.admin.module.system.api.service.dict.SysDictDataService;
import com.admin.module.system.api.service.dict.SysDictTypeService;
import com.admin.module.system.api.vo.dict.SysDictTypeVO;
import com.admin.module.system.biz.convert.dict.SysDictTypeConvert;
import com.admin.module.system.biz.dal.dataobject.SysDictTypeDO;
import com.admin.module.system.biz.dal.mapper.SysDictTypeMapper;
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
 * 字典类型服务实现类
 * 
 * 实现字典类型的业务逻辑处理
 * 包括CRUD操作、业务校验和数据转换
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataService dictDataService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.SYS_DICT_CACHE, allEntries = true)
    public Long createDictType(SysDictTypeCreateDTO createDTO) {
        // 校验字典类型是否已存在
        if (existsDictType(createDTO.getDictType())) {
            throw new ServiceException("字典类型已存在");
        }
        
        // 校验字典名称是否已存在
        if (existsDictName(createDTO.getDictName())) {
            throw new ServiceException("字典名称已存在");
        }

        // 转换并保存
        SysDictTypeDO dictTypeDO = SysDictTypeConvert.INSTANCE.convert(createDTO);
        dictTypeMapper.insert(dictTypeDO);
        
        log.info("创建字典类型成功，ID: {}, 字典类型: {}", dictTypeDO.getId(), dictTypeDO.getDictType());
        return dictTypeDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(SysDictTypeUpdateDTO updateDTO) {
        // 校验字典类型是否存在
        SysDictTypeDO existingDictType = dictTypeMapper.selectById(updateDTO.getId());
        if (existingDictType == null) {
            throw new ServiceException("字典类型不存在");
        }

        // 如果字典类型发生变化，需要校验新的字典类型是否已存在
        if (!existingDictType.getDictType().equals(updateDTO.getDictType())) {
            if (existsDictType(updateDTO.getDictType())) {
                throw new ServiceException("字典类型已存在");
            }
        }

        // 如果字典名称发生变化，需要校验新的字典名称是否已存在
        if (!existingDictType.getDictName().equals(updateDTO.getDictName())) {
            if (existsDictName(updateDTO.getDictName())) {
                throw new ServiceException("字典名称已存在");
            }
        }

        // 转换并更新
        SysDictTypeDO dictTypeDO = SysDictTypeConvert.INSTANCE.convert(updateDTO);
        dictTypeMapper.updateById(dictTypeDO);
        
        log.info("更新字典类型成功，ID: {}, 字典类型: {}", dictTypeDO.getId(), dictTypeDO.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        // 校验字典类型是否存在
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectById(id);
        if (dictTypeDO == null) {
            throw new ServiceException("字典类型不存在");
        }

        // 校验是否可以删除
        if (!canDeleteDictType(dictTypeDO.getDictType())) {
            throw new ServiceException("字典类型正在使用中，无法删除");
        }

        // 删除字典类型
        dictTypeMapper.deleteById(id);
        
        log.info("删除字典类型成功，ID: {}, 字典类型: {}", id, dictTypeDO.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictTypesBatch(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int deleteCount = 0;
        for (Long id : ids) {
            try {
                deleteDictType(id);
                deleteCount++;
            } catch (Exception e) {
                log.warn("删除字典类型失败，ID: {}, 错误信息: {}", id, e.getMessage());
            }
        }
        
        log.info("批量删除字典类型成功，删除数量: {}", deleteCount);
        return deleteCount;
    }

    @Override
    @Cacheable(value = CacheConstants.SYS_DICT_CACHE, key = "'type:' + #id", unless = "#result == null")
    public SysDictTypeVO getDictType(Long id) {
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectById(id);
        if (dictTypeDO == null) {
            throw new ServiceException("字典类型不存在");
        }
        
        return SysDictTypeConvert.INSTANCE.convert(dictTypeDO);
    }

    @Override
    public PageResult<SysDictTypeVO> getDictTypePage(SysDictTypeQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysDictTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryDTO.getDictName()), SysDictTypeDO::getDictName, queryDTO.getDictName())
                   .like(StrUtil.isNotBlank(queryDTO.getDictType()), SysDictTypeDO::getDictType, queryDTO.getDictType())
                   .eq(queryDTO.getStatus() != null, SysDictTypeDO::getStatus, queryDTO.getStatus())
                   .orderByDesc(SysDictTypeDO::getCreateTime);

        // 分页查询
        IPage<SysDictTypeDO> page = dictTypeMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryWrapper);
        
        // 转换结果
        List<SysDictTypeVO> list = SysDictTypeConvert.INSTANCE.convertList(page.getRecords());
        return new PageResult<>(list, page.getTotal(), page.getSize(), page.getCurrent());
    }


    @Override
    @Cacheable(value = CacheConstants.SYS_DICT_CACHE, key = "'enabled_types'", unless = "#result == null || #result.isEmpty()")
    public List<SysDictTypeVO> getEnabledDictTypes() {
        List<SysDictTypeDO> list = dictTypeMapper.selectEnabledDictTypes();
        return SysDictTypeConvert.INSTANCE.convertList(list);
    }

    @Override
    public SysDictTypeVO getDictTypeByType(String dictType) {
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectByDictType(dictType);
        if (dictTypeDO == null) {
            return null;
        }
        
        return SysDictTypeConvert.INSTANCE.convert(dictTypeDO);
    }

    private boolean existsDictType(String dictType) {
        if (StrUtil.isBlank(dictType)) {
            return false;
        }
        
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectByDictType(dictType);
        return dictTypeDO != null;
    }

    private boolean existsDictName(String dictName) {
        if (StrUtil.isBlank(dictName)) {
            return false;
        }
        
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectByDictName(dictName);
        return dictTypeDO != null;
    }

    private boolean canDeleteDictType(String dictType) {
        if (StrUtil.isBlank(dictType)) {
            return false;
        }
        
        Long count = dictTypeMapper.countDictDataByType(dictType);
        return count == null || count == 0;
    }

    @Override
    public void updateDictTypeStatus(Long id, Integer status) {
        // 校验字典类型是否存在
        SysDictTypeDO dictTypeDO = dictTypeMapper.selectById(id);
        if (dictTypeDO == null) {
            throw new ServiceException("字典类型不存在");
        }

        // 更新状态
        dictTypeDO.setStatus(status);
        dictTypeMapper.updateById(dictTypeDO);
        
        log.info("更新字典类型状态成功，ID: {}, 新状态: {}", id, status);
    }

    @Override
    public void refreshCache() {
        // TODO: 实现缓存刷新逻辑
        log.info("刷新字典缓存");
    }
}