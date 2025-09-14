package com.admin.module.notification.biz.controller.admin.notification;


import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.notification.api.dto.type.NotificationTypeCreateDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeUpdateDTO;
import com.admin.module.notification.api.dto.type.NotificationTypeQueryDTO;
import com.admin.module.notification.api.service.notification.NotificationTypeService;
import com.admin.module.notification.api.vo.type.NotificationTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知类型管理控制器
 * 
 * 提供通知类型管理相关的REST API接口
 * 包括通知类型的增删改查、状态管理等功能
 *
 * @author admin
 * @version 1.0
 * @since 2024-01-15
 */
@Tag(name = "管理后台 - 通知类型管理")
@RestController
@RequestMapping("/admin/notification/type")
@RequiredArgsConstructor
@Validated
public class NotificationTypeController {

    private final NotificationTypeService notificationTypeService;

    @Operation(
        summary = "获得通知类型分页列表", 
        description = "根据查询条件分页获取通知类型列表，支持按名称、编码、状态等条件筛选"
    )
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('notification:type:query')")
    public R<PageResult<NotificationTypeVO>> getNotificationTypePage(@Valid NotificationTypeQueryDTO queryDTO) {
        PageResult<NotificationTypeVO> pageResult = notificationTypeService.getNotificationTypePage(queryDTO);
        return R.ok(pageResult);
    }

    @Operation(
        summary = "获得通知类型列表", 
        description = "根据查询条件获取通知类型列表，不分页返回所有匹配的数据"
    )
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('notification:type:query')")
    public R<List<NotificationTypeVO>> getNotificationTypeList(@Valid NotificationTypeQueryDTO queryDTO) {
        List<NotificationTypeVO> list = notificationTypeService.getNotificationTypeList(queryDTO);
        return R.ok(list);
    }

    @Operation(
        summary = "获得启用的通知类型列表", 
        description = "获取所有启用状态的通知类型，按排序升序返回"
    )
    @GetMapping("/enabled")
    public R<List<NotificationTypeVO>> getEnabledTypes() {
        List<NotificationTypeVO> list = notificationTypeService.getEnabledTypes();
        return R.ok(list);
    }

    @Operation(
        summary = "获得通知类型详情", 
        description = "根据通知类型ID获取详细信息"
    )
    @Parameter(name = "id", description = "通知类型ID", required = true, example = "1024")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:type:query')")
    public R<NotificationTypeVO> getNotificationType(@PathVariable("id") Long id) {
        NotificationTypeVO notificationType = notificationTypeService.getNotificationType(id);
        return R.ok(notificationType);
    }

    @Operation(
        summary = "根据编码获取通知类型", 
        description = "根据类型编码获取通知类型信息"
    )
    @Parameter(name = "code", description = "类型编码", required = true, example = "SYSTEM_NOTICE")
    @GetMapping("/code/{code}")
    public R<NotificationTypeVO> getByCode(@PathVariable("code") String code) {
        NotificationTypeVO notificationType = notificationTypeService.getByCode(code);
        return R.ok(notificationType);
    }

    @Operation(
        summary = "创建通知类型", 
        description = "创建新的通知类型，需要提供类型基本信息"
    )
    @PostMapping
    @PreAuthorize("@ss.hasPermission('notification:type:create')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.INSERT, description = "创建通知类型")
    public R<Long> createNotificationType(@Valid @RequestBody NotificationTypeCreateDTO createDTO) {
        Long typeId = notificationTypeService.createNotificationType(createDTO);
        return R.ok(typeId);
    }

    @Operation(
        summary = "更新通知类型信息", 
        description = "更新通知类型的基本信息，支持修改名称、描述、图标等"
    )
    @PutMapping
    @PreAuthorize("@ss.hasPermission('notification:type:update')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新通知类型")
    public R<Boolean> updateNotificationType(@Valid @RequestBody NotificationTypeUpdateDTO updateDTO) {
        notificationTypeService.updateNotificationType(updateDTO);
        return R.ok(true);
    }

    @Operation(
        summary = "删除通知类型", 
        description = "根据通知类型ID删除类型，执行逻辑删除"
    )
    @Parameter(name = "id", description = "通知类型ID", required = true, example = "1024")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('notification:type:delete')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.DELETE, description = "删除通知类型")
    public R<Boolean> deleteNotificationType(@PathVariable("id") Long id) {
        notificationTypeService.deleteNotificationType(id);
        return R.ok(true);
    }

    @Operation(summary = "批量删除通知类型")
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermission('notification:type:delete')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除通知类型")
    public R<Boolean> deleteNotificationTypes(@RequestBody @NotEmpty(message = "删除类型不能为空") Long[] ids) {
        notificationTypeService.deleteNotificationTypes(ids);
        return R.ok(true);
    }

    @Operation(summary = "修改通知类型状态")
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermission('notification:type:update')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.UPDATE, description = "修改通知类型状态")
    public R<Boolean> changeStatus(@RequestParam("id") @NotNull(message = "类型ID不能为空") Long id,
                                   @RequestParam("status") @NotNull(message = "状态不能为空") Integer status) {
        notificationTypeService.changeStatus(id, status);
        return R.ok(true);
    }

    @Operation(summary = "批量修改通知类型状态")
    @PutMapping("/batchChangeStatus")
    @PreAuthorize("@ss.hasPermission('notification:type:update')")
    @OperationLog(title = "通知类型管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量修改通知类型状态")
    public R<Boolean> batchChangeStatus(@RequestBody @NotEmpty(message = "类型ID不能为空") Long[] ids,
                                        @RequestParam("status") @NotNull(message = "状态不能为空") Integer status) {
        notificationTypeService.batchChangeStatus(ids, status);
        return R.ok(true);
    }

    @Operation(summary = "校验类型编码是否唯一")
    @GetMapping("/checkCodeUnique")
    @PreAuthorize("@ss.hasPermission('notification:type:query')")
    public R<Boolean> checkCodeUnique(@RequestParam("code") String code,
                                      @RequestParam(value = "id", required = false) Long id) {
        boolean unique = notificationTypeService.checkCodeUnique(code, id);
        return R.ok(unique);
    }

    @Operation(summary = "校验类型名称是否唯一")
    @GetMapping("/checkNameUnique")
    @PreAuthorize("@ss.hasPermission('notification:type:query')")
    public R<Boolean> checkNameUnique(@RequestParam("name") String name,
                                      @RequestParam(value = "id", required = false) Long id) {
        boolean unique = notificationTypeService.checkNameUnique(name, id);
        return R.ok(unique);
    }
}