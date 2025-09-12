package com.admin.module.infra.biz.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件存储策略工厂
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
public class FileStorageFactory {

    private final List<FileStorageStrategy> storageStrategies;
    private final Map<String, FileStorageStrategy> strategyMap;

    public FileStorageFactory(List<FileStorageStrategy> storageStrategies) {
        this.storageStrategies = storageStrategies;
        this.strategyMap = storageStrategies.stream()
                .collect(Collectors.toMap(
                        FileStorageStrategy::getStorageType,
                        Function.identity()
                ));
    }

    /**
     * 根据存储类型获取策略
     *
     * @param storageType 存储类型
     * @return 文件存储策略
     */
    public FileStorageStrategy getStrategy(String storageType) {
        FileStorageStrategy strategy = strategyMap.get(storageType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
        return strategy;
    }

    /**
     * 获取默认策略（第一个可用的策略）
     *
     * @return 默认文件存储策略
     */
    public FileStorageStrategy getDefaultStrategy() {
        if (storageStrategies.isEmpty()) {
            throw new IllegalStateException("没有可用的文件存储策略");
        }
        return storageStrategies.get(0);
    }

    /**
     * 获取所有支持的存储类型
     *
     * @return 存储类型列表
     */
    public List<String> getSupportedStorageTypes() {
        return strategyMap.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }
}