package com.admin.module.notification.biz.service.notification;

import com.admin.common.core.domain.PageResult;
import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import com.admin.module.notification.api.dto.type.NotificationTypeCreateDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeQueryDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeUpdateDTO;
import com.admin.module.notification.api.service.notification.NotificationTypeService;
import com.admin.module.notification.api.vo.type.NotificationTypeVO;
import com.admin.module.notification.biz.convert.notification.NotificationTypeConvert;
import com.admin.module.notification.biz.dal.dataobject.NotificationTypeDO;
import com.admin.module.notification.biz.dal.mapper.NotificationTypeMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 通知类型服务实现类
 * 
 * 提供通知类型管理的具体实现
 * 包括通知类型的增删改查、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final NotificationTypeMapper notificationTypeMapper;

    @Override
    public PageResult<NotificationTypeVO> getNotificationTypePage(NotificationTypeQueryDTO queryDTO) {
        // 构建分页参数
        Page<NotificationTypeDO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<NotificationTypeDO> pageResult = notificationTypeMapper.selectNotificationTypePage(page, queryDTO);
        
        // 转换并构建分页结果
        List<NotificationTypeVO> records = NotificationTypeConvert.INSTANCE.convertList(pageResult.getRecords());
        return new PageResult<>(records, pageResult.getTotal());
    }

    @Override
    public List<NotificationTypeVO> getNotificationTypeList(NotificationTypeQueryDTO queryDTO) {
        // 查询列表
        List<NotificationTypeDO> list = notificationTypeMapper.selectNotificationTypeList(queryDTO);
        
        // 转换为VO并返回
        return NotificationTypeConvert.INSTANCE.convertList(list);
    }

    @Override
    public NotificationTypeVO getNotificationType(Long id) {
        // 查询通知类型
        NotificationTypeDO notificationTypeDO = notificationTypeMapper.selectById(id);
        if (notificationTypeDO == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
        }
        
        // 转换为VO并返回
        return NotificationTypeConvert.INSTANCE.convert(notificationTypeDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotificationType(NotificationTypeCreateDTO createDTO) {
        // 校验类型编码唯一性
        if (!checkCodeUnique(createDTO.getCode(), null)) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_CODE_EXISTS);
        }
        
        // 校验类型名称唯一性
        if (!checkNameUnique(createDTO.getName(), null)) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NAME_EXISTS);
        }
        
        // 转换为DO
        NotificationTypeDO notificationTypeDO = NotificationTypeConvert.INSTANCE.convert(createDTO);
        
        // 设置默认值
        if (notificationTypeDO.getSort() == null) {
            Integer maxSort = notificationTypeMapper.selectMaxSort();
            notificationTypeDO.setSort(maxSort != null ? maxSort + 1 : 1);
        }
        if (notificationTypeDO.getStatus() == null) {
            notificationTypeDO.setStatus(1); // 默认启用
        }
        if (notificationTypeDO.getIsSystem() == null) {
            notificationTypeDO.setIsSystem(0); // 默认非系统内置
        }
        
        // 插入数据库
        notificationTypeMapper.insert(notificationTypeDO);
        
        log.info("创建通知类型成功，ID: {}, 编码: {}, 名称: {}", 
                notificationTypeDO.getId(), notificationTypeDO.getCode(), notificationTypeDO.getName());
        
        return notificationTypeDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationType(NotificationTypeUpdateDTO updateDTO) {
        // 校验通知类型是否存在
        NotificationTypeDO existingType = notificationTypeMapper.selectById(updateDTO.getId());
        if (existingType == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
        }
        
        // 校验系统内置类型不能修改核心字段
        if (existingType.isSystemBuiltIn()) {
            if (!existingType.getCode().equals(updateDTO.getCode())) {
                throw new ServiceException("系统内置类型不能修改编码");
            }
        }
        
        // 校验类型编码唯一性
        if (!checkCodeUnique(updateDTO.getCode(), updateDTO.getId())) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_CODE_EXISTS);
        }
        
        // 校验类型名称唯一性
        if (!checkNameUnique(updateDTO.getName(), updateDTO.getId())) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NAME_EXISTS);
        }
        
        // 转换为DO
        NotificationTypeDO notificationTypeDO = NotificationTypeConvert.INSTANCE.convert(updateDTO);
        
        // 更新数据库
        notificationTypeMapper.updateById(notificationTypeDO);
        
        log.info("更新通知类型成功，ID: {}, 编码: {}, 名称: {}", 
                notificationTypeDO.getId(), notificationTypeDO.getCode(), notificationTypeDO.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationType(Long id) {
        // 校验通知类型是否存在
        NotificationTypeDO notificationTypeDO = notificationTypeMapper.selectById(id);
        if (notificationTypeDO == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
        }
        
        // 校验系统内置类型不能删除
        if (notificationTypeDO.isSystemBuiltIn()) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_SYSTEM_CANNOT_DELETE);
        }
        
        // TODO: 校验是否有关联的通知，如果有则不能删除
        
        // 执行删除
        notificationTypeMapper.deleteById(id);
        
        log.info("删除通知类型成功，ID: {}, 编码: {}, 名称: {}", 
                id, notificationTypeDO.getCode(), notificationTypeDO.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationTypes(Long[] ids) {
        for (Long id : ids) {
            deleteNotificationType(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationTypeStatus(Long id, Integer status) {
        // 校验通知类型是否存在
        NotificationTypeDO notificationTypeDO = notificationTypeMapper.selectById(id);
        if (notificationTypeDO == null) {
            throw new ServiceException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND);
        }
        
        // 更新状态
        notificationTypeDO.setStatus(status);
        notificationTypeMapper.updateById(notificationTypeDO);
        
        log.info("修改通知类型状态成功，ID: {}, 状态: {}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        // 直接调用已有的updateNotificationTypeStatus方法
        updateNotificationTypeStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchChangeStatus(Long[] ids, Integer status) {
        // TODO: 使用当前登录用户作为更新者
        String updater = "system";
        
        int updateCount = notificationTypeMapper.batchUpdateStatus(Arrays.asList(ids), status, updater);
        
        log.info("批量修改通知类型状态成功，更新数量: {}, 状态: {}", updateCount, status);
    }

    @Override
    public boolean checkCodeUnique(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        return notificationTypeMapper.checkCodeUnique(code, excludeId);
    }

    @Override
    public boolean checkNameUnique(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        return notificationTypeMapper.checkNameUnique(name, excludeId);
    }

    @Override
    public List<NotificationTypeVO> getEnabledTypes() {
        // 创建查询条件，只查询启用状态的类型
        NotificationTypeQueryDTO queryDTO = new NotificationTypeQueryDTO();
        queryDTO.setStatus(1); // 1表示启用状态
        List<NotificationTypeDO> list = notificationTypeMapper.selectNotificationTypeList(queryDTO);
        return NotificationTypeConvert.INSTANCE.convertList(list);
    }

    @Override
    public NotificationTypeVO getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        
        NotificationTypeDO notificationTypeDO = notificationTypeMapper.selectByCode(code);
        if (notificationTypeDO == null) {
            return null;
        }
        
        return NotificationTypeConvert.INSTANCE.convert(notificationTypeDO);
    }
}