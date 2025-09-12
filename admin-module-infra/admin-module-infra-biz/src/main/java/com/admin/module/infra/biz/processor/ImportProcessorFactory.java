package com.admin.module.infra.biz.processor;

import com.admin.common.enums.ErrorCode;
import com.admin.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导入处理器工厂
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImportProcessorFactory {

    private final List<ImportDataProcessor> importProcessors;
    private final Map<String, ImportDataProcessor> processorMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (ImportDataProcessor processor : importProcessors) {
            String dataType = processor.getSupportedDataType();
            processorMap.put(dataType, processor);
            log.info("注册导入处理器: {} -> {}", dataType, processor.getClass().getSimpleName());
        }
        log.info("导入处理器工厂初始化完成，共注册 {} 个处理器", processorMap.size());
    }

    /**
     * 获取导入处理器
     *
     * @param dataType 数据类型
     * @return 导入处理器
     */
    public ImportDataProcessor getProcessor(String dataType) {
        ImportDataProcessor processor = processorMap.get(dataType);
        if (processor == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "不支持的数据类型: " + dataType);
        }
        return processor;
    }

    /**
     * 检查是否支持指定数据类型
     *
     * @param dataType 数据类型
     * @return 是否支持
     */
    public boolean isSupported(String dataType) {
        return processorMap.containsKey(dataType);
    }

    /**
     * 获取所有支持的数据类型
     *
     * @return 数据类型列表
     */
    public List<String> getSupportedDataTypes() {
        return List.copyOf(processorMap.keySet());
    }
}