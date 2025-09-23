package com.admin.module.ai.biz.convert;

import com.admin.module.ai.api.enums.ChatStatus;
import com.admin.module.ai.api.enums.ModelType;
import com.admin.module.ai.api.vo.chat.ChatMessageVO;
import com.admin.module.ai.api.vo.chat.ChatSessionVO;
import com.admin.module.ai.biz.dal.dataobject.AiChatMessageDO;
import com.admin.module.ai.biz.dal.dataobject.AiChatSessionDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * AI对话转换器
 *
 * @author admin
 * @since 2024-01-15
 */
@Mapper(componentModel = "spring")
public interface AiChatConvert {
    
    AiChatConvert INSTANCE = Mappers.getMapper(AiChatConvert.class);
    
    /**
     * 会话数据对象转换为VO
     */
    @Mapping(source = "modelType", target = "modelType", qualifiedByName = "stringToModelType")
    @Mapping(source = "status", target = "status", qualifiedByName = "integerToChatStatus")
    ChatSessionVO convertToVO(AiChatSessionDO sessionDO);
    
    /**
     * 会话数据对象列表转换为VO列表
     */
    List<ChatSessionVO> convertToVOList(List<AiChatSessionDO> sessionDOList);
    
    /**
     * 消息数据对象转换为VO
     */
    ChatMessageVO convertToMessageVO(AiChatMessageDO messageDO);
    
    /**
     * 消息数据对象列表转换为VO列表
     */
    List<ChatMessageVO> convertToMessageVOList(List<AiChatMessageDO> messageDOList);
    
    @Named("stringToModelType")
    default ModelType stringToModelType(String modelType) {
        if (modelType == null) {
            return null;
        }
        try {
            return ModelType.valueOf(modelType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("integerToChatStatus")
    default ChatStatus integerToChatStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return ChatStatus.getByCode(status);
    }
}