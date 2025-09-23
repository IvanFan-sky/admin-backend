package com.admin.module.ai.biz.dal.mapper;

import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话消息Mapper接口
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessageDO> {
    
    /**
     * 根据会话ID查询消息列表
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<AiChatMessageDO> selectBySessionId(@Param("sessionId") String sessionId, 
                                           @Param("limit") Integer limit);
    
    /**
     * 根据会话ID查询最近的消息
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<AiChatMessageDO> selectRecentMessages(@Param("sessionId") String sessionId, 
                                              @Param("limit") Integer limit);
}