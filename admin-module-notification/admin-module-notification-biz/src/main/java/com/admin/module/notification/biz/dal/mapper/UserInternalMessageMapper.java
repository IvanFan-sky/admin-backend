package com.admin.module.notification.biz.dal.mapper;


import com.admin.common.core.domain.PageResult;
import com.admin.module.notification.biz.dal.dataobject.UserInternalMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户站内信关联 Mapper
 *
 * @author admin
 * @since 2025-01-14
 */
@Mapper
public interface UserInternalMessageMapper extends BaseMapper<UserInternalMessageDO> {

    /**
     * 分页查询用户站内信列表
     *
     * @param userId 用户ID
     * @param pageDTO 查询条件
     * @return 分页结果
     */
    PageResult<UserInternalMessageDO> selectUserPage(@Param("userId") Long userId, @Param("pageDTO") Object pageDTO);

    /**
     * 查询用户站内信列表
     *
     * @param userId 用户ID
     * @param receiveStatus 接收状态
     * @param readStatus 阅读状态
     * @param deleteStatus 删除状态
     * @return 用户站内信列表
     */
    List<UserInternalMessageDO> selectList(@Param("userId") Long userId, @Param("receiveStatus") Integer receiveStatus, @Param("readStatus") Integer readStatus, @Param("deleteStatus") Integer deleteStatus);

    /**
     * 查询用户未读站内信列表
     *
     * @param userId 用户ID
     * @return 未读站内信列表
     */
    List<UserInternalMessageDO> selectUnreadList(@Param("userId") Long userId);

    /**
     * 查询用户已读站内信列表
     *
     * @param userId 用户ID
     * @return 已读站内信列表
     */
    List<UserInternalMessageDO> selectReadList(@Param("userId") Long userId);

    /**
     * 查询用户收藏站内信列表
     *
     * @param userId 用户ID
     * @return 收藏站内信列表
     */
    List<UserInternalMessageDO> selectFavoriteList(@Param("userId") Long userId);

    /**
     * 查询用户已删除站内信列表
     *
     * @param userId 用户ID
     * @return 已删除站内信列表
     */
    List<UserInternalMessageDO> selectDeletedList(@Param("userId") Long userId);

    /**
     * 查询站内信的接收用户列表
     *
     * @param messageId 站内信ID
     * @param receiveStatus 接收状态
     * @return 用户站内信关联列表
     */
    List<UserInternalMessageDO> selectByMessageId(@Param("messageId") Long messageId, @Param("receiveStatus") Integer receiveStatus);

    /**
     * 查询用户对特定站内信的关联记录
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @return 用户站内信关联记录
     */
    UserInternalMessageDO selectByUserIdAndMessageId(@Param("userId") Long userId, @Param("messageId") Long messageId);

    /**
     * 统计用户未读站内信数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long countUnread(@Param("userId") Long userId);

    /**
     * 根据用户ID和用户站内信ID查询记录
     *
     * @param userId 用户ID
     * @param id 用户站内信ID
     * @return 用户站内信记录
     */
    UserInternalMessageDO selectByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * 查询用户站内信列表（带参数）
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param readStatus 读取状态
     * @param limit 限制数量
     * @return 用户站内信列表
     */
    List<UserInternalMessageDO> selectUserList(@Param("userId") Long userId, @Param("type") Integer type, 
                                               @Param("readStatus") Integer readStatus, @Param("limit") Integer limit);

    /**
     * 按类型统计用户未读数量
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @return 未读数量
     */
    Long countUnreadByType(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 查询用户统计信息
     *
     * @param userId 用户ID
     * @return 统计信息Map
     */
    @MapKey("userId")
    java.util.Map<String, Object> selectUserStatistics(@Param("userId") Long userId);

    /**
     * 查询用户类型统计
     *
     * @param userId 用户ID
     * @return 类型统计列表
     */
    @MapKey("type")
    List<java.util.Map<String, Object>> selectUserTypeCount(@Param("userId") Long userId);

    /**
     * 查询未读列表（带限制）
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 未读列表
     */
    List<UserInternalMessageDO> selectUnreadList(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询收藏列表（带限制）
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 收藏列表
     */
    List<UserInternalMessageDO> selectFavoriteList(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询最近列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近列表
     */
    List<UserInternalMessageDO> selectRecentList(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 按类型查询列表
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param limit 限制数量
     * @return 消息列表
     */
    List<UserInternalMessageDO> selectListByType(@Param("userId") Long userId, @Param("type") Integer type, @Param("limit") Integer limit);

    /**
     * 按优先级查询列表
     *
     * @param userId 用户ID
     * @param priority 优先级
     * @param limit 限制数量
     * @return 消息列表
     */
    List<UserInternalMessageDO> selectListByPriority(@Param("userId") Long userId, @Param("priority") Integer priority, @Param("limit") Integer limit);

    /**
     * 按类型标记已读
     *
     * @param userId 用户ID
     * @param type 消息类型
     * @param readTime 读取时间
     * @return 更新数量
     */
    int markAsReadByType(@Param("userId") Long userId, @Param("type") Integer type, @Param("readTime") LocalDateTime readTime);

    /**
     * 批量标记为已删除
     *
     * @param userId 用户ID
     * @param ids 消息ID列表
     * @param deleteTime 删除时间
     * @return 更新数量
     */
    int markAsDeletedBatch(@Param("userId") Long userId, @Param("ids") List<Long> ids, @Param("deleteTime") LocalDateTime deleteTime);

    /**
     * 删除过期消息
     *
     * @param userId 用户ID
     * @param expireTime 过期时间
     * @return 删除数量
     */
    int deleteExpiredMessages(@Param("userId") Long userId, @Param("expireTime") LocalDateTime expireTime);

    /**
     * 删除已读消息
     *
     * @param userId 用户ID
     * @param expireTime 过期时间
     * @return 删除数量
     */
    int deleteReadMessages(@Param("userId") Long userId, @Param("expireTime") LocalDateTime expireTime);

    /**
     * 批量更新接收状态
     *
     * @param ids ID列表
     * @param receiveStatus 接收状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateReceiveStatusBatch(@Param("ids") List<Long> ids, @Param("receiveStatus") Integer receiveStatus, @Param("updater") String updater);

    /**
     * 批量更新读取状态
     *
     * @param ids ID列表
     * @param readStatus 读取状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateReadStatusBatch(@Param("ids") List<Long> ids, @Param("readStatus") Integer readStatus, @Param("updater") String updater);

    /**
     * 批量更新回执状态
     *
     * @param ids ID列表
     * @param receiptStatus 回执状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateReceiptStatusBatch(@Param("ids") List<Long> ids, @Param("receiptStatus") Integer receiptStatus, @Param("updater") String updater);

    /**
     * 批量更新收藏状态
     *
     * @param ids ID列表
     * @param favoriteStatus 收藏状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateFavoriteStatusBatch(@Param("ids") List<Long> ids, @Param("favoriteStatus") Integer favoriteStatus, @Param("updater") String updater);

    /**
     * 批量更新删除状态
     *
     * @param ids ID列表
     * @param deleteStatus 删除状态
     * @param updater 更新人
     * @return 更新数量
     */
    int updateDeleteStatusBatch(@Param("ids") List<Long> ids, @Param("deleteStatus") Integer deleteStatus, @Param("updater") String updater);





    /**
     * 批量插入用户站内信关联记录
     *
     * @param userMessages 用户站内信关联列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<UserInternalMessageDO> userMessages);

    /**
     * 批量标记为已读
     *
     * @param userId 用户ID
     * @param messageIds 站内信ID列表
     * @param readTime 阅读时间
     * @param updater 更新人
     * @return 更新数量
     */
    int markAsReadBatch(@Param("userId") Long userId,
                       @Param("messageIds") List<Long> messageIds,
                       @Param("readTime") LocalDateTime readTime,
                       @Param("updater") String updater);

    /**
     * 标记单个站内信为已读
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @param readTime 阅读时间
     * @param updater 更新人
     * @return 更新数量
     */
    int markAsRead(@Param("userId") Long userId,
                  @Param("messageId") Long messageId,
                  @Param("readTime") LocalDateTime readTime,
                  @Param("updater") String updater);

    /**
     * 标记全部为已读
     *
     * @param userId 用户ID
     * @param readTime 阅读时间
     * @param updater 更新人
     * @return 更新数量
     */
    int markAllAsRead(@Param("userId") Long userId,
                     @Param("readTime") LocalDateTime readTime,
                     @Param("updater") String updater);

    /**
     * 切换收藏状态
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @param favoriteStatus 收藏状态
     * @param favoriteTime 收藏时间
     * @param updater 更新人
     * @return 更新数量
     */
    int toggleFavorite(@Param("userId") Long userId,
                      @Param("messageId") Long messageId,
                      @Param("favoriteStatus") Integer favoriteStatus,
                      @Param("favoriteTime") LocalDateTime favoriteTime,
                      @Param("updater") String updater);

    /**
     * 切换删除状态
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @param deleteStatus 删除状态
     * @param deleteTime 删除时间
     * @param updater 更新人
     * @return 更新数量
     */
    int toggleDelete(@Param("userId") Long userId,
                    @Param("messageId") Long messageId,
                    @Param("deleteStatus") Integer deleteStatus,
                    @Param("deleteTime") LocalDateTime deleteTime,
                    @Param("updater") String updater);

    /**
     * 提交回执
     *
     * @param userId 用户ID
     * @param messageId 站内信ID
     * @param receiptContent 回执内容
     * @param receiptTime 回执时间
     * @param updater 更新人
     * @return 更新数量
     */
    int submitReceipt(@Param("userId") Long userId,
                     @Param("messageId") Long messageId,
                     @Param("receiptContent") String receiptContent,
                     @Param("receiptTime") LocalDateTime receiptTime,
                     @Param("updater") String updater);
}