package com.admin.module.system.api.service.dict;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.dict.*;
import com.admin.module.system.api.vo.dict.SysDictDataVO;

import java.util.List;
import java.util.Set;

/**
 * 系统字典数据管理服务接口
 * 
 * 定义字典数据相关的业务操作规范
 * 包括字典数据生命周期管理、排序管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysDictDataService {

    /**
     * 创建字典数据
     * 
     * 1. 校验字典类型存在性
     * 2. 校验字典值唯一性
     * 3. 校验字典标签唯一性
     * 4. 自动设置排序值
     * 5. 保存字典数据信息
     *
     * @param createDTO 创建字典数据请求参数
     * @return 新创建的字典数据ID
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当字典类型不存在或字典值/标签已存在时抛出
     * @author admin
     * @since 1.0
     */
    Long createDictData(SysDictDataCreateDTO createDTO);

    /**
     * 更新字典数据
     * 
     * 1. 校验字典数据存在性
     * 2. 校验字典值唯一性（排除自身）
     * 3. 校验字典标签唯一性（排除自身）
     * 4. 使用乐观锁更新字典数据信息
     * 5. 记录操作日志
     *
     * @param updateDTO 更新字典数据请求参数
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当字典数据不存在、版本号不匹配或值/标签冲突时抛出
     * @author admin
     * @since 1.0
     */
    void updateDictData(SysDictDataUpdateDTO updateDTO);

    /**
     * 删除字典数据
     * 
     * 1. 校验字典数据存在性
     * 2. 执行逻辑删除
     * 3. 记录操作日志
     *
     * @param id 字典数据ID
     * @throws IllegalArgumentException 当ID为空或无效时抛出
     * @throws RuntimeException 当字典数据不存在时抛出
     * @author admin
     * @since 1.0
     */
    void deleteDictData(Long id);

    /**
     * 批量删除字典数据
     * 
     * @param ids 字典数据ID集合
     * @return 实际删除的字典数据数量
     * @author admin
     * @since 1.0
     */
    int deleteDictDatasBatch(Set<Long> ids);

    /**
     * 获取字典数据详情
     * 
     * @param id 字典数据ID
     * @return 字典数据详情信息
     * @throws IllegalArgumentException 当ID为空或无效时抛出
     * @throws RuntimeException 当字典数据不存在时抛出
     * @author admin
     * @since 1.0
     */
    SysDictDataVO getDictData(Long id);

    /**
     * 获取字典数据分页列表
     * 
     * @param queryDTO 查询条件
     * @return 字典数据分页结果
     * @author admin
     * @since 1.0
     */
    PageResult<SysDictDataVO> getDictDataPage(SysDictDataQueryDTO queryDTO);

    /**
     * 根据字典类型获取字典数据列表
     * 
     * @param dictType 字典类型
     * @return 字典数据列表，按排序值升序排列
     * @author admin
     * @since 1.0
     */
    List<SysDictDataVO> getDictDataByType(String dictType);

    /**
     * 根据字典类型获取启用状态的字典数据列表
     * 
     * @param dictType 字典类型
     * @return 启用状态的字典数据列表，按排序值升序排列
     * @author admin
     * @since 1.0
     */
    List<SysDictDataVO> getEnabledDictDataByType(String dictType);

    /**
     * 更新字典数据状态
     * 
     * @param id 字典数据ID
     * @param status 新的状态值
     * @author admin
     * @since 1.0
     */
    void updateDictDataStatus(Long id, Integer status);

    /**
     * 根据字典类型删除字典数据
     * 
     * 当删除字典类型时调用，级联删除其下的所有字典数据
     *
     * @param dictType 字典类型
     * @return 删除的字典数据数量
     * @author admin
     * @since 1.0
     */
    int deleteDictDataByType(String dictType);
}