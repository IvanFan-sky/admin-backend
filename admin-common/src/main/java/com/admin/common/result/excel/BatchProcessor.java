package com.admin.common.result.excel;

import java.util.List;

/**
 * Excel批处理器接口
 * 
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@FunctionalInterface
public interface BatchProcessor<T> {

    /**
     * 处理批次数据
     * 
     * @param batch 批次数据
     * @return 处理结果
     */
    BatchResult<T> process(List<T> batch);
}
