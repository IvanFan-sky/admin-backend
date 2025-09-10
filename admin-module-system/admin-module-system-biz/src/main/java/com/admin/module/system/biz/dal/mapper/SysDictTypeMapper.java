package com.admin.module.system.biz.dal.mapper;

import com.admin.module.system.biz.dal.dataobject.SysDictTypeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典类型数据访问层
 * 
 * 提供字典类型相关的数据库操作接口
 * 继承MyBatis-Plus的BaseMapper获得基础CRUD能力
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictTypeDO> {

    /**
     * 根据字典类型查询字典类型信息
     * 
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    SysDictTypeDO selectByDictType(@Param("dictType") String dictType);

    /**
     * 根据字典名称查询字典类型信息
     * 
     * @param dictName 字典名称
     * @return 字典类型信息
     */
    SysDictTypeDO selectByDictName(@Param("dictName") String dictName);

    /**
     * 查询所有启用状态的字典类型
     * 
     * @return 启用状态的字典类型列表
     */
    List<SysDictTypeDO> selectEnabledDictTypes();

    /**
     * 检查字典类型是否被字典数据使用
     * 
     * @param dictType 字典类型
     * @return 使用数量
     */
    Long countDictDataByType(@Param("dictType") String dictType);
}