package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysDictDataDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典数据数据访问层
 * 
 * 提供字典数据相关的数据库操作接口
 * 继承MyBatis-Plus的BaseMapper获得基础CRUD能力
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictDataDO> {

    /**
     * 根据字典类型查询字典数据列表
     * 
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictDataDO> selectByDictType(@Param("dictType") String dictType);

    /**
     * 根据字典类型查询启用状态的字典数据列表
     * 
     * @param dictType 字典类型
     * @return 启用状态的字典数据列表
     */
    List<SysDictDataDO> selectEnabledByDictType(@Param("dictType") String dictType);

    /**
     * 根据字典类型和字典值查询字典数据
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典数据信息
     */
    SysDictDataDO selectByDictTypeAndValue(@Param("dictType") String dictType, 
                                           @Param("dictValue") String dictValue);

    /**
     * 根据字典类型和字典标签查询字典数据
     * 
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典数据信息
     */
    SysDictDataDO selectByDictTypeAndLabel(@Param("dictType") String dictType, 
                                           @Param("dictLabel") String dictLabel);

    /**
     * 根据字典类型删除字典数据
     * 
     * @param dictType 字典类型
     * @return 删除的记录数
     */
    int deleteByDictType(@Param("dictType") String dictType);

    /**
     * 根据字典类型查询最大排序值
     * 
     * @param dictType 字典类型
     * @return 最大排序值
     */
    Integer selectMaxSortByDictType(@Param("dictType") String dictType);
}