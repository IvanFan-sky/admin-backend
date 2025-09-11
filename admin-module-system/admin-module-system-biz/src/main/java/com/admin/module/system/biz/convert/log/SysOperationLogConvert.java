package com.admin.module.system.biz.convert.log;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.module.system.api.dto.log.OperationLogDTO;
import com.admin.module.system.api.vo.log.SysOperationLogVO;
import com.admin.module.system.biz.dal.dataobject.SysOperationLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统操作日志转换器
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface SysOperationLogConvert {

    SysOperationLogConvert INSTANCE = Mappers.getMapper(SysOperationLogConvert.class);

    /**
     * 操作日志DTO转换为DO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    SysOperationLogDO convert(OperationLogDTO logDTO);

    /**
     * DO对象转换为VO对象
     */
    @Mapping(target = "businessTypeName", source = "businessType", qualifiedByName = "businessTypeName")
    @Mapping(target = "operatorTypeName", source = "operatorType", qualifiedByName = "operatorTypeName")
    @Mapping(target = "statusName", source = "status", qualifiedByName = "statusName")
    SysOperationLogVO convert(SysOperationLogDO logDO);

    /**
     * DO对象列表转换为VO对象列表
     */
    List<SysOperationLogVO> convertList(List<SysOperationLogDO> logList);

    /**
     * 分页结果转换
     */
    default PageResult<SysOperationLogVO> convertPage(PageResult<SysOperationLogDO> pageResult) {
        if (pageResult == null) {
            return null;
        }
        
        return new PageResult<>(
            convertList(pageResult.getRecords()),
            pageResult.getTotal()
        );
    }

    /**
     * 业务类型代码转换为名称
     */
    @Named("businessTypeName")
    default String businessTypeName(Integer businessType) {
        if (businessType == null) {
            return "未知";
        }
        
        for (OperationLog.BusinessType type : OperationLog.BusinessType.values()) {
            if (type.getCode() == businessType) {
                return type.getDescription();
            }
        }
        return "未知";
    }

    /**
     * 操作类型代码转换为名称
     */
    @Named("operatorTypeName")
    default String operatorTypeName(Integer operatorType) {
        if (operatorType == null) {
            return "未知";
        }
        
        for (OperationLog.OperatorType type : OperationLog.OperatorType.values()) {
            if (type.getCode() == operatorType) {
                return type.getDescription();
            }
        }
        return "未知";
    }

    /**
     * 状态代码转换为名称
     */
    @Named("statusName")
    default String statusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        
        return status == 1 ? "成功" : "失败";
    }
}