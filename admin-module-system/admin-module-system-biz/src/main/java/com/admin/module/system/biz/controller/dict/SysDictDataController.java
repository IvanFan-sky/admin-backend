package com.admin.module.system.biz.controller.dict;

import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.dict.*;
import com.admin.module.system.api.service.dict.SysDictDataService;
import com.admin.module.system.api.vo.dict.SysDictDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

/**
 * 系统字典数据管理控制器
 * 
 * 提供字典数据管理相关的RESTful API接口
 * 包括字典数据的增删改查、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 字典数据管理")
@RestController
@RequestMapping("/system/dict-data")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    /**
     * 创建字典数据
     * 
     * @param createDTO 创建字典数据请求参数
     * @return 新创建的字典数据ID
     */
    @Operation(summary = "创建字典数据")
    @PostMapping
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.INSERT, description = "创建字典数据")
    public R<Long> createDictData(@Valid @RequestBody SysDictDataCreateDTO createDTO) {
        Long dictDataId = dictDataService.createDictData(createDTO);
        return R.ok(dictDataId);
    }

    /**
     * 更新字典数据
     * 
     * @param updateDTO 更新字典数据请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新字典数据")
    @PutMapping
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新字典数据")
    public R<Void> updateDictData(@Valid @RequestBody SysDictDataUpdateDTO updateDTO) {
        dictDataService.updateDictData(updateDTO);
        return R.ok();
    }

    /**
     * 删除字典数据
     * 
     * @param id 字典数据ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "字典数据编号", required = true, example = "1")
    @DeleteMapping("/{id}")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.DELETE, description = "删除字典数据")
    public R<Void> deleteDictData(@PathVariable @NotNull @Positive Long id) {
        dictDataService.deleteDictData(id);
        return R.ok();
    }

    /**
     * 批量删除字典数据
     * 
     * @param ids 字典数据ID列表
     * @return 实际删除的字典数据数量
     */
    @Operation(summary = "批量删除字典数据")
    @DeleteMapping("/batch")
    @OperationLog(title = "字典管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除字典数据")
    public R<Integer> deleteDictDatasBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = dictDataService.deleteDictDatasBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 获取字典数据详情
     * 
     * @param id 字典数据ID
     * @return 字典数据详情信息
     */
    @Operation(summary = "获取字典数据详情")
    @Parameter(name = "id", description = "字典数据编号", required = true, example = "1")
    @GetMapping("/{id}")
    public R<SysDictDataVO> getDictData(@PathVariable @NotNull @Positive Long id) {
        SysDictDataVO dictDataVO = dictDataService.getDictData(id);
        return R.ok(dictDataVO);
    }

    /**
     * 获取字典数据分页列表
     * 
     * @param queryDTO 查询条件
     * @return 字典数据分页结果
     */
    @Operation(summary = "获取字典数据分页列表")
    @GetMapping("/page")
    public R<PageResult<SysDictDataVO>> getDictDataPage(@Valid SysDictDataQueryDTO queryDTO) {
        PageResult<SysDictDataVO> pageResult = dictDataService.getDictDataPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 根据字典类型获取字典数据列表
     * 
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Operation(summary = "根据字典类型获取字典数据列表")
    @Parameter(name = "dictType", description = "字典类型", required = true, example = "sys_user_sex")
    @GetMapping("/type/{dictType}")
    public R<List<SysDictDataVO>> getDictDataByType(@PathVariable String dictType) {
        List<SysDictDataVO> dictDataList = dictDataService.getDictDataByType(dictType);
        return R.ok(dictDataList);
    }

    /**
     * 根据字典类型获取启用状态的字典数据列表
     * 
     * @param dictType 字典类型
     * @return 启用状态的字典数据列表
     */
    @Operation(summary = "根据字典类型获取启用状态的字典数据列表")
    @Parameter(name = "dictType", description = "字典类型", required = true, example = "sys_user_sex")
    @GetMapping("/type/{dictType}/enabled")
    public R<List<SysDictDataVO>> getEnabledDictDataByType(@PathVariable String dictType) {
        List<SysDictDataVO> dictDataList = dictDataService.getEnabledDictDataByType(dictType);
        return R.ok(dictDataList);
    }

    /**
     * 更新字典数据状态
     * 
     * @param id 字典数据ID
     * @param status 新的状态值
     * @return 操作结果
     */
    @Operation(summary = "更新字典数据状态")
    @Parameter(name = "id", description = "字典数据编号", required = true, example = "1")
    @Parameter(name = "status", description = "字典数据状态", required = true, example = "1")
    @PutMapping("/{id}/status/{status}")
    public R<Void> updateDictDataStatus(@PathVariable @NotNull @Positive Long id, 
                                        @PathVariable @NotNull Integer status) {
        dictDataService.updateDictDataStatus(id, status);
        return R.ok();
    }
}