package com.admin.module.notification.biz.convert.message;



import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.api.dto.message.UserInternalMessageBatchDeleteDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageBatchFavoriteDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageBatchReadDTO;
import com.admin.module.notification.api.dto.message.UserInternalMessageReceiptDTO;
import com.admin.module.notification.api.vo.message.InternalMessageStatisticsVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageSimpleVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageStatisticsVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageTypeCountVO;
import com.admin.module.notification.api.vo.message.UserInternalMessageVO;
import com.admin.module.notification.biz.dal.dataobject.InternalMessageDO;    
import com.admin.module.notification.biz.dal.dataobject.UserInternalMessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户站内信关联对象转换器
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper(componentModel = "spring")
public interface UserInternalMessageConvert {

    UserInternalMessageConvert INSTANCE = Mappers.getMapper(UserInternalMessageConvert.class);

    // ========== 用户端转换 ==========

    /**
     * DO转换为用户端VO
     *
     * @param userMessageDO DO对象
     * @return 用户端VO对象
     */
    UserInternalMessageVO convert(UserInternalMessageDO userMessageDO);

    /**
     * DO列表转换为用户端VO列表
     *
     * @param list DO列表
     * @return 用户端VO列表
     */
    List<UserInternalMessageVO> convertList(List<UserInternalMessageDO> list);

    /**
     * DO分页结果转换为用户端VO分页结果
     *
     * @param page DO分页结果
     * @return 用户端VO分页结果
     */
    PageResult<UserInternalMessageVO> convertPage(PageResult<UserInternalMessageDO> page);

    /**
     * DO转换为用户端详情VO
     *
     * @param userMessageDO DO对象
     * @return 用户端详情VO对象
     */
    UserInternalMessageDetailVO convertDetail(UserInternalMessageDO userMessageDO);

    /**
     * DO转换为用户端详情VO（包含站内信信息）
     *
     * @param userMessageDO 用户站内信DO对象
     * @param messageDO 站内信DO对象
     * @return 用户端详情VO对象
     */
    default UserInternalMessageDetailVO convertDetail(UserInternalMessageDO userMessageDO, InternalMessageDO messageDO) {
        UserInternalMessageDetailVO detailVO = convertDetail(userMessageDO);
        if (messageDO != null) {
            detailVO.setTitle(messageDO.getTitle());
            detailVO.setContent(messageDO.getContent());
            detailVO.setType(messageDO.getType());
            detailVO.setPriority(messageDO.getPriority());
            detailVO.setSenderId(messageDO.getSenderId());
            detailVO.setSenderName(messageDO.getSenderName());
        }
        return detailVO;
    }

    /**
     * DO转换为用户端简单VO
     *
     * @param userMessageDO DO对象
     * @return 用户端简单VO对象
     */
    UserInternalMessageSimpleVO convertSimple(UserInternalMessageDO userMessageDO);

    /**
     * DO列表转换为用户端简单VO列表
     *
     * @param list DO列表
     * @return 用户端简单VO列表
     */
    List<UserInternalMessageSimpleVO> convertSimpleList(List<UserInternalMessageDO> list);

    // ========== 回执DTO转换 ==========

    /**
     * 回执DTO转换为DO
     *
     * @param receiptDTO 回执DTO
     * @return DO对象
     */
    UserInternalMessageDO convert(UserInternalMessageReceiptDTO receiptDTO);

    // ========== 统计转换 ==========

    /**
     * 用户消息统计转换为VO
     *
     * @param statistics 统计信息Map
     * @return 统计VO对象
     */
    default UserInternalMessageStatisticsVO convertStatistics(java.util.Map<String, Object> statistics) {
        if (statistics == null) {
            return new UserInternalMessageStatisticsVO();
        }
        
        UserInternalMessageStatisticsVO vo = new UserInternalMessageStatisticsVO();
        vo.setUnreadCount(getLongValue(statistics, "unread_count"));
        vo.setReadCount(getLongValue(statistics, "read_count"));
        vo.setFavoriteCount(getLongValue(statistics, "favorite_count"));
        vo.setDeletedCount(getLongValue(statistics, "deleted_count"));
        vo.setReceiptedCount(getLongValue(statistics, "receipted_count"));
        
        return vo;
    }

    /**
     * 消息统计转换为VO
     *
     * @param statistics 统计信息Map
     * @return 统计VO对象
     */
    default InternalMessageStatisticsVO convertMessageStatistics(java.util.Map<String, Object> statistics) {
        if (statistics == null) {
            return new InternalMessageStatisticsVO();
        }
        
        InternalMessageStatisticsVO vo = new InternalMessageStatisticsVO();
        vo.setTotalCount(getLongValue(statistics, "total_count"));
        vo.setReceivedCount(getLongValue(statistics, "received_count"));
        vo.setFailedCount(getLongValue(statistics, "failed_count"));
        vo.setReadCount(getLongValue(statistics, "read_count"));
        vo.setReceiptCount(getLongValue(statistics, "receipt_count"));
        vo.setFavoriteCount(getLongValue(statistics, "favorite_count"));
        
        // 计算百分比
        long totalCount = vo.getTotalCount();
        if (totalCount > 0) {
            vo.setReceivedRate(calculatePercentage(vo.getReceivedCount(), totalCount));
            vo.setFailedRate(calculatePercentage(vo.getFailedCount(), totalCount));
        }
        
        long receivedCount = vo.getReceivedCount();
        if (receivedCount > 0) {
            vo.setReadRate(calculatePercentage(vo.getReadCount(), receivedCount));
            vo.setReceiptRate(calculatePercentage(vo.getReceiptCount(), receivedCount));
            vo.setFavoriteRate(calculatePercentage(vo.getFavoriteCount(), receivedCount));
        }
        
        return vo;
    }

    /**
     * 从Map中获取Long值
     *
     * @param map Map对象
     * @param key 键
     * @return Long值
     */
    default Long getLongValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 计算百分比
     *
     * @param numerator 分子
     * @param denominator 分母
     * @return 百分比（保留2位小数）
     */
    default Double calculatePercentage(Long numerator, Long denominator) {
        if (denominator == null || denominator == 0) {
            return 0.0;
        }
        if (numerator == null) {
            numerator = 0L;
        }
        return Math.round((double) numerator / denominator * 10000.0) / 100.0;
    }

    // ========== 批量操作转换 ==========

    /**
     * 批量标记已读DTO转换
     *
     * @param batchReadDTO 批量标记已读DTO
     * @return 消息ID列表
     */
    default List<Long> convertBatchRead(UserInternalMessageBatchReadDTO batchReadDTO) {
        return batchReadDTO != null ? batchReadDTO.getMessageIds() : java.util.Collections.emptyList();
    }

    /**
     * 批量删除DTO转换
     *
     * @param batchDeleteDTO 批量删除DTO
     * @return 消息ID列表
     */
    default List<Long> convertBatchDelete(UserInternalMessageBatchDeleteDTO batchDeleteDTO) {
        return batchDeleteDTO != null ? batchDeleteDTO.getMessageIds() : java.util.Collections.emptyList();
    }

    /**
     * 批量收藏DTO转换
     *
     * @param batchFavoriteDTO 批量收藏DTO
     * @return 消息ID列表
     */
    default List<Long> convertBatchFavorite(UserInternalMessageBatchFavoriteDTO batchFavoriteDTO) {
        return batchFavoriteDTO != null ? batchFavoriteDTO.getMessageIds() : java.util.Collections.emptyList();
    }

    // ========== 类型统计转换 ==========

    /**
     * 类型统计转换为VO列表
     *
     * @param typeCounts 类型统计Map列表
     * @return 类型统计VO列表
     */
    default List<UserInternalMessageTypeCountVO> convertTypeCounts(List<java.util.Map<String, Object>> typeCounts) {
        if (typeCounts == null || typeCounts.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return typeCounts.stream().map(this::convertTypeCount).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 单个类型统计转换为VO
     *
     * @param typeCount 类型统计Map
     * @return 类型统计VO
     */
    default UserInternalMessageTypeCountVO convertTypeCount(java.util.Map<String, Object> typeCount) {
        if (typeCount == null) {
            return new UserInternalMessageTypeCountVO();
        }
        
        UserInternalMessageTypeCountVO vo = new UserInternalMessageTypeCountVO();
        vo.setType(getIntegerValue(typeCount, "type"));
        vo.setTypeName(getStringValue(typeCount, "type_name"));
        vo.setTotalCount(getLongValue(typeCount, "total_count"));
        vo.setUnreadCount(getLongValue(typeCount, "unread_count"));
        vo.setReadCount(getLongValue(typeCount, "read_count"));
        vo.setFavoriteCount(getLongValue(typeCount, "favorite_count"));
        vo.setDeletedCount(getLongValue(typeCount, "deleted_count"));
        
        return vo;
    }

    /**
     * 从Map中获取Integer值
     *
     * @param map Map对象
     * @param key 键
     * @return Integer值
     */
    default Integer getIntegerValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 从Map中获取String值
     *
     * @param map Map对象
     * @param key 键
     * @return String值
     */
    default String getStringValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}