package com.admin.module.notification.biz.controller.admin.message;


import com.admin.common.annotation.OperationLog;
import com.admin.common.core.domain.PageResult;
import com.admin.common.core.domain.R;
import com.admin.module.notification.api.dto.message.InternalMessageCreateDTO;
import com.admin.module.notification.api.dto.message.InternalMessageQueryDTO;
import com.admin.module.notification.api.dto.message.InternalMessageUpdateDTO;
import com.admin.module.notification.api.service.message.InternalMessageService;
import com.admin.module.notification.api.vo.message.InternalMessageDetailVO;
import com.admin.module.notification.api.vo.message.InternalMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 站内信管理 Controller
 *
 * @author admin
 * @since 2025-01-14
 */
@Tag(name = "管理后台 - 站内信管理")
@RestController
@RequestMapping("/notification/internal-message")
@Validated
public class InternalMessageController {

    @Resource
    private InternalMessageService internalMessageService;

    @PostMapping("/create")
    @Operation(summary = "创建站内信")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:create')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.INSERT, description = "创建站内信")
    public R<Long> createInternalMessage(@Valid @RequestBody InternalMessageCreateDTO createDTO) {
        return R.ok(internalMessageService.createInternalMessage(createDTO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新站内信")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:update')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "更新站内信")
    public R<Boolean> updateInternalMessage(@Valid @RequestBody InternalMessageUpdateDTO updateDTO) {
        internalMessageService.updateInternalMessage(updateDTO);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除站内信")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('notification:internal-message:delete')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.DELETE, description = "删除站内信")
    public R<Boolean> deleteInternalMessage(@RequestParam("id") Long id) {
        internalMessageService.deleteInternalMessage(id);
        return R.ok(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除站内信")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('notification:internal-message:delete')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.DELETE, description = "批量删除站内信")
    public R<Boolean> deleteInternalMessageBatch(@RequestParam("ids") List<Long> ids) {
        internalMessageService.deleteInternalMessageBatch(ids);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得站内信")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<InternalMessageDetailVO> getInternalMessage(@RequestParam("id") Long id) {
        InternalMessageDetailVO internalMessage = internalMessageService.getInternalMessage(id);
        return R.ok(internalMessage);
    }

    @GetMapping("/page")
    @Operation(summary = "获得站内信分页")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<PageResult<InternalMessageVO>> getInternalMessagePage(@Valid InternalMessageQueryDTO queryDTO) {
        PageResult<InternalMessageVO> pageResult = internalMessageService.getInternalMessagePage(queryDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得站内信列表")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<List<InternalMessageVO>> getInternalMessageList(@Valid InternalMessageQueryDTO queryDTO) {
        List<InternalMessageVO> list = internalMessageService.getInternalMessageList(queryDTO);
        return R.ok(list);
    }

    @GetMapping("/draft-list")
    @Operation(summary = "获得草稿列表")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<List<InternalMessageVO>> getDraftList(
            @RequestParam(value = "senderId", required = false) Long senderId) {
        List<InternalMessageVO> list = internalMessageService.getDraftList(senderId);
        return R.ok(list);
    }

    @GetMapping("/sent-list")
    @Operation(summary = "获得已发送列表")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<List<InternalMessageVO>> getSentList(
            @RequestParam(value = "senderId", required = false) Long senderId) {
        List<InternalMessageVO> list = internalMessageService.getSentList(senderId);
        return R.ok(list);
    }

    @GetMapping("/list-by-type")
    @Operation(summary = "按类型获得站内信列表")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<List<InternalMessageVO>> getListByType(
            @RequestParam("type") Integer type,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<InternalMessageVO> list = internalMessageService.getListByType(type, status, limit);
        return R.ok(list);
    }

    @GetMapping("/list-by-priority")
    @Operation(summary = "按优先级获得站内信列表")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<List<InternalMessageVO>> getListByPriority(
            @RequestParam("priority") Integer priority,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<InternalMessageVO> list = internalMessageService.getListByPriority(priority, status, limit);
        return R.ok(list);
    }

    @PostMapping("/send")
    @Operation(summary = "发送站内信")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('notification:internal-message:send')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "发送站内信")
    public R<Boolean> sendInternalMessage(@RequestParam("id") Long id) {
        internalMessageService.sendInternalMessage(id);
        return R.ok(true);
    }

    @PostMapping("/send-batch")
    @Operation(summary = "批量发送站内信")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('notification:internal-message:send')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量发送站内信")
    public R<Boolean> sendInternalMessageBatch(@RequestParam("ids") List<Long> ids) {
        internalMessageService.sendInternalMessageBatch(ids);
        return R.ok(true);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "撤回站内信")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('notification:internal-message:withdraw')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "撤回站内信")
    public R<Boolean> withdrawInternalMessage(@RequestParam("id") Long id) {
        internalMessageService.revokeInternalMessage(id);
        return R.ok(true);
    }

    @PostMapping("/update-status-batch")
    @Operation(summary = "批量更新站内信状态")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:update')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "批量更新站内信状态")
    public R<Boolean> updateInternalMessageStatusBatch(
            @RequestParam("ids") List<Long> ids,
            @RequestParam("status") Integer status) {
        internalMessageService.updateInternalMessageStatusBatch(ids, status, "system");
        return R.ok(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得站内信统计信息")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<Object> getStatistics(
            @RequestParam(value = "senderId", required = false) Long senderId,
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
        Object statistics = internalMessageService.getStatistics(senderId, startTime, endTime);
        return R.ok(statistics);
    }

    @GetMapping("/status-count")
    @Operation(summary = "获得站内信状态统计")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<Object> getStatusCount(
            @RequestParam(value = "senderId", required = false) Long senderId) {
        Object statusCounts = internalMessageService.getStatusCount(senderId);
        return R.ok(statusCounts);
    }

    @GetMapping("/type-count")
    @Operation(summary = "获得站内信类型统计")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<Object> getTypeCount(
            @RequestParam(value = "senderId", required = false) Long senderId) {
        Object typeCounts = internalMessageService.getTypeCount(senderId);
        return R.ok(typeCounts);
    }

    @GetMapping("/priority-count")
    @Operation(summary = "获得站内信优先级统计")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:query')")
    public R<Object> getPriorityCount(
            @RequestParam(value = "senderId", required = false) Long senderId) {
        Object priorityCounts = internalMessageService.getPriorityCount(senderId);
        return R.ok(priorityCounts);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出站内信 Excel")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:export')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.EXPORT, description = "导出站内信 Excel")
    public void exportInternalMessageExcel(@Valid InternalMessageQueryDTO queryDTO,
                                           HttpServletResponse response) throws IOException {
        // 设置一个很大的页面大小来实现不分页效果
        PageResult<InternalMessageVO> pageResult = internalMessageService.getInternalMessagePage(queryDTO);
        List<InternalMessageVO> list = pageResult.getRecords();
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=internal_messages.xlsx");
        
        // 导出 Excel (简化版本，不使用EasyExcel)
        // TODO: 实现Excel导出功能
        response.getWriter().write("Excel导出功能待实现");
    }

    @PostMapping("/process-scheduled")
    @Operation(summary = "处理定时发送的站内信")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:process')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "处理定时发送的站内信")
    public R<Boolean> processScheduledMessages() {
        internalMessageService.processScheduledMessages();
        return R.ok(true);
    }

    @PostMapping("/process-expired")
    @Operation(summary = "处理过期的站内信")
    @PreAuthorize("@ss.hasPermission('notification:internal-message:process')")
    @OperationLog(title = "站内信管理", businessType = OperationLog.BusinessType.UPDATE, description = "处理过期的站内信")
    public R<Boolean> processExpiredMessages() {
        internalMessageService.processExpiredMessages();
        return R.ok(true);
    }
}