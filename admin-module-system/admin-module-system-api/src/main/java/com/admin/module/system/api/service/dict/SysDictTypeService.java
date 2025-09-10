package com.admin.module.system.api.service.dict;

import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.dict.*;
import com.admin.module.system.api.vo.dict.SysDictTypeVO;

import java.util.List;
import java.util.Set;

/**
 * 系统字典类型管理服务接口
 * 
 * 定义字典类型相关的业务操作规范
 * 包括字典类型生命周期管理、数据校验等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
public interface SysDictTypeService {

    /**
     * 创建字典类型
     * 
     * 1. 校验字典类型唯一性
     * 2. 校验字典名称唯一性
     * 3. 设置默认值并保存字典类型信息
     * 4. 记录操作日志
     *
     * @param createDTO 创建字典类型请求参数，包含字典名称、类型、状态等信息
     * @return 新创建的字典类型ID
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当字典类型或名称已存在时抛出
     * @author admin
     * @since 1.0
     */
    Long createDictType(SysDictTypeCreateDTO createDTO);

    /**
     * 更新字典类型
     * 
     * 1. 校验字典类型存在性
     * 2. 校验字典类型唯一性（排除自身）
     * 3. 校验字典名称唯一性（排除自身）
     * 4. 使用乐观锁更新字典类型信息
     * 5. 记录操作日志
     *
     * @param updateDTO 更新字典类型请求参数，包含字典类型ID、版本号等信息
     * @throws IllegalArgumentException 当必填参数为空时抛出
     * @throws RuntimeException 当字典类型不存在、版本号不匹配或名称类型冲突时抛出
     * @author admin
     * @since 1.0
     */
    void updateDictType(SysDictTypeUpdateDTO updateDTO);

    /**
     * 删除字典类型
     * 
     * 1. 校验字典类型存在性
     * 2. 检查是否被字典数据使用
     * 3. 执行逻辑删除
     * 4. 记录操作日志
     *
     * @param id 字典类型ID
     * @throws IllegalArgumentException 当ID为空或无效时抛出
     * @throws RuntimeException 当字典类型不存在或正在使用中时抛出
     * @author admin
     * @since 1.0
     */
    void deleteDictType(Long id);

    /**
     * 批量删除字典类型
     * 
     * 对每个ID调用删除方法，确保单个失败不影响其他操作
     * 返回实际删除的数量，便于前端展示结果
     *
     * @param ids 字典类型ID集合，自动去重
     * @return 实际删除的字典类型数量
     * @throws IllegalArgumentException 当ID集合为空或包含无效ID时抛出
     * @author admin
     * @since 1.0
     */
    int deleteDictTypesBatch(Set<Long> ids);

    /**
     * 获取字典类型详情
     * 
     * @param id 字典类型ID
     * @return 字典类型详情信息，包含所有字段
     * @throws IllegalArgumentException 当ID为空或无效时抛出
     * @throws RuntimeException 当字典类型不存在时抛出
     * @author admin
     * @since 1.0
     */
    SysDictTypeVO getDictType(Long id);

    /**
     * 根据字典类型获取字典类型详情
     * 
     * @param dictType 字典类型标识
     * @return 字典类型详情信息，不存在时返回null
     * @author admin
     * @since 1.0
     */
    SysDictTypeVO getDictTypeByType(String dictType);

    /**
     * 获取字典类型分页列表
     * 
     * 支持多条件组合查询和排序
     * 默认按创建时间倒序排列
     *
     * @param queryDTO 查询条件，包含分页参数、字典名称、字典类型、状态等筛选条件
     * @return 字典类型分页结果，包含总数和当前页数据
     * @author admin
     * @since 1.0
     */
    PageResult<SysDictTypeVO> getDictTypePage(SysDictTypeQueryDTO queryDTO);

    /**
     * 获取所有启用状态的字典类型列表
     * 
     * 用于下拉选择等场景，只返回启用状态的字典类型
     * 按字典类型字母顺序排序
     *
     * @return 启用状态的字典类型列表
     * @author admin
     * @since 1.0
     */
    List<SysDictTypeVO> getEnabledDictTypes();

    /**
     * 更新字典类型状态
     * 
     * 支持启用/禁用字典类型
     * 禁用字典类型时会同时禁用其下的所有字典数据
     *
     * @param id 字典类型ID
     * @param status 新的状态值，0-禁用 1-启用
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当字典类型不存在时抛出
     * @author admin
     * @since 1.0
     */
    void updateDictTypeStatus(Long id, Integer status);

    /**
     * 刷新字典缓存
     * 
     * 清空并重新加载所有字典类型和字典数据的缓存
     * 用于数据更新后的缓存同步
     *
     * @author admin
     * @since 1.0
     */
    void refreshCache();
}