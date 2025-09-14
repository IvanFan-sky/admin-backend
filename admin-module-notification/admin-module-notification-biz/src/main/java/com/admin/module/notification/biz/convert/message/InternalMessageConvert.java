package com.admin.module.notification.biz.convert.message;



import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.message.InternalMessageCreateDTO;
import com.admin.module.notification.api.dto.message.InternalMessageUpdateDTO;
import com.admin.module.notification.api.vo.message.InternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.InternalMessageExportVO;
import com.admin.module.notification.api.vo.message.InternalMessageSimpleVO;
import com.admin.module.notification.api.vo.message.InternalMessageVO;
import com.admin.module.notification.biz.dal.dataobject.InternalMessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 站内信对象转换器
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper(componentModel = "spring")
public interface InternalMessageConvert {

    InternalMessageConvert INSTANCE = Mappers.getMapper(InternalMessageConvert.class);

    // ========== 管理端转换 ==========

    /**
     * 创建DTO转换为DO
     *
     * @param createDTO 创建DTO
     * @return DO对象
     */
    InternalMessageDO convert(InternalMessageCreateDTO createDTO);

    /**
     * 更新DTO转换为DO
     *
     * @param updateDTO 更新DTO
     * @return DO对象
     */
    InternalMessageDO convert(InternalMessageUpdateDTO updateDTO);

    /**
     * DO转换为VO
     *
     * @param messageDO DO对象
     * @return VO对象
     */
    InternalMessageVO convert(InternalMessageDO messageDO);

    /**
     * DO列表转换为VO列表
     *
     * @param list DO列表
     * @return VO列表
     */
    List<InternalMessageVO> convertList(List<InternalMessageDO> list);

    /**
     * DO分页结果转换为VO分页结果
     *
     * @param page DO分页结果
     * @return VO分页结果
     */
    PageResult<InternalMessageVO> convertPage(PageResult<InternalMessageDO> page);

    /**
     * DO转换为详情VO
     *
     * @param messageDO DO对象
     * @return 详情VO对象
     */
    InternalMessageDetailVO convertDetail(InternalMessageDO messageDO);

    // ========== 简单VO转换 ==========

    /**
     * DO转换为简单VO
     *
     * @param messageDO DO对象
     * @return 简单VO对象
     */
    InternalMessageSimpleVO convertSimple(InternalMessageDO messageDO);

    /**
     * DO列表转换为简单VO列表
     *
     * @param list DO列表
     * @return 简单VO列表
     */
    List<InternalMessageSimpleVO> convertSimpleList(List<InternalMessageDO> list);

    // ========== 导出VO转换 ==========

    /**
     * DO转换为导出VO
     *
     * @param messageDO DO对象
     * @return 导出VO对象
     */
    InternalMessageExportVO convertExport(InternalMessageDO messageDO);

    /**
     * DO列表转换为导出VO列表
     *
     * @param list DO列表
     * @return 导出VO列表
     */
    List<InternalMessageExportVO> convertExportList(List<InternalMessageDO> list);

}