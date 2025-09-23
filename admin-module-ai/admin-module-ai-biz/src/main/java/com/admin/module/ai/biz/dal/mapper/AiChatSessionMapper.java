package com.admin.module.ai.biz.dal.mapper;

import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI对话会话Mapper接口
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSessionDO> {
    
    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiChatSessionDO> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会话ID查询会话信息
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    AiChatSessionDO selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 更新会话统计信息
     *
     * @param sessionId 会话ID
     * @param messageCount 消息数量增量
     * @param tokenCount token数量增量
     * @param cost 成本增量
     */
    void updateSessionStats(@Param("sessionId") String sessionId, 
                           @Param("messageCount") Integer messageCount,
                           @Param("tokenCount") Integer tokenCount, 
                           @Param("cost") BigDecimal cost);
}