package com.admin.module.system.biz.controller.dict;

import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.system.api.dto.dict.SysDictTypeCreateDTO;
import com.admin.module.system.api.dto.dict.SysDictTypeQueryDTO;
import com.admin.module.system.api.dto.dict.SysDictTypeUpdateDTO;
import com.admin.module.system.api.service.dict.SysDictTypeService;
import com.admin.module.system.api.vo.dict.SysDictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 系统字典类型管理控制器
 * 
 * 提供字典类型管理相关的RESTful API接口
 * 包括字典类型的增删改查、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 字典类型管理")
@RestController
@RequestMapping("/system/dict-type")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    /**
     * 创建字典类型
     * 
     * @param createDTO 创建字典类型请求参数
     * @return 新创建的字典类型ID
     */
    @Operation(summary = "创建字典类型")
    @PostMapping
    public R<Long> createDictType(@Valid @RequestBody SysDictTypeCreateDTO createDTO) {
        Long dictTypeId = dictTypeService.createDictType(createDTO);
        return R.ok(dictTypeId);
    }

    /**
     * 更新字典类型
     * 
     * @param updateDTO 更新字典类型请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新字典类型")
    @PutMapping
    public R<Void> updateDictType(@Valid @RequestBody SysDictTypeUpdateDTO updateDTO) {
        dictTypeService.updateDictType(updateDTO);
        return R.ok();
    }

    /**
     * 删除字典类型
     * 
     * @param id 字典类型ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "字典类型编号", required = true, example = "1")
    @DeleteMapping("/{id}")
    public R<Void> deleteDictType(@PathVariable @NotNull @Positive Long id) {
        dictTypeService.deleteDictType(id);
        return R.ok();
    }

    /**
     * 批量删除字典类型
     * 
     * @param ids 字典类型ID列表
     * @return 实际删除的字典类型数量
     */
    @Operation(summary = "批量删除字典类型")
    @DeleteMapping("/batch")
    public R<Integer> deleteDictTypesBatch(@RequestBody @NotEmpty Set<@NotNull @Positive Long> ids) {
        int deleteCount = dictTypeService.deleteDictTypesBatch(ids);
        return R.ok(deleteCount);
    }

    /**
     * 获取字典类型详情
     * 
     * @param id 字典类型ID
     * @return 字典类型详情信息
     */
    @Operation(summary = "获取字典类型详情")
    @Parameter(name = "id", description = "字典类型编号", required = true, example = "1")
    @GetMapping("/{id}")
    public R<SysDictTypeVO> getDictType(@PathVariable @NotNull @Positive Long id) {
        SysDictTypeVO dictTypeVO = dictTypeService.getDictType(id);
        return R.ok(dictTypeVO);
    }

    /**
     * 根据字典类型获取字典类型详情
     * 
     * @param dictType 字典类型标识
     * @return 字典类型详情信息
     */
    @Operation(summary = "根据字典类型获取字典类型详情")
    @Parameter(name = "dictType", description = "字典类型标识", required = true, example = "sys_user_sex")
    @GetMapping("/type/{dictType}")
    public R<SysDictTypeVO> getDictTypeByType(@PathVariable String dictType) {
        SysDictTypeVO dictTypeVO = dictTypeService.getDictTypeByType(dictType);
        return R.ok(dictTypeVO);
    }

    /**
     * 获取字典类型分页列表
     * 
     * @param queryDTO 查询条件
     * @return 字典类型分页结果
     */
    @Operation(summary = "获取字典类型分页列表")
    @GetMapping("/page")
    public R<PageResult<SysDictTypeVO>> getDictTypePage(@Valid SysDictTypeQueryDTO queryDTO) {
        PageResult<SysDictTypeVO> pageResult = dictTypeService.getDictTypePage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取所有启用状态的字典类型列表
     * 
     * @return 启用状态的字典类型列表
     */
    @Operation(summary = "获取启用状态的字典类型列表")
    @GetMapping("/enabled")
    public R<List<SysDictTypeVO>> getEnabledDictTypes() {
        List<SysDictTypeVO> dictTypeList = dictTypeService.getEnabledDictTypes();
        return R.ok(dictTypeList);
    }

    /**
     * 更新字典类型状态
     * 
     * @param id 字典类型ID
     * @param status 新的状态值
     * @return 操作结果
     */
    @Operation(summary = "更新字典类型状态")
    @Parameter(name = "id", description = "字典类型编号", required = true, example = "1")
    @Parameter(name = "status", description = "字典类型状态", required = true, example = "1")
    @PutMapping("/{id}/status/{status}")
    public R<Void> updateDictTypeStatus(@PathVariable @NotNull @Positive Long id, 
                                        @PathVariable @NotNull Integer status) {
        dictTypeService.updateDictTypeStatus(id, status);
        return R.ok();
    }

    /**
     * 刷新字典缓存
     * 
     * @return 操作结果
     */
    @Operation(summary = "刷新字典缓存")
    @PostMapping("/refresh-cache")
    public R<Void> refreshCache() {
        dictTypeService.refreshCache();
        return R.ok();
    }
}