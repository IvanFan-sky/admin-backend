package com.admin.module.notification.api.vo.message;

import com.admin.framework.excel.core.annotations.DictFormat;
import com.admin.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内信导出VO
 *
 * @author admin
 * @since 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
public class InternalMessageExportVO {

    @ExcelProperty("站内信ID")
    private Long id;

    @ExcelProperty("消息标题")
    private String title;

    @ExcelProperty("消息内容")
    private String content;

    @ExcelProperty(value = "消息类型", converter = DictConvert.class)
    @DictFormat("message_type")
    private Integer type;

    @ExcelProperty(value = "优先级", converter = DictConvert.class)
    @DictFormat("message_priority")
    private Integer priority;

    @ExcelProperty("发送者")
    private String senderName;

    @ExcelProperty("接收者类型")
    private String receiverTypeName;

    @ExcelProperty(value = "发送状态", converter = DictConvert.class)
    @DictFormat("message_status")
    private Integer status;

    @ExcelProperty("成功发送数量")
    private Integer successCount;

    @ExcelProperty("发送失败数量")
    private Integer failureCount;

    @ExcelProperty("已读数量")
    private Integer readCount;

    @ExcelProperty("回执数量")
    private Integer receiptCount;

    @ExcelProperty("定时发送时间")
    private LocalDateTime scheduledTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("发送时间")
    private LocalDateTime sendTime;
}
