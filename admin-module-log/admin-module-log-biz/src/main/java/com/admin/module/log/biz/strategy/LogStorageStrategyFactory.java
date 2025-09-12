package com.admin.module.log.biz.strategy;

import com.admin.module.log.api.enums.LogStorageTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 日志存储策略工厂
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
public class LogStorageStrategyFactory {

    private final List<LogStorageStrategy> strategies;
    private Map<String, LogStorageStrategy> strategyMap;

    /**
     * 根据存储类型获取策略
     *
     * @param storageType 存储类型
     * @return 对应的存储策略
     */
    public LogStorageStrategy getStrategy(LogStorageTypeEnum storageType) {
        return getStrategy(storageType.getCode());
    }

    /**
     * 根据存储类型获取策略
     *
     * @param storageType 存储类型
     * @return 对应的存储策略
     */
    public LogStorageStrategy getStrategy(String storageType) {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            LogStorageStrategy::getStorageType,
                            Function.identity()
                    ));
        }
        
        LogStorageStrategy strategy = strategyMap.get(storageType);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的日志存储类型: " + storageType);
        }
        
        return strategy;
    }
}